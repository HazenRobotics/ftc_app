package org.firstinspires.ftc.teamcode.input;

import org.firstinspires.ftc.teamcode.reflection.FieldAccessor;

/** A mapping (wrapper) of the FTC gamepad buttons into our button system. */
public final class Gamepad {
    public final Button a;
    public final Button b;
    public final Button x;
    public final Button y;

    public final ButtonPair bx;
    public final ButtonPair ay;

    public final ButtonPair bumpers;
    public final Button left_bumper;
    public final Button right_bumper;

    public final Trigger left_trigger;
    public final Trigger right_trigger;

    public final Button left_stick_button;
    public final Button right_stick_button;
    public final Joystick left_stick;
    public final Joystick right_stick;

    public final Dpad dpad;

    public Gamepad(com.qualcomm.robotcore.hardware.Gamepad gamepad) {
        try {
            this.a = new Button(FieldAccessor.boola("a", gamepad));
            this.b = new Button(FieldAccessor.boola("b", gamepad));
            this.x = new Button(FieldAccessor.boola("x", gamepad));
            this.y = new Button(FieldAccessor.boola("y", gamepad));

            this.bx = new ButtonPair(b, x);
            this.ay = new ButtonPair(a, y);

            this.left_bumper = new Button(FieldAccessor.boola("left_bumper", gamepad));
            this.right_bumper = new Button(FieldAccessor.boola("right_bumper", gamepad));
            this.bumpers = new ButtonPair(left_bumper, right_bumper);

            this.left_trigger = new Trigger(FieldAccessor.floata("left_bumper", gamepad));
            this.right_trigger = new Trigger(FieldAccessor.floata("right_bumper", gamepad));

            this.left_stick_button = new Button(FieldAccessor.boola("left_joystick_button", gamepad));
            this.right_stick_button = new Button(FieldAccessor.boola("right_joystick_button", gamepad));
            this.left_stick = new Joystick(FieldAccessor.floata("left_joystick_x", gamepad), FieldAccessor.floata("left_joystick_y", gamepad));
            this.right_stick = new Joystick(FieldAccessor.floata("right_joystick_x", gamepad), FieldAccessor.floata("right_joystick_y", gamepad));

            this.dpad = new Dpad(
                    FieldAccessor.boola("dpad_up", gamepad),
                    FieldAccessor.boola("dpad_right", gamepad),
                    FieldAccessor.boola("dpad_down", gamepad),
                    FieldAccessor.boola("dpad_left", gamepad)
            );
        } catch(NoSuchFieldException e) {
            // If the official Gamepad has changed, there's nothing we can do.
            throw new RuntimeException(e);
        }
    }

    /** Run all of the button event listeners. */
    public void update() {
        bx.update();
        ay.update();
        bumpers.update();
        left_trigger.update();
        right_trigger.update();
        left_stick_button.update();
        right_stick_button.update();
        left_stick.update();
        right_stick.update();
        dpad.update();
    }
}
