package org.firstinspires.ftc.teamcode.input;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public abstract class Joystick extends Trigger {
    private float prev_x;
    private float prev_y;

    static Joystick generate(final Object obj, String fieldnamex, String fieldnamey) throws NoSuchFieldException, IllegalAccessException {
        // Sorry for the copy/pastes. I'll consider implementing generic field accessors later, but that seems excessive for now.
        final Field fieldx = obj.getClass().getField(fieldnamex);
        final Field fieldy = obj.getClass().getField(fieldnamey);
        if(obj == null)
            throw new IllegalArgumentException("Object was null when generating field accessor.");
        if(!fieldx.getType().equals(Float.TYPE))
            throw new IllegalArgumentException("Field is not correct type for joystick: " + fieldnamex + " : " + fieldx.getType());
        if(!fieldy.getType().equals(Float.TYPE))
            throw new IllegalArgumentException("Field is not correct type for joystick: " + fieldnamey + " : " + fieldy.getType());
        return new Joystick() {
            @Override
            public float x() {
                try {
                    return (float) fieldx.get(obj);
                } catch (IllegalAccessException e) {
                    throw new RuntimeException(e);
                }
            }

            @Override
            public float y() {
                try {
                    return (float) fieldy.get(obj);
                } catch (IllegalAccessException e) {
                    throw new RuntimeException(e);
                }
            }
        };
    }

    public abstract float x();
    public abstract float y();

    @Override
    public float pressure() {
        return (float) Math.hypot(x(), y());
    }


    private final Map<EventType, Set<AEventListener<JoystickEvent>>> listeners = new HashMap<>();

    public Joystick() {
        for(EventType type : EventType.values())
            listeners.put(type, new HashSet<AEventListener<JoystickEvent>>());
    }

    EventType update() {
        EventType type = super.update();

        JoystickEvent event = new JoystickEvent(type, this, x() - prev_x, y() - prev_y);
        for(EventType etype : EventType.values())
            if(type.compatibleWith(etype))
                for(AEventListener<JoystickEvent> listener : listeners.get(etype))
                    listener.on(event);

        this.prev_x = x();
        this.prev_y = y();

        return type;
    }

    public void addJoystickListener(AEventListener<JoystickEvent> listener, EventType type) {
        listeners.get(type).add(listener);
    }
}
