package org.firstinspires.ftc.teamcode.reflection;

public interface Mapping<T, V> {
    V map(T x);

    public static final Mapping<Boolean, Integer> BOOL_TO_SIGNUM = new Mapping<Boolean, Integer>() {
        @Override
        public Integer map(Boolean x) {
            return x ? 1 : 0;
        }
    };

    public static final Mapping<Integer, Double> INTEGER_TO_DOUBLE = new Mapping<Integer, Double>() {
        @Override
        public Double map(Integer x) {
            return x.doubleValue();
        }
    };

    public static final Mapping<Integer, Boolean> INTEGER_NONZERO = new Mapping<Integer, Boolean>() {
        @Override
        public Boolean map(Integer x) {
            return x != 0;
        }
    };

    public static final Mapping<Integer, Boolean> INTEGER_GTZERO = new Mapping<Integer, Boolean>() {
        @Override
        public Boolean map(Integer x) {
            return x > 0;
        }
    };

    public static final Mapping<Integer, Boolean> INTEGER_LTZERO = new Mapping<Integer, Boolean>() {
        @Override
        public Boolean map(Integer x) {
            return x < 0;
        }
    };

    public static final Mapping<Integer, Integer> INTEGER_SIGNUM = new Mapping<Integer, Integer>() {
        @Override
        public Integer map(Integer x) {
            return (int) Math.signum(x);
        }
    };
}
