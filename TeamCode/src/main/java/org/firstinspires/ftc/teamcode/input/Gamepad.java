package org.firstinspires.ftc.teamcode.input;

import org.firstinspires.ftc.teamcode.reflection.AccessorBuilder;

/** A mapping (wrapper) of the FTC gamepad buttons into our button system. */
public final class Gamepad {
    public final Button a;
    public final Button b;
    public final Button x;
    public final Button y;

    public final Button bumpers;

    public final Button trigger_left;
    public final Button trigger_right;

    public final Joystick stick_left;
    public final Joystick stick_right;

    public final Dpad dpad;

    public Gamepad(com.qualcomm.robotcore.hardware.Gamepad gamepad) {
        this.a = Button.fromPress(new AccessorBuilder().onObject(gamepad).get("a").finish().<Boolean>cast());
    }
}
