package org.firstinspires.ftc.teamcode.models;

import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;
import org.firstinspires.ftc.teamcode.objects.I2cRangeSensor;

public class Range extends Condition {
    protected final float distance;
    protected final I2cRangeSensor rangeSensor;
    protected final boolean moveGreater;
    protected final DistanceUnit unit;

    /**
     * Creates a range condition that moves the specified distance in centimeters
     * @param distance target distance to move
     * @param rangeSensor range sensor for distance to be read from
     * @param moveGreater whether moving to greater distance
     */
    public Range(float distance, I2cRangeSensor rangeSensor, boolean moveGreater) {
        this(distance, rangeSensor, moveGreater, DistanceUnit.CM);
    }

    /**
     * Creates a range condition that moves the specified distance in the specified units
     * @param distance target distance to move
     * @param rangeSensor range sensor for distance to be read from
     * @param moveGreater whether moving to greater distance
     * @param unit The unit type of the distance to be move
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
     * Returns the distance remaining to pass the target threshold
     * @return The distance remaining
     */
    public float getDistanceRemaining() {
        return distance - (float) rangeSensor.readUltrasonic(unit);
    }

    /**
     * Checks if distance is reached.
     * @return true if reached target distance
     */
    @Override
    public boolean isTrue() {
        double currentDistance = rangeSensor.readUltrasonic(unit);
        return moveGreater ? currentDistance >= distance : currentDistance <= distance;
    }
}