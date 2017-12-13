package org.firstinspires.ftc.teamcode.input;

public class ButtonEvent {
    public final EventType type;
    public final Button button;

    public ButtonEvent(EventType type, Button button) {
        this.type = type;
        this.button = button;
    }
}
