package org.firstinspires.ftc.teamcode.input;

import org.firstinspires.ftc.teamcode.reflection.Mapping;
import org.firstinspires.ftc.teamcode.reflection.Supplier;

public class BinaryInput {
    private final Supplier<Boolean> input;

    public BinaryInput(Supplier<Boolean> input) {
        this.input = input;
    }

    public boolean get() {
        return input.get();
    }

    public SignInput asSignum() {
        return new SignInput(input.map(Mapping.BOOL_TO_SIGNUM));
    }
}
