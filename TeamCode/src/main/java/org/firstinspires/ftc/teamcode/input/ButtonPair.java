package org.firstinspires.ftc.teamcode.input;

import org.firstinspires.ftc.teamcode.reflection.Supplier;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public final class ButtonPair extends Button {
    public final Button pos;
    public final Button right;
    public final Button up;

    public final Button neg;
    public final Button left;
    public final Button down;

    private Sign prev = Sign.ZERO;

    private final Map<EventType, Set<EventListener<SignEvent>>> listeners = new HashMap<>();

    public void listenSign(EventType type, EventListener<SignEvent> listener) {
        listeners.get(type).add(listener);
    }

    public ButtonPair(Button pos, Button neg) {
        super(new Supplier<Boolean>() {
            @Override
            public Boolean get() {
                return pos.pressed() || neg.pressed();
            }
        });
        this.pos = pos;
        this.right = pos;
        this.up = pos;
        this.neg = neg;
        this.left = neg;
        this.down = neg;
    }

    public ButtonPair(Supplier<Boolean> pos, Supplier<Boolean> neg) {
        this(new Button(pos), new Button(neg));
    }

    public Sign sign() {
        if(!(pos.pressed() ^ neg.pressed()))
            return Sign.ZERO;
        if(pos.pressed())
            return Sign.POSITIVE;
        return Sign.NEGATIVE;
    }

    @Override
    public EventType update() {
        pos.update();
        neg.update();

        EventType type;
        if(prev != Sign.ZERO && sign() == Sign.ZERO) type = EventType.RELEASE;
        else if(prev == Sign.ZERO && sign() != Sign.ZERO) type = EventType.RELEASE;
        else if(prev == sign().opposite()) type = EventType.CHANGED;
        else if(sign() == Sign.ZERO) type = EventType.INACTIVE;
        else type = EventType.ACTIVE;

        SignEvent event = new SignEvent(type, this, prev);
        runApplicableListeners(listeners, event);

        this.prev = sign();

        return type;
    }
}
