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

    public Joystick(Trigger x, Trigger y) {
        super(new Supplier<Float>() {
            @Override
            public Float get() {
                return (float) Math.hypot(x.pressure(), y.pressure());
            }
        });

        this.x = x;
        this.y = y;
        for(EventType type : EventType.values())
            listeners.put(type, new HashSet<>());
    }

    public Joystick(Supplier<Float> x, Supplier<Float> y) {
        this(new Trigger(x), new Trigger(y));
    }

    public float x() {
        return x.pressure();
    }

    public float y() {
        return y.pressure();
    }

    private final Map<EventType, Set<EventListener<JoystickEvent>>> listeners = new HashMap<>();

    EventType update() {
        EventType type = super.update();
        x.update();
        y.update();

        JoystickEvent event = new JoystickEvent(type, this, x() - prev_x, y() - prev_y);
        runApplicableListeners(listeners, event);

        this.prev_x = x();
        this.prev_y = y();

        return type;
    }

    public void listenJoystick(EventType type, EventListener<JoystickEvent> listener) {
        listeners.get(type).add(listener);
    }
}
