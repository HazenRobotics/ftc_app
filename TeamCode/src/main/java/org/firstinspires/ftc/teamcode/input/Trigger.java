package org.firstinspires.ftc.teamcode.input;

import org.firstinspires.ftc.teamcode.reflection.Supplier;

public class Trigger extends IButton {
    private final Supplier<Float> value;

    public static final float TRIGGER_THRESHOLD = 0;

    @Override
    public float value() {
        return value.get() > TRIGGER_THRESHOLD ? value.get() : 0;
    }

    public Trigger(final Supplier<Float> value) {
        this.value = value;
    }
}