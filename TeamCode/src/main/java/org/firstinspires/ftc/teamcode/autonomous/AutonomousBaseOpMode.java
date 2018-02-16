package org.firstinspires.ftc.teamcode.autonomous;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DigitalChannel;
import com.qualcomm.robotcore.hardware.HardwareDevice;
import com.qualcomm.robotcore.hardware.I2cDevice;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;
import org.firstinspires.ftc.robotcore.external.navigation.RelicRecoveryVuMark;
import org.firstinspires.ftc.teamcode.controllers.EncoderMecanumWheels;
import org.firstinspires.ftc.teamcode.controllers.IHardware;
import org.firstinspires.ftc.teamcode.controllers.LiftMotors;
import org.firstinspires.ftc.teamcode.models.Color;
import org.firstinspires.ftc.teamcode.RelicRecoveryLocalizer;

import org.firstinspires.ftc.teamcode.models.GyroTurn;
import org.firstinspires.ftc.teamcode.models.Range;
import org.firstinspires.ftc.teamcode.models.Timer;
import org.firstinspires.ftc.teamcode.sensors.I2cGyroSensor;
import org.firstinspires.ftc.teamcode.sensors.I2cRangeSensor;
import org.firstinspires.ftc.teamcode.sensors.I2cColorSensor;
import org.firstinspires.ftc.teamcode.output.Message;
import org.firstinspires.ftc.teamcode.output.Telemetry;

public class AutonomousBaseOpMode extends LinearOpMode implements IHardware {

	//Constants
	protected static final float JEWEL_COLOR_READ_DISTANCE = 6.5f;
	protected static final float JEWEL_FORWARD_DISTANCE = 11.0f;
	protected static final float JEWEL_BACKUP_DISTANCE = 12.5f;
	protected static final float JEWEL_END_DISTANCE = 15.0f;
	protected static final float ALIGNMENT_FORWARD_DISTANCE = 24.0f;
	protected static final float CRYPTO_BOX_TARGET_DISTANCE = 10.0f; //CryptoBox is 4in. deep
	protected static final float CRYPTO_BOX_PUSH_TARGET_DISTANCE = 7.0F; //CryptoBox is 4in. deep
	protected static final float DISTANCE_BETWEEN_CRYPTO_BOX_COLUMNS = 7.63f; //CryptoBox column width is 7.63in.
	protected static int color;

	//Vuforia
	protected static final String vuforiaKey = "AeCNMrn/////AAAAGRlPvGpkjUVapbG0iA01W9pxODQbY2cczmmaGy8CmYxrxKgX4Vf4DTayzCXCJeYBCtDVd5iWQFKFtnbAlSlvIqJmcUnLOF79x5QwSpMX9hJER259y94/" +
			"bdZGZYj9XRg07DZZOpFwAERjcIH6HBVJcTG6/M+oLw4ObLbiY0EqZhZA6app2Tep5BDzsDSI9DwWrR2LqqPxJSRwwGqxqlkja+u3ggLEQmWalqr2n20ywTZUpHvqtBuP53AgnJZCs4HNc57+XhhjkJWLIBnb3HBPZAZMA4uZfAq" +
			"I1uP8E1L+wgiAGretWwRrO3X/frXXIi5IJU9JDx52szfHeOr8kYBekeA/Ir5RygBs6yUNDPsepHkq";

	//Objects and sensors
	protected IHardware hardware;
	protected Telemetry telemetry;
	protected StartingPosition startingPosition;
	protected EncoderMecanumWheels motion;
	protected DcMotor claw;
	protected LiftMotors lift;
	protected Servo flicker;
	protected I2cGyroSensor gyro;
	protected I2cColorSensor colorSensor;
	protected I2cRangeSensor rangeSensor;
	protected RelicRecoveryLocalizer localizer;
	org.firstinspires.ftc.robotcore.external.Telemetry t;

	//Variables
	protected RelicRecoveryVuMark vuMark;
	protected String currentStep;

	public AutonomousBaseOpMode(StartingPosition startingPosition) {
		super();
		this.startingPosition = startingPosition;
	}

