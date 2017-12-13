package org.firstinspires.ftc.teamcode.input;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public abstract class Button {
    private boolean prev_pressed = false;
    private boolean toggle_active = false;

    private Button asToggle = null;

    static Button generate(final Object obj, String fieldname) throws NoSuchFieldException, IllegalAccessException {
        // Sorry for the copy/pastes. I'll consider implementing generic field accessors later, but that seems excessive for now.
        final Field field = obj.getClass().getField(fieldname);
        if(obj == null)
            throw new IllegalArgumentException("Object was null when generating field accessor.");
        if(!field.getType().equals(Boolean.TYPE))
            throw new IllegalArgumentException("Field is not correct type for button: " + fieldname + " : " + field.getType());
        return new Button() {
            @Override
            public boolean pressed() {
                try {
                    return (boolean) field.get(obj);
                } catch (IllegalAccessException e) {
                    throw new RuntimeException(e);
                }
            }
        };
    }

    public Button asToggle() {
        if(asToggle == null)
            this.asToggle = new Button() {
                @Override
                public boolean pressed() {
                    return toggle_active;
                }
            };
        return asToggle;
    }

    public abstract boolean pressed();

    private final Map<EventType, Set<AEventListener<ButtonEvent>>> listeners = new HashMap<>();

    public Button() {
        for(EventType type : EventType.values())
            listeners.put(type, new HashSet<AEventListener<ButtonEvent>>());
    }

    EventType update() {
        EventType type;
        if(pressed()) {
            if(prev_pressed) {
                type = EventType.PRESS;
                toggle_active = !toggle_active;
            }
            else type = EventType.ACTIVE;
        } else {
            if(prev_pressed) type = EventType.RELEASE;
            else type = EventType.INACTIVE;
        }

        ButtonEvent event = new ButtonEvent(type, this);
        for(EventType etype : EventType.values())
            if(type.compatibleWith(etype))
                for(AEventListener<ButtonEvent> listener : listeners.get(etype))
                    listener.on(event);

        this.prev_pressed = pressed();

        return type;
    }

    public void addPressListener(AEventListener<ButtonEvent> listener, EventType type) {
        listeners.get(type).add(listener);
    }
}
