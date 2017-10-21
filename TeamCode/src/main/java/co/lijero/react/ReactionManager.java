package co.lijero.react;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public final class ReactionManager {
    /** All of the external values being tracked. */
    private final Set<Reaction> trackers = new HashSet<>();
    /** The dependency graph for this reactive system. */
    private final Map<String, Set<Reaction>> dependents = new HashMap<>();
    /** The cache of current values. Used to make non-idempotent reactions work properly, aside from speed. */
    private final Map<String, Object> cache = new HashMap<>();

    /** Adds a dependency relation to the graph. */
    private void addDependent(String value, Reaction reactive) {
        if(!dependents.containsKey(value))
            dependents.put(value, new HashSet<Reaction>());
        dependents.get(value).add(reactive);
    }

    /** Get every reaction that depends on this one. */
    private Set<Reaction> getAllDependents(Reaction reaction) {
        Set<Reaction> dependents = new HashSet<>();
        dependents.add(reaction);
        for(Reaction dependency : this.dependents.get(reaction.getName()))
            dependents.addAll(getAllDependents(dependency));
        return dependents;
    }

    /** Gets every reaction that depends on this one that does not have any dependents itself. */
    private Set<Reaction> getUltimateDependencies(Reaction reaction) {
        Set<Reaction> reactionDependents = dependents.get(reaction);
        if(reactionDependents.isEmpty())
            reactionDependents.add(reaction);
        else for(Reaction dependent : reactionDependents)
            reactionDependents.addAll(getUltimateDependencies(dependent));
        return reactionDependents;
    }

    private Reactive getReactive(Method method) {
        return method.getAnnotation(Reactive.class);
    }

    private boolean isReactive(Method method) {
        return getReactive(method) != null;
    }

    /** Get the current value of a reaction. It's called fold because you're folding the dependency graph. */
    private Object fold(Reaction reaction) {
        if(cache.containsKey(reaction.getName()))
            return cache.get(reaction.getName());
        List<Object> inputs = new ArrayList<>();
        try {
            Object ret = reaction.invoke(inputs);
            cache.put(reaction.getName(), ret);
            return ret;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /* Invalidates the cache for all values dependent on this reaction. */
    private void invalidate(Reaction reaction) {
        cache.remove(reaction);
        for(Reaction dependent : dependents.get(reaction))
            if(cache.containsKey(dependent))
                invalidate(reaction);
    }

    /** Adds a reaction into the dependency graph. */
    private void registerDependencies(Reaction reaction, Method method) {
        for(String dependency : getReactive(method).depends())
            addDependent(dependency, reaction);
    }

    /** Adds a reaction into the dependency graph. */
    private void registerDependencies(Reaction reaction, String... dependencies) {
        for(String dependency : dependencies)
            addDependent(dependency, reaction);
    }

    /** Registers a reaction as a tracker. */
    private void registerTracker(Reaction reaction) {
        trackers.add(reaction);
    }

    private void registerAdaptedMethod(Reaction reaction, String... dependencies) {
        register(reaction);
        registerDependencies(reaction, dependencies);
        if(reaction.isTracker())
            registerTracker(reaction);
    }

    private void registerMethod(Reaction reaction, Method method) {
        register(reaction);
        registerDependencies(reaction, method);
        if(reaction.isTracker())
            registerTracker(reaction);
    }

    private void registerField(Reaction reaction) {
        register(reaction);
        registerTracker(reaction);
    }


    /** Registers a reactive value. */
    public void registerMethod(final Object object, final Method method) {
        registerMethod(new MethodReaction(object, method), method);
    }

    /** Registers an externally tracked value. */
    public void registerField(final Object object, final Field field) {
        registerField(new FieldReaction(object, field));
    }


    /** Registers a reactive value. */
    public void registerMethod(String name, final Object object, final Method method) {
        registerMethod(new MethodReaction(object, method, name), method);
    }

    /** Registers an externally tracked value. */
    public void registerField(String name, final Object object, final Field field) {
        registerField(new FieldReaction(object, field, name));
    }

    public void registerMethod(final Object object, final String method) {
        try {
            registerMethod(object, object.getClass().getMethod(method));
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }

    public void registerField(final Object object, final String field) {
        try {
            registerField(object, object.getClass().getField(field));
        } catch (NoSuchFieldException e) {
            throw new RuntimeException(e);
        }
    }

    public void registerMethod(String name, final Object object, final String method) {
        try {
            registerMethod(name, object, object.getClass().getMethod(method));
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }

    public void registerField(String name, final Object object, final String field) {
        try {
            registerField(object, object.getClass().getField(field));
        } catch (NoSuchFieldException e) {
            throw new RuntimeException(e);
        }
    }

    public void registerNonreactiveMethod(String name, Object object, Method method, String... dependencies) {
        registerAdaptedMethod(new AdaptedMethodReaction(object, method, name), dependencies);
    }

    public void registerNonreactiveMethod(Object object, Method method, String... dependencies) {
        registerAdaptedMethod(new AdaptedMethodReaction(object, method), dependencies);
    }

    public void registerNonreactiveMethod(String name, final Object object, final String method, String... dependencies) {
        try {
            registerNonreactiveMethod(name, object, object.getClass().getMethod(method), dependencies);
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }

    public void registerNonreactiveMethod(final String method, final Object object, String... dependencies) {
        try {
            registerNonreactiveMethod(object, object.getClass().getMethod(method), dependencies);
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }

    public void registerStaticMethod(String name, Method method, String... dependencies) {
        registerNonreactiveMethod(name, null, method, dependencies);
    }

    public void registerStaticMethod(Method method, String... dependencies) {
        registerNonreactiveMethod((Object) null, method, dependencies);
    }

    public void registerStaticMethod(String name, String method, Class<?> clazz, String... dependencies) {
        try {
            registerNonreactiveMethod(name, null, clazz.getMethod(method), dependencies);
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }

    public void registerStaticMethod(String method, Class<?> clazz, String... dependencies) {
        try {
            registerNonreactiveMethod((Object) null, clazz.getMethod(method), dependencies);
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }

    /** Register all of an object's reactions into this reactive system. */
    public void register(Object object) {
        for (Method method : object.getClass().getMethods())
            if (isReactive(method))
                register(method);
    }

    /** Update the reactive system's valeus. */
    public void update() {
        try {
            // Invalidate all changed values and their dependents
            for (Reaction tracker : trackers) {
                if (tracker.invoke().equals(cache.get(tracker.getName())))
                    return;
                invalidate(tracker);
            }

            // Find out what needs to be pushed
            // Techically we could use getAllDependencies and it'd be equivalent because caching, but this is more efficient
            Set<Reaction> ultimateDependencies = new HashSet<>();
            for (Reaction tracker : trackers)
                ultimateDependencies.addAll(getUltimateDependencies(tracker));

            // Update all of the changed values
            for(Reaction reaction : ultimateDependencies)
                fold(reaction);
        } catch(Exception e) {
            throw new RuntimeException(e);
        }
    }
}
