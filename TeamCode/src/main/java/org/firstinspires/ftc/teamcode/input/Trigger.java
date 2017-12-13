package org.firstinspires.ftc.teamcode.input;

import org.firstinspires.ftc.teamcode.reflection.Supplier;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class Trigger extends Button {
    private final Supplier<Float> value;
    private float prev_pressure;

    public static final float PRESSURE_THRESHOLD = 0;

    public float pressure() {
        return value.get();
    }

    private final Map<EventType, Set<EventListener<TriggerEvent>>> listeners = new HashMap<>();

    public Trigger(Supplier<Float> value) {
        // Duplicated PRESSURE_THRESHOLD code due to Java's limitations, /again/
        super(new Supplier<Boolean>() {
            @Override
            public Boolean get() {
                return Math.abs(value.get()) > PRESSURE_THRESHOLD;
            }
        });
        Supplier<Float> realValue = new Supplier<Float>() {
            @Override
            public Float get() {
                return Math.abs(value.get()) > PRESSURE_THRESHOLD ? value.get() : 0f;
            }
        };
        this.value = realValue;
        for(EventType type : EventType.values())
            listeners.put(type, new HashSet<EventListener<TriggerEvent>>());
    }

    EventType update() {
        EventType type = super.update();

        TriggerEvent event = new TriggerEvent(type, this, pressure() - prev_pressure);
        runApplicableListeners(listeners, event);

        this.prev_pressure = pressure();

        return type;
    }

    public void listenPressure(EventType type, EventListener<TriggerEvent> listener) {
        listeners.get(type).add(listener);
    }
}
