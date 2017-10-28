package org.firstinspires.ftc.teamcode.interfaces;

/**
 * A means to control the arm's lift.
 */
public interface ILift {
	/**
	 * Set the height of the lift to however many glyphs tall.
	 * @param glyphHeight The height to raise the lift to. Should be between 0 and 4.
	 */
	public void setLiftHeight(int glyphHeight);
	public void setScoopTopHeight(double scoopTopHeight);
	public void setScoopBottomHeight(double scoopBottomHeight);

}