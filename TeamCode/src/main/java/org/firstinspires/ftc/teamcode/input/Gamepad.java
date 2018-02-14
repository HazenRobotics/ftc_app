package org.firstinspires.ftc.teamcode.input;

/** A mapping (wrapper) of the FTC gamepad buttons into our button system. */
public final class Gamepad {
    public final BinaryInput a;
    public final BinaryInput b;
    public final BinaryInput x;
    public final BinaryInput y;

    public final SignInput bumpers;

    public final ScalarInput left_trigger;
    public final ScalarInput right_trigger;

    public final Joystick left_stick;
    public final Joystick right_stick;

    public final Dpad dpad;
}
