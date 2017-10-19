package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Servo;

import org.firstinspires.ftc.teamcode.autonomous.controllers.MotionController;
import org.firstinspires.ftc.teamcode.input.Button;
import org.firstinspires.ftc.teamcode.input.ButtonManager;
import org.firstinspires.ftc.teamcode.interfaces.IWheels;
import org.firstinspires.ftc.teamcode.models.Position;
import org.firstinspires.ftc.teamcode.models.Vector;
import org.firstinspires.ftc.teamcode.output.Message;
import org.firstinspires.ftc.teamcode.output.Telemetry;

@TeleOp(name="TeleOp", group="TeleOp")
public class RobotTeleOp extends LinearOpMode {
    protected Telemetry telemetry = new Telemetry(super.telemetry);

    //Add all global objects and lists
    protected ButtonManager buttons = new ButtonManager();

    //Lift Vars
    protected boolean autoMainLiftRunning = false;
    protected int lift_position;


    //Add Motors, Servos, Sensors, etc here
    //EX: protected DcMotor motor;
    protected Servo claw;
    //protected DcMotor arm;
    protected DcMotor armMotor;
    protected CRServo armControlServo;
    boolean armManual = false;
    //
    //Motors for each wheel
    protected DcMotor leftFront;
    protected DcMotor rightFront;
    protected DcMotor leftBack;
    protected DcMotor rightBack;

    //Lift Objects
    protected DcMotor mainLift;
    protected Servo smallLift;

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
    public void runOpMode() {
        setupHardware();
        setupButtons();
        //Add any further initialization (methods) here

        waitForStart();

        telemetry.add("Lift Position", new Message.IMessageData() {
            @Override
            public String getMessage() {
                return mainLift.toString();
            }
        });

        while (opModeIsActive()) {
            buttons.update();

            lift();

            lift_position = mainLift.getCurrentPosition();

            //Arm extension part
            //armExtension();

            claw();
            drive();

            telemetry.update();
            idle();
        }
    }

    protected void setupHardware() {
        mainLift = hardwareMap.dcMotor.get("mainLift");
        mainLift.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        mainLift.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        smallLift = hardwareMap.servo.get("smallLift");

        claw = hardwareMap.servo.get("claw");
        //claw.setDirection(DcMotor.Direction.FORWARD);
        armControlServo = hardwareMap.crservo.get("armControlServo");
        armMotor = hardwareMap.dcMotor.get("armMotor");
        armMotor.setDirection(DcMotor.Direction.FORWARD);

        leftFront = hardwareMap.dcMotor.get("leftFront");
        rightFront = hardwareMap.dcMotor.get("rightFront");
        leftBack = hardwareMap.dcMotor.get("leftBack");
        rightBack = hardwareMap.dcMotor.get("rightBack");

        leftFront.setDirection(DcMotor.Direction.REVERSE);
        leftBack.setDirection(DcMotor.Direction.REVERSE);

        // TODO: add IWheels implementation
        wheels = new MotionController(null, new Position(new Vector(0, 0), 0));
    }

