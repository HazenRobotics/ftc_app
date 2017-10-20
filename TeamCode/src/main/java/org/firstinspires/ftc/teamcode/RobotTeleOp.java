package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Servo;

import java.lang.reflect.Field;

import org.firstinspires.ftc.teamcode.autonomous.controllers.MotionController;
import org.firstinspires.ftc.teamcode.input.Button;
import org.firstinspires.ftc.teamcode.input.ButtonManager;
import org.firstinspires.ftc.teamcode.input.gamepad.ButtonEvent;
import org.firstinspires.ftc.teamcode.input.gamepad.JoystickDimension;
import org.firstinspires.ftc.teamcode.input.gamepad.annotations.ButtonListener;
import org.firstinspires.ftc.teamcode.input.gamepad.annotations.JoystickLinearListener;
import org.firstinspires.ftc.teamcode.input.gamepad.annotations.JoystickListener;
import org.firstinspires.ftc.teamcode.input.gamepad.annotations.LinearListener;
import org.firstinspires.ftc.teamcode.input.gamepad.values.IGamepadButton;
import org.firstinspires.ftc.teamcode.input.gamepad.values.IGamepadJoystick;
import org.firstinspires.ftc.teamcode.input.gamepad.values.ILinearValue;
import org.firstinspires.ftc.teamcode.interfaces.IWheels;
import org.firstinspires.ftc.teamcode.models.Position;
import org.firstinspires.ftc.teamcode.models.Vector;
import org.firstinspires.ftc.teamcode.output.Message;
import org.firstinspires.ftc.teamcode.output.Telemetry;

@TeleOp(button="TeleOp", group="TeleOp")
public class RobotTeleOp extends OpMode {
    protected Telemetry telemetry = new Telemetry(super.telemetry);
    protected ButtonManager buttons = new ButtonManager();

    // The arm components
    protected Servo claw;
    protected DcMotor armMotor;
    protected CRServo armControlServo;

    // Motors for each wheel
    protected DcMotor leftFront;
    protected DcMotor rightFront;
    protected DcMotor leftBack;
    protected DcMotor rightBack;

    // The lift motor
    protected DcMotor lift;

    //Add all Constants here
    //EX: protected final double MOTOR_POWER = 0.5;
    //private final double CLAW_POSITION_ONE = 0.0;
    //private final double CLAW_POSITION_TWO = 0.5;
    protected final double ARM_MOTOR_POWER = 0.4;
    protected final double ARM_SERVO_POWER = 0.4;
    //protected final double CLAW_POWER = 0.2;
    protected final double JOYSTICK_ERROR_RANGE = 0.1;

    //Lift Constants
    protected static final double SMALL_LIFT_LOWER_POS = 0.0, SMALL_LIFT_UPPER_POS = 0.0;
    protected static final double GLYPH_HEIGHT = 0.0; //Insert Glyph Height Here
    protected static final int LIFT_COUNTS_PER_MOTOR_REV = 1440 ;    // eg: TETRIX Motor Encoder
    protected static final double LIFT_GEAR_REDUCTION = 2.0 ;     // This is < 1.0 if geared UP
    protected static final double LIFT_INCHES_PER_REV = 0.0;// Dont know yet
    protected static final int LIFT_COUNTS_PER_INCH = (int) (LIFT_COUNTS_PER_MOTOR_REV / LIFT_INCHES_PER_REV);
    protected static final int COUNT_PER_GLYPH_HEIGHT = (int) (GLYPH_HEIGHT * LIFT_COUNTS_PER_INCH);
    protected static final double MAIN_LIFT_SPEED = 0.5;
    protected static final int MAIN_LIFT_ERROR_RANGE = 20;

    protected MotionController wheels;
    
    @Override
    public void init() {
    	setupHardware();
        setupTelemetry();
        setupButtons();
    }
    
    @Override
    public void loop() {
    	buttons.update();
        telemetry.update();
    }
    
    /** This is probably a bit excessive. I can roll it back if you want. */
    private void initialize(String name) {
    	Field field =  getClass().getDeclaredField(name);
    	String getterName = "get" + field.getType().getSimpleName();
    	Object value = getClass().getMethod(getterName).invoke(this, name);
    	field.set(this, value);
    }

    protected void setupHardware() {
    	initialize("mainLift");
        lift.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        lift.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        initialize("smallLift");

        initialize("claw");
        initialize("armControlServo");
        initialize("armMotor");
        armMotor.setDirection(DcMotor.Direction.FORWARD);

        initialize("leftFront");
        initialize("rightFront");
        initialize("leftBack");
        initialize("rightBack");

        // TODO: add IWheels implementation
        wheels = new MotionController(null, new Position(new Vector(0, 0), 0));
    }
    
