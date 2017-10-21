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

    //initial setup, defines motor and limit switches.
    public ArmMotors(IHardware hardware) {
        this.hardware = hardware;
        this.claw = hardware.getMotor("claw");
        this.limitOpen = hardware.getDigitalChannel("clawOpenSensor");
        this.limitClosed = hardware.getDigitalChannel("clawClosedSensor");
        limitOpen.setMode(DigitalChannel.Mode.INPUT);
        limitClosed.setMode(DigitalChannel.Mode.INPUT);
    }

    //when this method used, claw closes. (motor moves from starting position till claw closed on glyph,
    // limit switch then activates and stops motor)
    @Override
    public void grabGlyph() {
        claw.setPower(CLAW_POWER);
        while(!limitClosed.getState()) hardware.idle();
        claw.setPower(0);
    }

    //when this method used, claw opens. (motor moves toward starting position,
    // limit switch activates at initial position and stops motor)
    @Override
    public void dropGlyph() {
        claw.setPower(-CLAW_POWER);
        while(!limitOpen.getState()) hardware.idle();
        claw.setPower(0);
    }
}