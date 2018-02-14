package org.firstinspires.ftc.teamcode.reflection;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class AccessorBuilder {
	private Class<?> clazz = null;
	private Object object = null;
	private String name = null;
	private Invokable value = null;
	
	/**
	 * Specifies which class this reaction uses. Only useful on static fields and methods,
	 * since onObject also adds a class, and constantValue doesn't need one.
	 */
	public AccessorBuilder onClass(Class<?> clazz) {
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
	public AccessorBuilder onObject(Object object) {
		if(this.object != null)
			throw new IllegalStateException("Reaction's object has already been specified!");
		if(clazz != null && !object.getClass().equals(clazz))
			throw new IllegalStateException("Reaction's class doesn't match given object!");
		this.clazz = object.getClass();
		this.object = object;
		return this;
	}
	
	/** Makes the reaction return a constant value. */
	public AccessorBuilder constantValue(final Object object) {
		if(value != null)
			throw new IllegalStateException("Reaction already has a value!");
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
	public AccessorBuilder get(final Field field) {
		if(value != null)
			throw new IllegalStateException("Reaction already has a value!");
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
	public AccessorBuilder get(String field) {
		if(clazz == null)
			throw new IllegalStateException("Must provide class or object before getting field by name.");
		try {
			return get(clazz.getDeclaredField(field));
		} catch (NoSuchFieldException | SecurityException e) {
			throw new RuntimeException(e);
		}
	}
	
	/** Makes a reaction by running the given method with the given dependencies. */
	public AccessorBuilder run(final Method method) {
		if(value != null)
			throw new IllegalStateException("Reaction already has a value!");
		this.value = new Invokable() {
			@Override
			public Object invoke(Object... args) {
				if (args.length != method.getParameterTypes().length)
		            throw new IllegalArgumentException(method.getName() + " does not have " + args.length + " arguments: expected " + method.getParameterTypes().length);
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
	public AccessorBuilder run(String method, String... dependencies) {
		if(clazz == null)
			throw new IllegalStateException("Must provide class or object before getting method by name!");
		try {
			return run(clazz.getDeclaredMethod(method));
		} catch (SecurityException | NoSuchMethodException e) {
			throw new RuntimeException(e);
		}
	}
	
	/** Finish building the reaction and add it to the reactive system. A name and value must be provided before calling this method. */
	public<T> Accessor<T> finish(Accessor... dependencies) {
		if(value == null)
			throw new IllegalStateException("Reaction does not have a value!");
		
		return new Accessor(value, dependencies);
	}
}