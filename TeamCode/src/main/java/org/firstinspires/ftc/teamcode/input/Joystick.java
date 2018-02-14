package org.firstinspires.ftc.teamcode.input;

public final class Joystick {
    public final VectorInput position;
    public final BinaryInput button;

    public Joystick(VectorInput position, BinaryInput button) {
        this.position = position;
        this.button = button;
    }
}
