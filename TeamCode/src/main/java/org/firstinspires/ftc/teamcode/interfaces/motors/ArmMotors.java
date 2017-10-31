package org.firstinspires.ftc.teamcode.interfaces.motors;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DigitalChannel;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.teamcode.interfaces.IArm;
import org.firstinspires.ftc.teamcode.interfaces.IHardware;

import java.util.Dictionary;

public class ArmMotors implements IArm {
    //Vars
    protected IHardware hardware;
    protected ElapsedTime clawRuntime = new ElapsedTime();

    //Objects
    protected DcMotor claw;
    //protected DigitalChannel limitOpen;
    protected DigitalChannel limitClosed;

    //Constants
    protected final double CLAW_POWER = 0.2;

    //Initial setup, defines motor and limit switches.
    public ArmMotors(IHardware hardware) {
        this.hardware = hardware;
        this.claw = hardware.getMotor("claw");
        //this.limitOpen = hardware.getDigitalChannel("clawOpenSensor");
        this.limitClosed = hardware.getDigitalChannel("clawClosedSensor");
        //limitOpen.setMode(DigitalChannel.Mode.INPUT);
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
    // stops after moving 2 seconds)
    @Override
    public void dropGlyph() {
        claw.setPower(-CLAW_POWER);
        //while(!limitOpen.getState()) hardware.idle();
        clawRuntime.reset();
        while (clawRuntime.seconds() < 2.0) hardware.idle();
        claw.setPower(0);
    }
}