package org.firstinspires.ftc.teamcode.models;


import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.teamcode.objects.I2cGyroSensor;

public class GyroTurn extends Condition {
    protected float targetAngle;

    protected final I2cGyroSensor gyroSensor;
    protected final boolean positiveTurn;
    protected final AngleUnit unit;

    public GyroTurn(float angle, I2cGyroSensor gyroSensor, boolean positiveTurn) {
        this.gyroSensor = gyroSensor;
        this.positiveTurn = positiveTurn;
        this.unit = AngleUnit.DEGREES;
        this.targetAngle = gyroSensor.getIntegratedZ(unit) + (positiveTurn ? angle : -angle);
    }

    public GyroTurn(float angle, I2cGyroSensor gyroSensor, boolean positiveTurn, AngleUnit unit) {
        this.gyroSensor = gyroSensor;
        this.positiveTurn = positiveTurn;
        this.unit = unit;
        this.targetAngle = gyroSensor.getIntegratedZ(unit) + (positiveTurn ? angle : -angle);
    }

    //TODO: finish
    public GyroTurn(float angle, I2cGyroSensor gyroSensor, boolean positiveTurn, boolean absoluteHeading) {
        this.gyroSensor = gyroSensor;
        this.positiveTurn = positiveTurn;
        this.unit = AngleUnit.DEGREES;
        if (absoluteHeading) {
            float heading = gyroSensor.getHeading(unit);
            float currentAngle = gyroSensor.getIntegratedZ();
            if (positiveTurn) {
                targetAngle = (heading > angle ? unit.fromDegrees(360) : 0) - heading + angle;
            } else {
                targetAngle = (heading < angle ? unit.fromDegrees(360) : 0) - heading + angle ;
            }


        }





    }

    @Override
    public boolean isTrue() {
        float currentAngle = gyroSensor.getIntegratedZ(unit);
        return false;
    }
}
