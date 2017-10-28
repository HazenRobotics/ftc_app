package org.firstinspires.ftc.teamcode.autonomous;

import org.firstinspires.ftc.teamcode.models.Color;
import org.firstinspires.ftc.teamcode.models.Position;

/**
 * One of the four starting positions for the robot.
 *
 * See {@link Position} for an explanation of the coordinate grid.
 */
public enum StartingPosition {
    RED_1(Color.RED, 0.0f, 90.0f),
    RED_2(Color.RED, 90.0f, 90.0f),
    BLUE_1(Color.BLUE, 0.0f, -90.0f),
    BLUE_2(Color.BLUE, -90.0f, -90.0f);

    private final Color teamColor;
    private final float targetHeading;
    private final float angleToCryptoBox;

    private StartingPosition(Color teamColor, float targetHeading, float angleToCryptoBox) {
        this.teamColor = teamColor;
        this.targetHeading = targetHeading;
        this.angleToCryptoBox = angleToCryptoBox;
    }

    public float getTargetHeading() {
        return targetHeading;
    }

    public float getAngleToCryptoBox() {
        return angleToCryptoBox;
    }

    /**
     * @return The team color of this starting position.
     */
    public Color getTeamColor() {
        return teamColor;
    }
}
