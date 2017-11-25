package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;

/**
 * Created by Robotics on 10/5/2017.
 */
//@Disabled
@TeleOp(name = "Motor Test", group = "Test")
public class MotorTest extends LinearOpMode {
    @Override
    public void runOpMode() throws InterruptedException {
        DcMotor motor = hardwareMap.dcMotor.get("motor");
        waitForStart();
        while (opModeIsActive()) {
            motor.setPower(-gamepad1.left_stick_y);
            if(gamepad1.dpad_down)
                motor.setDirection(DcMotorSimple.Direction.REVERSE);
            else if(gamepad1.dpad_up)
                motor.setDirection(DcMotorSimple.Direction.FORWARD);
            telemetry.addData(">", "Power: " + motor.getPower());
            //telemetry.addData(">", "Dir: " + servo.getDirection());
            telemetry.update();
            idle();

        }
    }
}
