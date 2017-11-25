package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.DigitalChannel;
import com.qualcomm.robotcore.hardware.HardwareDevice;
import com.qualcomm.robotcore.hardware.I2cDevice;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.teamcode.input.Button;
import org.firstinspires.ftc.teamcode.input.ButtonManager;
import org.firstinspires.ftc.teamcode.interfaces.IHardware;
import org.firstinspires.ftc.teamcode.input.Toggle;

/**
 * Created by Alex on 9/23/2017.
 */

@TeleOp(name="TeleOp", group="TeleOp")
public class RobotTeleOp extends LinearOpMode implements IHardware {

    //Add all global objects and lists
    protected ButtonManager buttons = new ButtonManager();

    //Lift Vars
    protected boolean autoMainLiftRunning = false;
    protected int lift_position;

    //Add Motors, Servos, Sensors, etc here
    //EX: protected DcMotor motor;

    //Claw and Arm Objects
    protected DcMotor armMotor;
    protected boolean armManual = true;
    protected DcMotor claw;

    //Wheel Motors
    protected DcMotor leftFront;
    protected DcMotor rightFront;
    protected DcMotor leftBack;
    protected DcMotor rightBack;

    //Lift Objects
    protected DcMotor mainLift;

    //Add all Constants here
    //EX: protected final double MOTOR_POWER = 0.5;
    protected final double ARM_MOTOR_POWER = 0.4;
    protected final double CLAW_POWER = 0.2;
    protected final double LIFT_POWER = 0.3;
    protected final double JOYSTICK_ERROR_RANGE = 0.1;

    //Lift Constants
    protected static final double GLYPH_HEIGHT = 0.0; //TODO: Insert Glyph Height Here
    protected static final int LIFT_COUNTS_PER_MOTOR_REV = 1440 ;    // TODO: eg: TETRIX Motor Encoder
    protected static final double LIFT_INCHES_PER_REV = 0.0;// TODO: Dont know yet
    protected static final int LIFT_COUNTS_PER_INCH = (int) (LIFT_COUNTS_PER_MOTOR_REV / LIFT_INCHES_PER_REV);
    protected static final int COUNT_PER_GLYPH_HEIGHT = (int) (GLYPH_HEIGHT * LIFT_COUNTS_PER_INCH);
    protected static final double MAIN_LIFT_SPEED = 0.5;
    protected static final int MAIN_LIFT_ERROR_RANGE = 20;


    @Override
    public void runOpMode() {

        setupHardware();
        setupButtons();
        //Add any further initialization (methods) here

        waitForStart();

        while (opModeIsActive()) {
            buttons.update();
            //Add any non-toggles here

            claw();
            arm();
            //lift();
            lift2();
            drive();

            telemetry.update();
            idle();
        }
    }



    protected void setupHardware() {
        //Initializes the motor/servo variables here
        /*EX:
        motor = getMotor("motor");
        motor.setDirection(DcMotor.Direction.FORWARD);*/
        mainLift = getMotor("lift");
        mainLift.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        mainLift.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

        claw = getMotor("claw");
        claw.setDirection(DcMotor.Direction.FORWARD);
        armMotor = getMotor("arm");
        armMotor.setDirection(DcMotor.Direction.FORWARD);

        leftFront = getMotor("leftFront");
        rightFront = getMotor("rightFront");
        leftBack = getMotor("leftBack");
        rightBack = getMotor("rightBack");

        leftFront.setDirection(DcMotorSimple.Direction.REVERSE);
        leftBack.setDirection(DcMotorSimple.Direction.REVERSE);
        rightFront.setDirection(DcMotorSimple.Direction.FORWARD);
        rightBack.setDirection(DcMotorSimple.Direction.FORWARD);
    }

    //claw function, run by servo
    protected void setupButtons() {

        /*buttons.add(new Button() {
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
        });*/

        //when b is pressed once, changes mode of the arm to manual.  Pressed again, arm automatic.
        buttons.add(new Toggle() {
            @Override
            public boolean isInputPressed() {
                return gamepad2.b;
            }

            @Override
            public void onActivate() {
                armManual = false;
            }

            @Override
            public void onDeactivate() {
                armManual = true;
            }
        });
    }

