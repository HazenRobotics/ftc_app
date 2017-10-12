package org.firstinspires.ftc.teamcode.autonomous;

import org.firstinspires.ftc.teamcode.interfaces.Color;
import org.firstinspires.ftc.teamcode.interfaces.Position;

/**
 * One of the four starting positions for the robot.
 *
 * TODO: figure out coordinate grid
 */
public enum StartingPosition {
    RED_1(Color.RED, null, null),
    RED_2(Color.RED, null, null),
    BLUE_1(Color.BLUE, null, null),
    BLUE_2(Color.BLUE, null, null);

    private final Color teamColor;
    private final Position startingPosition;
    private final Position pictographPosition;

    private StartingPosition(Color teamColor, Position startingPosition, Position pictographPosition) {
        this.teamColor = teamColor;
        this.startingPosition = startingPosition;
        this.pictographPosition = pictographPosition;
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

    public Position getPictographPosition() { return pictographPosition; }

    public Position getCryptoboxPosition() { return null; }

    public Position getGlyphPosition() { return null; }
}
