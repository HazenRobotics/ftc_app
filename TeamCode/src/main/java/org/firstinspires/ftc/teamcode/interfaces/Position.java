package org.firstinspires.ftc.teamcode.interfaces;

/**
 * A robot position: the location and orientation of the robot.
 */
public class Position {
    private final Vector location;
    private final double orientation;

    public Position(Vector location, double orientation) {
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
     * See {@link Vector#getDelta(Vector)}.
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
