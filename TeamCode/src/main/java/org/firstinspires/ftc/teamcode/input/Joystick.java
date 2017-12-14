package org.firstinspires.ftc.teamcode.input;

import org.firstinspires.ftc.teamcode.reflection.Supplier;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public final class Joystick extends Trigger {
    public final Trigger x;
    public final Trigger y;

    private float prev_x;
    private float prev_y;

    public Joystick(final Trigger x, final Trigger y) {
        super(new Supplier<Float>() {
            @Override
            public Float get() {
                return (float) Math.hypot(x.value(), y.value());
            }
        });

        this.x = x;
        this.y = y;
        for(EventType type : EventType.values())
            listeners.put(type, new HashSet<EventListener>());
    }

    public Joystick(Supplier<Float> x, Supplier<Float> y) {
        this(new Trigger(x), new Trigger(y));
    }

    public float x() {
        return x.value();
    }

    public float y() {
        return y.value();
    }

    private final Map<EventType, Set<EventListener>> listeners = new HashMap<>();

    @Override
    public void update() {
        super.update();
        x.update();
        y.update();
    }

    @Override
    public void step() {
        super.step();
        x.step();
        y.step();
    }
}
