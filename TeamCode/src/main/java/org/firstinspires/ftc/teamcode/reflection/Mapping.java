package org.firstinspires.ftc.teamcode.reflection;

public abstract class Mapping<T, V> {
    public static final Mapping IDENTITY = new Mapping() {
        @Override
        public Object map(Object x) {
            return x;
        }
    };
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
    public static final Mapping<Double, Integer> DOUBLE_TO_INTEGER = new Mapping<Double, Integer>() {
        @Override
        public Integer map(Double x) {
            return x.intValue();
        }
    };
    public static final Mapping<Double, Boolean> DOUBLE_NONZERO = new Mapping<Double, Boolean>() {
        @Override
        public Boolean map(Double x) {
            return x != 0;
        }
    };
    public static final Mapping<Double, Double> DOUBLE_NEGATE = new Mapping<Double, Double>() {
        @Override
        public Double map(Double x) {
            return -x;
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

    public abstract V map(T x);
}
