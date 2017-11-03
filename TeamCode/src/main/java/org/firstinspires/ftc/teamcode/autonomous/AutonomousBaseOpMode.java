package org.firstinspires.ftc.teamcode.autonomous;

import com.qualcomm.hardware.modernrobotics.ModernRoboticsI2cGyro;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DigitalChannel;
import com.qualcomm.robotcore.hardware.HardwareDevice;
import com.qualcomm.robotcore.hardware.I2cDevice;
import com.qualcomm.robotcore.hardware.Servo;

import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;
import org.firstinspires.ftc.robotcore.external.navigation.RelicRecoveryVuMark;
import org.firstinspires.ftc.teamcode.interfaces.IHardware;
import org.firstinspires.ftc.teamcode.interfaces.motors.LiftMotors;
import org.firstinspires.ftc.teamcode.interfaces.motors.MechanamMotors;
import org.firstinspires.ftc.teamcode.models.Condition;
import org.firstinspires.ftc.teamcode.RelicRecoveryLocalizer;

import org.firstinspires.ftc.teamcode.models.Color;
import org.firstinspires.ftc.teamcode.objects.I2cRangeSensor;
import org.firstinspires.ftc.teamcode.objects.I2cColorSensor;
import org.firstinspires.ftc.teamcode.output.Message;
import org.firstinspires.ftc.teamcode.output.Telemetry;

public class AutonomousBaseOpMode extends LinearOpMode implements IHardware {

	//Constants
	protected static final float JEWEL_READ_DISTANCE = 4.5f;
	protected static final float JEWEL_KNOCK_DISTANCE = 13.5f;
	protected static final float JEWEL_STRAFE_DISTANCE = 6.0f;
	protected static final float SCOOP_BALL_HEIGHT = 0.1f;
	protected static final float JEWEL_FORWARD_DISTANCE = 7.5f;
	protected static final float JEWEL_BACKUP_DISTANCE = 14.0f;
    protected static int COLOR;

	protected static final float VUFORIA_MOVEMENT_BUFFER_DISTANCE = 1.5f;
	protected static final float CRYPTO_BOX_TARGET_DISTANCE = 3.0f;
	protected static final float FACING_ERROR_RANGE = 5;
	protected static final String vuforiaKey = "AeCNMrn/////AAAAGRlPvGpkjUVapbG0iA01W9pxODQbY2cczmmaGy8CmYxrxKgX4Vf4DTayzCXCJeYBCtDVd5iWQFKFtnbAlSlvIqJmcUnLOF79x5QwSpMX9hJER259y94/" +
			"bdZGZYj9XRg07DZZOpFwAERjcIH6HBVJcTG6/M+oLw4ObLbiY0EqZhZA6app2Tep5BDzsDSI9DwWrR2LqqPxJSRwwGqxqlkja+u3ggLEQmWalqr2n20ywTZUpHvqtBuP53AgnJZCs4HNc57+XhhjkJWLIBnb3HBPZAZMA4uZfAq" +
			"I1uP8E1L+wgiAGretWwRrO3X/frXXIi5IJU9JDx52szfHeOr8kYBekeA/Ir5RygBs6yUNDPsepHkq";

	//Objects and sensors
	protected IHardware hardware;
	protected Telemetry telemetry;
	protected StartingPosition startingPosition;
	protected MechanamMotors motion;
	protected LiftMotors lift;
	protected I2cColorSensor colorSensor;
	protected I2cRangeSensor rangeSensor;
	protected ModernRoboticsI2cGyro gyro;
    protected RelicRecoveryLocalizer localizer;

	//Variables
	protected RelicRecoveryVuMark vuuMark;
	protected String currentStep;
	protected Message stepMessage;

	public AutonomousBaseOpMode(StartingPosition startingPosition) {
		this.startingPosition = startingPosition;

	}

	public void initialize() {
		currentStep = "Initializing";
		stepMessage = telemetry.add("Step >", new Message.IMessageData() {
			@Override
			public String getMessage() {
				return currentStep;
			}
		});
		this.hardware = this;
		this.telemetry = new Telemetry(super.telemetry);
		this.motion = new MechanamMotors(hardware);
		this.lift = new LiftMotors(hardware);
		this.colorSensor = new I2cColorSensor((I2cDevice) hardware.get("jewelSensor"));
		this.rangeSensor = new I2cRangeSensor((I2cDevice) hardware.get("rangeSensor"));
		//TODO: Might throw exception about bad cast?
		//this.gyro = (ModernRoboticsI2cGyro) hardware.getDevice("gyro");
		gyro = null;
		this.localizer = new RelicRecoveryLocalizer(vuforiaKey, true, true);


		/*gyro.calibrate();
		//TODO: Does this even work?
		while(gyro.isCalibrating()) {
			sleep(10);
		}*/

	}

