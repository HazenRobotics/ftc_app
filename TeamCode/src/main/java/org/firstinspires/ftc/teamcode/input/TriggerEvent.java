package org.firstinspires.ftc.teamcode.input;

public class TriggerEvent extends ButtonEvent {
    public final Trigger trigger;
    public final float delta;

    public TriggerEvent(EventType type, Trigger trigger, float delta) {
        super(type, trigger);
        this.trigger = trigger;
        this.delta = delta;
    }
}
