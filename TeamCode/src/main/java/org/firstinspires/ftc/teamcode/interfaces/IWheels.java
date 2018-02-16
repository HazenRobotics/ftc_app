package org.firstinspires.ftc.teamcode.interfaces;

import org.firstinspires.ftc.teamcode.models.Condition;
import org.firstinspires.ftc.teamcode.models.Vector;

/**
 * A means to control the robot's wheels.
 */
public interface IWheels {
	/**
     * Moves directly forward until a given condition is true without any turning
     * @param condition The amount to move.
     */
	void move(Condition condition, boolean positiveDir);
	
	/**
	 * Turns the robot in a given direction until a condition is true
	 * @param condition The angle to turn in degrees.
	 */
	void turn(Condition condition, boolean positiveDir);
}
