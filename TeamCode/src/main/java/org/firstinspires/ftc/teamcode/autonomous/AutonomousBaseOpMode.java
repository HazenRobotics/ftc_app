package org.firstinspires.ftc.teamcode.autonomous;

import com.qualcomm.hardware.modernrobotics.ModernRoboticsI2cGyro;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DigitalChannel;
import com.qualcomm.robotcore.hardware.HardwareDevice;
import com.qualcomm.robotcore.hardware.I2cDevice;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;
import org.firstinspires.ftc.robotcore.external.navigation.RelicRecoveryVuMark;
import org.firstinspires.ftc.teamcode.interfaces.IHardware;
import org.firstinspires.ftc.teamcode.interfaces.motors.LiftMotors;
import org.firstinspires.ftc.teamcode.interfaces.motors.MechanamMotors;
import org.firstinspires.ftc.teamcode.models.Color;
import org.firstinspires.ftc.teamcode.models.Condition;
import org.firstinspires.ftc.teamcode.RelicRecoveryLocalizer;

import org.firstinspires.ftc.teamcode.objects.I2cRangeSensor;
import org.firstinspires.ftc.teamcode.objects.I2cColorSensor;
import org.firstinspires.ftc.teamcode.output.Message;
import org.firstinspires.ftc.teamcode.output.Telemetry;

public class AutonomousBaseOpMode extends LinearOpMode implements IHardware {

	//Constants
	protected static final float JEWEL_COLOR_READ_DISTANCE = 6.5f;
	protected static final float DRIVE_SPEED = 0.2f;
	protected static final float JEWEL_FORWARD_DISTANCE = 11.0f;
	protected static final float JEWEL_BACKUP_DISTANCE = 12.5f;
	protected static final float JEWEL_END_DISTANCE = 15.0f;
	protected static final float ALIGNMENT_FORWARD_DISTANCE = 24.0f;
	protected static final double CLAW_POWER = 0.2;
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
	protected MechanamMotors motion;
	protected DcMotor claw;
	protected LiftMotors lift;
	protected Servo flicker;
	protected ModernRoboticsI2cGyro gyro;
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
		this.motion = new MechanamMotors(hardware);
		this.lift = new LiftMotors(hardware);
		this.colorSensor = new I2cColorSensor((I2cDevice) hardware.get("jewelSensor"));
		this.rangeSensor = new I2cRangeSensor((I2cDevice) hardware.get("rangeSensor"));
		this.flicker = hardware.getServo("flicker");
		this.localizer = new RelicRecoveryLocalizer(vuforiaKey, true, true);
		localizer.activate();

		//claw
		claw = getMotor("claw");

		claw.setDirection(DcMotor.Direction.FORWARD);

		//Moves during init
		flicker.setDirection(Servo.Direction.REVERSE);
		flicker.setPosition(0);

		//gyro calibration
		gyro = (ModernRoboticsI2cGyro) get("gyro");
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
		currentStep = "Reading color";
		telemetry.update();

		motion.move(0, new Condition() {
			@Override
			public boolean isTrue() {
				currentStep = "Moving Forward:" + rangeSensor.readUltrasonic(DistanceUnit.INCH);
				telemetry.update();
				return rangeSensor.readUltrasonic(DistanceUnit.INCH) < JEWEL_COLOR_READ_DISTANCE;
			}
		}, DRIVE_SPEED);

		sleep(1000);

		color = colorSensor.readColor();

		currentStep = "Jewel color: " + String.valueOf(color);
		telemetry.update();

		sleep(1000);

		motion.move(180, new Condition() {
			@Override
			public boolean isTrue() {
				currentStep = "Moving Back";
				telemetry.update();

				return rangeSensor.readUltrasonic(DistanceUnit.INCH) > JEWEL_BACKUP_DISTANCE;
			}
		}, DRIVE_SPEED);

		currentStep = "Opening flicker";
		telemetry.update();
		flicker.setPosition(0.5);

		sleep(1000);

		currentStep = "Moving towards jewel";
		telemetry.update();
		motion.move(0, new Condition() {
			@Override
			public boolean isTrue() {
				return rangeSensor.readUltrasonic(DistanceUnit.INCH) < JEWEL_FORWARD_DISTANCE;
			}
		}, DRIVE_SPEED);

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

		motion.move(180, new Condition() {
			@Override
			public boolean isTrue() {

				currentStep = "Moving Back";
				telemetry.update();
				return rangeSensor.readUltrasonic(DistanceUnit.INCH) > JEWEL_END_DISTANCE;
			}
		}, DRIVE_SPEED);


		currentStep = "Closing Flicker";
		telemetry.update();

