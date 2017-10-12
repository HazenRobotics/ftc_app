package org.firstinspires.ftc.teamcode.interfaces;

/**
 * One of the four starting positions for the robot.
 *
 * TODO: figure out coordinate grid
 */
public enum StartingPosition {
    RED_1(Color.RED, null),
    RED_2(Color.RED, null),
    BLUE_1(Color.BLUE, null),
    BLUE_2(Color.BLUE, null);

    private final Color teamColor;
    private final Position startingPosition;

    private StartingPosition(Color teamColor, Position startingPosition) {
        this.teamColor = teamColor;
        this.startingPosition = startingPosition;
    }

    /**
     * @return The team color of this starting position.
     */
    public Color getTeamColor() {
        return teamColor;
    }

    /**
     * @return The actual initial starting location and orientation that will be set up before autonomous mode.
     */
    public Position getStartingPosition() {
        return startingPosition;
    }
}
