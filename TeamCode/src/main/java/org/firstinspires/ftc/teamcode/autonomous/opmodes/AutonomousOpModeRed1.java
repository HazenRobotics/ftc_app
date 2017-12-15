package org.firstinspires.ftc.teamcode.autonomous.opmodes;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;

import org.firstinspires.ftc.teamcode.autonomous.StartingPosition;

import org.firstinspires.ftc.teamcode.autonomous.AutonomousBaseOpMode;
@Disabled
@Autonomous(name="Autonomous Red 1", group="Autonomous")
public class AutonomousOpModeRed1 extends AutonomousBaseOpMode {
	public AutonomousOpModeRed1() {
		super(StartingPosition.RED_1);
	}
}
