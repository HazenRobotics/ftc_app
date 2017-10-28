package org.firstinspires.ftc.teamcode.interfaces;

import org.firstinspires.ftc.teamcode.models.Vector;

/**
 * A means to control the robot's wheels.
 */
public interface IWheels {
	/**
     * Moves directly by a given displacement without any turning via mechanam wheels.
     * @param displacement The amount to move.
     */
	public void move(Vector displacement);
	
	/**
	 * Turns the robot by a given angle.
	 * @param angle The angle to turn in degrees.
	 */
	public void turn(double angle);
}
