package org.firstinspires.ftc.teamcode.interfaces.motors;

import com.qualcomm.robotcore.hardware.DcMotor;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;
import org.firstinspires.ftc.teamcode.interfaces.IHardware;
import org.firstinspires.ftc.teamcode.interfaces.IWheelsEncoder;
import org.firstinspires.ftc.teamcode.models.Condition;

public class EncoderMecanumWheels extends MecanumWheels implements IWheelsEncoder
{
    protected static final double COUNTS_PER_REV = 1680.0;
    protected static final double WHEEL_DIAMETER = 4.0;
    protected static final double WHEEL_SLIP_MULTIPLIER = 3.375f;
    protected static final double COUNTS_PER_INCH = (COUNTS_PER_REV / WHEEL_DIAMETER) / WHEEL_SLIP_MULTIPLIER;
    protected static final double ROBOT_RADIUS = 9.0;
    protected static final double ROBOT_TURNING_CIRCUMFERENCE = Math.PI * (2 * ROBOT_RADIUS);

    public EncoderMecanumWheels(IHardware hardware) {
        super(hardware);
    }

    @Override
    public void move(float distance, DistanceUnit unit) {
        move(distance, unit, 0, DEFAULT_ANGLE_UNIT, DRIVE_SPEED);
    }

    public void move(float distance, DistanceUnit distanceUnit, float strafeAngle, AngleUnit angleUnit, float speed) {
        double counts = distanceUnit.toInches(distance) * COUNTS_PER_INCH;
        double strafeAngleRadians = angleUnit.toRadians(strafeAngle);
        double x = Math.sin(strafeAngleRadians);
        double y = Math.cos(strafeAngleRadians);

        //Used to determine the greatest possible value of y +/- x to scale them
        double magnitude = Math.abs(y) + Math.abs(x);
        //Used to prevent setting motor to power over 1
        double scale = Math.max(1, magnitude);

        double leftFrontPower = (y + x) / scale;
        double rightFrontPower = (y - x) / scale;
        double leftBackPower = (y - x) / scale;
        double rightBackPower = (y + x) / scale;

        int leftFrontTarget = (int) (leftFrontPower * counts);
        int rightFrontTarget = (int) (rightFrontPower * counts);
        int leftBackTarget = (int) (leftBackPower * counts);
        int rightBackTarget = (int) (rightBackPower * counts);


        leftFront.setTargetPosition(leftFront.getCurrentPosition() + leftFrontTarget);
        leftBack.setTargetPosition(leftBack.getCurrentPosition() + leftBackTarget);
        rightFront.setTargetPosition(rightFront.getCurrentPosition() + rightFrontTarget);
        rightBack.setTargetPosition(rightBack.getCurrentPosition() + rightBackTarget);

        leftFront.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        leftBack.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        rightFront.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        rightBack.setMode(DcMotor.RunMode.RUN_TO_POSITION);

        leftFront.setPower(leftFrontPower * speed);
        leftBack.setPower(leftBackPower * speed);
        rightFront.setPower(rightFrontPower * speed);
        rightBack.setPower(rightBackPower * speed);

        while (leftFront.isBusy() && leftBack.isBusy() && rightFront.isBusy() && rightBack.isBusy()) {
            hardware.idle();
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


    public int move(Condition condition, float strafeAngle, AngleUnit unit, float speed) {
        int initialCounts;

        //If the front left wheel power is not going to be zero, we can use it to track distance,
        //otherwise use the front right wheel
        if ((y - x) != 0) {
            initialCounts = leftFront.getCurrentPosition();
        } else {
            initialCounts = rightFront.getCurrentPosition();
        }
        super.move(condition, strafeAngle, unit, speed);
    }

    @Override
    public void turn(float angle, AngleUnit unit) {
        turn(angle, unit, TURN_SPEED);
    }

    public void turn(float angle, AngleUnit unit, float speed) {
        double turnDistance = (unit.toDegrees(angle) / 360) * ROBOT_TURNING_CIRCUMFERENCE;
        double counts = turnDistance * COUNTS_PER_INCH;

        double leftFrontPower;
        double rightFrontPower;
        double leftBackPower;
        double rightBackPower;

        //checks to see if angle is positive or negative to determine if bot turns left or right
        if (angle > 0) {
            leftFrontPower = -DRIVE_SPEED;
            leftBackPower = -DRIVE_SPEED;
            rightFrontPower = DRIVE_SPEED;
            rightBackPower = DRIVE_SPEED;
        } else {
            leftFrontPower = DRIVE_SPEED;
            leftBackPower = DRIVE_SPEED;
            rightFrontPower = -DRIVE_SPEED;
            rightBackPower = -DRIVE_SPEED;
        }

        int leftFrontTarget = (int) (leftFrontPower * counts);
        int rightFrontTarget = (int) (rightFrontPower * counts);
        int leftBackTarget = (int) (leftBackPower * counts);
        int rightBackTarget = (int) (rightBackPower * counts);

        leftFront.setTargetPosition(leftFront.getCurrentPosition() + leftFrontTarget);
        leftBack.setTargetPosition(leftBack.getCurrentPosition() + leftBackTarget);
        rightFront.setTargetPosition(rightFront.getCurrentPosition() + rightFrontTarget);
        rightBack.setTargetPosition(rightBack.getCurrentPosition() + rightBackTarget);

        leftFront.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        leftBack.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        rightFront.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        rightBack.setMode(DcMotor.RunMode.RUN_TO_POSITION);

        leftFront.setPower(leftFrontPower * speed);
        leftBack.setPower(leftBackPower * speed);
        rightFront.setPower(rightFrontPower * speed);
        rightBack.setPower(rightBackPower * speed);

        while (leftFront.isBusy() && leftBack.isBusy() && rightFront.isBusy() && rightBack.isBusy()) {
            hardware.idle();
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
