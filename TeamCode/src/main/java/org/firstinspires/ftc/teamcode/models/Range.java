package org.firstinspires.ftc.teamcode.models;

import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;
import org.firstinspires.ftc.teamcode.objects.I2cRangeSensor;

/**
 * Range is a condition type which {@link #isTrue()} when the {@link #rangeSensor} associated with the condition either detects is is greater than or less than a certain {@link #distance}
 */
public class Range extends Condition {
    protected final float distance;
    protected final I2cRangeSensor rangeSensor;
    protected final boolean moveGreater;
    protected final DistanceUnit unit;

    /**
     * Creates a range condition that moves the specified distance in centimeters
     * @param distance The target distance for the condition to be true
     * @param rangeSensor The range sensor which will be used to check the condition
     * @param moveGreater If the condition will be true when the sensor reads greater than the target distance, or less than
     */
    public Range(float distance, I2cRangeSensor rangeSensor, boolean moveGreater) {
        this(distance, rangeSensor, moveGreater, DistanceUnit.CM);
    }

    /**
     * Creates a range condition that moves the specified distance in the specified units
     * @param distance The target distance for the condition to be true
     * @param rangeSensor The range sensor which will be used to check the condition
     * @param moveGreater If the condition will be true when the sensor reads greater than the target distance, or less than
     * @param unit The unit type of the distance to be checked
     */
    public Range(float distance, I2cRangeSensor rangeSensor, boolean moveGreater, DistanceUnit unit) {
        this.distance = distance;
        this.rangeSensor = rangeSensor;
        this.moveGreater = moveGreater;
        this.unit = unit;
    }

    /**
     * Returns the target distance threshold for the condition to be true
     * @return The target distance
     */
    public float getTargetDistance() {
        return distance;
    }

    /**
     * Returns the target distance threshold for the condition to be true
     * @param unit The unit type for the distance to be returned in
     * @return The target distance
     */
    public float getTargetDistance(DistanceUnit unit) {
        return (float) unit.fromUnit(this.unit, getTargetDistance());
    }

    /**
     * Returns the distance remaining to pass the target threshold
     * @return The distance remaining
     */
    public float getDistanceRemaining() {
        return distance - (float) rangeSensor.readUltrasonic(unit);
    }

    /**
     * Returns the distance remaining to pass the target threshold
     * @param unit The unit type for the distance to be returned in
     * @return The distance remaining
     */
    public float getDistanceRemaining(DistanceUnit unit) {
        return (float) unit.fromUnit(this.unit, getDistanceRemaining());
    }

    /**
     * Checks if the target {@link #distance} has been reached.
     * @return If reached target distance
     */
    @Override
    public boolean condition() {
        double currentDistance = rangeSensor.readUltrasonic(unit);
        return moveGreater ? currentDistance >= distance : currentDistance <= distance;
    }
}