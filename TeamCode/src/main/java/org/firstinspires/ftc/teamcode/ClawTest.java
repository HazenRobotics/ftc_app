package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.DigitalChannel;
import com.qualcomm.robotcore.hardware.HardwareDevice;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.teamcode.input.Button;
import org.firstinspires.ftc.teamcode.input.ButtonManager;
import org.firstinspires.ftc.teamcode.interfaces.IHardware;
import org.firstinspires.ftc.teamcode.input.Toggle;

//Created by Alex on 9/23/2017.



@TeleOp(name="ClawTest", group="TeleOp")
public class ClawTest extends LinearOpMode implements IHardware {


    protected DcMotor claw;

    protected final double CLAW_POWER = 0.2;

    @Override
    public void runOpMode() {

        setupHardware();
        waitForStart();

        while (opModeIsActive()) {
            claw();
            telemetry.update();
            idle();
        }
    }



    protected void setupHardware() {
        claw = getMotor("claw");
        claw.setDirection(DcMotor.Direction.FORWARD);
    }


    //when claw has reached the correct position or moved open long enough, the claw stops moving.
 protected void claw() {
        if(gamepad2.y)
        {
            claw.setPower(CLAW_POWER);
        }
        else if(gamepad2.x)
        {
            claw.setPower(-CLAW_POWER);
        }
        else
        {
            claw.setPower(0);
        }
    }

    @Override
    public void idle(long milliseconds) {
        // This is probably the wrong way to handle this-- spin loop.
        // However, it's better than Thread.idleFor()-- probably.
        long endTime = System.currentTimeMillis() + milliseconds;
        while(System.currentTimeMillis() < endTime && opModeIsActive()) {
            telemetry.update();
            idle();
        }
    }

    @Override
    public DcMotor getMotor(String name) {
        return hardwareMap.dcMotor.get(name);
    }

    @Override
    public Servo getServo(String name) {
        return hardwareMap.servo.get(name);
    }

    @Override
    public DigitalChannel getDigitalChannel(String name) {
        return hardwareMap.digitalChannel.get(name);
    }

    @Override
    public HardwareDevice get(String name) {
        return hardwareMap.get(name);
    }
}
