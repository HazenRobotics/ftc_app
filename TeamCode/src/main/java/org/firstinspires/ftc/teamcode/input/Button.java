package org.firstinspires.ftc.teamcode.input;

import org.firstinspires.ftc.teamcode.reflection.Supplier;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class Button extends Trigger {
    public Button(final Supplier<Boolean> value) {
        super(new Supplier<Float>() {
            @Override
            public Float get() {
                return value.get() ? 1.0f : 0;
            }
        });
    }
}