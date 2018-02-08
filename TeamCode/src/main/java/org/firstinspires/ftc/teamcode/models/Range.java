package org.firstinspires.ftc.teamcode.models;

import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;
import org.firstinspires.ftc.teamcode.objects.I2cRangeSensor;

public class Range extends Condition {
    protected final float distance;
    protected final I2cRangeSensor rangeSensor;
    protected final boolean moveGreater;

    /**
     * A condition to move until distance reached.
     * @param distance target distance to move
     * @param rangeSensor range sensor for distance to be read from
     * @param moveGreater whether moving to greater distance
     */
    public Range(float distance, I2cRangeSensor rangeSensor, boolean moveGreater) {
        this.distance = distance;
        this.rangeSensor = rangeSensor;
        this.moveGreater = moveGreater;
    }

    /**
     * Checks if distance is reached.
     * @return true if reached target distance
     */
    @Override
    public boolean isTrue() {
        double currentDistance = rangeSensor.readUltrasonic(DistanceUnit.INCH);
        return moveGreater ? currentDistance > distance : currentDistance < distance;
    }
}