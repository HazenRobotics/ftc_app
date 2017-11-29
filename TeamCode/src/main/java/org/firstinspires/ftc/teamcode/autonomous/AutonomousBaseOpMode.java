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

import java.io.PrintWriter;
import java.io.StringWriter;

public class 	AutonomousBaseOpMode extends LinearOpMode implements IHardware {

	//Constants
	protected static final float JEWEL_READ_DISTANCE = 5.3f;
	protected static final float JEWEL_KNOCK_DISTANCE = 24.0f;
	protected static final float JEWEL_STRAFE_DISTANCE = 6.0f;
	protected static final float SCOOP_BALL_HEIGHT = 0.125f;
	protected static final float JEWEL_FORWARD_DISTANCE = 8.0f;
	protected static final float JEWEL_BACKUP_DISTANCE = 13.5f;
	protected static final float DRIVE_SPEED = 0.25f;
    protected static int COLOR;
	protected static final double JEWEL_STRAFFE_ERROR = 0.0;

	static int count = 0;

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
    final protected RelicRecoveryLocalizer localizer;

	//Variables
	protected RelicRecoveryVuMark vuMark;
	protected String currentStep;
	protected Message stepMessage;
	org.firstinspires.ftc.robotcore.external.Telemetry t;

	public AutonomousBaseOpMode(StartingPosition startingPosition) {
		this.startingPosition = startingPosition;

		//Stuff
		currentStep = "Initializing";
		/*stepMessage = telemetry.add("Step >", new Message.IMessageData() {
			@Override
			public String getMessage() {
				return currentStep;
			}
		});*/
		t = super.telemetry;
		this.hardware = this;
		this.telemetry = new Telemetry(super.telemetry);
		this.motion = new MechanamMotors(hardware);
		this.lift = new LiftMotors(hardware);
		hardware.getServo("flicker").setPosition(1);
		this.colorSensor = new I2cColorSensor((I2cDevice) hardware.get("jewelSensor"));
		this.rangeSensor = new I2cRangeSensor((I2cDevice) hardware.get("rangeSensor"));
		//TODO: Might throw exception about bad cast?
		//this.gyro = (ModernRoboticsI2cGyro) hardware.getDevice("gyro");
		gyro = null;
		this.localizer = new RelicRecoveryLocalizer(vuforiaKey, true, true);
		localizer.activate();


		/*gyro.calibrate();
		//TODO: Does this even work?
		while(gyro.isCalibrating()) {
			sleep(10);
		}*/
	}

	public void initialize() {
		currentStep = "Initializing";
		/*stepMessage = telemetry.add("Step >", new Message.IMessageData() {
			@Override
			public String getMessage() {
				return currentStep;
			}
		});*/
		t = super.telemetry;
		this.hardware = this;
		this.telemetry = new Telemetry(super.telemetry);
		this.motion = new MechanamMotors(hardware);
		this.lift = new LiftMotors(hardware);
		hardware.getServo("flicker").setPosition(1);
		this.colorSensor = new I2cColorSensor((I2cDevice) hardware.get("jewelSensor"));
		this.rangeSensor = new I2cRangeSensor((I2cDevice) hardware.get("rangeSensor"));
		//TODO: Might throw exception about bad cast?
		//this.gyro = (ModernRoboticsI2cGyro) hardware.getDevice("gyro");
		gyro = null;
		//this.localizer = new RelicRecoveryLocalizer(vuforiaKey, true, true);
		localizer.activate();

		/*gyro.calibrate();
		//TODO: Does this even work?
		while(gyro.isCalibrating()) {
			sleep(10);
		}*/
	}

	@Override
	public void runOpMode() {
		//initialize();

		currentStep = "Waiting for start";
		telemetry.update();

		waitForStart();

		currentStep = "Running Autonomous";
		telemetry.update();


		//readPictograph();
		knockOverJewel();
		//moveToCryptoBox();
		//scoreGlyph();
	}