    protected void setupTelemetry() {
    	telemetry.add("Lift Position", new Message.IMessageData() {
            @Override
            public String getMessage() {
                return lift.toString();
            }
        });
    }
    
    protected void setupButtons() {
    	buttons.registerButton("liftUp", gamepad2, "dpad_up");
    	buttons.registerButton("liftDown", gamepad2, "dpad_down");
    	buttons.registerButton("liftBusy", new IGamepadButton() {
			@Override
			public boolean isPressed() {
				return lift.isBusy();
			}
    	});
    	// TODO: WARNING: THIS (potentially) CONFLICTS WITH THE ABOVE
    	// Either disable one, make a variable to track which one to use, or suffer potential conflicts.
    	buttons.registerLinear("liftPower", new ILinearValue() {
			@Override
			public float getValue() {
				return gamepad2.right_trigger - gamepad2.left_trigger;
			}
    	});
    	
    	buttons.registerJoystick("gamepad2RightStick", gamepad2, "right_stick");
    	buttons.registerJoystickLinearAlias("gamepad2RightStick", "claw", JoystickDimension.X);
    	
    	buttons.registerButton("armUp", gamepad1, "x");
    	buttons.registerButton("armDown", gamepad1, "y");
    	
    	buttons.registerJoystick("wheels", gamepad1, "right_stick");
    	
    	buttons.registerListeners(this);
    }
    
    private int getGlyphRow() {
    	return lift.getCurrentPosition() / COUNT_PER_GLYPH_HEIGHT;
    }
    
    @ButtonListener(button = "liftUp")
    public void onLiftUpPress() {
    	int nextPosition = Math.max(getGlyphRow() + 1, 3);
    	lift.setTargetPosition(nextPosition * COUNT_PER_GLYPH_HEIGHT);
    	lift.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        lift.setPower(MAIN_LIFT_SPEED);
    }
    
    @ButtonListener(button = "liftDown")
    public void onLiftDownPress() {
    	int nextPosition = Math.max(getGlyphRow() - 1, 0);
		lift.setTargetPosition(nextPosition * COUNT_PER_GLYPH_HEIGHT);
		lift.setMode(DcMotor.RunMode.RUN_TO_POSITION);
	    lift.setPower(-MAIN_LIFT_SPEED);
    }
    
    @LinearListener(name = "liftPower")
    public void withLiftPower(float motor_power) {
        if((motor_power > 0 && lift.getCurrentPosition() >= COUNT_PER_GLYPH_HEIGHT * 4)
                || motor_power < 0 && lift.getCurrentPosition() <= 0)
            return;
        lift.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        lift.setPower(motor_power);
        autoMainLiftRunning = false;
    }
    
    @LinearListener(name = "liftPower")
    public void onLiftPowerRelease() {
    	lift.setPower(0);
    }
    
    @ButtonListener(button = "liftBusy", event = ButtonEvent.RELEASE)
    public void onLiftUnbusy() {
        lift.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        lift.setPower(0);
    }
    
    @LinearListener(name = "claw")
    public void withClawPower(float claw) {
    	int clawDirection = Math.signum(claw);
    	float clawDisplacement = clawDirection * 0.1;
    	float nextPosition = claw.getPosition() + clawDisplacement;
    	if ((clawDirection > 0 && claw.getPosition() < 1)
    			|| (clawDirection < 0 && claw.getPosition() <= 0))
            claw.setPosition(nextPosition);
    }
    
    @ButtonListener(button = "armUp")
    public void onArmUp() {
    	armMotor.setPower(ARM_MOTOR_POWER);
        armControlServo.setPower(ARM_SERVO_POWER);
    }
    
    @ButtonListener(button = "armDown")
    public void onArmDown() {
    	armMotor.setPower(-ARM_MOTOR_POWER);
        armControlServo.setPower(-ARM_SERVO_POWER);
    }
    
    @ButtonListener(button = "armUp", event = ButtonEvent.RELEASE)
    @ButtonListener(button = "armDown", event = ButtonEvent.RELEASE)
    public void onArmRelease() {
    	armMotor.setPower(0);
        armControlServo.setPower(0);
    }

    @JoystickListener(joystick = "wheels")
    protected void drive(float x, float y) {
        wheels.move(Vector.fromPolar(x, y));
    }
    
    public DcMotor getDcMotor(String name) {
    	return hardwareMap.dcMotor.get(name);
    }
    
    public Servo getServo(String name) {
    	return hardwareMap.servo.get(name);
    }
    
    public CRServo getCRServo(String name) {
    	return hardwareMap.crservo.get(name);
    }
}
