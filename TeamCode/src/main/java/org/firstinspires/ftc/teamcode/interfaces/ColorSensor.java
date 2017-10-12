package org.firstinspires.ftc.teamcode.interfaces;

/**
 * The interface for accessing the robot's color sensor.
 */
public interface ColorSensor {
    /**
     * @return The color currently detected in front of the sensor.
     */
    public Color sense();
}
