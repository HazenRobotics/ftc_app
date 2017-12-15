package org.firstinspires.ftc.teamcode.autonomous.opmodes;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;

import org.firstinspires.ftc.teamcode.autonomous.AutonomousBaseOpMode;
import org.firstinspires.ftc.teamcode.autonomous.StartingPosition;
@Disabled
@Autonomous(name="Autonomous Blue 1", group="Autonomous")
public class AutonomousOpModeBlue1 extends AutonomousBaseOpMode {
	public AutonomousOpModeBlue1() {
		super(StartingPosition.BLUE_1);
	}
}
