package org.firstinspires.ftc.teamcode.testclasses;

import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;

@Disabled
@TeleOp(name = "Motor Joystick DPad Control Test", group = "Test Classes")
/**
 * Test Class to run a motor with the power of the Joystick's Y position.
 * DPad up and down sets the motor's direction to forward or reverse respectively.
 */
public class MotorTest extends LinearOpMode {
    @Override
    public void runOpMode() throws InterruptedException {
        DcMotor motor = hardwareMap.dcMotor.get("motorTest");
        waitForStart();
        while (opModeIsActive()) {
            motor.setPower(-gamepad1.left_stick_y);
            if(gamepad1.dpad_down)
                motor.setDirection(DcMotorSimple.Direction.REVERSE);
            else if(gamepad1.dpad_up)
                motor.setDirection(DcMotorSimple.Direction.FORWARD);
            telemetry.addData(">", "Power: " + motor.getPower());
            telemetry.addData(">", "Dir: " + motor.getDirection());
            telemetry.update();
            idle();

        }
    }
}
