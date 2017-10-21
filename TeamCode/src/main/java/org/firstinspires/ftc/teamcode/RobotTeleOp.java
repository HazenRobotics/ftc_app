package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Servo;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

import org.firstinspires.ftc.teamcode.autonomous.ArmDirection;
import org.firstinspires.ftc.teamcode.autonomous.controllers.MotionController;
import org.firstinspires.ftc.teamcode.models.Position;
import org.firstinspires.ftc.teamcode.models.Vector;
import org.firstinspires.ftc.teamcode.output.Message;
import org.firstinspires.ftc.teamcode.output.Telemetry;

import co.lijero.react.ReactionManager;
import co.lijero.react.Reactive;

@TeleOp(name="TeleOp", group="TeleOp")
public class RobotTeleOp extends OpMode {
    protected Telemetry telemetry = new Telemetry(super.telemetry);
    protected ReactionManager reactions = new ReactionManager();

    // The arm components
    @Hardware
    protected Servo claw;
    @Hardware
    protected DcMotor armMotor;
    @Hardware
    protected CRServo armControlServo;

    // Motors for each wheel
    @Hardware
    protected DcMotor leftFront;
    @Hardware
    protected DcMotor rightFront;
    @Hardware
    protected DcMotor leftBack;
    @Hardware
    protected DcMotor rightBack;

    // The lift motor
    @Hardware
    protected DcMotor lift;

    //Add all Constants here
    protected static final double ARM_MOTOR_POWER = 0.4;
    protected static final double ARM_SERVO_POWER = 0.4;
    protected static final double JOYSTICK_ERROR_RANGE = 0.1;

    //Lift Constants
    protected static final double GLYPH_HEIGHT = 0.0; //Insert Glyph Height Here
    protected static final int LIFT_COUNTS_PER_MOTOR_REV = 1440 ;    // eg: TETRIX Motor Encoder
    protected static final double LIFT_GEAR_REDUCTION = 2.0 ;     // This is < 1.0 if geared UP
    protected static final double LIFT_INCHES_PER_REV = 0.0;// Dont know yet
    protected static final int LIFT_COUNTS_PER_INCH = (int) (LIFT_COUNTS_PER_MOTOR_REV / LIFT_INCHES_PER_REV);
    protected static final int COUNT_PER_GLYPH_HEIGHT = (int) (GLYPH_HEIGHT * LIFT_COUNTS_PER_INCH);
    protected static final double MAIN_LIFT_SPEED = 0.5;

    protected MotionController wheels;
    
    @Override
    public void init() {
    	setupHardware();
        setupTelemetry();
        setupButtons();
    }
    
    @Override
    public void loop() {
    	reactions.update();
        telemetry.update();
    }