	private void knockOverJewel() {
		//Moves forward to the appropriate distance to read the color of the jewel
		currentStep = "Reading Color";

        motion.move(0, new Condition() {
			@Override
			public boolean isTrue() {
				t.addData(">", "Moving Forward: " + rangeSensor.readUltrasonic(DistanceUnit.INCH));
				t.update();
				return rangeSensor.readUltrasonic(DistanceUnit.INCH) < JEWEL_READ_DISTANCE;
			}
		}, DRIVE_SPEED);

		sleep(1000);

        COLOR = colorSensor.readColor();
		telemetry.notify("Jewel Color >", String.valueOf(COLOR), 3.0);

		sleep(1000);

		//Moves back to the distance to be able to lower the scoop then knock the jewel
		currentStep = "Knocking Jewel";
		motion.move(180, new Condition() {
			@Override
			public boolean isTrue() {
				t.addData(">", "Moving Back");
				telemetry.update();
				return rangeSensor.readUltrasonic(DistanceUnit.INCH) > JEWEL_KNOCK_DISTANCE;
			}
		}, DRIVE_SPEED);

		sleep(1000);


		//Based on the color detected, knock the right or left jewel
		if ((COLOR >= 1 && COLOR <= 4 && startingPosition.getTeamColor() == Color.RED) ||(COLOR  >= 9 && COLOR <= 11 && startingPosition.getTeamColor() == Color.BLUE)) {
			//Strafe
			currentStep = "Strafe";
			telemetry.update();
			final RelicRecoveryLocalizer.MatrixPosition init = localizer.getUpdatedCryptoKeyPosition();
			final double originalX = DistanceUnit.INCH.fromCm(init.getX());

			try {
				motion.move(-90, new Condition() {
					@Override
					public boolean isTrue() {
						count++;
						RelicRecoveryLocalizer.MatrixPosition key = localizer.getUpdatedCryptoKeyPosition();
						t.addData("Pos", "X; " + DistanceUnit.INCH.fromCm(key.getX()) + " Y: " + DistanceUnit.INCH.fromCm(key.getY()));
						t.addData("Count >", "C: " + count);
						t.update();
						return DistanceUnit.INCH.fromCm(key.getX()) - originalX > JEWEL_STRAFE_DISTANCE;
					}
				}, DRIVE_SPEED / 5.0);
			} catch(NullPointerException e) {
				sleep(3);
				t.addData("Count >", "C: " + count);
				StringWriter sw = new StringWriter();
				PrintWriter pw = new PrintWriter(sw);
				e.printStackTrace(pw);
				String sStackTrace = sw.toString();
				t.addData("Error >", e.getLocalizedMessage());
				t.addData("Error >", sStackTrace);
				telemetry.update();
			}
		}

		sleep(1000);


        //Drop Small Lift
		currentStep = "Drop Small Lift";
		telemetry.update();
		lift.setScoopBottomHeight(SCOOP_BALL_HEIGHT);

		sleep(5000);

		//Forward under other colored ball
		currentStep = "Move under ball";
		telemetry.update();
		motion.move(0, new Condition() {
			@Override
			public boolean isTrue() {
				return rangeSensor.readUltrasonic(DistanceUnit.INCH) < JEWEL_FORWARD_DISTANCE;
			}
		}, DRIVE_SPEED);

		sleep(5000);

		//motion.move(0, JEWEL_FORWARD_DISTANCE);

		//Flip the ball
		currentStep = "Flip the ball";
		telemetry.update();
		lift.raiseScoop();

		telemetry.update();
		sleep(5000);

		motion.move(180, new Condition() {
			@Override
			public boolean isTrue() {
				t.addData(">", "Moving Back");
				telemetry.update();
				return rangeSensor.readUltrasonic(DistanceUnit.INCH) > JEWEL_BACKUP_DISTANCE;
			}
		}, DRIVE_SPEED);

		sleep(5000);
    }

