package org.firstinspires.ftc.teamcode.input;

import org.firstinspires.ftc.teamcode.models.Vector;
import org.firstinspires.ftc.teamcode.reflection.BiMapping;
import org.firstinspires.ftc.teamcode.reflection.Mapping;
import org.firstinspires.ftc.teamcode.reflection.Supplier;

public class Button {
    private static final double INPUT_ACTIVATION_LIMIT = 0.05;
    private static final Mapping<Double, Double> MAP_ROUND_ACTIVATION = new Mapping<Double, Double>() {
        @Override
        public Double map(Double x) {
            return roundActivation(x);
        }
    };
    private static final Mapping<Vector, Vector> MAP_VECTOR_ROUND_ACTIVATION =
            Vector.liftMapMagnitude(MAP_ROUND_ACTIVATION);
    private final Supplier<Vector> source;
    private Vector previous = Vector.ZERO;

    protected Button(Supplier<Vector> source) {
        this.source = source.map(MAP_VECTOR_ROUND_ACTIVATION);
    }

    private static Double roundActivation(Double x) {
        if (x < INPUT_ACTIVATION_LIMIT)
            return 0.0;
        return x;
    }

    public static Button fromVector(Supplier<Vector> source) {
        return new Button(source);
    }

    public static Button fromVector(Supplier<Double> x, Supplier<Double> y) {
        return Button.fromVector(x.composeMap(y, new BiMapping<Double, Double, Vector>() {
            @Override
            public Vector map(Double x, Double y) {
                return new Vector(x, y);
            }
        }));
    }

    public static Button fromScalar(Supplier<Double> input) {
        return new Button(input.map(new Mapping<Double, Vector>() {
            @Override
            public Vector map(Double x) {
                return Vector.fromPolar(x, 0);
            }
        }));
    }

    public static Button fromSignum(Supplier<Integer> input) {
        return Button.fromScalar(input.map(Mapping.INTEGER_TO_DOUBLE));
    }

    public static Button fromSignum(Supplier<Boolean> pos, Supplier<Boolean> neg) {
        return Button.fromSignum(pos.map(Mapping.BOOL_TO_SIGNUM)
                .composeMap(neg.map(Mapping.BOOL_TO_SIGNUM),
                        BiMapping.INTEGER_SUB));
    }

    public static Button fromPress(Supplier<Boolean> input) {
        return Button.fromSignum(input.map(new Mapping<Boolean, Integer>() {
            @Override
            public Integer map(Boolean x) {
                return x ? 1 : 0;
            }
        }));
    }

    void updatePrevious() {
        previous = source.get();
    }

    public Vector delta() {
        return position().delta(previous);
    }

    public boolean hasChangedSince(Vector previous) {
        return position().delta(previous).getMagnitude() > INPUT_ACTIVATION_LIMIT;
    }

    public Supplier<Vector> supplyPosition() {
        return source;
    }

    public Vector position() {
        return source.get();
    }

    public Supplier<Double> supplyX() {
        return supplyPosition().map(Vector.TO_X);
    }

    public double x() {
        return supplyX().get();
    }

    public Supplier<Double> supplyY() {
        return supplyPosition().map(Vector.TO_Y);
    }

    public double y() {
        return supplyY().get();
    }

    public Supplier<Double> supplyMagnitude() {
        return supplyPosition().map(Vector.TO_MAGNITUDE);
    }

    public double magnitude() {
        return supplyMagnitude().get();
    }

    public Supplier<Double> supplyAngle() {
        return supplyPosition().map(Vector.TO_ANGLE);
    }

    public double angle() {
        return supplyAngle().get();
    }

    public Supplier<Integer> supplySignumX() {
        return supplyX().map(Mapping.DOUBLE_TO_INTEGER).map(Mapping.INTEGER_SIGNUM);
    }

    public int signumX() {
        return supplySignumX().get();
    }

    public Supplier<Integer> supplySignumY() {
        return supplyY().map(Mapping.DOUBLE_TO_INTEGER).map(Mapping.INTEGER_SIGNUM);
    }

    public int signumY() {
        return supplySignumY().get();
    }

    public Supplier<Boolean> supplyPressedX() {
        return supplyX().map(Mapping.DOUBLE_NONZERO);
    }

    public boolean pressedX() {
        return supplyPressedX().get();
    }

    public Supplier<Boolean> supplyPressedY() {
        return supplyY().map(Mapping.DOUBLE_NONZERO);
    }

    public boolean pressedY() {
        return supplyPressedY().get();
    }

    public Button invert() {
        return Button.fromVector(source.map(Vector.MAP_NEGATE));
    }

    public Button invertX() {
        return Button.fromVector(source.map(Vector.MAP_NEGATE_X));
    }

    public Button invertY() {
        return Button.fromVector(source.map(Vector.MAP_NEGATE_Y));
    }
}
