package org.firstinspires.ftc.teamcode.testclasses;

import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DigitalChannel;
import com.qualcomm.robotcore.hardware.HardwareDevice;
import com.qualcomm.robotcore.hardware.Servo;

import org.firstinspires.ftc.teamcode.interfaces.IHardware;
import org.firstinspires.ftc.teamcode.controllers.IHardware;


@Disabled
@TeleOp(name="Motor Button Control Test", group="Test Classes")
/**
 * Test Class to control a motor with the name 'motorTest' by using Gamepad 1's y and x buttons.
 * <br></br> Y would set the motor to its positive MOTOR_POWER and X would set the motor to its negative MOTOR_POWER.
 * If neither and pressed, the power will be set to 0.
 */
public class MotorButtonControlTest extends LinearOpMode implements IHardware {

    protected DcMotor testMotor;
    protected final double MOTOR_POWER = 0.2;

    @Override
    public void runOpMode() {
        setupHardware();
        waitForStart();
        while (opModeIsActive()) {
            if(gamepad1.y)
            {
                testMotor.setPower(MOTOR_POWER);
            }
            else if(gamepad1.x)
            {
                testMotor.setPower(-MOTOR_POWER);
            }
            else
            {
                testMotor.setPower(0);
            }
            idle();
        }
    }

    protected void setupHardware() {
        testMotor = getMotor("motorTest");
        testMotor.setDirection(DcMotor.Direction.FORWARD);
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
