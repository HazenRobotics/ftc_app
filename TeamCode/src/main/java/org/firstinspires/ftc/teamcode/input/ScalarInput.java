package org.firstinspires.ftc.teamcode.input;

import org.firstinspires.ftc.teamcode.models.Vector;
import org.firstinspires.ftc.teamcode.reflection.Mapping;
import org.firstinspires.ftc.teamcode.reflection.Supplier;

public class ScalarInput {
    private static final double INPUT_ACTIVATION_LIMIT = 0.05;

    private final Supplier<Double> input;

    public ScalarInput(Supplier<Double> input) {
        this.input = input.map(new Mapping<Double, Double>() {
            @Override
            public Double map(Double x) {
                if(x < INPUT_ACTIVATION_LIMIT && x > -INPUT_ACTIVATION_LIMIT)
                    return 0.0;
                return x;
            }
        });
    }

    public double value() {
        return input.get();
    }

    public SignInput asSignum() {
        return new SignInput(new Supplier<Integer>() {
            @Override
            public Integer get() {
                return (int) Math.signum(value());
            }
        });
    }

    public VectorInput asMagnitude() {
        return new VectorInput(new Supplier<Vector>() {
            @Override
            public Vector get() {
                return Vector.fromPolar(input.get(), 0);
            }
        });
    }
}
