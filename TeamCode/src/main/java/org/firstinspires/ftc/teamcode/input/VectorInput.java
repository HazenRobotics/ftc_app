package org.firstinspires.ftc.teamcode.input;

import org.firstinspires.ftc.teamcode.models.Vector;
import org.firstinspires.ftc.teamcode.reflection.Supplier;

public class VectorInput {
    private final Supplier<Vector> input;

    public final ScalarInput x;
    public final ScalarInput y;

    public final ScalarInput magnitude;
    public final ScalarInput angle;

    private final ScalarInput magnitudeScalar() {
        return new ScalarInput(input.map(Vector.TO_MAGNITUDE));
    }

    private final ScalarInput angleScalar() {
        return new ScalarInput(input.map(Vector.TO_ANGLE));
    }

    public VectorInput(final Supplier<Vector> input) {
        this.input = input;

        this.x = new ScalarInput(new Supplier<Double>() {
            @Override
            public Double get() {
                return input.get().getX();
            }
        });

        this.y = new ScalarInput(new Supplier<Double>() {
            @Override
            public Double get() {
                return input.get().getY();
            }
        });

        this.magnitude = magnitudeScalar();
        this.angle = angleScalar();
    }

    public VectorInput(final ScalarInput x, final ScalarInput y) {
        this.input = new Supplier<Vector>() {
            @Override
            public Vector get() {
                return new Vector(x.value(), y.value());
            }
        };

        this.x = x;
        this.y = y;

        this.magnitude = magnitudeScalar();
        this.angle = angleScalar();
    }

    public Vector position() {
        return new Vector(x.value(), y.value());
    }

    public VectorInput negate() {
        return new VectorInput(input.map(Vector.MAP_NEGATE));
    }


}
