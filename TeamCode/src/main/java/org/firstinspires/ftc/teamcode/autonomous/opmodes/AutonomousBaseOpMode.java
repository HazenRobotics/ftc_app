package org.firstinspires.ftc.teamcode.autonomous.opmodes;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DigitalChannel;
import com.qualcomm.robotcore.hardware.I2cDevice;
import com.qualcomm.robotcore.hardware.Servo;

import org.firstinspires.ftc.teamcode.autonomous.Autonomous;
import org.firstinspires.ftc.teamcode.autonomous.StartingPosition;
import org.firstinspires.ftc.teamcode.interfaces.IHardware;
import org.firstinspires.ftc.teamcode.output.Message;
import org.firstinspires.ftc.teamcode.output.Telemetry;

public class AutonomousBaseOpMode extends LinearOpMode implements IHardware {
	private final StartingPosition startingPosition;
	
	private final IHardware hardware = this;
	
	private final Telemetry telemetry = new Telemetry(super.telemetry);
	private String status;
	
	public AutonomousBaseOpMode(StartingPosition startingPosition) {
		this.startingPosition = startingPosition;
	}
	
	public void initialize() {
		status = "Initializing";
		telemetry.add("Status", new Message.IMessageData() {
			@Override
			public String getMessage() {
				return status;
			}
		});
    	telemetry.update();
	}

    @Override
    public void runOpMode() {
    	initialize();
    	
    	status = "Waiting for start";
    	telemetry.update();
    	
    	waitForStart();
    	
    	// TODO: Rework this entire method of handling autonomous control flow
    	Thread autonomous = new Thread(new Autonomous(hardware, startingPosition));
    	autonomous.start();
    	
        while (opModeIsActive());
        
        // It remains to be seen whether idle() is actually interruptable. If not, I'll need something else.
        autonomous.interrupt();
    }

	@Override
	public DcMotor getMotor(String name) {
		return hardwareMap.dcMotor.get(name);
	}

	@Override
	public Servo getServo(String name) {
		return hardwareMap.servo.get(name);
	}

	@Override
	public DigitalChannel getDigitalChannel(String name) {
		return hardwareMap.digitalChannel.get(name);
	}

	@Override
	public I2cDevice getDevice(String name) {
		return hardwareMap.i2cDevice.get(name);
	}
}