    protected void initializeVariables() {
        Map<String, String> hardwareNameMap = new HashMap<>();
        hardwareNameMap.put("DcMotor", "dcMotor");
        hardwareNameMap.put("Servo", "servo");
        hardwareNameMap.put("CRServo", "crservo");

        try {
            for (Field field : getClass().getFields()) {
                if (field.getAnnotation(Hardware.class) != null) {
                    String hardwareMapFieldName = hardwareNameMap.get(field.getType().getSimpleName());
                    Object hardwareMapField = hardwareMap.getClass().getField(hardwareMapFieldName).get(hardwareMap);
                    Object hardwareObject = hardwareMapField.getClass().getMethod("get").invoke(hardwareMapField);
                    field.set(this, hardwareObject);
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    protected void setupHardware() {
        initializeVariables();

        lift.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        lift.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

        armMotor.setDirection(DcMotor.Direction.FORWARD);

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

    @Reactive(depends = { "liftPowerUp", "liftPowerDown" })
    public double liftPower(float liftPowerUp, float liftPowerDown) {
        return liftPowerUp - liftPowerDown;
    }

    private static float adjustJoystickPosition(float position) {
        return position < JOYSTICK_ERROR_RANGE ? 0 : position;
    }

    @Reactive(depends = "liftPosition")
    public int glyphRow(int liftPosition) {
        return liftPosition / COUNT_PER_GLYPH_HEIGHT;
    }

    protected void setupButtons() {
        reactions.registerField("liftUp", gamepad2, "dpad_up");
        reactions.registerField("liftDown", gamepad2, "dpad_down");
        reactions.registerStaticMethod("liftDirection", "fromButtons", ArmDirection.class, "liftUp", "liftDown");
    	// TODO: WARNING: THIS CONFLICTS WITH THE ABOVE: MANUAL VS AUTOMATIC. PICK ONE.
    	// Right now, the manual controls below override the automatic controls when it's active.
        reactions.registerField("liftPowerUp", gamepad2, "right_trigger");
        reactions.registerField("liftPowerDown", gamepad2, "left_trigger");
        reactions.registerMethod("liftPosition", lift, "getCurrentPosition");

        reactions.registerField("clawPower", gamepad2, "right_stick_x");
        reactions.registerMethod("clawPosition", claw, "getCurrentPosition");
        reactions.registerStaticMethod("clawDirection", "fromSign", ArmDirection.class, "clawPower");
        reactions.registerField("armExtend", gamepad1, "x");
        reactions.registerField("armRetract", gamepad1, "y");
        reactions.registerStaticMethod("armDirection", "fromButtons", ArmDirection.class, "armExtend", "armRetract");

        reactions.registerField("rawWheelPowerX", gamepad1, "right_stick_x");
    	reactions.registerField("rawWheelPowerY", gamepad1, "right_stick_y");
        reactions.registerStaticMethod("wheelPowerX", "adjustJoystickValue", getClass(), "rawWheelPowerX");
        reactions.registerStaticMethod("wheelPowerY", "adjustJoystickValue", getClass(), "rawWheelPowerY");

    	reactions.register(this);
    }

    @Reactive(depends = { "liftDirection", "glyphRow" })
    public int nextGlyphRow(ArmDirection liftDirection, int glyphRow) {
        switch(liftDirection) {
            case EXTEND:
                return Math.min(glyphRow + 1, 3);
            case RETRACT:
                return Math.max(glyphRow - 1, 0);
        }
        return glyphRow;
    }

    @Reactive(depends = { "liftPosition", "nextGlyphRow" })
    public void withNextGlyphRow(int liftPosition, int nextGlyphRow) {
        int destinationLiftPosition = nextGlyphRow * COUNT_PER_GLYPH_HEIGHT;
        int distanceToMove = destinationLiftPosition - liftPosition;
        lift.setTargetPosition(distanceToMove);
        lift.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        lift.setPower(MAIN_LIFT_SPEED);
    }

    @Reactive(depends = { "liftPower", "liftPosition" })
    public void withLiftPower(float liftPower, float liftPosition) {
        if((liftPower > 0 && liftPosition >= COUNT_PER_GLYPH_HEIGHT * 4)
                || liftPower < 0 && liftPosition <= 0)
            return;
        lift.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        lift.setPower(liftPower);
    }

    @Reactive(depends = "armDirection")
    public void withArmDirection(ArmDirection direction) {
        armMotor.setPower(direction.toSign() * ARM_MOTOR_POWER);
        armControlServo.setPower(direction.toSign() * ARM_SERVO_POWER);
    }
    
    @Reactive(depends = { "clawPosition", "clawDirection" })
    public void withClawPower(float clawPosition, ArmDirection clawDirection) {
        switch(clawDirection) {
            case EXTEND:
                claw.setPosition(clawPosition + 0.1f);
            case RETRACT:
                claw.setPosition(clawPosition - 0.1f);
        }
    }

    @Reactive(depends = { "wheelPowerX", "wheelPowerY" })
    protected void withWheels(float wheelPowerX, float wheelPowerY) {
        wheels.move(Vector.fromPolar(wheelPowerX, wheelPowerY));
    }
}
