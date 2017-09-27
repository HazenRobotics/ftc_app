package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.util.Range;

/**
 * Created by davegoldy on 4/3/17.
 */

@TeleOp(name = "TeleOp", group = "TeleOp")
public class DriverControl extends LinearOpMode
{
    private DcMotor fRightMotor;
    private DcMotor fLeftMotor;

    @Override
    public void runOpMode() throws InterruptedException
    {
        setupHardware();
        waitForStart();
        while (opModeIsActive())
        {
            // throttle: left_stick_y ranges from -1 to 1, where -1 is full up, and
            // 1 is full down
            // direction: left_stick_x ranges from -1 to 1, where -1 is full left
            // and 1 is full right
            float throttle = -gamepad1.left_stick_y;
            float direction = gamepad1.left_stick_x;
            float right = throttle - direction;
            float left = throttle + direction;

            // clip the right/left values so that the values never exceed +/- 1
            right = Range.clip(right, -1, 1);
            left = Range.clip(left, -1, 1);

            // write the values to the motors
            fRightMotor.setPower(right);
            fLeftMotor.setPower(left);

		/*
         * Send telemetry data back to driver station.
		 */
            telemetry.addData("left power", "Left Power: " + String.format("%.2f", left));
            telemetry.addData("right power", "Right Power: " + String.format("%.2f", right));
            telemetry.update();
        }

    }


    /**
     * All if the initial hardware setup code belongs in this method.
     *
     * @throws InterruptedException
     */
    protected void setupHardware() throws InterruptedException
    {
        fLeftMotor = hardwareMap.dcMotor.get("leftMotor");
        fRightMotor = hardwareMap.dcMotor.get("rightMotor");

        // eg: Set the drive motor directions:
        // Reverse the motor that runs backwards when connected directly to the battery
        fLeftMotor.setDirection(DcMotor.Direction.REVERSE);
        fRightMotor.setDirection(DcMotor.Direction.FORWARD);
    }

}
