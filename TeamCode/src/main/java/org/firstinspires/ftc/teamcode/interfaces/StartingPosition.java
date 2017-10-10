package org.firstinspires.ftc.teamcode.interfaces;

/**
 * Created by Robotics on 10/10/2017.
 */

public class StartingLocation {
    public static final StartingPosition RED_1 = new StartingLocation(Color.RED, null);
    public static final StartingPosition RED_2 = new StartingLocation(Color.RED, null);
    public static final StartingPosition BLUE_1 = new StartingLocation(Color.BLUE, null);
    public static final StartingPosition BLUE_2 = new StartingLocation(Color.BLUE, null);

    private final Color teamColor;
    private final Position startingPosition;

    public StartingLocation(Color teamColor, Position startingPosition) {
        this.teamColor = teamColor;
        this.startingPosition = startingPosition;
    }

    public Color getTeamColor() {
        return teamColor;
    }

    public Position getStartingPosition() {
        return startingPosition;
    }
}