	/**
	 * <strong>Initialization of hardware</strong><br>
	 */
	public void initialize() {
		//Telemetry
		//t = super.telemetry;
		telemetry = new Telemetry(super.telemetry);
		telemetry.add("Step", new Message.IMessageData() {
			@Override
			public String getMessage() {
				return currentStep;
			}
		});

		telemetry.update();

		//init hardware
		this.hardware = this;
		this.motion = new EncoderMecanumWheels(hardware);
		this.lift = new LiftMotors(hardware);
		this.colorSensor = new I2cColorSensor((I2cDevice) hardware.get("jewelSensor"));
		this.rangeSensor = new I2cRangeSensor((I2cDevice) hardware.get("rangeSensor"));
		this.flicker = hardware.getServo("flicker");
		this.localizer = new RelicRecoveryLocalizer(vuforiaKey, true, true);
		localizer.activate();
		/*
		//claw
		claw = getMotor("claw");
		claw.setDirection(DcMotor.Direction.FORWARD);*/

		// Sets the servo to it default position during autonomous
		flicker.setDirection(Servo.Direction.REVERSE);
		flicker.setPosition(0);

        //initialization and calibration of the gyro
		this.gyro = new I2cGyroSensor((I2cDevice) hardware.get("gyro"));
		gyro.calibrate();
		if(gyro.isCalibrating()) {
			idle();
		}
		sleep(500);
	}

	@Override
	public void runOpMode() {

		currentStep = "Initializing";
		initialize();


		currentStep = "Waiting for Start";
		telemetry.update();
		waitForStart();

		readPictograph();
		knockOverJewel();
		moveToCryptoBox();
		scoreGlyph();
	}

	/**
	 * <strong>Read Pictograph</strong><br>
	 * Reads the Pictograph using Vuforia (no motion)
	 */
	private void readPictograph() {
		currentStep = "Reading Pictograph";
		telemetry.update();
		sleep(1000);
		vuMark = localizer.cryptoKey();
	}

	/**
	 * <strong>Knocks over Jewel</strong><br>
	 * Move forward using ultrasonic sensor. Reads color of right Jewel using color sensor. Moves back using ultrasonic sensor.
	 * Extends Flicker. Moves forward using unltrasonic sensor. Flicks off Jewel based off reads for color sensor.
	 */
	private void knockOverJewel() {
        //(Step #1)
		currentStep = "Moving Closer";
		telemetry.update();
		motion.move(new Range(JEWEL_COLOR_READ_DISTANCE, rangeSensor, true), true);
        sleep(1000);

        //(Step #2)
        currentStep = "Reading Color";
		telemetry.update();

		color = colorSensor.readColor();
		currentStep = "Jewel color: " + String.valueOf(color);
		telemetry.update();

		sleep(1000);

        //(Step #3)
		currentStep = "Jewel Color: " + String.valueOf(color);
		motion.move(new Range(JEWEL_BACKUP_DISTANCE, rangeSensor, true), false);

        //(Step #4)
		currentStep = "Opening flicker";
		telemetry.update();
		flicker.setPosition(0.6);
		sleep(1000);

        //(Step #5)
		currentStep = "Moving towards jewel";
		telemetry.update();
		motion.move(new Range(JEWEL_FORWARD_DISTANCE, rangeSensor, false), true);

		sleep(500);

		//Based on the color detected, knock the right or left jewel
		currentStep = "Flicking Jewel";
		telemetry.update();
		if ((color >= 1 && color <= 4 && startingPosition.getTeamColor() == Color.RED) ||(color >= 9 && color <= 11 && startingPosition.getTeamColor() == Color.BLUE)) {
			flicker.setPosition(1);
		} else {
			flicker.setPosition(0);
		}

		sleep(1000);

		currentStep = "Moving Back";
		telemetry.update();
		motion.move(new Range(JEWEL_END_DISTANCE, rangeSensor, true), false);

		currentStep = "Closing Flicker";
		telemetry.update();

		flicker.setPosition(0);
	}