	@Override
	public void runOpMode() {
		initialize();

		currentStep = "Waiting for start";
		telemetry.update();

		waitForStart();

		currentStep = "Running Autonomous";

		knockOverJewel();
		//readPictograph();
		//moveToCryptoBox();
		//scoreGlyph();
	}

	private void knockOverJewel() {
		//Moves forward to the appropriate distance to read the color of the jewel
		currentStep = "Reading Color";

        motion.move(new Condition() {
			@Override
			public boolean isTrue() {
				return rangeSensor.readUltrasonic(DistanceUnit.INCH) < JEWEL_READ_DISTANCE;
			}
		});

        COLOR = colorSensor.readColor();
		telemetry.notify("Jewel Color >", String.valueOf(COLOR), 3.0);

		//Moves back to the distance to be able to lower the small lift then knock the jewel
		currentStep = "Knocking Jewel";
		motion.move(new Condition() {
			@Override
			public boolean isTrue() {
				return rangeSensor.readUltrasonic(DistanceUnit.INCH) > JEWEL_KNOCK_DISTANCE;
			}
		});


		//Based on the color detected, knock the right or left jewel
		if (COLOR >= 1 && COLOR <= 4 && startingPosition.getTeamColor() == Color.RED) {
			//Strafe
			motion.move(-90, JEWEL_STRAFE_DISTANCE);
		}

        //Drop Small Lift
		lift.setScoopBottomHeight(SCOOP_BALL_HEIGHT);

		//Forward under other coloured ball
		motion.move(0, JEWEL_FORWARD_DISTANCE);

		//Flip the ball

		lift.raiseScoop();

        motion.move(180, JEWEL_BACKUP_DISTANCE);
    }

	private void readPictograph() {
		//wait so that vuforia has time to attempt to read the key
		currentStep = "Reading Pictograph";
		sleep(1000);
		double displacement = motion.move(90, new Condition() {
			@Override
			public boolean isTrue() {
				return localizer.cryptoKeyIsVisible();
			}
		}, 0.5);
		vuuMark = localizer.cryptoKey();
		motion.move(-90, displacement);

		currentStep = "Turning towards CryptoBox";
		motion.turn(startingPosition.getAngleToCryptoBox());
	}

	private void moveToCryptoBox(){

		motion.turn(true, new Condition() {
			@Override
			public boolean isTrue() {
				return tapeIsVisible();
			}
		});

		RelicRecoveryLocalizer.MatrixPosition pos = getTapePos();

		double x = pos.getX();
		double y = pos.getY();
		//TODO: Improve math to account for the heading returning between 0 and 360, not -180 and 180?
		double turnAngle = startingPosition.getTargetHeading() - gyro.getHeading();

		currentStep = "Adjusting Facing to CryptoBox";
		motion.turn(turnAngle > 0, new Condition() {
			@Override
			public boolean isTrue() {
				return Math.abs(gyro.getHeading() - startingPosition.getTargetHeading()) < FACING_ERROR_RANGE;
			}
		});

		currentStep = "Aligning with CryptoBox";
		//TODO: Make better aligning algorithm?
		motion.move(-90, x);

		currentStep = "Approaching CryptoBox";
		if(tapeIsVisible()) {
			y = getTapePos().getY();
		}

		motion.move(y - VUFORIA_MOVEMENT_BUFFER_DISTANCE);

		motion.move(new Condition() {
			@Override
			public boolean isTrue() {
				return rangeSensor.readUltrasonic(DistanceUnit.INCH) < CRYPTO_BOX_TARGET_DISTANCE;
			}
		});
	}

	private void scoreGlyph() {
		currentStep = "Scoring Glyph";
	}

	public void idle(long milliseconds) {
		// This is probably the wrong way to handle this-- spin loop.
		// However, it's better than Thread.idleFor()-- probably.
		long endTime = System.currentTimeMillis() + milliseconds;
		while(System.currentTimeMillis() < endTime && opModeIsActive()) {
			telemetry.update();
			hardware.idle();
		}
	}

	//Determines if the correct color tape for out team is visible
	protected boolean tapeIsVisible() {
		return (!localizer.redIsVisible() && startingPosition.getTeamColor() == Color.RED) || (!localizer.blueIsVisible() && startingPosition.getTeamColor() == Color.BLUE);
	}

	//Returns the location of the tape of our team color
	protected RelicRecoveryLocalizer.MatrixPosition getTapePos() {
		return (startingPosition.getTeamColor() == Color.BLUE ? localizer.getUpdatedBluePosition() : localizer.getUpdatedRedPosition());
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
	public HardwareDevice get(String name) {
		return hardwareMap.get(name);
	}
}
