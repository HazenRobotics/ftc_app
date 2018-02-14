package org.firstinspires.ftc.teamcode.models;


import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.teamcode.objects.I2cGyroSensor;

public class GyroTurn extends Condition {
    protected final float targetAngle;
    protected final I2cGyroSensor gyroSensor;
    protected final boolean positiveTurn;
    protected final AngleUnit unit;

    public GyroTurn(float angle, I2cGyroSensor gyroSensor, boolean positiveTurn) {
        this(angle, gyroSensor, positiveTurn, false, AngleUnit.DEGREES);
    }

    public GyroTurn(float angle, I2cGyroSensor gyroSensor, boolean positiveTurn, AngleUnit unit) {
        this(angle, gyroSensor, positiveTurn, false, unit);
    }

    public GyroTurn(float angle, I2cGyroSensor gyroSensor, boolean positiveTurn, boolean absoluteHeading) {
        this(angle, gyroSensor, positiveTurn, absoluteHeading, AngleUnit.DEGREES);
    }

    public GyroTurn(float angle, I2cGyroSensor gyroSensor, boolean positiveTurn, boolean absoluteHeading, AngleUnit unit) {
        this.gyroSensor = gyroSensor;
        this.positiveTurn = positiveTurn;
        this.unit = unit;
        if (absoluteHeading) {
            float heading = gyroSensor.getHeading(unit);
            float deltaAngle = ((positiveTurn ? (heading > angle) : (heading < angle))              //If ahead of where the angle is (for whichever direction we are going)
                    ? unit.fromDegrees(360) : 0)                                                    //Then add 360 to do one circle around,
                    - (heading - angle);                                                            //and subtract the difference in angle to hit the spot behind the current position if ahead of it,
            //or move forward the difference if it is in front of the current position
            this.targetAngle = gyroSensor.getIntegratedZ() + (positiveTurn ? deltaAngle : -deltaAngle);
        } else {
            this.targetAngle = gyroSensor.getIntegratedZ(unit) + (positiveTurn ? angle : -angle);
        }
    }

    public float getTargetAngle() {
        return targetAngle;
    }

    public float getTargetHeading() {
        return unit.normalize(targetAngle);
    }

    public float getAngleRemaining() {
        return targetAngle - gyroSensor.getIntegratedZ(unit);
    }

    @Override
    public boolean isTrue() {
        float currentAngle = gyroSensor.getIntegratedZ(unit);
        return positiveTurn ? currentAngle >= targetAngle : currentAngle <= targetAngle;
    }
}
