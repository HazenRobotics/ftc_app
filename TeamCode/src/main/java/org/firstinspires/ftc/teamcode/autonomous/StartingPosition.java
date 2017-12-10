package org.firstinspires.ftc.teamcode.autonomous;

import org.firstinspires.ftc.teamcode.models.Color;
import org.firstinspires.ftc.teamcode.models.Position;

/**
 * One of the four starting positions for the robot.
 *
 * See {@link Position} for an explanation of the coordinate grid.
 */
public enum StartingPosition {
    //team color, movement angle on balance, distance from stone to box, angle to face box
    RED_1(Color.RED, 90.0f, 27.815f, -90.0f), //top left
    RED_2(Color.RED, 90.0f, 24.0f, 90.0f), //bottom left
    BLUE_1(Color.BLUE, -90.0f, 27.815f, 90f), //top right
    BLUE_2(Color.BLUE, -90.0f, 24.0f, 90.0f); //bottom right

    private final Color teamColor;
    private final float movementAngle;
    private final float baseDistance;
    private final float angleToCryptoBox;

    private StartingPosition(Color teamColor, float movementAngle, float baseDistance, float angleToCryptoBox) {
        this.teamColor = teamColor;
        this.movementAngle = movementAngle;
        this.baseDistance = baseDistance;
        this.angleToCryptoBox = angleToCryptoBox;
    }

    public float getMovementAngle() {
        return movementAngle;
    }

    public float getAngleToCryptoBox() {
        return angleToCryptoBox;
    }

    public float getBaseDistance() {
        return baseDistance;
    }

    /**
     * @return The team color of this starting position.
     */
    public Color getTeamColor() {
        return teamColor;
    }
}
