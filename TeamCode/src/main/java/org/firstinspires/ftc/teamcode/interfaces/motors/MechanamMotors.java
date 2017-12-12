package org.firstinspires.ftc.teamcode.interfaces.motors;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;

import org.firstinspires.ftc.teamcode.interfaces.IHardware;
import org.firstinspires.ftc.teamcode.interfaces.IWheels;
import org.firstinspires.ftc.teamcode.models.Condition;
import org.firstinspires.ftc.teamcode.models.Vector;

/**
 * Created by Robotics on 10/10/2017.
 */

public class MechanamMotors implements IWheels {
    protected IHardware hardware;

    //Declare motor Variables
    protected DcMotor leftFront;
    protected DcMotor rightFront;
    protected DcMotor leftBack;
    protected DcMotor rightBack;

    //Declare Constants
    //TODO: Currently arbitrary numbers, need to change for actual robot
    protected static final double COUNTS_PER_REV = 1680.0;
    protected static final double WHEEL_DIAMETER = 4.0;
    protected static final double WHEEL_SLIP_MULTIPLIER = 1.0f;
    protected static final double COUNTS_PER_INCH = (COUNTS_PER_REV / WHEEL_DIAMETER) / WHEEL_SLIP_MULTIPLIER;
    protected static final double ROBOT_RADIUS = 9.0;
    protected static final double ROBOT_TURNING_CIRCUMFERENCE = Math.PI * (2 * ROBOT_RADIUS);
    protected static final double DRIVE_SPEED = 1.0;

    //Global Variables


    public MechanamMotors(IHardware hardware) {
        this.hardware = hardware;
        leftFront = hardware.getMotor("leftFront");
        leftBack = hardware.getMotor("leftBack");
        rightFront = hardware.getMotor("rightFront");

        rightBack = hardware.getMotor("rightBack");

        leftFront.setDirection(DcMotorSimple.Direction.REVERSE);
        leftBack.setDirection(DcMotorSimple.Direction.REVERSE);
        rightFront.setDirection(DcMotorSimple.Direction.FORWARD);
        rightBack.setDirection(DcMotorSimple.Direction.FORWARD);
    }

    public void move(Vector displacement) {
        move(displacement.getAngle(), displacement.getMagnitude());
    }

    public void move(double moveDistance) {
        move(0, moveDistance);
    }

    public void move(Condition condition) {
        move(0, condition);
    }

    public void move(double strafeAngle, double moveDistance) {
        move(strafeAngle, moveDistance, DRIVE_SPEED);
    }

    public double move(double strafeAngle, Condition condition) {
        return move(strafeAngle, condition, DRIVE_SPEED);
    }

    public void turn(double turnAngle) {
        turn(turnAngle, DRIVE_SPEED);
    }

    public void turn(boolean positiveDir, Condition condition) {
        turn(positiveDir, condition, DRIVE_SPEED);
    }

    public void move(double strafeAngle, double moveDistance, double speed) {
        double counts = moveDistance * COUNTS_PER_INCH;
        double strafeAngleRadians = Math.toRadians(strafeAngle);
        double x = Math.sin(strafeAngleRadians);
        double y = Math.cos(strafeAngleRadians);

        double magnitude = Math.abs(y) + Math.abs(x); //Used to determine the greatest possible value of y +/- x to scale them
        double scale = Math.max(1, magnitude); //Used to prevent setting motor to power over 1

        double leftFrontPower = (y + x) / scale;
        double rightFrontPower = (y - x) / scale;
        double leftBackPower = (y - x) / scale;
        double rightBackPower = (y + x) / scale;

        //TODO: Might need to scale these values
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

    public void turn(double turnAngle, double speed) {
        double turnDistance = (turnAngle / 360) * ROBOT_TURNING_CIRCUMFERENCE;
        double counts = turnDistance * COUNTS_PER_INCH;

        double leftFrontPower = 0;
        double rightFrontPower = 0;
        double leftBackPower = 0;
        double rightBackPower = 0;

        if (turnAngle > 0) { //checks to see if angle is positive or negative to determine if bot turns left or right
            leftFrontPower = -DRIVE_SPEED;
            leftBackPower = -DRIVE_SPEED;
            rightFrontPower = DRIVE_SPEED;
            rightBackPower = DRIVE_SPEED;
        }
        //s
        else {
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

    public void turn(boolean positiveDir, Condition condition, double speed) {
        double leftFrontPower = 0;
        double rightFrontPower = 0;
        double leftBackPower = 0;
        double rightBackPower = 0;

        if (positiveDir) {
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

        leftFront.setPower(leftFrontPower * speed);
        leftBack.setPower(leftBackPower * speed);
        rightFront.setPower(rightFrontPower * speed);
        rightBack.setPower(rightBackPower * speed);

        while (!condition.isTrue()) {
            hardware.idle();
        }
        leftFront.setPower(0);
        leftBack.setPower(0);
        rightFront.setPower(0);
        rightBack.setPower(0);
    }

    public double move(double strafeAngle, Condition condition, double speed) {
        double strafeAngleRadians = Math.toRadians(strafeAngle);
        double x = Math.sin(strafeAngleRadians);
        double y = Math.cos(strafeAngleRadians);

        double magnitude = Math.abs(y) + Math.abs(x); //Used to determine the greatest possible value of y +/- x to scale them
        double scale = Math.max(1, magnitude); //Used to prevent setting motor to power over 1

        double leftFrontPower = (y - x) / scale;
        double rightFrontPower = (y - x) / scale;
        double leftBackPower = (y + x) / scale;
        double rightBackPower = (y - x) / scale;

        int initialCounts;

        //If the front left wheel power is not going to be zero, we can use it to track distance,
        //otherwise use the front right wheel
        if (leftFrontPower != 0) {
            initialCounts = leftFront.getCurrentPosition();
        } else {
            initialCounts = rightFront.getCurrentPosition();
        }

        //pick wheel to track
        //get that wheels initial position

        leftFront.setPower(leftFrontPower * speed);
        leftBack.setPower(leftBackPower * speed);
        rightFront.setPower(rightFrontPower * speed);
        rightBack.setPower(rightBackPower * speed);

        while (!condition.isTrue()) {
            hardware.idle();
        }

        leftFront.setPower(0);
        leftBack.setPower(0);
        rightFront.setPower(0);
        rightBack.setPower(0);

        int finalCounts;
        double distance;

        if (leftFrontPower != 0) {
            finalCounts = leftFront.getCurrentPosition();
            distance = (finalCounts - initialCounts) / (leftFrontPower * COUNTS_PER_INCH);
        } else {
            finalCounts = rightFront.getCurrentPosition();
            distance = (finalCounts - initialCounts) / (rightFrontPower * COUNTS_PER_INCH);
        }

        //get final position
        //do inverse math to find distance moved

        return distance;
    }
}
