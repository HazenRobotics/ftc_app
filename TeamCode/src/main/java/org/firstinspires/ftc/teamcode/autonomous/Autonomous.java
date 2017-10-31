package org.firstinspires.ftc.teamcode.autonomous;

import com.qualcomm.hardware.modernrobotics.ModernRoboticsI2cGyro;

import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;
import org.firstinspires.ftc.robotcore.external.navigation.RelicRecoveryVuMark;
import org.firstinspires.ftc.teamcode.interfaces.IHardware;
import org.firstinspires.ftc.teamcode.interfaces.motors.LiftMotors;
import org.firstinspires.ftc.teamcode.interfaces.motors.MechanumMotors;
import org.firstinspires.ftc.teamcode.models.Condition;
import org.firstinspires.ftc.teamcode.RelicRecoveryLocalizer;

import org.firstinspires.ftc.teamcode.models.Color;
import org.firstinspires.ftc.teamcode.objects.I2cRangeSensor;
import org.firstinspires.ftc.teamcode.objects.I2cColorSensor;
import org.firstinspires.ftc.teamcode.output.Message;
import org.firstinspires.ftc.teamcode.output.Telemetry;

public class Autonomous implements Runnable {

	//Buggyness with psuhes, yaya
	//Constants
	protected static final float JEWEL_READ_DISTANCE = 7.5f;
	protected static final float JEWEL_KNOCK_DISTANCE = 10.5f;
	protected static final float JEWEL_STRAFE_DISTANCE = 0;
	protected static final float JEWEL_STRAFE_ANGLE = 75;
	protected static final float JEWEL_BACKUP_DISTANCE = 0;
	protected static final float COLOR_STRAFE_DISTANCE = 0.0f;
	protected static final float VUFORIA_MOVEMENT_BUFFER_DISTANCE = 1.5f;
	protected static final float CRYPTO_BOX_TARGET_DISTANCE = 3.0f;
	protected static final float FACING_ERROR_RANGE = 5;
	protected static final String vuforiaKey = "AeCNMrn/////AAAAGRlPvGpkjUVapbG0iA01W9pxODQbY2cczmmaGy8CmYxrxKgX4Vf4DTayzCXCJeYBCtDVd5iWQFKFtnbAlSlvIqJmcUnLOF79x5QwSpMX9hJER259y94/" +
			"bdZGZYj9XRg07DZZOpFwAERjcIH6HBVJcTG6/M+oLw4ObLbiY0EqZhZA6app2Tep5BDzsDSI9DwWrR2LqqPxJSRwwGqxqlkja+u3ggLEQmWalqr2n20ywTZUpHvqtBuP53AgnJZCs4HNc57+XhhjkJWLIBnb3HBPZAZMA4uZfAq" +
			"I1uP8E1L+wgiAGretWwRrO3X/frXXIi5IJU9JDx52szfHeOr8kYBekeA/Ir5RygBs6yUNDPsepHkq";

	//Objects and sensors
	protected final IHardware hardware;
	protected final Telemetry telemetry;
	protected final StartingPosition startingPosition;
	protected final MechanumMotors motion;
	protected final LiftMotors lift;
	protected final I2cColorSensor colorSensor;
	protected final I2cRangeSensor rangeSensor;
	protected final ModernRoboticsI2cGyro gyro;
    protected final RelicRecoveryLocalizer localizer;

	//Variables
	protected RelicRecoveryVuMark vuuMark;
	protected String currentStep;
	protected Message stepMessage;

	public Autonomous(IHardware hardware, Telemetry telemetry, StartingPosition startingPosition) {
		currentStep = "Initializing";
		stepMessage = telemetry.add("Step >", new Message.IMessageData() {
			@Override
			public String getMessage() {
				return currentStep;
			}
		});

		this.hardware = hardware;
		this.telemetry = telemetry;
		this.startingPosition = startingPosition;
		this.motion = new MechanumMotors(hardware);
		this.lift = new LiftMotors(hardware);
		this.colorSensor = new I2cColorSensor(hardware.getDevice("jewelSensor"));
		this.rangeSensor = new I2cRangeSensor(hardware.getDevice("rangeSensor"));
		//TODO: Might throw exception about bad cast?
		this.gyro = (ModernRoboticsI2cGyro) hardware.getDevice("gyro");
		this.localizer = new RelicRecoveryLocalizer(vuforiaKey, true, true);

		gyro.calibrate();
		//TODO: Does this even work?
		while(gyro.isCalibrating()) {
			sleep(10);
		}

	}
	
	@Override
	public void run() {
		knockOverJewel();
		readPictograph();
		moveToCryptoBox();
		scoreGlyph();
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
		
		//Strafes to the side if need be and then reads the color
		motion.move(90, COLOR_STRAFE_DISTANCE);

        int color = colorSensor.readColor();
		telemetry.notify("Jewel Color >", String.valueOf(color), 3.0);

		motion.move(-90, COLOR_STRAFE_DISTANCE);
		
		//Moves back to the distance to be able to lower the small lift then knock the jewel
		currentStep = "Knocking Jewel";
		motion.move(new Condition() {
			@Override
			public boolean isTrue() {
				return rangeSensor.readUltrasonic(DistanceUnit.INCH) > JEWEL_KNOCK_DISTANCE;
			}
		});
        //Drop Small Lift
		lift.dropScoop();
		//Based on the color detected, knock the right or left jewel
        if (color >= 1 && color <= 4 && startingPosition.getTeamColor() == Color.BLUE) {
			//Knock right side (same side as color sensor)
			telemetry.notify("Hitting >", "Right Jewel", 3.0);
            motion.move(-JEWEL_STRAFE_ANGLE, JEWEL_STRAFE_DISTANCE);
        } else {
			//Knock left side
			telemetry.notify("Hitting >", "Left Jewel", 3.0);
            motion.move(JEWEL_STRAFE_ANGLE, JEWEL_STRAFE_DISTANCE);
        }
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

	private void sleep(long milliseconds) {
		try {
			Thread.sleep(milliseconds);
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
		}
		hardware.idle();
	}

	//Determines if the correct color tape for out team is visible
	protected boolean tapeIsVisible() {
		return (!localizer.redIsVisible() && startingPosition.getTeamColor() == Color.RED) || (!localizer.blueIsVisible() && startingPosition.getTeamColor() == Color.BLUE);
	}

	//Returns the location of the tape of our team color
	protected RelicRecoveryLocalizer.MatrixPosition getTapePos() {
		return (startingPosition.getTeamColor() == Color.BLUE ? localizer.getUpdatedBluePosition() : localizer.getUpdatedRedPosition());
	}
}