    //when claw has reached the correct position or moved open long enough, the claw stops moving.
    protected void claw() {
        if(gamepad2.dpad_left){
            claw.setPower(-CLAW_POWER);
        }
        else if(gamepad2.dpad_right){
            claw.setPower(CLAW_POWER);
        }
        else {
            claw.setPower(0.0);
        }
    }

    protected void drive() {

        //left stick controls movement
        //right stick controls turning

        double turn_x = gamepad1.right_stick_x; //stick that determines how far robot is turning
        double magnitude = Math.abs(gamepad1.left_stick_y) + Math.abs(gamepad1.left_stick_x) + Math.abs(turn_x); //Used to determine the greatest possible value of y +/- x to scale them
        double scale = Math.max(1, magnitude); //Used to prevent setting motor to power over 1
        double x = gamepad1.left_stick_x;
        double y = -gamepad1.right_stick_y;

        double leftFrontPower = (y + x + turn_x) / scale;
        double rightFrontPower = (y - x - turn_x) / scale;
        double leftBackPower = (y - x + turn_x) / scale;
        double rightBackPower = (y + x - turn_x) / scale;

        //setting power for each of the 4 wheels
        leftFront.setPower(leftFrontPower);
        rightFront.setPower(rightFrontPower);
        leftBack.setPower(leftBackPower);
        rightBack.setPower(rightBackPower);
    }

    //Add new methods for functionality down here

    //Sets new position for main life when using the up d pad by using the current position to figure out what height marker it is in between.
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
        //Main Lift Power using Triggers
        if((gamepad2.right_trigger > 0) && (gamepad2.left_trigger == 0) && (mainLift.getCurrentPosition() < COUNT_PER_GLYPH_HEIGHT * 4)) {
            mainLift.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
            mainLift.setPower(gamepad2.right_trigger);
            autoMainLiftRunning = false;
        }
        else if((gamepad2.right_trigger == 0) && (gamepad2.left_trigger > 0) && (mainLift.getCurrentPosition() > 0)){
            mainLift.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
            mainLift.setPower(-gamepad2.left_trigger);
            autoMainLiftRunning = false;
        }
        else if (!autoMainLiftRunning){
            mainLift.setPower(0.0);
        }

        //D Pad used to control Main Lift (Added as buttons), stops here
        if(!mainLift.isBusy() && autoMainLiftRunning){
            mainLift.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
            mainLift.setPower(0);
            autoMainLiftRunning = false;
        }

        telemetry.addData("Right Trigger >", gamepad2.right_trigger);
        // Debugs to show the motor position
        lift_position = mainLift.getCurrentPosition();
        telemetry.addData("Lift position", "MainLift Position:" + lift_position);
    }

    //backup code for lift, currently not in use
    protected void lift2() {
        if(gamepad2.dpad_up){
            mainLift.setPower(LIFT_POWER);
        }
        else if(gamepad2.dpad_down){
            mainLift.setPower(-LIFT_POWER);
        }
        else{
            mainLift.setPower(0.0);
        }
    }



    protected void arm() {
        if(armManual)
            manualArmControl();
        else
            automaticArmControl();
    }

    //When x is pressed, arm extends.  When b is pressed, arm retracts.
    protected void automaticArmControl()
    {
        if(gamepad2.a)
        {
            armMotor.setPower(ARM_MOTOR_POWER);
        }
        else if(gamepad2.y)
        {
            armMotor.setPower(-ARM_MOTOR_POWER);
        }
        else
        {
            armMotor.setPower(0);
        }
    }

    //The second driver controls how fast both the continuous servo and motor runs.  Extends and retracts arm.
    //left stick = continuous servo.
    //right stick = motor
    //Forward = extend, backwards = retract
    protected void manualArmControl()
    {
        if(gamepad2.right_stick_y > JOYSTICK_ERROR_RANGE || gamepad2.right_stick_y < -JOYSTICK_ERROR_RANGE)
        {
            armMotor.setPower(gamepad2.right_stick_y);
            telemetry.addData("Right Stick Power: ",gamepad2.right_stick_y);
            telemetry.update();
        }
        else
        {
            armMotor.setPower(0);
        }
    }

    @Override
    public void idle(long milliseconds) {
        // This is probably the wrong way to handle this-- spin loop.
        // However, it's better than Thread.idleFor()-- probably.
        long endTime = System.currentTimeMillis() + milliseconds;
        while(System.currentTimeMillis() < endTime && opModeIsActive()) {
            telemetry.update();
            idle();
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
