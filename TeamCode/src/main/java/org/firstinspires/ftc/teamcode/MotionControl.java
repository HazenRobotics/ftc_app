package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.hardware.DcMotor;

/**
 * Created by Robotics on 10/10/2017.
 */

public class MotionControl {
    //Declare motor Variables
    protected DcMotor leftFront;
    protected DcMotor rightFront;
    protected DcMotor leftBack;
    protected DcMotor rightBack;

    //Declare Constants
    protected static final double COUNTS_PER_REV = 1440.0;
    protected static final double WHEEL_DIAMETER = 4.0;
    protected static final double COUNTS_PER_INCH = (COUNTS_PER_REV / WHEEL_DIAMETER);
    protected static final double BASE_WHEEL_POWER = 0.5;

    //Global Variables

    public void move(double moveDistance, double strafeAngle)
    {
        double counts = moveDistance * COUNTS_PER_INCH;
        double strafeAngleRadians = Math.toRadians(strafeAngle);
        double x = Math.cos(strafeAngleRadians);
        double y = Math.sin(strafeAngleRadians);
        //declare targets?

        double magnitude = Math.abs(y) + Math.abs(x); //total sum of all inputs
        double scale = Math.max(1, magnitude); //determines whether magnitude or 1 is greater (prevents from setting motor to power over 1)

        double leftFrontPower = (y + x) / scale;
        double rightFrontPower = (y - x) / scale;
        double leftBackPower = (y - x) / scale;
        double rightBackPower = (y + x) / scale;

        //TODO: Might need to scale these values
        double leftFrontTarget = leftFrontPower * counts;
        double rightFrontTarget = rightFrontPower * counts;
        double leftBackTarget = leftBackPower * counts;
        double rightBackTarget = rightBackPower * counts;

        leftFront.setTargetPosition((int) leftFrontTarget);
        leftBack.setTargetPosition((int) leftBackTarget);
        rightFront.setTargetPosition((int) rightFrontTarget);
        rightBack.setTargetPosition((int) rightBackTarget);

        leftFront.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        leftBack.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        rightFront.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        rightBack.setMode(DcMotor.RunMode.RUN_TO_POSITION);

        leftFront.setPower(leftFrontPower);
        leftBack.setPower(leftBackPower);
        rightFront.setPower(rightFrontPower);
        rightBack.setPower(rightBackPower);

        while(leftFront.isBusy() && leftBack.isBusy() && rightFront.isBusy() && rightBack.isBusy()) {
            //can be used to display messages on the phone through telemetry
        }

        leftFront.setPower(0);
        leftBack.setPower(0);
        rightFront.setPower(0);
        rightBack.setPower(0);

        leftFront.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        leftBack.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        rightFront.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        rightBack.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
    }

    public void turn(double turnAngle)
    {

    }
}
