package org.firstinspires.ftc.teamcode.input;

public class ButtonEvent {
    public final EventType type;
    public final Button button;
    public final boolean pressed;

    public ButtonEvent(EventType type, Button button) {
        this.type = type;
        this.button = button;
        this.pressed = button.pressed();
    }
}
