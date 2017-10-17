package org.firstinspires.ftc.teamcode.interfaces;

/**
 * A means to control the robot's clamp. If that's what we're even making, I don't even know tbh.
 */
public interface IArm {
	/**
	 * Clamp the glyph in front of you.
	 */
	public void grabGlyph();
	
	/**
	 * Drop any glyph we're holding.
	 */
	public void dropGlyph();
}
