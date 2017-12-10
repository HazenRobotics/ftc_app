package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.DigitalChannel;
import com.qualcomm.robotcore.hardware.HardwareDevice;
import com.qualcomm.robotcore.hardware.Servo;

import org.firstinspires.ftc.teamcode.input.Button;
import org.firstinspires.ftc.teamcode.input.ButtonManager;
import org.firstinspires.ftc.teamcode.interfaces.IHardware;
import org.firstinspires.ftc.teamcode.interfaces.motors.MechanamMotors;
import org.firstinspires.ftc.teamcode.models.Condition;

import com.qualcomm.hardware.modernrobotics.ModernRoboticsI2cGyro;


/**
 * Created by Alex on 9/23/2017.
 */

@TeleOp(name="CompTeleOp", group="TeleOp")
public class RobotTeleOp extends LinearOpMode implements IHardware {

    //Add Motors, Servos, Sensors, etc here
    // EX: protected DcMotor motor;

    protected ButtonManager buttons = new ButtonManager();

    //Claw and Arm Objects
    protected DcMotor armMotor;
    protected DcMotor claw;

    //Wheel Motors
    protected DcMotor leftFront;
    protected DcMotor rightFront;
    protected DcMotor leftBack;
    protected DcMotor rightBack;

    //Lift Objects
    protected DcMotor lift;

    //Flicker Objects
    protected Servo flicker;

    //Sensors
    protected ModernRoboticsI2cGyro gyro;

    //Add all Constants here
    //EX: protected final double MOTOR_POWER = 0.5;
    protected final static double CLAW_POWER = 0.4;
    protected final static double LIFT_POWER = 0.3;
    protected final static double JOYSTICK_ERROR_RANGE = 0.05;
    protected final  static int AUTO_TURN_AMOUNT = 90;


    protected IHardware hardware;
    protected MechanamMotors motion;

    @Override
    public void runOpMode() {

        setupHardware();
        setupButtons();
        //Add any further initialization (methods) here

        waitForStart();

        while (opModeIsActive()) {
            buttons.update();
            claw();
            arm();
            lift();
            drive();
            idle();
        }
    }


    protected void setupHardware() {
        //Initializes the motor/servo variables here
        /*EX:
        motor = getMotor("motor");
        motor.setDirection(DcMotor.Direction.FORWARD);*/
        lift = getMotor("lift");
        lift.setDirection(DcMotor.Direction.FORWARD);

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

        this.hardware = this;
        this.motion = new MechanamMotors(hardware);

        flicker = getServo("flicker");
        flicker.setDirection(Servo.Direction.REVERSE);
        flicker.setPosition(0);

        gyro.calibrate();
        while(gyro.isCalibrating()){
            idle();
        }

        sleep(500);
    }


    //when claw has reached the correct position or moved open long enough, the claw stops moving.
    protected void claw() {
        if(gamepad2.dpad_left){
            claw.setPower(CLAW_POWER);
        }
        else if(gamepad2.dpad_right){
            claw.setPower(-CLAW_POWER);
        }
        else {
            claw.setPower(0.0);
        }
    }

    protected void drive() {
        //left stick controls movement
        //right stick controls turning

        //left stick x = strafe
        //left stick y = drive, forwards/backwards
        //right stick = turn
        double turn_x = gamepad1.right_stick_x; //stick that determines how far robot is turning
        double x = gamepad1.left_stick_x;
        double y = -gamepad1.left_stick_y;
        double magnitude = Math.abs(y) + Math.abs(x) + Math.abs(turn_x); //Used to determine the greatest possible value of y +/- x to scale them
        double scale = Math.max(1, magnitude); //Used to prevent setting motor to power over 1

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

    /**
     * <strong>lift</strong><br>
     * Up arrow on dpad is up on lift, same setup for down.
     */
    protected void lift() {
        if(gamepad2.dpad_up){
            lift.setPower(LIFT_POWER);
        }
        else if(gamepad2.dpad_down){
            lift.setPower(-LIFT_POWER);
        }
        else{
            lift.setPower(0.0);
        }
    }

    //The second driver controls how fast both the continuous servo and motor runs.  Extends and retracts arm.
    //left stick = continuous servo.
    //right stick = motor
    //Forward = extend, backwards = retract
    protected void arm()
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

    protected void setupButtons() {
        buttons.add(new Button() {
            @Override
            public boolean isInputPressed() {
                return gamepad1.x;
            }

            @Override
            public void onPress() {
                final int startHeading = gyro.getIntegratedZValue();
                motion.turn(true, new Condition() {
                    @Override
                    public boolean isTrue() {
                        double turn_x = gamepad1.right_stick_x;
                        double x = gamepad1.left_stick_x;
                        double y = -gamepad1.left_stick_y;
                        double currentHeading = gyro.getIntegratedZValue();
                        if((Math.abs(turn_x) > JOYSTICK_ERROR_RANGE || Math.abs(x) > JOYSTICK_ERROR_RANGE ||Math.abs(y) > JOYSTICK_ERROR_RANGE)) {
                            return true;
                        }
                        return (currentHeading - (startHeading - AUTO_TURN_AMOUNT)) > 0;
                    }
                });
            }
        });
        buttons.add(new Button() {
            @Override
            public boolean isInputPressed() {
                return gamepad1.b;
            }

            @Override
            public void onPress() {
                final int startHeading = gyro.getIntegratedZValue();
                motion.turn(true, new Condition() {
                    @Override
                    public boolean isTrue() {
                        double turn_x = gamepad1.right_stick_x;
                        double x = gamepad1.left_stick_x;
                        double y = -gamepad1.left_stick_y;
                        double currentHeading = gyro.getIntegratedZValue();
                        if(Math.abs(turn_x) > JOYSTICK_ERROR_RANGE || Math.abs(x) > JOYSTICK_ERROR_RANGE ||Math.abs(y) > JOYSTICK_ERROR_RANGE) {
                            return true;
                        }
                        return (currentHeading - (startHeading + AUTO_TURN_AMOUNT)) > 0;
                    }
                });
            }
        });
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
