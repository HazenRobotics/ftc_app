package org.firstinspires.ftc.teamcode.interfaces;

/**
 * A location or displacement in two dimensions.
 *
 * TODO: normalize the vector automatically
 */
public class Vector {
    /**
     * This vector is internally implemented with x/y, but it could also be implemented with polar coordinates and make no difference.
     */
    private final double x, y;

    /**
     * Constructs a new vector from an x and y value.
     *
     * See {@link #fromPolar(double, double)} to make a vector from polar coordinates.
     * @param x
     * @param y
     */
    public Vector(double x, double y) {
        this.x = x;
        this.y = y;
    }

    /**
     * @return The x displacement of the vector in x/y form.
     */
    public double getX() {
        return x;
    }

    /**
     * @return The y displacement of the vector in x/y form.
     */
    public double getY() {
        return y;
    }

    /**
     * @return The angle of the vector, or the angle to get to the x/y from the origin.
     */
    public double getAngle() {
        return Math.toDegrees(Math.atan2(x, y));
    }

    /**
     * @return The magnitude of the vector, or the total distance of the x/y from the origin.
     */
    public double getMagnitude() {
        return Math.hypot(x, y);
    }

    /**
     * Displaces this vector by the given other vector.
     * @param displacement The displacement vector.
     * @return A new vector displaced by the given value.
     */
    public Vector add(Vector displacement) {
        return new Vector(this.x + displacement.x, this.y + displacement.y);
    }


    public Vector getDelta(Vector other) {
        return new Vector(x - other.x, y - other.y);
    }

    public static Vector fromPolar(double magnitude, double angle) {
        return new Vector(
                magnitude * Math.cos(Math.toRadians(angle)),
                magnitude * Math.sin(Math.toRadians(angle))
        );
    }
}
