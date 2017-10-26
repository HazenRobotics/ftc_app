package org.firstinspires.ftc.teamcode.autonomous;

import org.firstinspires.ftc.teamcode.autonomous.controllers.GlyphController;
import org.firstinspires.ftc.teamcode.autonomous.controllers.MotionController;
import org.firstinspires.ftc.teamcode.models.Vector;
import org.firstinspires.ftc.teamcode.sensors.ColorSensor;

public class Autonomous implements Runnable {
	/**
	 * Assuming we start off facing the jewel, this is how far we have to
	 * travel to reach the jewel.
	 */
	public static final float DISTANCE_TO_JEWEL = 0;
	/** How far do we have to strafe to knock over the jewel? */
	public static final float JEWEL_STRAFE_DISTANCE = 0;
	/**
	 * The direction we have to strafe to travel toward whatever is sensed
	 * by the color sensor. LEFT or RIGHT would be more meaningful values,
	 * but it's not worth the effort of implementing that. Instead, this
	 * should be +90 degrees (right) or -90 degrees (left).
	 * 
	 * The opposite direction is at {@link #SIDE_OPPOSITE_TO_COLOR_SENSOR}.
	 */
	public static final float SIDE_OF_COLOR_SENSOR = 90;
	/** The opposite of {@link #SIDE_OF_COLOR_SENSOR}. */
	public static final float SIDE_OPPOSITE_TO_COLOR_SENSOR = -SIDE_OF_COLOR_SENSOR;
	
	private final StartingPosition startingPosition;
	private final MotionController motion;
	private final GlyphController arm;
	private final ColorSensor colorSensor;
	
	public Autonomous(StartingPosition startingPosition, MotionController motion, GlyphController arm, ColorSensor colorSensor) {
		this.startingPosition = startingPosition;
		this.motion = new MotionController(motion, startingPosition.getStartingPosition());
		this.colorSensor = colorSensor;
		this.arm = arm;
	}
	
	@Override
	public void run() {
		knockOverJewel();
		Object pattern = readPictograph();
		while(stillTimeToFetchGlyph()) {
			fetchGlyph();
			dropOffGlyph(pattern);
		}
		visitSafeZone();
	}
	
	private void knockOverJewel() {
		// TODO: Swap to motion.makePosition(startingPosition.jewelPosition)?
		// It would be more complex to do the same task, but it's semantically correct.
		motion.move(DISTANCE_TO_JEWEL);
		if(startingPosition.getTeamColor().approximatelyEquals(colorSensor.sense()))
			motion.strafe(JEWEL_STRAFE_DISTANCE, SIDE_OF_COLOR_SENSOR);
		else

			motion.strafe(JEWEL_STRAFE_DISTANCE, SIDE_OPPOSITE_TO_COLOR_SENSOR);
	}
	
	private Object readPictograph() {
		motion.makePosition(startingPosition.getPictographPosition());
		// read pictograph
		return null;
	}
	
	private boolean stillTimeToFetchGlyph() { return true; }
	
	private void fetchGlyph() {
		Vector glyph = locateGlyph();
		motion.goTo(glyph);
		arm.pickUp();
	}
	
	private void dropOffGlyph(Object pattern) {
		motion.makePosition(startingPosition.getCryptoboxPosition());
		arm.setDown();
	}

	private Vector locateGlyph() {
		motion.makePosition(startingPosition.getGlyphPosition());
		return null;
	}
	
	private void visitSafeZone() {
		motion.goTo(startingPosition.getCryptoboxPosition().getLocation());
	}
}
