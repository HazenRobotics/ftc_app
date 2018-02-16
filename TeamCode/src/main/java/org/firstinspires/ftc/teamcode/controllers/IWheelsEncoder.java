package org.firstinspires.ftc.teamcode.controllers;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;

/**
 * A means to control a robot with encoder functionality's wheels and movement
 */
public interface IWheelsEncoder extends IWheels {
    /**
     * Moves directly forward for a given distance using encoders without any turning
     * @param distance The distance to move
     * @param unit The unit in which the distance is in
     */
    void move(float distance, DistanceUnit unit);

    /**
     * Turns for a given angle using encoders
     * @param angle The angle for which to turn
     * @param unit The unit in which the angle is in
     */
    void turn(float angle, AngleUnit unit);
}
