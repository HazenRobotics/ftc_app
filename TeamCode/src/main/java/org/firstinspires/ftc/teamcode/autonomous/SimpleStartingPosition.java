package org.firstinspires.ftc.teamcode.autonomous;

import org.firstinspires.ftc.teamcode.models.Color;
import org.firstinspires.ftc.teamcode.models.Position;

/**
 * One of the four starting positions for the robot.
 *
 * See {@link Position} for an explanation of the coordinate grid.
 */
public enum SimpleStartingPosition {
    //team color, movement angle on balance, distance from stone to box, angle to face box
    RED_1(Color.RED, 60.0f, 32.0f), //top left
    RED_2(Color.RED, 95.0f, 35.0f), //bottom left
    BLUE_1(Color.BLUE, -60.0f, 32.815f), //top right
    BLUE_2(Color.BLUE, -95.0f, 35.0f); //bottom right

    private final Color teamColor;
    private final float movementAngle;
    private final float baseDistance;

    private SimpleStartingPosition(Color teamColor, float movementAngle, float baseDistance) {
        this.teamColor = teamColor;
        this.movementAngle = movementAngle;
        this.baseDistance = baseDistance;
    }

    public float getMovementAngle() {
        return movementAngle;
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
