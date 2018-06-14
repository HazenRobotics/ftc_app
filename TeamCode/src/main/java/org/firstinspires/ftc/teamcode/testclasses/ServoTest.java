package org.firstinspires.ftc.teamcode.testclasses;

import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.Servo;

@Disabled
@TeleOp(name = "Servo Control Test", group = "Test Classes")
/**
 * Test Class to set a servo to the position of the Right Joystick's Y position.
 * DPad up and down sets the servo's position to POSITION2 or POSITION1 respectively.
 */
public class ServoTest extends LinearOpMode {
    private static final double POSITION1 = 0.0;
    private static final double POSITION2 = 1.0;

    @Override
    public void runOpMode() throws InterruptedException {
        Servo servo = hardwareMap.servo.get("servoTest");
        waitForStart();
        while (opModeIsActive()) {
           if (gamepad1.a)
               servo.setPosition(Math.abs(gamepad1.right_stick_y));
            if(gamepad1.dpad_down)
                servo.setPosition(POSITION1);
            else if(gamepad1.dpad_up)
                servo.setPosition(POSITION2);
            telemetry.addData("Servo Position: ", servo.getPosition());
            telemetry.update();
            idle();

        }
    }
}
