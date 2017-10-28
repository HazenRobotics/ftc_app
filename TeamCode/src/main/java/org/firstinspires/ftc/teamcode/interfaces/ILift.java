package org.firstinspires.ftc.teamcode.interfaces;

/**
 * A means to control the arm's mainLift.
 */
public interface ILift {
	/**
	 * Set the height of the mainLift to however many glyphs tall.
	 * @param glyphHeight The height to raise the mainLift to. Should be between 0 and 4.
	 */
	public void setLiftHeight(int glyphHeight);
}