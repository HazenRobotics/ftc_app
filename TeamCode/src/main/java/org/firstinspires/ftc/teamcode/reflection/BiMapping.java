package org.firstinspires.ftc.teamcode.reflection;

public abstract class BiMapping<T, V, W> {
    public static final BiMapping<Integer, Integer, Integer> INTEGER_SUB = new BiMapping<Integer, Integer, Integer>() {
        @Override
        public Integer map(Integer x, Integer y) {
            return x - y;
        }
    };

    public abstract W map(T x, V y);

    public BiMapping<V, T, W> flip() {
        return new BiMapping<V, T, W>() {
            @Override
            public W map(V x, T y) {
                return BiMapping.this.map(y, x);
            }
        };
    }
}
