package org.firstinspires.ftc.teamcode.input;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public abstract class Trigger extends Button {
    private float prev_pressure;

    static Trigger generate(final Object obj, String fieldname) throws NoSuchFieldException, IllegalAccessException {
        // Sorry for the copy/pastes. I'll consider implementing generic field accessors later, but that seems excessive for now.
        final Field field = obj.getClass().getField(fieldname);
        if(obj == null)
            throw new IllegalArgumentException("Object was null when generating field accessor.");
        if(!field.getType().equals(Float.TYPE))
            throw new IllegalArgumentException("Field is not correct type for trigger: " + fieldname + " : " + field.getType());
        return new Trigger() {
            @Override
            public float pressure() {
                try {
                    return (float) field.get(obj);
                } catch (IllegalAccessException e) {
                    throw new RuntimeException(e);
                }
            }
        };
    }

    public static final float PRESSURE_THRESHOLD = 0;

    @Override
    public boolean pressed() {
        return pressure() > PRESSURE_THRESHOLD;
    }

    public abstract float pressure();

    private final Map<EventType, Set<AEventListener<TriggerEvent>>> listeners = new HashMap<>();

    public Trigger() {
        for(EventType type : EventType.values())
            listeners.put(type, new HashSet<AEventListener<TriggerEvent>>());
    }

    EventType update() {
        EventType type = super.update();

        TriggerEvent event = new TriggerEvent(type, this, pressure() - prev_pressure);
        for(EventType etype : EventType.values())
            if(type.compatibleWith(etype))
                for(AEventListener<TriggerEvent> listener : listeners.get(etype))
                    listener.on(event);

        this.prev_pressure = pressure();

        return type;
    }

    public void addTriggerListener(AEventListener<TriggerEvent> listener, EventType type) {
        listeners.get(type).add(listener);
    }
}
