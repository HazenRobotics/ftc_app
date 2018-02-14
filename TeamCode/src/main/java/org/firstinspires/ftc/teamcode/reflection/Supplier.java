package org.firstinspires.ftc.teamcode.reflection;

public abstract class Supplier<T> {
    public abstract T get();

    public<V> Supplier<V> cast() {
        return new Supplier<V>() {
            @Override
            public V get() {
                return (V) Supplier.this.get();
            }
        };
    }

    public static<V> Supplier<V> constant(final V x) {
        return new Supplier<V>() {
            @Override
            public V get() {
                return x;
            }
        };
    }

    public<V> Supplier<V> map(final Mapping<T, V> f) {
        return new Supplier<V>() {
            @Override
            public V get() {
                return f.map(Supplier.this.get());
            }
        };
    }
}
