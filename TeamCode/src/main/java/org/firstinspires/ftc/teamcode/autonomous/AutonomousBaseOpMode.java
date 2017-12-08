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

public class 	AutonomousBaseOpMode extends LinearOpMode implements IHardware {

	//Constants
	protected static final float JEWEL_COLOR_READ_DISTANCE = 6.5f;
	protected static final float JEWEL_FORWARD_DISTANCE = 10.0f;
	protected static final float JEWEL_BACKUP_DISTANCE = 15.5f;
	protected static final float JEWEL_END_DISTANCE = 15.5f;
	protected static final float DRIVE_SPEED = 0.2f;
	protected static int color;
	protected final double CLAW_POWER = 0.2;

	static int count = 0;
	protected static final float CRYPTO_BOX_TARGET_DISTANCE = 12.0f;
	protected static final String vuforiaKey = "AeCNMrn/////AAAAGRlPvGpkjUVapbG0iA01W9pxODQbY2cczmmaGy8CmYxrxKgX4Vf4DTayzCXCJeYBCtDVd5iWQFKFtnbAlSlvIqJmcUnLOF79x5QwSpMX9hJER259y94/" +
			"bdZGZYj9XRg07DZZOpFwAERjcIH6HBVJcTG6/M+oLw4ObLbiY0EqZhZA6app2Tep5BDzsDSI9DwWrR2LqqPxJSRwwGqxqlkja+u3ggLEQmWalqr2n20ywTZUpHvqtBuP53AgnJZCs4HNc57+XhhjkJWLIBnb3HBPZAZMA4uZfAq" +
			"I1uP8E1L+wgiAGretWwRrO3X/frXXIi5IJU9JDx52szfHeOr8kYBekeA/Ir5RygBs6yUNDPsepHkq";
	protected static final float DISTANCE_BETWEEN_CRYPTO_BOX_COLUMNS = 1f; //FIXME: UPDATE THIS NUM
	
	//Objects and sensors
	protected IHardware hardware;
	protected Telemetry telemetry;
	protected StartingPosition startingPosition;
	protected MechanamMotors motion;
	protected DcMotor claw;
	protected LiftMotors lift;
	protected Servo flicker;
	protected I2cColorSensor colorSensor;
	protected I2cRangeSensor rangeSensor;
	protected ModernRoboticsI2cGyro gyro;
    protected RelicRecoveryLocalizer localizer;

	//Variables
	protected RelicRecoveryVuMark vuMark;
	protected String currentStep;
	protected Message stepMessage;
	org.firstinspires.ftc.robotcore.external.Telemetry t;

	public AutonomousBaseOpMode(StartingPosition startingPosition) {
		super();
		this.startingPosition = startingPosition;
	}

	public void initialize() {

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
		this.colorSensor = new I2cColorSensor((I2cDevice) hardware.get("jewelSensor"));
		this.rangeSensor = new I2cRangeSensor((I2cDevice) hardware.get("rangeSensor"));
		this.flicker = hardware.getServo("flicker");
		//TODO: Might throw exception about bad cast?
		//this.gyro = (ModernRoboticsI2cGyro) hardware.getDevice("gyro");
		gyro = null;
		this.localizer = new RelicRecoveryLocalizer(vuforiaKey, true, true);
		localizer.activate();

		claw = getMotor("claw");
		claw.setDirection(DcMotor.Direction.FORWARD);


		/*gyro.calibrate();
		//TODO: Does this even work?
		while(gyro.isCalibrating()) {
			sleep(10);
		}*/

		//Moves during init
		flicker.setPosition(1);

	}

	@Override
	public void runOpMode() {
		initialize();

		currentStep = "Waiting for start";
		t.update();

		waitForStart();

		currentStep = "Running Autonomous";
		t.update();

		readPictograph();
		knockOverJewel();
		//moveToCryptoBox(); //NO LONGER USED
		//scoreGlyph();
	}

