package co.lijero.react;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Set;

/**
 * Builds a new reactive value and adds it to a ReactionManager.
 */
public class ReactionBuilder {
	private final ReactionManager manager;
	private Class<?> clazz = null;
	private Object object = null;
	private String name = null;
	private String defaultName = null;
	private Set<String> aliases = new HashSet<>();
	private Invokable value = null;
	private String[] dependencies = new String[] { };
	private boolean isTracker;
	
	ReactionBuilder(ReactionManager manager) {
		this.manager = manager;
	}
	
	/** Gives this reaction a name, instead of any default ones. */
	public ReactionBuilder name(String name) {
		if(this.name != null)
			throw new IllegalStateException("Reaction already has a name!");
		this.name = name;
		return this;
	}
	
	/**Adds an alias for this reaction. */
	public ReactionBuilder addAlias(String alias) {
		aliases.add(alias);
		return this;
	}
	
	/**
	 * Specifies which class this reaction uses. Only useful on static fields and methods,
	 * since onObject also adds a class, and constantValue doesn't need one.
	 */
	public ReactionBuilder onClass(Class<?> clazz) {
		if(object != null && !object.getClass().equals(clazz))
			throw new IllegalStateException("Reaction's object doesn't match given class!");
		if(object == null && clazz != null)
			throw new IllegalStateException("Reaction's class has already been specified!");
		this.clazz = clazz;
		return this;
	}
	
	/**
	 * Specifies which object a method will be called on or a field will be accessed from.
	 * Unnecessary for constant values, and onClass should be preferred for static fields and methods.
	 */
	public ReactionBuilder onObject(Object object) {
		if(this.object != null)
			throw new IllegalStateException("Reaction's object has already been specified!");
		if(clazz != null && !object.getClass().equals(clazz))
			throw new IllegalStateException("Reaction's class doesn't match given object!");
		this.clazz = object.getClass();
		this.object = object;
		return this;
	}
	
	/** Makes the reaction return a constant value. */
	public ReactionBuilder constantValue(Object object) {
		if(value != null)
			throw new IllegalStateException("Reaction already has a value!");
		// Constant values do not change and therefore do not need to be tracked
		this.isTracker = false;
		this.value = new Invokable() {
			@Override
			public Object invoke(Object... args) {
				if (args.length != 0)
					throw new IllegalArgumentException(name + " does not have " + args.length + " arguments: is a field.");
				return object;
			}
		};
		return this;
	}
	
	/** Makes the reaction track the given field. */
	public ReactionBuilder get(Field field) {
		if(value != null)
			throw new IllegalStateException("Reaction already has a value!");
		// Fields are by definition tracked. Constant values hopefully used constantValue instead for efficiency.
		this.isTracker = true;
		this.defaultName = field.getName();
		this.value = new Invokable() {
			@Override
			public Object invoke(Object... args) {
				if (args.length != 0)
		            throw new IllegalArgumentException(field.getName() + " does not have " + args.length + " arguments: is a field.");
				try {
					return field.get(object);
				} catch (IllegalAccessException | IllegalArgumentException e) {
					throw new RuntimeException(e);
				}
			}
		};
		return this;
	}
	
	/** Makes the reaction track the given field. A class or object must be provided before calling this method. */
	public ReactionBuilder get(String field) {
		if(clazz == null)
			throw new IllegalStateException("Must provide class or object before getting field by name.");
		try {
			return get(clazz.getDeclaredField(field));
		} catch (NoSuchFieldException | SecurityException e) {
			throw new RuntimeException(e);
		}
	}
	
	/** Makes a reaction by running the given method with the given dependencies. */
	public ReactionBuilder run(Method method, String... dependencies) {
		if(value != null)
			throw new IllegalStateException("Reaction already has a value!");
		// Methods with no dependencies are getters, and getters are trackers.
		this.isTracker = dependencies.length == 0;
		this.dependencies = dependencies;
		this.defaultName = method.getName();
		this.value = new Invokable() {
			@Override
			public Object invoke(Object... args) {
				if (args.length != method.getParameterCount())
		            throw new IllegalArgumentException(method.getName() + " does not have " + args.length + " arguments: expected " + method.getParameterCount());
				try {
					return method.invoke(object, args);
				} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
					throw new RuntimeException(e);
				}
			}
		};
		return this;
	}
	
	/** Makes a reaction by running the given method with the given dependencies. A class or object must be provided before calling this method. */
	public ReactionBuilder run(String method, String... dependencies) {
		if(clazz == null)
			throw new IllegalStateException("Must provide class or object before getting method by name!");
		try {
			return run(clazz.getDeclaredMethod(method));
		} catch (SecurityException | NoSuchMethodException e) {
			throw new RuntimeException(e);
		}
	}
	
	/** Makes this reaction a tracker. */
	public ReactionBuilder run(Tracker tracker, String... dependencies) {
		if(dependencies.length == 0)
			throw new IllegalArgumentException("You can't track nothing!");
		return onObject(tracker).run("update", dependencies);
	}
	
	/** Finish building the reaction and add it to the reactive system. A name and value must be provided before calling this method. */
	public void finish() {
		if(name == null && defaultName == null)
			throw new IllegalStateException("Reaction does not have a name!");
		if(value == null)
			throw new IllegalStateException("Reaction does not have a value!");
		
		Reaction reaction = new Reaction() {
			@Override
			public String getName() {
				return name != null ? name : defaultName;
			}
			
			@Override
			public String[] getDependencies() {
				return dependencies;
			}
			
			@Override
			public Object invoke(Object... args) {
				return value.invoke(args);
			}
		};
		
		manager.registerReaction(reaction);
		manager.registerDependencies(reaction, dependencies);
		if(this.isTracker)
			manager.registerTracker(reaction);
	}
}