	private void readPictograph() {
		//wait so that vuforia has time to attempt to read the key
		currentStep = "Reading Pictograph";
		sleep(1000);
		/*double displacement = motion.move(90, new Condition() {
			@Override
			public boolean isTrue() {
				telemetry.update();
				return localizer.cryptoKeyIsVisible();
			}
		}, 0.5);*/
		vuMark = localizer.cryptoKey();
		//motion.move(-90, displacement);

	}

	private void moveToCryptoBox(){

		currentStep = "Turning towards CryptoBox";
		telemetry.update();

		motion.turn(startingPosition.getAngleToCryptoBox());

		motion.turn(true, new Condition() {
			@Override
			public boolean isTrue() {
				telemetry.update();
				return tapeIsVisible();
			}
		});

		RelicRecoveryLocalizer.MatrixPosition pos = getTapePos();

		double x = pos.getX();
		double y = pos.getY();

		if (((startingPosition.equals(StartingPosition.BLUE_1) || startingPosition.equals(StartingPosition.BLUE_2)) && (y>0)
				|| ((startingPosition.equals(StartingPosition.RED_1) || startingPosition.equals(StartingPosition.RED_2)) && (y<0)))){
			//TODO: Improve math to account for the heading returning between 0 and 360, not -180 and 180?
			double turnAngle = startingPosition.getMovementAngle() - gyro.getHeading();

			currentStep = "Adjusting Facing to CryptoBox";
			motion.turn(turnAngle > 0, new Condition() {
				@Override
				public boolean isTrue() {
					telemetry.update();
					return Math.abs(gyro.getHeading() - startingPosition.getMovementAngle()) < FACING_ERROR_RANGE;
				}
			});

			currentStep = "Aligning with CryptoBox";
			telemetry.update();
			//TODO: Make better aligning algorithm?
			motion.move(-90, x);

			currentStep = "Approaching CryptoBox";
			if(tapeIsVisible()) {
				telemetry.update();
				y = getTapePos().getY();
			}

			motion.move(y - VUFORIA_MOVEMENT_BUFFER_DISTANCE);

			motion.move(new Condition() {
				@Override
				public boolean isTrue() {
					telemetry.update();
					return rangeSensor.readUltrasonic(DistanceUnit.INCH) < CRYPTO_BOX_TARGET_DISTANCE;
				}
			});
		}
		else
			idle();
	}

	private void scoreGlyph() {
		currentStep = "Scoring Glyph";
		//FIXME: GARY NEEDS TO WORK ON THIS
		/*turn(angle from starting postion);
		move(distance from starting position);
		if(color is red) {
			swithc: //vumark
			case:

		}
		else {
			swithc:
			case:
		}
		turn(angle back to box);
		move(condition {
			until ultrasonic right;
		});*/

		float MovementAngle = startingPosition.getMovementAngle();
		float AngleToCryptoBox = startingPosition.getAngleToCryptoBox();
		float BaseDistance = startingPosition.getBaseDistance();


		motion.turn(MovementAngle);
		motion.move(BaseDistance);
		switch (vuMark){
			case LEFT:
				if(startingPosition.getTeamColor() == Color.BLUE){

				}
				if(startingPosition.getTeamColor() == Color.RED){

				}
				break;
			case RIGHT:
				if(startingPosition.getTeamColor() == Color.BLUE){

				}
				if(startingPosition.getTeamColor() == Color.RED){

				}
				break;
			case CENTER:
				if(startingPosition.getTeamColor() == Color.BLUE){

				}
				if(startingPosition.getTeamColor() == Color.RED){

				}
				break;
			default: //SCORE ON FIRST ONE
				motion.move(0,10);//change values later
				t.addData("position:" , vuMark);
				break;
		}



		switch(vuMark) {
			case LEFT:
				motion.move(-90,2);
				motion.turn(90);
				motion.move(10);
				t.addData("position:", vuMark);
				break;

			case RIGHT:

				break;
			case UNKNOWN:
				break;

			default:
				motion.move(0,10);//change values later
				t.addData("position:" , vuMark);
				break;
		}
		telemetry.update();
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
