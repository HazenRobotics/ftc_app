package co.lijero.react;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/** See {@link org.firstinspires.ftc.teamcode.RobotTeleOp} for how this is used.
 * 
 * Terminology:
 * 		Reaction: A variable in the system. As a verb, it means "to update".
 * 		Tracker: A reaction that tracks a value outside this system and makes sure it stays updated.
 * 		Dependent: A reaction that somehow uses the value of another one, and needs to be updated if its parent is.
 * 		Dependency graph: The graph (as in a discrete graph-- think like trees and linked lists)
 * 			of every single dependency relationship in the system.
 * 
 * Warning: Beware using values outside the system that you expect to change, since they will not trigger
 * 		reactions! Track them within this system first.
 */
public final class ReactionManager {
	/** All of the variables in this system. */
	private final Map<String, Reaction> variables = new HashMap<>();
    /** All of the external values being tracked. */
    private final Set<Reaction> trackers = new HashSet<>();
    /** The dependency graph for this reactive system. */
    private final Map<String, Set<Reaction>> dependents = new HashMap<>();
    /** The cache of current values. Used to make non-idempotent reactions work properly, aside from speed. */
    private final Map<Reaction, Object> cache = new HashMap<>();
    /** Newly-registered reactions that need to be initialized. */
    private final List<Reaction> uninitialized = new ArrayList<>();

    /** Adds a dependency relation to the graph. */
    private void addDependent(String value, Reaction reactive) {
        if(!dependents.containsKey(value))
            dependents.put(value, new HashSet<Reaction>());
        dependents.get(value).add(reactive);
    }

    /** The instance of Reactive for the given method. */
    private static Reactive getReactive(Method method) {
        return method.getAnnotation(Reactive.class);
    }

    /** The instance of Reactive for the given method. */
    private static Reactive getReactive(Field field) {
        return field.getAnnotation(Reactive.class);
    }

    /** Does the method have a reactive annotation? */
    private static boolean isReactive(Method method) {
        return getReactive(method) != null;
    }

    /** Does the method have a reactive annotation? */
    private static boolean isReactive(Field field) {
        return getReactive(field) != null;
    }
    
    /** Get all methods marked with @Reactive in a class. */
    private static Method[] getReactiveMethods(Class<?> clazz) {
    	List<Method> methods = new ArrayList<>();
    	for(Method method : clazz.getMethods())
    		if(isReactive(method))
    			methods.add(method);
    	return (Method[]) methods.toArray();
    }
    
    /** Get all fields marked with @Reactive in a class. */
    private static Field[] getReactiveFields(Class<?> clazz) {
    	List<Field> fields = new ArrayList<>();
    	for(Field field : clazz.getFields())
    		if(isReactive(field))
    			fields.add(field);
    	return (Field[]) fields.toArray();
    }
    
    /** Register a reaction into the system. */
    void registerReaction(Reaction reaction) {
    	variables.put(reaction.getName(), reaction);
    	uninitialized.add(reaction);
    }
    
    /** Register a reaction's dependencies into the dependency tree. */
    void registerDependencies(Reaction reaction, String... dependencies) {
        for(String dependency : dependencies)
            addDependent(dependency, reaction);
    }
    
    /** Register a reaction as a tracker. */
    void registerTracker(Reaction reaction) {
        trackers.add(reaction);
    }
    
    /** Build a new reaction. */
    public ReactionBuilder make() {
    	return new ReactionBuilder(this);
    }
    
    /** Register all of an object's reactions into this reactive system. */
    public void register(Object object) {
    	// Get all methods marked with @Reactive
        for (Method method : getReactiveMethods(object.getClass())) {
        	// Retrieve that annotation's values
            Reactive reactive = getReactive(method);
            // Make a new reaction with this information
            ReactionBuilder reaction = make();
            // Pass the object to run the method on
            reaction.onObject(object);
            // Pass the method and its dependencies
            reaction.run(method, reactive.depends());
            // Use the variable name given if it exists.
            if(!reactive.name().isEmpty())
            	reaction.name(reactive.name());
            // Register the reaction
            reaction.finish();
        }
        
        // This is the same as above. Unfortunately, I can't factor it out because of
        // the dependency chain and type issues.
        for (Field field : getReactiveFields(object.getClass())) {
        	Reactive reactive = getReactive(field);
        	if(reactive.depends().length != 0)
        		throw new IllegalArgumentException("A field cannot have dependencies!");
            ReactionBuilder reaction = make();
            reaction.onObject(object);
            // If the value is final, just grab it in advance
            if(Modifier.isFinal(field.getModifiers())) {
				try {
					reaction.constantValue(field.get(object));
				} catch (IllegalArgumentException | IllegalAccessException e) {
					// I'm pretty sure this isn't possible.
					throw new RuntimeException(e);
				}
            // Otherwise just do it the normal way
            } else {
            	reaction.get(field);
            }
            if(!reactive.name().isEmpty())
            	reaction.name(reactive.name());
            reaction.finish();
        }
    }

    /* Invalidates the cache for all values dependent on this reaction. */
    private void invalidate(Reaction reaction) {
        cache.remove(reaction);
        for(Reaction dependent : dependents.get(reaction.getName()))
            if(cache.containsKey(dependent))
                invalidate(reaction);
    }

    /** Get the current value of a reaction. It's called fold because you're folding the dependency graph. */
    private Object fold(Reaction reaction) {
    	// Retrieve the value from the cache if it's still around.
        if(cache.containsKey(reaction))
            return cache.get(reaction);
        // Otherwise, accumulate all of the input values 
        List<Object> inputs = new ArrayList<>();
        for(String variable : reaction.getDependencies())
        	inputs.add(fold(variables.get(variable)));
        // And then get the result of the reaction
        try {
            Object ret = reaction.invoke(inputs);
            // and cache it
            cache.put(reaction, ret);
            return ret;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    
    /** Gets the current value of the variable with the given name. */
    public Object get(String variable) {
    	return cache.get(variables.get(variable));
    }
    
    /**
     * Gets every reaction that depends on this one that does not have any dependents itself.
     */
    private Set<Reaction> getUltimateDependencies(Reaction reaction) {
        Set<Reaction> reactionDependents = dependents.get(reaction.getName());
        if(reactionDependents.isEmpty())
            reactionDependents.add(reaction);
        else for(Reaction dependent : reactionDependents)
            reactionDependents.addAll(getUltimateDependencies(dependent));
        return reactionDependents;
    }

    /** Update the reactive system's values. */
    public void update() {
        try {
            // Invalidate all changed values and their dependents
            for (Reaction tracker : trackers) {
                if (tracker.invoke().equals(cache.get(tracker)))
                    continue;
                invalidate(tracker);
            }

            // Find out what values we need to fold to completely rebuild the tree
            Set<Reaction> requiringUpdate = new HashSet<>();
            for (Reaction tracker : trackers)
                requiringUpdate.addAll(getUltimateDependencies(tracker));
            
            // All uninitialized reactions need to get an initial value
            // Not semantically correct but whatever
            requiringUpdate.addAll(uninitialized);
            uninitialized.clear();

            // Update all of the changed values
            for(Reaction reaction : requiringUpdate)
                fold(reaction);
        } catch(Exception e) {
            throw new RuntimeException(e);
        }
    }
}
