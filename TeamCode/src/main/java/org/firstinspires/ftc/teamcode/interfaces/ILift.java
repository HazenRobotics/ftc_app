package org.firstinspires.ftc.teamcode.interfaces;

/**
 * A means to control the arm's lift.
 */
public interface ILift {
	
	/**
	 * Raise the lift by however much it needs to be raised for a glyph.
	 */
	public void raise();
	
	/**
	 * Bring the glyph to the ground.
	 */
	public void lower();
}
