package org.firstinspires.ftc.teamcode.autonomous;

import com.qualcomm.hardware.modernrobotics.ModernRoboticsI2cGyro;
import com.qualcomm.robotcore.hardware.Gyroscope;
import com.qualcomm.robotcore.hardware.I2cAddr;

import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;
import org.firstinspires.ftc.robotcore.external.navigation.RelicRecoveryVuMark;
import org.firstinspires.ftc.teamcode.Condition;
import org.firstinspires.ftc.teamcode.MotionControl;
import org.firstinspires.ftc.teamcode.RelicRecoveryLocalizer;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.teamcode.models.Color;
import org.firstinspires.ftc.teamcode.objects.I2cRangeSensor;
import org.firstinspires.ftc.teamcode.objects.I2cColorSensor;
public class Autonomous implements Runnable {

	HardwareMap hardwareMap;
	public static final float JEWEL_READ_DISTANCE = 7.5f;
	public static final float JEWEL_KNOCK_DISTANCE = 10.5f;
	public static final float JEWEL_STRAFE_DISTANCE = 0;
    public static final float JEWEL_STRAFE_ANGLE = 75;
    public static final float JEWEL_BACKUP_DISTANCE = 0;
	public static final float COLOR_STRAFE_DISTANCE = 0.0f;
	public static final float VUFORIA_MOVEMENT_BUFFER_DISTANCE = 1.5f;
	public static final float CRYPTO_BOX_TARGET_DISTANCE = 3.0f;
	public static final float FACING_ERROR_RANGE = 5;
	public static final double WORKING_DISTANCE = 0; //change this to be the distance away that we need to be from the crypto box before vuforia stops working

	private final StartingPosition startingPosition;
	private final MotionControl motion;
	private final I2cColorSensor colorSensor;
    protected final RelicRecoveryLocalizer localizer;
    RelicRecoveryVuMark vuuMark;
    I2cRangeSensor rangeSensor;
	ModernRoboticsI2cGyro gyro;

	public Autonomous(HardwareMap hardwareMap, StartingPosition startingPosition) {
		this.hardwareMap = hardwareMap;
		this.startingPosition = startingPosition;
		this.motion = new MotionControl(hardwareMap);
		this.colorSensor = new I2cColorSensor(new I2cAddr(0x28), hardwareMap.i2cDevice.get("jewelSensor"));
		this.localizer = new RelicRecoveryLocalizer("AeCNMrn/////AAAAGRlPvGpkjUVapbG0iA01W9pxODQbY2cczmmaGy8CmYxrxKgX4Vf4DTayzCXCJeYBCtDVd5iWQFKFtnbAlSlvIqJmcUnLOF79x5QwSpMX9hJER259y94/" +
				"bdZGZYj9XRg07DZZOpFwAERjcIH6HBVJcTG6/M+oLw4ObLbiY0EqZhZA6app2Tep5BDzsDSI9DwWrR2LqqPxJSRwwGqxqlkja+u3ggLEQmWalqr2n20ywTZUpHvqtBuP53AgnJZCs4HNc57+XhhjkJWLIBnb3HBPZAZMA4uZfAq" +
				"I1uP8E1L+wgiAGretWwRrO3X/frXXIi5IJU9JDx52szfHeOr8kYBekeA/Ir5RygBs6yUNDPsepHkq", true, true);
		rangeSensor = new I2cRangeSensor(new I2cAddr(0x28), hardwareMap.i2cDevice.get("rangeSensor"));
		gyro = (ModernRoboticsI2cGyro) hardwareMap.gyroSensor.get("gyro");
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
		moveToCryptobox();
		scoreGlyph();
	}
	
	private void knockOverJewel() {
        motion.move(0, new Condition() {
			@Override
			public boolean isTrue() {
				return rangeSensor.readUltrasonic(DistanceUnit.INCH) < JEWEL_READ_DISTANCE;
			}
		});
		motion.move(COLOR_STRAFE_DISTANCE, 90);
        int color = colorSensor.readColor();
		motion.move(COLOR_STRAFE_DISTANCE, -90);
		motion.move(0, new Condition() {
			@Override
			public boolean isTrue() {
				return rangeSensor.readUltrasonic(DistanceUnit.INCH) > JEWEL_KNOCK_DISTANCE;
			}
		});
        //Drop Small Lift
        if (color >= 1 && color <= 4 && startingPosition.getTeamColor() == Color.BLUE) {
            motion.move(JEWEL_STRAFE_DISTANCE, -JEWEL_STRAFE_ANGLE);
        } else {
            motion.move(JEWEL_STRAFE_DISTANCE, JEWEL_STRAFE_ANGLE);
        }
        motion.move(JEWEL_BACKUP_DISTANCE, 180);
    }

	private void readPictograph() {
		sleep(1000);
		double displacement = motion.move(90, new Condition() {
			@Override
			public boolean isTrue() {
				return localizer.cryptoKeyIsVisible();
			}
		}, 0.5);
		vuuMark = localizer.cryptoKey();
		motion.move(displacement, -90);
		motion.turn(startingPosition.getAngleToCryptoBox());
	}

	private void moveToCryptobox(){
		motion.turn(true, new Condition() {
			@Override
			public boolean isTrue() {
				return tapeIsVisible();
			}
		});

		RelicRecoveryLocalizer.MatrixPosition pos = getTapePos();

		double x = pos.getX();
		double y = pos.getY();
		double turnAngle = startingPosition.getTargetHeading() - gyro.getHeading();
		motion.turn(turnAngle > 0, new Condition() {
			@Override
			public boolean isTrue() {
				return Math.abs(gyro.getHeading() - startingPosition.getTargetHeading()) < FACING_ERROR_RANGE;
			}
		});
		motion.move(x, -90);

		if(tapeIsVisible()) {
			y = getTapePos().getY();
		}

		motion.move(y - VUFORIA_MOVEMENT_BUFFER_DISTANCE, 0);

		motion.move(0, new Condition() {
			@Override
			public boolean isTrue() {
				return rangeSensor.readUltrasonic(DistanceUnit.INCH) < CRYPTO_BOX_TARGET_DISTANCE;
			}
		});
	}

	private void scoreGlyph() {

	}

	private void sleep(long millisecononds) {
		try {
			Thread.sleep(millisecononds);
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
		}
	}

	protected boolean tapeIsVisible() {
		return (!localizer.redIsVisible() && startingPosition.getTeamColor() == Color.RED) || (!localizer.blueIsVisible() && startingPosition.getTeamColor() == Color.BLUE);
	}

	protected RelicRecoveryLocalizer.MatrixPosition getTapePos() {
		return (startingPosition.getTeamColor() == Color.BLUE ? localizer.getUpdatedBluePosition() : localizer.getUpdatedRedPosition());
	}
}
