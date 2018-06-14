package org.firstinspires.ftc.teamcode.autonomous.opmodes;



import com.qualcomm.robotcore.eventloop.opmode.Autonomous;

import org.firstinspires.ftc.teamcode.autonomous.SimpleAutonomous;
import org.firstinspires.ftc.teamcode.autonomous.SimpleStartingPosition;

@Autonomous(name="Simple Red 1", group="Simple")
public class SimpleAutonomousRed1 extends SimpleAutonomous {
    public SimpleAutonomousRed1() {
        super(SimpleStartingPosition.RED_1);
    }
}
