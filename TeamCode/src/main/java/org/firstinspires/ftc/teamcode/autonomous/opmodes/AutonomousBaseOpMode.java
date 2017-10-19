package org.firstinspires.ftc.teamcode.autonomous.opmodes;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

import org.firstinspires.ftc.teamcode.autonomous.Autonomous;
import org.firstinspires.ftc.teamcode.autonomous.StartingPosition;
import org.firstinspires.ftc.teamcode.autonomous.controllers.GlyphController;
import org.firstinspires.ftc.teamcode.autonomous.controllers.MotionController;
import org.firstinspires.ftc.teamcode.interfaces.DcMotor;
import org.firstinspires.ftc.teamcode.interfaces.IArm;
import org.firstinspires.ftc.teamcode.interfaces.IHardware;
import org.firstinspires.ftc.teamcode.interfaces.ILift;
import org.firstinspires.ftc.teamcode.interfaces.IWheels;
import org.firstinspires.ftc.teamcode.interfaces.motors.LiftMotors;
import org.firstinspires.ftc.teamcode.interfaces.motors.MechanamMotors;
import org.firstinspires.ftc.teamcode.output.Telemetry;
import org.firstinspires.ftc.teamcode.sensors.ColorSensor;

public class AutonomousBaseOpMode extends LinearOpMode implements IHardware {
	private final StartingPosition startingPosition;
	
	private final IHardware hardware = this;
	private IArm arm;
	private ILift lift;
	private IWheels wheels;
	private ColorSensor colorSensor;
	
	private final Telemetry telemetry = new Telemetry(super.telemetry);
	private String status;
	
	public AutonomousBaseOpMode(StartingPosition startingPosition) {
		this.startingPosition = startingPosition;
	}
	
	public void initialize() {
		status = "Initializing";
		
		telemetry.add("Status", () -> { return status; });
    	telemetry.update();
		
		arm = null;
		lift = new LiftMotors(hardware);
		wheels = new MechanamMotors(hardware);
		
		colorSensor = null;
	}

    @Override
    public void runOpMode() {
    	initialize();
    	
    	status = "Waiting for start";
    	telemetry.update();
    	
    	GlyphController glyphController = new GlyphController(arm, lift);
    	MotionController motionController = new MotionController(wheels, startingPosition.getStartingPosition());
    	
    	waitForStart();
    	
    	// TODO: Rework this entire method of handling autonomous control flow
    	Thread autonomous = new Thread(new Autonomous(startingPosition, motionController, glyphController, colorSensor));
    	autonomous.start();
    	
        while (opModeIsActive());
        
        // It remains to be seen whether idle() is actually interruptable. If not, I'll need something else.
        autonomous.interrupt();
    }

	@Override
	public DcMotor getMotor(String name) {
		return hardwareMap.dcMotor.get(name);
	}
}
