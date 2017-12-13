package org.firstinspires.ftc.teamcode.input;

public class JoystickEvent extends TriggerEvent {
    public final Joystick joystick;
    public final float x;
    public final float y;
    public final float delta_x;
    public final float delta_y;

    public JoystickEvent(EventType type, Joystick joystick, float delta_x, float delta_y) {
        super(type, joystick, (float) Math.hypot(delta_x, delta_y));
        this.joystick = joystick;
        this.x = joystick.x();
        this.y = joystick.y();
        this.delta_x = delta_x;
        this.delta_y = delta_y;
    }
}
