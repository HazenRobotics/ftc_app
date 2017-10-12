package org.firstinspires.ftc.teamcode.interfaces;

/**
 * An interface for controlling the wheels of the robot's mechanam wheels.
 */
public abstract class MotionController {
    /**
     * Moves by the given distance by turning and then moving forward. Not to be confused with {@link #strafe(double, double)}, which moves directly by that much.
     * @param x
     * @param y
     */
    public final void move(double x, double y) {
        move(new Vector(x, y));
    }

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
     * Moves directly by a given displacement without any turning. {@link #move(double, double)}, which turns and then moves forward.
     * @param x The amount to move forward/backward.
     * @param y The amount to move side-to-side.
     */
    public void strafe(double x, double y) {
        strafe(new Vector(x, y));
    }

    /**
     *
     * @param displacement
     */
    public abstract void strafe(Vector displacement);
    public abstract void turn(double orientation);
}
