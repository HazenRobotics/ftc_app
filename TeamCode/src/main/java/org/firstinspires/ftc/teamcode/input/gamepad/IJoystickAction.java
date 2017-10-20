package org.firstinspires.ftc.teamcode.input.gamepad;

/**
 * An event behavior based on joystick values
 */
public interface IJoystickAction {
	/**
	 * @param x The joystick's x position
	 * @param y The joystick's y position
	 */
	public void act(float x, float y);
}
