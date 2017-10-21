package org.firstinspires.ftc.teamcode.interfaces.motors;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DigitalChannel;
import com.qualcomm.robotcore.hardware.Servo;

import org.firstinspires.ftc.teamcode.interfaces.IArm;
import org.firstinspires.ftc.teamcode.interfaces.IHardware;

import java.util.Dictionary;

public class ArmMotors implements IArm {
    private IHardware hardware;
    private DcMotor claw;
    private DigitalChannel limitOpen;
    private DigitalChannel limitClosed;
    private final double CLAW_POWER = 0.2;

    public ArmMotors(IHardware hardware) {
        this.hardware = hardware;
        this.claw = hardware.getMotor("claw");
        this.limitOpen = hardware.getDigitalChannel("clawOpenSensor");
        this.limitClosed = hardware.getDigitalChannel("clawClosedSensor");
        limitOpen.setMode(DigitalChannel.Mode.INPUT);
        limitClosed.setMode(DigitalChannel.Mode.INPUT);
    }

    @Override
    public void grabGlyph() {
        claw.setPower(-CLAW_POWER);
        while(!limitClosed.getState()) hardware.idle();
        claw.setPower(0);
    }

    @Override
    public void dropGlyph() {
        claw.setPower(CLAW_POWER);
        while(!limitOpen.getState()) hardware.idle();
        claw.setPower(0);
    }
}