package org.firstinspires.ftc.teamcode.controllers;

import android.support.annotation.NonNull;

import com.qualcomm.robotcore.hardware.DcMotor;

import org.firstinspires.ftc.robotcore.external.Const;
import org.firstinspires.ftc.robotcore.external.NonConst;
import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;
import org.firstinspires.ftc.teamcode.BuildConfig;
import org.firstinspires.ftc.teamcode.models.Condition;

public class EncoderMecanumWheels extends MecanumWheels implements IWheelsEncoder
{
    protected static final DistanceUnit DEFAULT_DISTANCE_UNIT = DistanceUnit.INCH;
    protected static final int COUNTS_PER_REV = 1680;
    protected static final double WHEEL_DIAMETER = DEFAULT_DISTANCE_UNIT.fromInches(4.0);
    protected static final double WHEEL_SLIP_MULTIPLIER = 3.375;
    protected static final double COUNTS_PER_UNIT = (COUNTS_PER_REV / WHEEL_DIAMETER) / WHEEL_SLIP_MULTIPLIER;
    protected static final double ROBOT_RADIUS = DEFAULT_DISTANCE_UNIT.fromInches(9.0);
    protected static final double ROBOT_TURNING_CIRCUMFERENCE = Math.PI * (2 * ROBOT_RADIUS);

    public EncoderMecanumWheels(IHardware hardware) {
        super(hardware);
    }

    @Override
    public void move(float distance, DistanceUnit unit) {
        move(distance, unit, 0, DEFAULT_ANGLE_UNIT, DRIVE_SPEED);
    }

    public void move(float distance, DistanceUnit distanceUnit, float strafeAngle, AngleUnit angleUnit, float speed) {
        int baseCounts = (int) (DEFAULT_DISTANCE_UNIT.fromUnit(distanceUnit, distance) * COUNTS_PER_UNIT);
        Counts counts = Counts.fromCoefficients(calculateMove(strafeAngle, angleUnit), baseCounts);
        runByCounts(counts, speed);
    }

    public Counts recordedMove(Condition condition, float strafeAngle, AngleUnit unit, float speed) {
        Counts initial = Counts.fromPosition(this);
        move(condition, strafeAngle, unit, speed);
        Counts finial = Counts.fromPosition(this);
        return finial.subtracted(initial);
    }

    @Override
    public void turn(float angle, AngleUnit unit) {
        turn(angle, unit, TURN_SPEED);
    }

    public void turn(float angle, AngleUnit unit, float speed) {
        double turnDistance = (unit.toDegrees(angle) / 360) * ROBOT_TURNING_CIRCUMFERENCE;
        int baseCounts = (int) (turnDistance * COUNTS_PER_UNIT);

        Counts counts = Counts.fromCoefficients(calculateTurn(angle > 0), baseCounts);
        runByCounts(counts, speed);
    }

    public Counts recordedTurn(Condition condition, boolean positiveDir, float speed) {
        Counts initial = Counts.fromPosition(this);
        turn(condition, positiveDir, speed);
        Counts finial = Counts.fromPosition(this);
        return finial.subtracted(initial);
    }

    public void runByCounts(Counts counts, float speed) {
        leftFront.setTargetPosition(leftFront.getCurrentPosition() + counts.leftFront);
        leftBack.setTargetPosition(leftBack.getCurrentPosition() + counts.leftBack);
        rightFront.setTargetPosition(rightFront.getCurrentPosition() + counts.rightFront);
        rightBack.setTargetPosition(rightBack.getCurrentPosition() + counts.rightBack);

        leftFront.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        leftBack.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        rightFront.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        rightBack.setMode(DcMotor.RunMode.RUN_TO_POSITION);

        setPower(counts.toCoefficient(), speed);
        while (leftFront.isBusy() && leftBack.isBusy() && rightFront.isBusy() && rightBack.isBusy()) {
            hardware.idle();
        }
        setPower(ZEROED_COEFFICIENTS, speed);

        leftFront.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        leftBack.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        rightFront.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        rightBack.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
    }

    public static class Counts {
        /**
         * The number of counts for the front left wheel
         */
        public int leftFront = 0;

        /**
         * The number of counts for the back left wheel
         */
        public int leftBack = 0;

        /**
         * The number of counts for the front right wheel
         */
        public int rightFront = 0;

        /**
         * The number of counts for the back right wheel
         */
        public int rightBack = 0;

        /**
         * Default constructor dor Counts, which initializes all four wheel counts to 0
         */
        public Counts() {}