	private void knockOverJewel() {
		//Moves forward to the appropriate distance to read the color of the jewel
		currentStep = "Reading color";

		final double startingUltrasonicReading = rangeSensor.readUltrasonic(DistanceUnit.INCH);

		motion.move(0, new Condition() {
			@Override
			public boolean isTrue() {
				currentStep = "Moving Forward:" + rangeSensor.readUltrasonic(DistanceUnit.INCH);
				t.update();
				return rangeSensor.readUltrasonic(DistanceUnit.INCH) < JEWEL_COLOR_READ_DISTANCE;
			}
		}, DRIVE_SPEED);

		sleep(1000);

		color = colorSensor.readColor();

		t.addData(">","Jewel color: " + String.valueOf(color));
		t.update();

		sleep(1000);

		motion.move(180, new Condition() {
			@Override
			public boolean isTrue() {
				t.addData(">", "Moving Back");
				t.update();
				return rangeSensor.readUltrasonic(DistanceUnit.INCH) > JEWEL_BACKUP_DISTANCE;
			}
		}, DRIVE_SPEED);

		currentStep = "Opening flicker";
		t.update();

		flicker.setPosition(0.5);

		sleep(1000);

		currentStep = "Moving towards jewel";
		t.update();

		motion.move(0, new Condition() {
			@Override
			public boolean isTrue() {
				return rangeSensor.readUltrasonic(DistanceUnit.INCH) < JEWEL_FORWARD_DISTANCE;
			}
		}, DRIVE_SPEED);

		sleep(500);

		//Based on the color detected, knock the right or left jewel
		if ((color >= 1 && color <= 4 && startingPosition.getTeamColor() == Color.RED) ||(color >= 9 && color <= 11 && startingPosition.getTeamColor() == Color.BLUE)) {
			flicker.setPosition(0);
		} else {
			flicker.setPosition(1);
		}

		sleep(1000);

		currentStep = "Moving back";
		t.update();
		motion.move(180, new Condition() {
			@Override
			public boolean isTrue() {
				t.addData(">", "Moving Back");
				t.update();
				return rangeSensor.readUltrasonic(DistanceUnit.INCH) > JEWEL_END_DISTANCE;
			}
		}, DRIVE_SPEED);

		flicker.setPosition(1);
    }

	private void readPictograph() {
		//wait so that vuforia has time to attempt to read the key
		currentStep = "Reading Pictograph";
		t.update();
		sleep(1000);
		vuMark = localizer.cryptoKey();
	}

	private void moveToCryptoBox(){

		//TO MOVE BY TIME:
		/*motion.move(new Condition() {
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
		});*/

		float MovementAngle = startingPosition.getMovementAngle();
		float AngleToCryptoBox = startingPosition.getAngleToCryptoBox();
		float BaseDistance = startingPosition.getBaseDistance();

		currentStep = "Turning towards CryptoBox";
		t.update();
		motion.turn(MovementAngle);

		currentStep = "Moving towards CryptoBox";
		motion.move(BaseDistance);

		if(startingPosition == StartingPosition.RED_2) {
			currentStep = "Moving to adjust alignment with CryptoBox";
			t.update();
			motion.move(3.815);
			motion.turn(-90.0);
		}
		if(startingPosition == StartingPosition.BLUE_2) {
			currentStep = "Moving to adjust alignment with CryptoBox";
			t.update();
			motion.move(3.815);
			motion.turn(90.0);
		}

		currentStep = "Moving to VuMark location";
		t.update();
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
			default: //Default, score in left
				if(startingPosition.getTeamColor() == Color.BLUE){
					//no movement
				}
				if(startingPosition.getTeamColor() == Color.RED){
					motion.move(DISTANCE_BETWEEN_CRYPTO_BOX_COLUMNS * 2);
				}
				break;
		}

		currentStep = "Moving forward until close to CyrptoBox";
		t.update();
		motion.move(new Condition() {
			@Override
			public boolean isTrue() {
				t.update();
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
		claw.setPower(CLAW_POWER);
		runtime.reset();
		while(runtime.seconds()  < targetTime){
			currentStep = "releasing glyph, secs left: " + (targetTime-runtime.seconds());
		}
		claw.setPower(0);

		currentStep = "pushing glyph into box";
		t.update();
		motion.move(new Condition() {
			@Override
			public boolean isTrue() {
				t.update();
				return rangeSensor.readUltrasonic(DistanceUnit.INCH) < 6; //Turn into magic num
			}
		});
	}

	private void scoreGlyph() {


		
	}

	public void idle(long milliseconds) {
		// This is probably the wrong way to handle this-- spin loop.
		// However, it's better than Thread.idleFor()-- probably.
		long endTime = System.currentTimeMillis() + milliseconds;
		while(System.currentTimeMillis() < endTime && opModeIsActive()) {
			t.update();
			hardware.idle();
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
