package org.firstinspires.ftc.teamcode.interfaces;

/**
 * Created by Robotics on 10/10/2017.
 */

public class Location {
    private double x, y;

    public Location(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public double getAngle() {
        return Math.toDegrees(Math.atan2(x, y));
    }

    public double getMagnitude() {
        return Math.hypot(x, y);
    }

    public Location getDelta(Location other) {
        return new Location(x - other.x, y - other.y);
    }

    public static Location fromPolar(double magnitude, double angle) {
        return new Location(
                magnitude * Math.cos(angle),
                magnitude * Math.sin(angle)
        );
    }
}
