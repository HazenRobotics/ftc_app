package org.firstinspires.ftc.teamcode.interfaces;

/**
 * Controls the robot's arm
 */
public interface ArmController {
	/**
	 * Pick up whatever is directly in front of us
	 */
	public void pickUp();

	/**
	 * Set down anything we may be holding
	 */
	public void setDown();

	/**
	 * Lift block a given height
	 * @param height The height to lift the block
	 */
	public void liftBlock(float height);
}
