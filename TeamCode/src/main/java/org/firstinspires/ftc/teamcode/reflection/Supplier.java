package org.firstinspires.ftc.teamcode.reflection;

public abstract class Supplier<T> {
    public static <V> Supplier<V> constant(final V x) {
        return new Supplier<V>() {
            @Override
            public V get() {
                return x;
            }
        };
    }

    public abstract T get();

    public <V> Supplier<V> cast() {
        return new Supplier<V>() {
            @Override
            public V get() {
                return (V) Supplier.this.get();
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

    public <V, W> Supplier<W> composeMap(final Supplier<V> y, final BiMapping<T, V, W> f) {
        return new Supplier<W>() {
            @Override
            public W get() {
                return f.map(Supplier.this.get(), y.get());
            }
        };
    }
}
