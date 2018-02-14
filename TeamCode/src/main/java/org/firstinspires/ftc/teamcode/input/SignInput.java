package org.firstinspires.ftc.teamcode.input;

import org.firstinspires.ftc.teamcode.reflection.Mapping;
import org.firstinspires.ftc.teamcode.reflection.Supplier;

public class SignInput {
    private final Supplier<Integer> input;

    public final BinaryInput pos;
    public final BinaryInput neg;

    public SignInput(final Supplier<Integer> input) {
        this.input = input.map(Mapping.INTEGER_SIGNUM);

        this.pos = new BinaryInput(input.map(Mapping.INTEGER_GTZERO));
        this.neg = new BinaryInput(input.map(Mapping.INTEGER_LTZERO));
    }

    public SignInput(final BinaryInput pos, final BinaryInput neg) {
        this.input = new Supplier<Integer>() {
            @Override
            public Integer get() {
                return pos.asSignum().signum() - neg.asSignum().signum();
            }
        };

        this.pos = pos;
        this.neg = neg;
    }

    public Integer signum() {
        return input.get();
    }

    public BinaryInput asActivity() {
        return new BinaryInput(input.map(Mapping.INTEGER_NONZERO));
    }

    public ScalarInput asScalar() {
        return new ScalarInput(input.map(Mapping.INTEGER_TO_DOUBLE));
    }
}
