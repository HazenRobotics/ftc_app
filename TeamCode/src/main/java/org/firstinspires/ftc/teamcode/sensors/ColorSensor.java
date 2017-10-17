package org.firstinspires.ftc.teamcode.sensors;

import org.firstinspires.ftc.teamcode.models.Color;

/**
 * The interface for accessing the robot's color sensor.
 */
public interface ColorSensor {
    /**
     * @return The color currently detected in front of the sensor.
     */
    public Color sense();
}
