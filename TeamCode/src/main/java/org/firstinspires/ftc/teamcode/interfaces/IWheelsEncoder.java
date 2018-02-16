package org.firstinspires.ftc.teamcode.interfaces;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;

public interface IWheelsEncoder extends IWheels {
    void move(float distance, DistanceUnit unit);

    void turn(float angle, AngleUnit unit);
}
