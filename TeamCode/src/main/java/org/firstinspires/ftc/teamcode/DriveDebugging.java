package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;

/**
 * Created by Robotics on 11/3/2017.
 */
@Disabled
@TeleOp(name = "Drive Debugging", group = "Test")
public class DriveDebugging extends LinearOpMode{
    @Override
    public void runOpMode() throws InterruptedException {
        DcMotor leftF = hardwareMap.dcMotor.get("leftFront");
        DcMotor leftB = hardwareMap.dcMotor.get("leftBack");
        DcMotor rightF = hardwareMap.dcMotor.get("rightFront");
        DcMotor rightB = hardwareMap.dcMotor.get("rightBack");

        leftF.setDirection(DcMotorSimple.Direction.REVERSE);
        leftB.setDirection(DcMotorSimple.Direction.REVERSE);
        rightF.setDirection(DcMotorSimple.Direction.FORWARD);
        rightB.setDirection(DcMotorSimple.Direction.FORWARD);

        waitForStart();

        while (opModeIsActive()) {
            leftF.setPower(-gamepad1.left_stick_y);
            leftB.setPower(-gamepad2.left_stick_y);
            rightF.setPower(-gamepad1.right_stick_y);
            rightB.setPower(-gamepad2.right_stick_y);
        }
    }
}
