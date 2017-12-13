package org.firstinspires.ftc.teamcode.reflection;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

/** Gets the value of a field (variable) via reflection. */
public class FieldAccessor<T> implements Supplier<T> {
    /** The object the field is in. (e.g. position) */
    private final Object object;
    /** The field we're accessing. (e.g. x)*/
    private final Field field;

    public FieldAccessor(Field field, Object object) {
        // We don't need an object for a static field, by definition.
        // If you forgot: static means we can do MyClass.myThing rather than myObject.myThing.
        if(object == null && Modifier.isStatic(field.getModifiers()))
            throw new IllegalArgumentException("Object was null when generating field accessor and " + field.getName() + " is not static.");
        // Read as "can we write (T) object.field without a ClassCastException".
        // Can we actually access the value?
        if(!(field.isAccessible() || Modifier.isPublic(field.getModifiers())))
            throw new IllegalArgumentException("Field " + field.getName() + " is not accessible.");
        // Make sure this is "position.x" and not "magic.x".
        if(!field.getDeclaringClass().isInstance(object))
            throw new IllegalArgumentException(field.getName() + " is not compatible with the object " + object);
        this.object = object;
        this.field = field;
    }

    public FieldAccessor(Field field) {
        this(field, null);
    }

    public FieldAccessor(String field, Object object) throws NoSuchFieldException {
        this(object.getClass().getField(field), object);
    }

    public FieldAccessor(String field, Class<?> declaringClass) throws NoSuchFieldException {
        this(declaringClass.getField(field), null);
    }

    public FieldAccessor(Class<? extends T> clazz, Field field, Object object) {
        this(field, object);
        if(!field.getType().isAssignableFrom(clazz))
            throw new IllegalArgumentException("Field is not correct type for trigger: " + field.getName() + " : " + field.getType());
    }

    public FieldAccessor(Class<? extends T> clazz, Field field) {
        this(clazz, field, null);
    }

    public FieldAccessor(Class<? extends T> clazz, String field, Object object) throws NoSuchFieldException {
        this(clazz, object.getClass().getField(field), object);
    }

    public FieldAccessor(Class<? extends T> clazz, String field, Class<?> declaringClass) throws NoSuchFieldException {
        this(clazz, declaringClass.getField(field), null);
    }

    public static final FieldAccessor<Boolean> boola(String field, Object object) throws NoSuchFieldException {
        return new FieldAccessor<>(Boolean.TYPE, field, object);
    }

    public static final FieldAccessor<Float> floata(String field, Object object) throws NoSuchFieldException {
        return new FieldAccessor<>(Float.TYPE, field, object);
    }

    public static<T> FieldAccessor<T> unsafe(String field, Object object) {
        try {
            return new FieldAccessor<>(object.getClass().getField(field), object);
        } catch (NoSuchFieldException e) {
            throw new RuntimeException(e);
        }
    }

    ///
    /// The "unsafe" constructors aren't really unsafe-- it's just due to a java limitation that I have to add them.
    /// It's more unsafe in the "trust me" sense than the "unpredictable" sense.
    ///

    public static<T> FieldAccessor<T> unsafe(String field, Class<?> declaringClass) {
        try {
            return new FieldAccessor<>(declaringClass.getField(field), null);
        } catch (NoSuchFieldException e) {
            throw new RuntimeException(e);
        }
    }

    public static<T> FieldAccessor<T> unsafe(Class<? extends T> clazz, String field, Object object) {
        try {
            return new FieldAccessor<>(clazz, object.getClass().getField(field), object);
        } catch (NoSuchFieldException e) {
            throw new RuntimeException(e);
        }
    }

    public static<T> FieldAccessor<T> unsafe(Class<? extends T> clazz, String field, Class<?> declaringClass) {
        try {
            return new FieldAccessor<>(clazz, declaringClass.getField(field), null);
        } catch (NoSuchFieldException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public T get() {
        try {
            return (T) field.get(object);
        } catch (IllegalAccessException e) {
            // We already checked in the constructor that the field is accessible, so this shouldn't be possible.
            throw new RuntimeException(e);
        }
    }
}
