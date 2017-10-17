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
    //currently arbitrary numbers, need to change for actual robot
    protected static final double COUNTS_PER_REV = 1440.0;
    protected static final double WHEEL_DIAMETER = 4.0;
    protected static final double COUNTS_PER_INCH = (COUNTS_PER_REV / WHEEL_DIAMETER);
    protected static final double ROBOT_RADIUS = 1.0;
    protected static final double ROBOT_TURNING_CIRCUMFERENCE = Math.PI * (2 * ROBOT_RADIUS);
    protected static final double DRIVE_SPEED = 1.0;

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
        int leftFrontTarget = (int) (leftFrontPower * counts);
        int rightFrontTarget = (int) (rightFrontPower * counts);
        int leftBackTarget = (int) (leftBackPower * counts);
        int rightBackTarget = (int) (rightBackPower * counts);

        leftFront.setTargetPosition(leftFrontTarget);
        leftBack.setTargetPosition(leftBackTarget);
        rightFront.setTargetPosition(rightFrontTarget);
        rightBack.setTargetPosition(rightBackTarget);

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
            //idle();
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
        double turnDistance = (turnAngle / 360) * ROBOT_TURNING_CIRCUMFERENCE;
        double counts = turnDistance * COUNTS_PER_INCH;

        double leftFrontPower = 0;
        double rightFrontPower = 0;
        double leftBackPower = 0;
        double rightBackPower = 0;

        if(turnAngle > 0){ //checks to see if angle is positive or negative to determine if bot turns left or right
            leftFrontPower = -DRIVE_SPEED;
            leftBackPower = -DRIVE_SPEED;
            rightFrontPower = DRIVE_SPEED;
            rightBackPower = DRIVE_SPEED;
        }
        //s
        else{
            leftFrontPower = DRIVE_SPEED;
            leftBackPower = DRIVE_SPEED;
            rightFrontPower = -DRIVE_SPEED;
            rightBackPower = -DRIVE_SPEED;
        }

        //TODO: Might need to scale these values
        int leftFrontTarget = (int) (leftFrontPower * counts);
        int rightFrontTarget = (int) (rightFrontPower * counts);
        int leftBackTarget = (int) (leftBackPower * counts);
        int rightBackTarget = (int) (rightBackPower * counts);

        leftFront.setTargetPosition(leftFrontTarget);
        leftBack.setTargetPosition(leftBackTarget);
        rightFront.setTargetPosition(rightFrontTarget);
        rightBack.setTargetPosition(rightBackTarget);

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
            //idle();
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
}