	/**
	 * <strong>Move to CryptoBoc</strong><br>
	 * Moves infront of CyrptoBox using distances (no sensors)
	 */
	private void moveToCryptoBox(){
		float movementAngle = startingPosition.getMovementAngle();
		float angleToCryptoBox = startingPosition.getAngleToCryptoBox();
		float baseDistance = startingPosition.getBaseDistance();

		currentStep = "Turning towards CryptoBox";
		telemetry.update();

		motion.turn(new GyroTurn(movementAngle, gyro, movementAngle > 0), movementAngle > 0);

		currentStep = "Moving towards CryptoBox";
		telemetry.update();

        motion.move(baseDistance, DistanceUnit.INCH);

		if(startingPosition == StartingPosition.RED_2) {
			currentStep = "Moving to adjust alignment with CryptoBox";
			telemetry.update();
			motion.move(new Range(ALIGNMENT_FORWARD_DISTANCE, rangeSensor, false, DistanceUnit.INCH), true);

            motion.turn(new GyroTurn(90, gyro, true), true);
		}
		else if(startingPosition == StartingPosition.BLUE_2) {
			currentStep = "Moving to adjust alignment with CryptoBox";
			telemetry.update();
			motion.move(new Range(ALIGNMENT_FORWARD_DISTANCE, rangeSensor, false), true);
            motion.turn(new GyroTurn(-90, gyro, false), false);
		}

		currentStep = "Moving to VuMark location";
		telemetry.update();
		switch (vuMark){
			case LEFT:
				if(startingPosition.getTeamColor() == Color.BLUE){
					//no movement
				}
				if(startingPosition.getTeamColor() == Color.RED){
					motion.move(DISTANCE_BETWEEN_CRYPTO_BOX_COLUMNS * 2, DistanceUnit.INCH);
				}
				break;
			case RIGHT:
				if(startingPosition.getTeamColor() == Color.BLUE){
					motion.move(DISTANCE_BETWEEN_CRYPTO_BOX_COLUMNS * 2, DistanceUnit.INCH);
				}
				if(startingPosition.getTeamColor() == Color.RED){
					//no movement
				}
				break;
			case CENTER:
				if(startingPosition.getTeamColor() == Color.BLUE){
					motion.move(DISTANCE_BETWEEN_CRYPTO_BOX_COLUMNS * 1, DistanceUnit.INCH);
				}
				if(startingPosition.getTeamColor() == Color.RED){
					motion.move(DISTANCE_BETWEEN_CRYPTO_BOX_COLUMNS * 1, DistanceUnit.INCH);
				}
				break;
			default: //Default, scores in left most column
				if(startingPosition.getTeamColor() == Color.BLUE){
					//no movement
				}
				if(startingPosition.getTeamColor() == Color.RED){
					motion.move(DISTANCE_BETWEEN_CRYPTO_BOX_COLUMNS * 2, DistanceUnit.INCH);
				}
				break;
		}

		currentStep = "Turning to face CryptoBox";
		telemetry.update();
        motion.turn(new GyroTurn(angleToCryptoBox, gyro, angleToCryptoBox > 0), angleToCryptoBox > 0);
	}

	/**
	 * <strong>Score Glyph</strong><br>
	 * Scores glyph by dropping the glyph and pushing it in
	 */
	private void scoreGlyph() {
		motion.move(new Range(CRYPTO_BOX_TARGET_DISTANCE, rangeSensor, false), true);

		motion.move(new Timer(10), true);

		ElapsedTime runtime = new ElapsedTime();
		double targetTime = 2;
		runtime.reset();
		while(runtime.seconds()  < targetTime){
			currentStep = "Releasing glyph, secs left:" + (targetTime-runtime.seconds());
			telemetry.update();
		}
		claw.setPower(0);

        currentStep = "Pushing glyph into box";
		telemetry.update();
        motion.move(new Range(CRYPTO_BOX_PUSH_TARGET_DISTANCE, rangeSensor, false), true);
	}

	//Hardware
	@Override
	public void idle(long milliseconds) {
		long endTime = System.currentTimeMillis() + milliseconds;
		while(System.currentTimeMillis() < endTime && opModeIsActive()) {
			super.idle();
		}
	}

	//Condenses the syntax for getting the motor reference
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
