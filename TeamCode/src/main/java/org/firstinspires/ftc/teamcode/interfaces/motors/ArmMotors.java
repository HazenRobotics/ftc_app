package org.firstinspires.ftc.teamcode.interfaces.motors;

import com.qualcomm.robotcore.hardware.Servo;

import org.firstinspires.ftc.teamcode.interfaces.IArm;
import org.firstinspires.ftc.teamcode.interfaces.IHardware;

public class ArmMotors implements IArm {
    private IHardware hardware;
    private Servo claw;
    private final double openPosition = 0.75;
    private final double closedPosition = 0.0;

    public ArmMotors(IHardware hardware) {
        this.hardware = hardware;
        this.claw = hardware.getServo("claw");
    }

    @Override
    public void grabGlyph() {
        claw.setPosition(openPosition);
    }

    @Override
    public void dropGlyph() {
        claw.setPosition(closedPosition);
    }
}