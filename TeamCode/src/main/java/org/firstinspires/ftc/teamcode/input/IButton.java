package org.firstinspires.ftc.teamcode.input;

import org.firstinspires.ftc.teamcode.reflection.Supplier;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public abstract class IButton {
    private boolean stepState = false;

    private final Map<EventType, Set<EventListener>> pressListeners = new HashMap<>();
    private final Map<EventType, Set<EventListener>> signListeners = new HashMap<>();
    private final Map<EventType, Set<EventListener>> valueListeners = new HashMap<>();

    private boolean was_pressed = false;
    private int previous_sign = 0;
    private float previous_value = 0;

    private boolean toggleState = false;

    private IButton inverted;
    private IButton asToggle;

    private static final void initializeSetMap(Map<EventType, Set<EventListener>> listeners) {
        for(EventType type : EventType.values())
            listeners.put(type, new HashSet<EventListener>());
    }

    public IButton() {
        initializeSetMap(pressListeners);
        initializeSetMap(signListeners);
        initializeSetMap(valueListeners);
    }

    public boolean pressed() {
        return sign() != 0;
    }

    public int sign() {
        return (int) Math.signum(value());
    }

    public abstract float value();

    public IButton asToggle() {
        if(asToggle == null)
            asToggle = new Button(new Supplier<Boolean>() {
                @Override
                public Boolean get() {
                    return IButton.this.toggleState;
                }
            });
        return asToggle;
    }

    public IButton invert() {
        if(inverted == null)
            inverted = new IButton() {
                @Override
                public float value() {
                    return -IButton.this.value();
                }
            };
        return inverted;
    }

    public void listenPress(EventType type, EventListener listener) {
        pressListeners.get(type).add(listener);
    }

    public void listenSign(EventType type, EventListener listener) {
        signListeners.get(type).add(listener);
    }

    public void listenValue(EventType type, EventListener listener) {
        valueListeners.get(type).add(listener);
    }

    public EventType pressEventType() {
        if(pressed()) {
            if (was_pressed) return EventType.PRESS;
            return EventType.ACTIVE;
        }
        if(was_pressed) return EventType.RELEASE;
        return EventType.INACTIVE;
    }

    public EventType signEventType() {
        if(previous_sign == 0 && sign() == 0) return EventType.RELEASE;
        if(previous_sign == 0 && sign() != 0) return EventType.RELEASE;
        if(previous_sign == -sign()) return EventType.CHANGED;
        if(sign() == 0) return EventType.INACTIVE;
        return EventType.ACTIVE;
    }

    public EventType valueEventType() {
        return signEventType();
    }

    public float delta() {
        return value() - previous_value;
    }

    void step() {
        if(asToggle != null) asToggle.step();
        if(inverted != null) inverted.step();
        stepState = true;
    }

    void update() {
        if(!stepState) return;
        stepState = false;

        was_pressed = pressed();
        previous_sign = sign();
        previous_value = value();

        if(!was_pressed && pressed())
            toggleState = !toggleState;

        if(asToggle != null) asToggle.update();
        if(inverted != null) inverted.update();

        runApplicableListeners(pressListeners, pressEventType());
        runApplicableListeners(signListeners, signEventType());
        runApplicableListeners(valueListeners, valueEventType());
    }

    private void runApplicableListeners(Map<EventType, Set<EventListener>> listeners, EventType type) {
        for(EventType etype : EventType.values())
            if(type.compatibleWith(etype))
                for(EventListener listener : listeners.get(etype))
                    listener.on(type, this);
    }
}
