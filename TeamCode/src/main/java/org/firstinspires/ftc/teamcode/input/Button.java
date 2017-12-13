package org.firstinspires.ftc.teamcode.input;

import org.firstinspires.ftc.teamcode.reflection.Supplier;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class Button {
    private final Supplier<Boolean> value;

    private boolean was_pressed = false;
    private boolean toggle_active = false;

    private Button asToggle = null;

    public Button asToggle() {
        if(asToggle == null)
            this.asToggle = new Button(new Supplier<Boolean>() {
                @Override
                public Boolean get() {
                    return toggle_active;
                }
            });
        return asToggle;
    }

    public boolean pressed() {
        return value.get();
    }

    private final Map<EventType, Set<EventListener<ButtonEvent>>> listeners = new HashMap<>();

    public Button(Supplier<Boolean> value) {
        this.value = value;
        for(EventType type : EventType.values())
            listeners.put(type, new HashSet<>());
    }

    EventType update() {
        EventType type;
        if(pressed()) {
            if(was_pressed) {
                type = EventType.PRESS;
                toggle_active = !toggle_active;
            }
            else type = EventType.ACTIVE;
        } else {
            if(was_pressed) type = EventType.RELEASE;
            else type = EventType.INACTIVE;
        }

        ButtonEvent event = new ButtonEvent(type, this);
        runApplicableListeners(listeners, event);

        this.was_pressed = pressed();

        return type;
    }

    public void listenPress(Runnable listener) {
        listenButton(EventType.PRESS, new EventListener<ButtonEvent>() {
            @Override
            public void on(ButtonEvent event) {
                listener.run();
            }
        });
    }

    public void listenButton(EventType type, EventListener<ButtonEvent> listener) {
        listeners.get(type).add(listener);
    }

    protected static<T extends ButtonEvent> void runApplicableListeners(Map<EventType, Set<EventListener<T>>> listeners, T event) {
        for(EventType etype : EventType.values())
            if(event.type.compatibleWith(etype))
                for(EventListener<T> listener : listeners.get(etype))
                    listener.on(event);
    }
}
