package org.firstinspires.ftc.teamcode.interfaces;

/**
 * Decorates a motion controller with a concept of absolute position, to allow motion relative to the game board rather than just the robot.
 */
public class TravelController extends MotionController {
    private Position currentPosition;
    private MotionController internal;

    /**
     * This class uses the decorator pattern, so it must have another controller to wrap.
     * @param internal The motion controller that implements the actual motion.
     * @param startingPosition The position that the robot is currently at, to track from.
     */
    public TravelController(MotionController internal, Position startingPosition) {
        this.internal = internal;
        this.currentPosition = startingPosition;
    }

    @Override
    public void strafe(Vector displacement) {
        this.currentPosition = currentPosition.add(displacement);
        internal.strafe(displacement);
    }

    @Override
    public void turn(double orientation) {
        this.currentPosition = currentPosition.rotate(orientation);
        internal.turn(orientation);
    }

    /**
     * Moves by a fixed quantity relative to the game board, rather than relative to the robot.
     * @param displacement The amount to move by on the board.
     */
    public void strafeAbsolute(Vector displacement) {
        goTo(currentPosition.getLocation().getDelta(displacement));
    }

    /**
     * Go to a specific location on the game board.
     * @param location The location to go to.
     */
    public void goTo(Vector location) {
        strafe(currentPosition.getLocation().getDelta(location));
    }

    /**
     * Makes a specific position on the game board: it goes to the expected location, and turns itself to the requested orientation.
     * @param position
     */
    public void makePosition(Position position) {
        goTo(position.getLocation());
        turn(currentPosition.getOrientation() - position.getOrientation());
    }

    /**
     * @return The robot's current position, as tracked by this class.
     */
    public Position getPosition() {
        return currentPosition;
    }
}
