package org.firstinspires.ftc.teamcode.controllers;

import org.firstinspires.ftc.teamcode.models.Condition;
import org.firstinspires.ftc.teamcode.models.Vector;


/**
 * A means to control a robot's wheels and movement
 */
public interface IWheels {
	/**
     * Moves directly forward until a given condition is true without any turning
     * @param condition Wheels will move until this condition is true
     */
	void move(Condition condition, boolean positiveDir);
	
	/**
	 * Turns the robot in a given direction until a condition is true
	 * @param condition Turn until this condition is true
	 */
	void turn(Condition condition, boolean positiveDir);
}
