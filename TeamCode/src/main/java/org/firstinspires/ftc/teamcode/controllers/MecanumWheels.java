package org.firstinspires.ftc.teamcode.controllers;

import android.support.annotation.NonNull;

import com.qualcomm.robotcore.hardware.DcMotor;
import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.teamcode.BuildConfig;
import org.firstinspires.ftc.teamcode.models.Condition;

/**
 * Controller for Mecanum Wheels, which allow a bot to move in all directions and strafe
 */
public class MecanumWheels implements IWheels {
    protected IHardware hardware;

    protected DcMotor leftFront;
    protected DcMotor rightFront;
    protected DcMotor leftBack;
    protected DcMotor rightBack;

    protected static final float DRIVE_SPEED = 0.5f;
    protected static final float TURN_SPEED = 0.3f;
    protected static final AngleUnit DEFAULT_ANGLE_UNIT = AngleUnit.DEGREES;
    protected static final Coefficients ZEROED_COEFFICIENTS = new Coefficients(0.0, 0.0, 0.0, 0.0);

    /**
     * Initializes the class to use the four wheels
     * @param hardware
     */
    public MecanumWheels(IHardware hardware) {
        this.hardware = hardware;
        
        leftFront = hardware.getMotor("leftFront");
        leftBack = hardware.getMotor("leftBack");
        rightFront = hardware.getMotor("rightFront");
        rightBack = hardware.getMotor("rightBack");

        leftFront.setDirection(DcMotor.Direction.REVERSE);
        leftBack.setDirection(DcMotor.Direction.REVERSE);
        rightFront.setDirection(DcMotor.Direction.FORWARD);
        rightBack.setDirection(DcMotor.Direction.FORWARD);
    }

    @Override
    public void move(Condition condition, boolean positiveDir) {
        move(condition, positiveDir ? DEFAULT_ANGLE_UNIT.fromDegrees(0f) : DEFAULT_ANGLE_UNIT.fromDegrees(180f), DEFAULT_ANGLE_UNIT, DRIVE_SPEED);
    }

    public void move(Condition condition, float strafeAngle, AngleUnit unit, float speed) {
        runByCoefficients(condition, calculateMove(strafeAngle, unit), speed);
    }

    protected Coefficients calculateMove(float strafeAngle, AngleUnit unit) {
        double strafeAngleRadians = unit.toRadians(strafeAngle);
        double x = Math.sin(strafeAngleRadians);
        double y = Math.cos(strafeAngleRadians);

        //Used to determine the greatest possible value of y +/- x to scale them
        double magnitude = Math.abs(y) + Math.abs(x);
        //Used to prevent setting motor to power over 1
        double scale = Math.max(1, magnitude);

        Coefficients coefficients = new Coefficients();
        coefficients.leftFront = (y - x) / scale;
        coefficients.leftBack = (y + x) / scale;
        coefficients.rightFront = (y - x) / scale;
        coefficients.rightBack = (y - x) / scale;
        return coefficients;
    }

    @Override
    public void turn(Condition condition, boolean positiveDir) {
        turn(condition, positiveDir, TURN_SPEED);
    }
    
    public void turn(Condition condition, boolean positiveDir, float speed) {
        runByCoefficients(condition, calculateTurn(positiveDir), speed);
    }

    protected Coefficients calculateTurn(boolean positiveDir) {
        //Makes one side or the other move backwards to turn left or right based on the angle
        int leftCoefficient = positiveDir ? -1 : 1;
        int rightCoefficient = positiveDir ? 1 : -1;

        Coefficients coefficients = new Coefficients();
        coefficients.leftFront = leftCoefficient;
        coefficients.leftBack = leftCoefficient;
        coefficients.rightFront = rightCoefficient;
        coefficients.rightBack = rightCoefficient;
        return coefficients;
    }

    public void runByCoefficients(Condition condition, Coefficients coefficients, float speed) {
        setPower(coefficients, speed);
        while (!condition.isTrue()) {
            hardware.idle();
        }
        setPower(ZEROED_COEFFICIENTS, speed);
    }

    public void setPower(Coefficients coefficients, float speed) {
        if(BuildConfig.DEBUG)
            assert Math.abs(speed) <= 1 : "Movement Speed must be between positive and negative 1";
        double magnitude = coefficients.getLargestMagnitude();
        magnitude = magnitude < 1 ? 1 : magnitude;
        leftFront.setPower(coefficients.leftFront * speed / magnitude);
        leftBack.setPower(coefficients.leftBack * speed / magnitude);
        rightFront.setPower(coefficients.rightFront * speed / magnitude);
        rightBack.setPower(coefficients.rightBack * speed / magnitude);
    }

    public static class Coefficients {
        /**
         * The coefficient for the front left wheel
         */
        public double leftFront = 0.0;

        /**
         * The coefficient for the back left wheel
         */
        public double leftBack = 0.0;

        /**
         * The coefficient for the front right wheel
         */
        public double rightFront = 0.0;

        /**
         * The coefficient for the back right wheel
         */
        public double rightBack = 0.0;

        /**
         * Default constructor for Coefficients, which initializes all four wheel coefficients to 0.0
         */
        public Coefficients() {}

        /**
         * Creates a Coefficients object which holds coefficient values for the four different wheels
         * @param leftFrontCounts The coefficient for the front left wheel
         * @param leftBackCounts The coefficient for the front left wheel
         * @param rightFrontCounts The coefficient for the front left wheel
         * @param rightBackCounts The coefficient for the front left wheel
         */
        public Coefficients(double leftFrontCounts, double leftBackCounts, double rightFrontCounts, double rightBackCounts) {
            this.leftFront = leftFrontCounts;
            this.leftBack = leftBackCounts;
            this.rightFront = rightFrontCounts;
            this.rightBack = rightBackCounts;
        }

        /**
         * Copies the values of the array to be the coefficient values of the different wheels in a Coefficients object
         * 0th Index = Front Left
         * 1st Index = Back Left
         * 2nd Index = Front Right
         * 3rd Index = Back Right
         * @param coefficients The array from which to copy the different coefficient values; Must have a size of at least 4
         */
        @NonNull
        public static Coefficients fromArray(double[] coefficients) {
            if(BuildConfig.DEBUG)
                assert coefficients.length >= 4 : "A wheel coefficients array must contain at least 4 values";
            return new Coefficients(coefficients[0], coefficients[1], coefficients[2], coefficients[3]);
        }

        /**
         * Makes a copy of the internal coefficient array which holds the coefficient values for the different wheels
         * 0th Index = Front Left
         * 1st Index = Back Left
         * 2nd Index = Front Right
         * 3rd Index = Back Right
         * @return Returns a copy of the internal coefficients array, which has a length of 4
         */
        public double[] toArray() {
            return new double[]{leftFront, leftBack, rightFront, rightBack};
        }

        /**
         * Returns the largest absolute coefficient value out of all of the wheels
         * @return The largest coefficient value
         */
        public double getLargestMagnitude() {
            return Math.max(Math.max(Math.abs(leftFront), Math.abs(leftBack)), Math.max(Math.abs(rightFront), Math.abs(rightBack)));
        }
    }
}