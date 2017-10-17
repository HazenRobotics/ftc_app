package org.firstinspires.ftc.teamcode.autonomous.controllers;

import org.firstinspires.ftc.teamcode.interfaces.IArm;
import org.firstinspires.ftc.teamcode.interfaces.ILift;

/**
 * A means for autonomous to access the lift and arm, and control glyphs.
 */
public class GlyphController {
	private final IArm arm;
	private final ILift lift;
	
	/**
	 * Creates a new glyph controller using the given hardware.
	 * @param arm The arm to grab glyphs.
	 * @param lift The lift to raise the arm.
	 */
	public GlyphController(IArm arm, ILift lift) {
		this.arm = arm;
		this.lift = lift;
	}
	
	/**
	 * Attempts to pick up a glyph in front of the robot.
	 */
	public void pickUp() {
		arm.grabGlyph();
		lift.raise();
	}
	
	/**
	 * Sets down any glyph the robot may be carrying.
	 */
	public void setDown() {
		lift.lower();
		arm.dropGlyph();
	}
}
