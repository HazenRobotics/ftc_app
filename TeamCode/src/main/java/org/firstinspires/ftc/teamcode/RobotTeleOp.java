package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Servo;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

import org.firstinspires.ftc.teamcode.autonomous.controllers.MotionController;
import org.firstinspires.ftc.teamcode.models.Position;
import org.firstinspires.ftc.teamcode.models.Vector;
import org.firstinspires.ftc.teamcode.output.Message;

import co.lijero.react.ReactionManager;
import co.lijero.react.Reactive;

@TeleOp(name="TeleOp", group="TeleOp")
public class RobotTeleOp extends OpMode {
    protected ReactionManager reactions = new ReactionManager();
    protected Telemetry telemetry = new Telemetry(super.telemetry, reactions);

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
        setupTrackers();
    }
    
    @Override
    public void loop() {
    	reactions.update();
        telemetry.update();
    }

    // This may be excessive. Just FYI it has nothing to do with @Reactive.
    // This is the actual implementation of @Hardware.
    protected void initializeVariables() {
    	// This related the name of a class to the hardwareMap field equivalent.
    	// e.g. the class is called DcMotor, but to get a DcMotor it's hardwareMap.dcMotor.get(...)
    	// as opposed to hardwareMap.DcMotor.get(...)
        Map<String, String> hardwareNameMap = new HashMap<>();
        hardwareNameMap.put("DcMotor", "dcMotor");
        hardwareNameMap.put("Servo", "servo");
        hardwareNameMap.put("CRServo", "crservo");

        try {
        	// To clarify: a field is just a variable in a class, e.g. frontLeft, reactions, wheels, LIFT_COUNTS_PER_INCH
        	// We're going to get every field in teleop
            for (Field field : getClass().getFields()) {
            	// We're checking if each field has the @Hardware annotation.
            	// We're using != null because there is no hasAnnotation(): it just returns null if the annotation doesn't exist
                if (field.getAnnotation(Hardware.class) != null) {
                	// We're using the hardwareNameMap described above to figure out how to retrieve the value based on the class name.
                	// This is e.g. hardwareMap.dcMotor or hardwareMap.servo.
                    String hardwareMapFieldName = hardwareNameMap.get(field.getType().getSimpleName());
                    // This is the actual value of the hardwareMap.hardwareMapFieldName.
                    Object hardwareMapField = hardwareMap.getClass().getField(hardwareMapFieldName).get(hardwareMap);
                    // We now get the actual hardware instance.
                    Object hardwareObject = hardwareMapField.getClass().getMethod("get").invoke(hardwareMapField);
                    // Initialize the variable with that value.
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
        // This branch doesn't have motion_implementation stuff yet
        wheels = new MotionController(null, new Position(new Vector(0, 0), 0));
    }
    
    protected void setupTelemetry() {
    	// We display the reactive value liftPosition to the telemetry
    	telemetry.track("Lift Position", "liftPosition");
    }

    /** The joystick position tracking isn't perfect. This rounds down small values to prevent slow drift. */
    private static float adjustJoystickPosition(float position) {
        return position < JOYSTICK_ERROR_RANGE ? 0 : position;
    }

    /** Tracks a bunch of variables in the Reactive system. */
    protected void setupTrackers() {
    	/*
    	 * This is where the reaction system starts: what the reactive system does does in fact
    	 * have a name-- the reactive programming paradigm. But that's not important right now.
    	 * 
    	 * Conceptually, this is pretty abstract, but it'll make a lot more sense once you see
    	 * how it's used. Just bear with me, okay?
    	 * 
    	 * In normal programming, a variable contains a value, e.g.
    	 * 	 x = 0
    	 * but in reactive programming, a variable tracks a value /over time/. This means that a
    	 * variable doesn't store a value, but how you obtain that value. Here's an example:
    	 *   x = 3;
    	 *   y = x * 2;
    	 *   x = 5;
    	 * In normal programming, y = 6. In reactive programming, y = 10. Y is not a value of x
    	 * times 2, it is DEFINED AS x * 2. No matter what x is, y will always be x * 2. It
    	 * works a lot like how math does, essentially, at least in terms of domain constraints.
    	 * 
    	 * From here, the actual usage will explain it to you.
    	 */
    	
    	/*
    	 * Here, we are reactively saying
    	 *    liftUp = gamepad2.dpad_up
    	 *  which is important to remember means "liftUp is defined as gamepad2.dpad_up", not
    	 *  "liftUp is the current value of gamepad2.dpad_up".
    	 *  
    	 *  Let's break the line down a bit.
    	 *  
    	 *  reactions: reactions stores all of the variables in the system.
    	 *  make: we are now making a new variable-- this is like the =
    	 *  name: gives the name of the variable
    	 *  onObject: we are getting a field from gamepad2
    	 *		 field: a field is just a variable in a class, e.g. frontLeft, reactions, wheels, LIFT_COUNTS_PER_INCH
    	 *	get: we are getting a field
    	 *	finish: once we have finished making our reaction, we run this to add it to the reactive system.
    	 *
    	 * we can't just write in gamepad2.dpad_up, or Java would just interpret it as the current
    	 *  	value of dpad_up! We have to write it in some other way, and this is how we do it-- break it into
    	 *  	gamepad2 (onObject) and the field (get).
    	 *  
    	 *  So from now on, liftUp will always be whatever value gamepad2.dpad_up is.
    	 */
    	// Lift automatic controls
    	reactions.make().name("liftUp").onObject(gamepad2).get("dpad_up").finish();
    	reactions.make().name("liftDown").onObject(gamepad2).get("dpad_down").finish();
        /*
         * This is new. Here, we're saying
         * 		liftDirection = ArmDirection.fromButtons(liftUp, liftDown)
         *
         * There are clearer ways to write this same thing, but this makes more sense since it's
         * shorter, and we're going to be doing this a lot. We'll get to the other ways later.
         * 
         * onClass: we're using this instead of onObject because ArmDirection.fromButtons is static, so it
         * doesn't make sense to give it something to call fromButtons on!
         * 
         * run: now instead of getting a field, we're making a method call. We're calling ArmDirection.fromButtons
         * on the values of liftUp and liftDown-- the first parameter is the method name, and the rest of it is
         * the values passed into the method.
         * 
         * We can't write ArmDirection::fromButtons, because what do you think this is, Java 8?
         */
    	reactions.make().name("liftDirection").onClass(MotionType.class).run("fromButtons", "liftUp", "liftDown").finish();
        
        // Lift manual controls
    	reactions.make().name("liftPowerUp").onObject(gamepad2).get("right_trigger").finish();
    	reactions.make().name("liftPowerDown").onObject(gamepad2).get("left_trigger").finish();

        // Claw controls
    	reactions.make().name("clawPower").onObject(gamepad2).get("right_stick_x").finish();
    	reactions.make().name("clawPosition").onObject(claw).run("getPosition").finish();
    	reactions.make().name("clawDirection").onClass(MotionType.class).run("fromSign", "clawPower").finish();
        
        // Arm controls
    	reactions.make().name("armExtend").onObject(gamepad1).get("x").finish();
    	reactions.make().name("armRetract").onObject(gamepad1).get("y").finish();
    	reactions.make().name("armDirection").onClass(MotionType.class).run("fromButtons", "armExtend", "armRetract").finish();

        // Movement controls
        registerJoystick(gamepad1, "right_stick", "wheelPowerX", "wheelPowerY");

        /*
         * We're taking all of the reactive variables defined in this class and adding them to the system as well.
         */
    	reactions.register(this);
    }
    
    /**
     * This makes registering joysticks easier by automating the gamepad access and adjustJoystickValue conversions.
     * 
     * @param base_name The base name of the joystick, e.g. right_stick for the values gamepad.right_stick_x and right_stick_y
     */
    protected void registerJoystick(Object gamepad, String base_name, String x_name, String y_name) {
    	String raw_x_name = "__raw_" + base_name + "_x";
    	String raw_y_name = "__raw_" + base_name + "_y";
    	reactions.make().name(raw_x_name).onObject(gamepad).get(base_name + "_x").finish();
    	reactions.make().name(raw_y_name).onObject(gamepad).get(base_name + "_y").finish();
    	reactions.make().name(x_name).onClass(getClass()).run("adjustJoystickValue", raw_x_name).finish();
    	reactions.make().name(y_name).onClass(getClass()).run("adjustJoystickValue", raw_y_name).finish();
    }
    
    /*
     * Now we're getting to why it matters.
     * 
     * @Reactive means that this is NOT A METHOD, it is a VARIABLE.
     * Can you think of why?
     * 
     * If you were attentive earlier, you may have made the connection that, wait a minute, variables aren't
     * values, they're definitions of a value? Isn't that just a function? Well yes, it is! This function
     * tells us that
     * 		glyphRow = liftPosition / COUNT_PER_GLYPH_HEIGHT
     * .
     * 
     * Breakdown:
     * 
     * @Reactive: This is a variable in the reactive system
     * 		name = "asad": While we do not use it here, you can set the name of the variable in the reactive system
     * 			to be something other than the method name. We're not doing that here, so it's just called glyphRow.
     * 		depends = "liftPosition": This function has parameters. We're just telling the reactive system that the
     * 			parameter passed in should be the one called "liftPosition". I actually wanted this to be
     * 			automatic too, just like the variable name is, but it's not possible because Java doesn't keep
     * 			parameter names at runtime unless you have debug symbols enabled, and I wouldn't want to force that.
     *			I could technically do it with a compiler plugin but hahaha no.
     * 
     * Actually, the rest I hope is obvious.
     */
    /** Calculates the row that the lift is currently closest to. */
    @Reactive(depends = "liftPosition")
    public int glyphRow(int liftPosition) {
        return liftPosition / COUNT_PER_GLYPH_HEIGHT;
    }

    /*
     * This is the same thing, only we're now using two variables.
     */
    /** Decides what row the lift will travel to next. */
    @Reactive(depends = { "liftDirection", "glyphRow" })
    public int nextGlyphRow(MotionType liftDirection, int glyphRow) {
        switch(liftDirection) {
            case EXTEND:
                return Math.min(glyphRow + 1, 3);
            case RETURN:
                return Math.max(glyphRow - 1, 0);
            default:
				return glyphRow;
        }
    }

    /*
     * Now we're actually using the values: the lift motors are now always
     * up-to-date with the gamepad controls!
     */
    @Reactive(depends = "nextGlyphRow")
    public void runLiftAutomatic(int liftPosition, int nextGlyphRow) {
        int distanceToMove = nextGlyphRow * COUNT_PER_GLYPH_HEIGHT;
        lift.setTargetPosition(distanceToMove);
        lift.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        lift.setPower(MAIN_LIFT_SPEED);
    }
    
    @Reactive(depends = { "liftPowerUp", "liftPowerDown" })
    public double liftPower(float liftPowerUp, float liftPowerDown) {
        return liftPowerUp - liftPowerDown;
    }

    @Reactive(depends = { "liftPower", "liftPosition" })
    public void runLiftManual(float liftPower, float liftPosition) {
        if((liftPower > 0 && liftPosition >= COUNT_PER_GLYPH_HEIGHT * 4)
                || liftPower < 0 && liftPosition <= 0)
            return;
        lift.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        lift.setPower(liftPower);
    }

    @Reactive(depends = "armDirection")
    public void runArm(MotionType direction) {
        armMotor.setPower(direction.toSign() * ARM_MOTOR_POWER);
        armControlServo.setPower(direction.toSign() * ARM_SERVO_POWER);
    }
    
    /*
     * Remember that registerConversion thing earlier?
     * This is the long way of doing that. I could actually still do that,
     * but it makes more sense to keep all of this stuff grouped together.
     */
    @Reactive(depends = "clawDirection")
    public float clawMovement(MotionType clawDirection) {
    	return clawDirection.toSign() * 0.1f;
    }
    
    @Reactive(depends = { "clawPosition", "clawMovement" })
    public void runClaw(float clawPosition, float clawMovement) {
    	claw.setPosition(clawPosition + clawMovement);
    }

    @Reactive(depends = { "wheelPowerX", "wheelPowerY" })
    protected void withWheels(float wheelPowerX, float wheelPowerY) {
        wheels.move(Vector.fromPolar(wheelPowerX, wheelPowerY));
    }
}
