package org.firstinspires.ftc.teamcode.models;

/**
 * A robot position: the location and orientation of the robot.
 *
 *	The location is stored as the x and y coordinates relative to the
 * center of the game, where x is the team member axis and y is the
 * alliance axis, e.g.
 * 
 * <pre>
 *        y-axis
 *          |
 *          v
 *          
 *   Blue 1 | Blue 2
 *          |
 *          |
 *  ----------------- <-- x-axis
 *          |
 *          |
 *   Red  1 | Red  2
 * </pre>
 */
public class Position {
	/**
	 * The width of the game.
	 */
	public static final float GAME_SIZE = 0;
	
	/** Explained in {@link Position} */
    private final Vector location;
    private final double orientation;

    /**
     * @param location The robot's location must be within the game bounds.
     * @param orientation The direction that the robot is facing.
     */
    public Position(Vector location, double orientation) {
    	if(location.getX() > GAME_SIZE || location.getY() > GAME_SIZE
    			|| location.getX() < -GAME_SIZE || location.getY() < -GAME_SIZE)
			throw new IllegalArgumentException("Current position is outside of game bounds!");
        this.location = location;
        this.orientation = orientation;
    }

    /**
     * @return The location of the robot.
     */
    public Vector getLocation() {
        return location;
    }

    /**
     * See {@link Vector#delta(Vector)}.
     *
     * @return A new displaced position.
     */
    public Position add(Vector displacement) {
        return new Position(location.add(displacement), orientation);
    }

    /**
     * Returns a new position rotated by the given angle-- not to be confused with rotating the Vector.
     * @param angle The angle to rotate by.
     * @return A new rotated position.
     */
    public Position rotate(double angle) {
        return new Position(location, orientation + angle);
    }

    /**
     * @return The direction the robot faces
     */
    public double getOrientation() {
        return orientation;
    }
}
