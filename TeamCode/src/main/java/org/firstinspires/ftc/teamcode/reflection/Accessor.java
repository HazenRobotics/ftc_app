package org.firstinspires.ftc.teamcode.reflection;

public class Accessor<T> extends Supplier<T> {
    private final Invokable value;
    private final Accessor[] dependencies;

    public Accessor(Invokable value, Accessor... dependencies) {
        this.value = value;
        this.dependencies = dependencies;
    }

    public T get() {
        Object[] params = new Object[dependencies.length];
        for (int i = 0; i < dependencies.length; i++)
            params[i] = dependencies[i].get();
        return (T) value.invoke(params);
    }
}