		flicker.setPosition(0);
	}

	/**
	 * <strong>Move to CryptoBoc</strong><br>
	 * Moves infront of CyrptoBox using distances (no sensors)
	 */
	private void moveToCryptoBox(){
		float MovementAngle = startingPosition.getMovementAngle();
		float AngleToCryptoBox = startingPosition.getAngleToCryptoBox();
		float BaseDistance = startingPosition.getBaseDistance();

		currentStep = "Turning towards CryptoBox";
		telemetry.update();

		gyroTurn(MovementAngle);

		currentStep = "Moving towards CryptoBox";
		telemetry.update();


		motion.move(BaseDistance);

		if(startingPosition == StartingPosition.RED_2) {
			currentStep = "Moving to adjust alignment with CryptoBox";
			telemetry.update();
			motion.move(new Condition() {
				@Override
				public boolean isTrue() {
					currentStep = "Moving Forward:" + rangeSensor.readUltrasonic(DistanceUnit.INCH);
					telemetry.update();
					return rangeSensor.readUltrasonic(DistanceUnit.INCH) < ALIGNMENT_FORWARD_DISTANCE;
				}
			});

			gyroTurn(90);
		}
		if(startingPosition == StartingPosition.BLUE_2) {
			currentStep = "Moving to adjust alignment with CryptoBox";
			telemetry.update();
			motion.move(new Condition() {
				@Override
				public boolean isTrue() {
					currentStep = "Moving Forward:" + rangeSensor.readUltrasonic(DistanceUnit.INCH);
					telemetry.update();
					return rangeSensor.readUltrasonic(DistanceUnit.INCH) < ALIGNMENT_FORWARD_DISTANCE;
				}
			});
			gyroTurn(-90);
		}

		currentStep = "Moving to VuMark location";
		telemetry.update();
		switch (vuMark){
			case LEFT:
				if(startingPosition.getTeamColor() == Color.BLUE){
					//no movement
				}
				if(startingPosition.getTeamColor() == Color.RED){
					motion.move(DISTANCE_BETWEEN_CRYPTO_BOX_COLUMNS * 2);
				}
				break;
			case RIGHT:
				if(startingPosition.getTeamColor() == Color.BLUE){
					motion.move(DISTANCE_BETWEEN_CRYPTO_BOX_COLUMNS * 2);
				}
				if(startingPosition.getTeamColor() == Color.RED){
					//no movement
				}
				break;
			case CENTER:
				if(startingPosition.getTeamColor() == Color.BLUE){
					motion.move(DISTANCE_BETWEEN_CRYPTO_BOX_COLUMNS * 1);
				}
				if(startingPosition.getTeamColor() == Color.RED){
					motion.move(DISTANCE_BETWEEN_CRYPTO_BOX_COLUMNS * 1);
				}
				break;
			default: //Default, scores in left most column
				if(startingPosition.getTeamColor() == Color.BLUE){
					//no movement
				}
				if(startingPosition.getTeamColor() == Color.RED){
					motion.move(DISTANCE_BETWEEN_CRYPTO_BOX_COLUMNS * 2);
				}
				break;
		}

		currentStep = "Turning to face CryptoBox";
		telemetry.update();

		gyroTurn(AngleToCryptoBox);
	}

	/**
	 * <strong>Score Glyph</strong><br>
	 * Scores glyph by dropping the glyph and pushing it in
	 */
	private void scoreGlyph() {
		motion.move(new Condition() {
			@Override
			public boolean isTrue() {
				currentStep = "Moving forward until close to CyrptoBox";
				telemetry.update();
				return rangeSensor.readUltrasonic(DistanceUnit.INCH) < CRYPTO_BOX_TARGET_DISTANCE;
			}
		});

		motion.move(new Condition() {
			protected long last = System.currentTimeMillis();
			protected long timePassed = 0;
			protected final long MS_TO_WAIT = 10;
			@Override
			public boolean isTrue() {
				long current = System.currentTimeMillis();
				long delta = current - last;
				last = current;
				timePassed += delta;
				return timePassed >= MS_TO_WAIT;
			}
		});

		ElapsedTime runtime = new ElapsedTime();
		double targetTime = 2;
		runtime.reset();
		while(runtime.seconds()  < targetTime){
			currentStep = "Releasing glyph, secs left:" + (targetTime-runtime.seconds());
			telemetry.update();
		}
		claw.setPower(0);

		motion.move(new Condition() {
			@Override
			public boolean isTrue() {
				currentStep = "Pushing glyph into box";
				telemetry.update();
				return rangeSensor.readUltrasonic(DistanceUnit.INCH) < CRYPTO_BOX_PUSH_TARGET_DISTANCE;
			}
		});
	}

	//Helper Methods

	public void gyroTurn(final float turn){
		final int heading = gyro.getIntegratedZValue();

		motion.turn(turn > 0, new Condition() {
			@Override
			public boolean isTrue() {
                currentStep = "turning " + turn + " degrees.  Degrees left: " + (turn < 0 ? -1 : 1) * (gyro.getIntegratedZValue() - (heading + turn));
                telemetry.update();
				return (turn < 0 ? -1 : 1) * (gyro.getIntegratedZValue() - (heading + turn)) > 0;
			}
		});
	}

	//Hardware
	@Override
	public void idle(long milliseconds) {
		long endTime = System.currentTimeMillis() + milliseconds;
		while(System.currentTimeMillis() < endTime && opModeIsActive()) {
			super.idle();
		}
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