    protected void setupButtons() {
        buttons.add(new Button() {
            @Override
            public boolean isInputPressed() {
                return gamepad2.dpad_up;
            }

            @Override
            public void onPress() {
                calculateTargetPositionUP();
                mainLift.setMode(DcMotor.RunMode.RUN_TO_POSITION);
                mainLift.setPower(MAIN_LIFT_SPEED);
                autoMainLiftRunning = true;
            }
        });

        buttons.add(new Button() {
            @Override
            public boolean isInputPressed() {
                return gamepad2.dpad_down;
            }

            @Override
            public void onPress() {
                calculateTargetPositionDOWN();
                mainLift.setMode(DcMotor.RunMode.RUN_TO_POSITION);
                mainLift.setPower(-MAIN_LIFT_SPEED);
                autoMainLiftRunning = true;
            }
        });

        buttons.add(new Button() {
            @Override
            public boolean isInputPressed() {
                return gamepad2.right_trigger > 0 || gamepad2.left_trigger > 0;
            }

            @Override
            public void onPress() {
                float motor_power = gamepad2.right_trigger - gamepad2.left_trigger;
                if((motor_power > 0 && mainLift.getCurrentPosition() >= COUNT_PER_GLYPH_HEIGHT * 4)
                        || motor_power < 0 && mainLift.getCurrentPosition() <= 0)
                    return;
                mainLift.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
                mainLift.setPower(motor_power);
                autoMainLiftRunning = false;
            }

            @Override
            public void onRelease() {
                mainLift.setPower(0);
            }
        });

        buttons.add(new Button() {
            @Override
            public boolean isInputPressed() {
                return Math.abs(gamepad2.right_stick_x) - JOYSTICK_ERROR_RANGE > 0;
            }

            @Override
            public void onPress() {
                if (gamepad2.right_stick_x > 0 && claw.getPosition() < 1)
                    claw.setPosition(claw.getPosition() + 0.1);
                if (gamepad2.right_stick_x < 0 && claw.getPosition() <= 0)
                    claw.setPosition(claw.getPosition() - 0.1);
            }
        });

        buttons.add(new Button() {
            @Override
            public boolean isInputPressed() {
                return armManual;
            }

            @Override
            public void onPress() {
                manualArmControl();
            }

            @Override
            public void onRelease() {
                automaticArmControl();
            }
        });

        buttons.add(new Button() {
            @Override
            public boolean isInputPressed() {
                return gamepad1.x;
            }

            @Override
            public void onPress() {
                armMotor.setPower(ARM_MOTOR_POWER);
                armControlServo.setPower(ARM_SERVO_POWER);
            }

            @Override
            public void onRelease() {
                armMotor.setPower(0);
                armControlServo.setPower(0);
            }
        });

        buttons.add(new Button() {
            @Override
            public boolean isInputPressed() {
                return gamepad1.y;
            }

            @Override
            public void onPress() {
                armMotor.setPower(-ARM_MOTOR_POWER);
                armControlServo.setPower(-ARM_SERVO_POWER);
            }

            @Override
            public void onRelease() {
                armMotor.setPower(0);
                armControlServo.setPower(0);
            }
        });
    }

    protected void drive() {
        wheels.move(Vector.fromPolar(gamepad1.right_stick_y, gamepad1.right_stick_x));
    }

    //Add new methods for functionality down here

    //Sets new position for main life when using the up d pad by using the current position to figure out what height marker it is inbetween.
    //Finds the next Lift's Target Position when going up
    protected void calculateTargetPositionUP() {
        lift_position = mainLift.getCurrentPosition();
        int glyphRow = lift_position / COUNT_PER_GLYPH_HEIGHT;

        int errorOver = lift_position % COUNT_PER_GLYPH_HEIGHT;
        int errorUnder = COUNT_PER_GLYPH_HEIGHT - errorOver;

        int nextPosition = COUNT_PER_GLYPH_HEIGHT * (glyphRow + (errorUnder < MAIN_LIFT_ERROR_RANGE ? 2 : 1));
        if (nextPosition > COUNT_PER_GLYPH_HEIGHT * 4)
            mainLift.setTargetPosition(COUNT_PER_GLYPH_HEIGHT * 4);
        else
            mainLift.setTargetPosition(nextPosition);
    }

    //Finds the next Lift's Target Position when going down
    protected void calculateTargetPositionDOWN() {
        lift_position = mainLift.getCurrentPosition();
        int glyphRow = lift_position / COUNT_PER_GLYPH_HEIGHT;

        int errorOver = lift_position % COUNT_PER_GLYPH_HEIGHT;
        //int errorUnder = COUNT_PER_GLYPH_HEIGHT - errorOver;

        int nextPosition = COUNT_PER_GLYPH_HEIGHT * (glyphRow - (errorOver < MAIN_LIFT_ERROR_RANGE ? 1 : 0));
        if (nextPosition < 0 )
            mainLift.setTargetPosition(0);
        else
            mainLift.setTargetPosition(nextPosition);
    }

    protected void lift() {
        // TODO: I don't know what this does, it may need refactoring.
        //D Pad used to control Main Lift (Added as buttons), stops here
        if(!(mainLift.isBusy() && autoMainLiftRunning)){
            mainLift.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
            mainLift.setPower(0);
            autoMainLiftRunning = false;
        }
    }

    //The second driver controls how fast both the continuous servo and motor runs.  Extends and retracts arm.
    //left stick = continuous servo.
    //right stick = motor
    //Forward = extend, backwards = retract
    protected void manualArmControl()
    {
        if(gamepad2.left_stick_y>JOYSTICK_ERROR_RANGE || gamepad2.left_stick_y<-JOYSTICK_ERROR_RANGE)
        {
            armControlServo.setPower(gamepad2.left_stick_x);
        }
        else
        {
            armControlServo.setPower(0);
        }
        if(gamepad2.right_stick_y>JOYSTICK_ERROR_RANGE || gamepad2.right_stick_y<-JOYSTICK_ERROR_RANGE)
        {
            armMotor.setPower(gamepad2.right_stick_y);
        }
        else
        {
            armMotor.setPower(0);
        }
    }
}
