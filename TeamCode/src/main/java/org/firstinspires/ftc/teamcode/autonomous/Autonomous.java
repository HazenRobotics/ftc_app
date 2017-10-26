package org.firstinspires.ftc.teamcode.autonomous;

import com.qualcomm.robotcore.hardware.I2cAddr;
import com.qualcomm.robotcore.hardware.I2cDevice;
import com.qualcomm.robotcore.util.Range;

import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;
import org.firstinspires.ftc.robotcore.external.navigation.RelicRecoveryVuMark;
import org.firstinspires.ftc.teamcode.RelicRecoveryLocalizer;
import org.firstinspires.ftc.teamcode.autonomous.controllers.GlyphController;
import org.firstinspires.ftc.teamcode.autonomous.controllers.MotionController;
import org.firstinspires.ftc.teamcode.models.Vector;
import org.firstinspires.ftc.teamcode.objects.I2cRangeSensor;
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
	public static final double WORKING_DISTANCE = 0; //change this to be the distance away that we need to be from the crypto box before vuforia stops working
	
	private final StartingPosition startingPosition;
	private final MotionController motion;
	private final GlyphController arm;
	private final ColorSensor colorSensor;
	protected final RelicRecoveryLocalizer localizer;
	RelicRecoveryVuMark vuuMark;
	I2cRangeSensor rangeSensor;
	//private final RangeSensor rangeSense;

	public Autonomous(StartingPosition startingPosition, MotionController motion, ColorSensor colorSensor, GlyphController arm, I2cDevice rangeHardware) {
		this.startingPosition = startingPosition;
		this.motion = new MotionController(motion, startingPosition.getStartingPosition());
		this.colorSensor = colorSensor;
		this.arm = arm;
		this.localizer = new RelicRecoveryLocalizer("AeCNMrn/////AAAAGRlPvGpkjUVapbG0iA01W9pxODQbY2cczmmaGy8CmYxrxKgX4Vf4DTayzCXCJeYBCtDVd5iWQFKFtnbAlSlvIqJmcUnLOF79x5QwSpMX9hJER259y94/" +
				"bdZGZYj9XRg07DZZOpFwAERjcIH6HBVJcTG6/M+oLw4ObLbiY0EqZhZA6app2Tep5BDzsDSI9DwWrR2LqqPxJSRwwGqxqlkja+u3ggLEQmWalqr2n20ywTZUpHvqtBuP53AgnJZCs4HNc57+XhhjkJWLIBnb3HBPZAZMA4uZfAq" +
				"I1uP8E1L+wgiAGretWwRrO3X/frXXIi5IJU9JDx52szfHeOr8kYBekeA/Ir5RygBs6yUNDPsepHkq", true, true);
		rangeSensor = new I2cRangeSensor(new I2cAddr(0x28), rangeHardware);
		//this.rangeSense = rngsns
	}
	
	@Override
	public void run() {
		knockOverJewel();
		readPictograph();

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
	
	private void readPictograph() {
		while (!localizer.cryptoKeyIsVisible()) {
			//move to find it
		}
		vuuMark = localizer.cryptoKey();
	}

	private void moveToCryptobox(){
		while (!localizer.redIsVisible()) //Needs to change according to the position of the robot
		{
			motion.turn(5);
			try {
				wait(500);//Not Exact Time
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		double x =localizer.getUpdatedRedPosition().getX();
		double y = localizer.getUpdatedRedPosition().getY();
		double offSet=  localizer.getUpdatedRedPosition().getAngle();
		motion.turn(-(Math.atan2(y,x)));
		motion.strafe(-x,0);
		motion.move(0,y-WORKING_DISTANCE);
		while (rangeSensor.readUltrasonic(DistanceUnit.INCH)>3.0){
			motion.move(0,0.5);//Not Exact Numbers
			try {
				wait(500);//Not Exact Time
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
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
