package org.firstinspires.ftc.teamcode.models;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.teamcode.sensors.I2cGyroSensor;

/**
 * GyroTurn is a condition type which {@link #isTrue()} when the {@link #gyroSensor} associated with the condition either detects is is greater than or less than a certain {@link #targetAngle}
 */
public class GyroTurn extends Condition {
    protected final float targetAngle;
    protected final I2cGyroSensor gyroSensor;
    protected final boolean positiveTurn;
    protected final AngleUnit unit;

    /**
     * Creates a GyroTurn condition that turns the specified angle in degrees in either the positive or negative direction
     * @param angle The angle to turn by
     * @param gyroSensor The gyro sensor which will be used to check the condition
     * @param positiveTurn If the condition is meant for a positive direction turn, and will be true when it is greater than a certain angle, or less than
     */
    public GyroTurn(float angle, I2cGyroSensor gyroSensor, boolean positiveTurn) {
        this(angle, gyroSensor, positiveTurn, false, AngleUnit.DEGREES);
    }

    /**
     * Creates a GyroTurn condition that turns the specified angle in degrees in either the positive or negative direction
     * @param angle The angle to turn by
     * @param gyroSensor The gyro sensor which will be used to check the condition
     * @param positiveTurn If the condition is meant for a positive direction turn, and will be true when it is greater than a certain angle, or less than
     * @param unit The unit type of the angle to be turned to
     */
    public GyroTurn(float angle, I2cGyroSensor gyroSensor, boolean positiveTurn, AngleUnit unit) {
        this(angle, gyroSensor, positiveTurn, false, unit);
    }

    /**
     * Creates a GyroTurn condition that turns the specified angle in degrees in either the positive or negative direction
     * @param angle The angle to turn by or move to
     * @param gyroSensor The gyro sensor which will be used to check the condition
     * @param positiveTurn If the condition is meant for a positive direction turn, and will be true when it is greater than a certain angle, or less than
     * @param absoluteHeading If an absolute or relative angle heading will be used
     */
    public GyroTurn(float angle, I2cGyroSensor gyroSensor, boolean positiveTurn, boolean absoluteHeading) {
        this(angle, gyroSensor, positiveTurn, absoluteHeading, AngleUnit.DEGREES);
    }

    /**
     * Creates a GyroTurn condition that turns the specified angle in degrees in either the positive or negative direction
     * @param angle The angle to turn by or move to
     * @param gyroSensor The gyro sensor which will be used to check the condition
     * @param positiveTurn If the condition is meant for a positive direction turn, and will be true when it is greater than a certain angle, or less than
     * @param absoluteHeading If an absolute or relative angle heading will be used
     * @param unit The unit type of the angle to be turned to
     */
    public GyroTurn(float angle, I2cGyroSensor gyroSensor, boolean positiveTurn, boolean absoluteHeading, AngleUnit unit) {
        this.gyroSensor = gyroSensor;
        this.positiveTurn = positiveTurn;
        this.unit = unit;
        if (absoluteHeading) {
            float heading = gyroSensor.getHeading(unit);
            float deltaAngle = ((positiveTurn ? (heading > angle) : (heading < angle))              //If ahead of where the angle is (for whichever direction we are going)
                    ? unit.fromDegrees(360) : 0)                                                    //Then add 360 to do one circle around,
                    - (heading - angle);                                                            //and subtract the difference in angle to hit the spot behind the current position if ahead of it,
            //or move forward the difference if it is in front of the current position
            this.targetAngle = gyroSensor.getIntegratedZValue() + (positiveTurn ? deltaAngle : -deltaAngle);
        } else {
            this.targetAngle = gyroSensor.getIntegratedZValue(unit) + (positiveTurn ? angle : -angle);
        }
    }

    /**
     * Returns the target angle threshold for the condition to be true
     * @return The target angle in the condition's {@link #unit} type
     */
    public float getTargetAngle() {
        return targetAngle;
    }

    /**
     * Returns the target angle threshold for the condition to be true
     * @param unit The unit type of the angle to be returned
     * @return The target angle in the specified unit type
     */
    public float getTargetAngle(AngleUnit unit) {
        return unit.fromUnit(this.unit, getTargetHeading());
    }

    /**
     * Returns the target heading angle threshold for the condition to be true
     * @return The target heading in the condition's {@link #unit} type
     */
    public float getTargetHeading() {
        return unit.normalize(targetAngle);
    }

    /**
     * Returns the target heading angle threshold for the condition to be true
     * @param unit The unit type of the angle to be returned
     * @return The target heading in the specified unit type
     */
    public float getTargetHeading(AngleUnit unit) {
        return unit.fromUnit(this.unit, getTargetHeading());
    }

    /**
     * Returns the angle remaining to reach the target angle
     * @return The change in angle left in the condition's {@link #unit} type
     */
    public float getAngleRemaining() {
        return targetAngle - gyroSensor.getIntegratedZValue(unit);
    }

    /**
     * Returns the target heading angle threshold for the condition to be true
     * @param unit The unit type of the angle to be returned
     * @return The change in angle left in the specified unit type
     */
    public float getAngleRemaining(AngleUnit unit) {
        return targetAngle - gyroSensor.getIntegratedZValue(unit);
    }

    /**
     * Checks if the {@link #targetAngle} has been reached.
     * @return If reached target angle
     */
    @Override
    protected boolean condition() {
        float currentAngle = gyroSensor.getIntegratedZValue(unit);
        return positiveTurn ? currentAngle >= targetAngle : currentAngle <= targetAngle;
    }
}
