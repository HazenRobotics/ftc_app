package org.firstinspires.ftc.teamcode.autonomous.controllers;

import org.firstinspires.ftc.teamcode.interfaces.Wheels;
import org.firstinspires.ftc.teamcode.models.Position;
import org.firstinspires.ftc.teamcode.models.Vector;

/**
 * The means for autonomous to control the robot's motion.
 * 
 * It includes advanced motion abstractions, and the concept of absolute position.
 */
public class MotionController implements Wheels {
    private Position currentPosition;
    private final Wheels wheels;

    /**
     * This class is a decorator wrapping IWheels with more useful controls.
     * @param wheels The actual implementation of the wheels.
     * @param startingPosition The position that the robot is currently at, to track from.
     */
    public MotionController(Wheels wheels, Position startingPosition) {
        this.wheels = wheels;
        this.currentPosition = startingPosition;
    }
    
    ///
    /// SECTION: Raw controls.
    /// Re-exports the wheel controls, in addition to absolute tracking.
    ///
	
	/**
     * Moves directly by a given displacement without any turning.
     * @param x The amount to move forward/backward.
     * @param y The amount to move side-to-side.
     */
    @Override
	public void strafe(Vector displacement) {
    	this.currentPosition = currentPosition.add(displacement);
		wheels.strafe(displacement);
	}
	
	/**
	 * Turns the robot by a given angle.
	 * @param angle The angle to turn in degrees.
	 */
    @Override
	public void turn(double angle) {
    	this.currentPosition = currentPosition.rotate(angle);
		wheels.turn(angle);
	}
    
    ///
    /// SECTION: Relative motion.
    /// Extensions on top of the regular wheel motion controls.
    ///
    
    /**
     * Moves by the given distance by turning and then moving forward. Not to be confused with {@link #strafe(double, double)}, which moves directly by that much.
     * @param x The distance forward/backward to move.
     * @param y The distance side-to-side to move.
     */
    public final void move(double x, double y) {
        move(new Vector(x, y));
    }

    /**
     * Turns in a direction and then moves forward. Not to be confused with {@link #strafe(Vector)}, which goes directly to a position without turning.
     * @param displacement The amount to move by.
     */
    public final void move(Vector displacement) {
        turn(displacement.getAngle());
        move(displacement.getMagnitude());
    }

    /**
     * Moves forward.
     * @param distance The distance to move forward.
     */
    public final void move(double distance) {
        strafe(distance, 0);
    }

    /**
     * Moves directly by a given displacement without any turning. Not to be confused with {@link #move(double, double)}, which turns and then moves forward.
     * @param x The amount to move forward/backward.
     * @param y The amount to move side-to-side.
     */
    public void strafe(double x, double y) {
        strafe(new Vector(x, y));
    }
    
    ///
    /// SECTION: Absolute motion
    /// Implemented on the advanced constructs defined prior.
    ///

    /**
     * Moves by a fixed quantity relative to the game board, rather than relative to the robot.
     * @param displacement The amount to move by on the board.
     */
    public void strafeAbsolute(Vector displacement) {
        goTo(currentPosition.getLocation().delta(displacement));
    }

    /**
     * Go to a specific location on the game board.
     * @param location The location to go to.
     */
    public void goTo(Vector location) {
        strafe(currentPosition.getLocation().delta(location));
    }
    
    /**
     * Faces a given direction relative to the game board.
     * @see Position
     * @param direction The direction to face.
     */
    public void face(double direction) {
    	turn(currentPosition.getOrientation() - direction);
    }

    /**
     * Makes a specific position on the game board: it goes to the expected location, and turns itself to the requested orientation.
     * @param position The location and orientation to make.
     */
    public void makePosition(Position position) {
        goTo(position.getLocation());
        face(position.getOrientation());
    }

    /**
     * @return The robot's current position, as tracked by this class.
     */
    public Position getPosition() {
        return currentPosition;
    }
}