        /**
         * Creates a Counts object which holds count values for the four different wheels
         * @param leftFrontCounts The amount of counts for the front left wheel
         * @param leftBackCounts The amount of counts for the front left wheel
         * @param rightFrontCounts The amount of counts for the front left wheel
         * @param rightBackCounts The amount of counts for the front left wheel
         */
        public Counts(int leftFrontCounts, int leftBackCounts, int rightFrontCounts, int rightBackCounts) {
            this.leftFront = leftFrontCounts;
            this.leftBack = leftBackCounts;
            this.rightFront = rightFrontCounts;
            this.rightBack = rightBackCounts;
        }

        /**
         * Copies the values of the array to be the count values of the different wheels in a Counts object
         * 0th Index = Front Left
         * 1st Index = Back Left
         * 2nd Index = Front Right
         * 3rd Index = Back Right
         * @param counts The array from which to copy the different count values; Must have a size of at least 4
         */
        @NonNull
        public static Counts fromArray(int[] counts) {
            if(BuildConfig.DEBUG)
                assert counts.length >= 4 : "A wheel counts array must contain at least 4 values";
            return new Counts(counts[0], counts[1], counts[2], counts[3]);
        }

        /**
         * Makes a copy of the internal count array which holds the count values for the different wheels
         * 0th Index = Front Left
         * 1st Index = Back Left
         * 2nd Index = Front Right
         * 3rd Index = Back Right
         * @return Returns a copy of the internal counts array, which has a length of 4
         */
        public int[] toArray() {
            return new int[]{leftFront, leftBack, rightFront, rightBack};
        }

        /**
         * Returns the largest absolute counts value out of all of the wheels
         * @return The largest counts value
         */
        public int getLargestMagnitude() {
            return Math.max(Math.max(Math.abs(leftFront), Math.abs(leftBack)), Math.max(Math.abs(rightFront), Math.abs(rightBack)));
        }

        /**
         * Creates a Counts object from a Coefficient object by multiplying the coefficients by the base counts
         * @param coefficients The coefficients to use to make the counts
         * @param baseCounts The base counts to multiply the coefficients by
         * @return A new Counts object with each wheel count being its coefficient * baseCounts
         */
        @NonNull
        public static Counts fromCoefficients(@NonNull Coefficients coefficients, int baseCounts) {
            return new Counts((int) (coefficients.leftFront * baseCounts), (int) (coefficients.leftBack * baseCounts), (int) (coefficients.rightFront * baseCounts), (int) (coefficients.rightBack * baseCounts));
        }

        public Coefficients toCoefficient() {
            double magnitude = this.getLargestMagnitude();
            magnitude = magnitude == 0 ? 1 : magnitude;
            Coefficients coefficients = new Coefficients();
            coefficients.leftFront = this.leftFront / magnitude;
            coefficients.leftBack = this.leftBack / magnitude;
            coefficients.rightFront = this.rightFront / magnitude;
            coefficients.rightBack = this.rightBack / magnitude;
            return coefficients;
        }

        public Coefficients toCoefficient(int baseCounts) {
            Coefficients coefficients = new Coefficients();
            double magnitude = this.getLargestMagnitude();
            if(baseCounts == 0 || magnitude == 0) {
                coefficients.leftFront = 0.0;
                coefficients.leftBack = 0.0;
                coefficients.rightFront = 0.0;
                coefficients.rightBack = 0.0;
            } else {
                magnitude = magnitude > baseCounts ? magnitude : baseCounts;
                coefficients.leftFront = this.leftFront / magnitude;
                coefficients.leftBack = this.leftBack / magnitude;
                coefficients.rightFront = this.rightFront / magnitude;
                coefficients.rightBack = this.rightBack / magnitude;
            }
            return coefficients;
        }

        @NonNull
        public static Counts fromPosition(@NonNull EncoderMecanumWheels wheels) {
            return new Counts(wheels.leftFront.getCurrentPosition(), wheels.leftBack.getCurrentPosition(), wheels.rightFront.getCurrentPosition(), wheels.rightBack.getCurrentPosition());
        }

        @Const
        @NonNull
        public Counts subtracted(@NonNull Counts counts) {
            return new Counts(this.leftFront - counts.leftFront, this.leftBack - counts.leftBack, this.rightFront - counts.rightFront, this.rightBack - counts.rightBack);
        }

        @NonConst
        public void subtract(@NonNull Counts counts) {
            this.leftFront -= counts.leftFront;
            this.leftBack -= counts.leftBack;
            this.rightFront -= counts.rightFront;
            this.rightBack -= counts.rightBack;
        }
    }
}
