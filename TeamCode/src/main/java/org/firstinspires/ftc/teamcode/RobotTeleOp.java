package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.Servo;

import java.util.ArrayList;

/**
 * Created by Alex on 9/23/2017.
 */

@TeleOp(name="TeleOp", group="TeleOp")
@Disabled
public class RobotTeleOp extends LinearOpMode {

    //Add all global objects and lists
    protected ArrayList<Toggle> ToggleList = new ArrayList<Toggle>();


    //Add Motors, Servos, Sensors, etc here
    //EX: protected DcMotor motor;
    protected Servo claw;
    //protected DcMotor arm;
    protected DcMotor armMotor;
    protected CRServo armControlServo;
    boolean armManual = false;
    //protected DcMotor claw;

    //Motors for each wheel
    protected DcMotor leftFront;
    protected DcMotor rightFront;
    protected DcMotor leftBack;
    protected DcMotor rightBack;

    //Add all Constants here
    //EX: protected final double MOTOR_POWER = 0.5;
    //private final double CLAW_POSITION_ONE = 0.0;
    //private final double CLAW_POSITION_TWO = 0.5;
    protected final double ARM_MOTOR_POWER = 0.4;
    protected final double ARM_SERVO_POWER = 0.4;
    //protected final double CLAW_POWER = 0.2;
    protected final double JOYSTICK_ERROR_RANGE = 0.1;

    @Override
    public void runOpMode() {

        setupHardware();
        setupToggleList();
        //Add any further initialization (methods) here

        waitForStart();

        while (opModeIsActive()) {
            //toggleLogic();
            //clawFunction();

            //Add any non-toggles here

            //Arm extension part
            //armExtension();

            claw();
            toggleLogic();

            //nextPosition
            /*if ((currentPosition < GLYPH_HEIGHT * 0) && (currentPosition > GLYPH_HEIGHT * 0)) {
                nextPosition = glyphOne;
            }
            else if ((currentPosition < GLYPH_HEIGHT * 0) && (currentPosition > GLYPH_HEIGHT * 0)) {
                nextPosition = glyphTwo;
            }
            else if ((currentPosition < GLYPH_HEIGHT * 0) && (currentPosition > GLYPH_HEIGHT * 0)) {
                nextPosition = glyphThree;
            }
            else if ((currentPosition < GLYPH_HEIGHT * 0) && (currentPosition > GLYPH_HEIGHT * 0)) {
                nextPosition = glyphFour;
            }

            //D Pad used to control Main Lift
            if((gamepad2.dpad_up == false) && (gamepad2.dpad_down == false)) {
                DPadMoving = false;
            }


            if(gamepad2.dpad_up == true) {
                DPadMoving = true
                mainLift.setPower(nextPosition-currentPosition);
            }*/
            drive();

            telemetry.update();
            idle();
        }
    }

    protected void setupHardware() {
        //Initializes the motor/servo variables here
        /*EX:
        motor = hardwareMap.dcMotor.get("motor");
        motor.setDirection(DcMotor.Direction.FORWARD);*/

        claw = hardwareMap.servo.get("claw");
        //claw = hardwareMap.dcMotor.get("claw");
        //claw.setDirection(DcMotor.Direction.FORWARD);
        //arm = hardwareMap.dcMotor.get("arm");
        //arm.setDirection(DcMotor.Direction.FORWARD);
        armControlServo = hardwareMap.crservo.get("armControlServo");
        armMotor = hardwareMap.dcMotor.get("armMotor");
        armMotor.setDirection(DcMotor.Direction.FORWARD);

        leftFront = hardwareMap.dcMotor.get("leftFront");
        rightFront = hardwareMap.dcMotor.get("rightFront");
        leftBack = hardwareMap.dcMotor.get("leftBack");
        rightBack = hardwareMap.dcMotor.get("rightBack");

        leftFront.setDirection(DcMotor.Direction.REVERSE);
        leftBack.setDirection(DcMotor.Direction.REVERSE);
    }

    //for arm, switches version of code running from version1 to version2 and back again
    protected void setupToggleList() {
        //Add any mechanics that can be controlled by a toggle here
        //EX:
        /*ToggleList.add(new Toggle() {
            protected boolean input() {return gamepad1.a;}
            protected void turnOn() {motor.setPower(MOTOR_POWER);}
            protected void turnOff() {motor.setPower(0);}
            protected void debug() {telemetry.addData("Motor", "On: %b, Power: %.2f", isOn(), (isOn() ? MOTOR_POWER : 0.0));}
        })*/


        ToggleList.add(new Toggle() {
            //The y button on gamepad2 will trigger our toggle
            protected boolean input() {return gamepad2.y;}
            protected void turnOn() {armManual = true;}
            protected void turnOff() {armManual = false;}
            protected void debug() {telemetry.addData("Arm Version", (isOn() ? "manual" : "automatic"));}
        });
    }

    protected void toggleLogic() {
        for(Toggle t: ToggleList) {
            t.logic();
            t.debug();
        }
    }

    protected void drive() {

        //left stick controls movement
        //right stick controls turning

        double turn_x = gamepad1.right_stick_x; //stick that determines how far robot is turning
        double magnitude = Math.abs(gamepad1.left_stick_y) + Math.abs(gamepad1.left_stick_x) + Math.abs(turn_x); //total sum of all inputs
        double scale = Math.max(1, magnitude); //determines whether magnitude or 1 is greater (prevents from setting motor to power over 1)
        double x = gamepad1.left_stick_x;
        double y = -gamepad1.left_stick_y;


        double leftFrontPower = (y + x + turn_x) / scale;
        double rightFrontPower = (y - x - turn_x) / scale;
        double leftBackPower =(y - x + turn_x) / scale;
        double rightBackPower = (y + x - turn_x) / scale;

        //setting power for each of the 4 wheels
        leftFront.setPower(leftFrontPower);
        rightFront.setPower(rightFrontPower);
        leftBack.setPower(leftBackPower);
        rightBack.setPower(rightBackPower);
    }

    //Add new methods for functionality down here

    //scissor lift arm moved by pressing up or down arrows on d-pad.
/*    protected void armExtension() {
        //When up arrow pressed, arm moves forward.  When up arrow released, arm stops moving.
            if(gamepad2.dpad_up == true)
                arm.setPower(ARM_POWER);
            else if(gamepad2.dpad_up == false)
                arm.setPower(0.0);
            //When down arrow pressed, arm retracts.  When down arrow released, arm stops moving
            if(gamepad2.dpad_down == true)
                arm.setPower(-ARM_POWER);
            else if(gamepad2.dpad_down == false)
                arm.setPower(0.0);
    }*/

    //Back up: Claw with motor
/*    protected void clawFunction() {
        //When up arrow pressed, arm moves forward.  When up arrow released, arm stops moving.
        if(gamepad2.y == true)
            claw.setPower(CLAW_POWER);

        else if(gamepad2.y == false)
            claw.setPower(0.0);
        //When down arrow pressed, arm retracts.  When down arrow released, arm stops moving
        if(gamepad2.b == true)
            claw.setPower(-CLAW_POWER);
        else if(gamepad2.b == false)
            claw.setPower(0.0);
    }*/

    //If right stick pointed forward, arm moves forward.  If right stick pointed towards back, arm moves back into robot.
    //If right stick pointed left, claw closes.  If right stick pointed right, claw opens.  Right stick in center, no movement.
/*    protected void armPlusClaw()
    {

        if (gamepad2.right_stick_x > JOYSTICK_ERROR_RANGE) {
            claw.setPower(-CLAW_POWER);
        }
        else if (gamepad2.right_stick_x < -JOYSTICK_ERROR_RANGE) {
            claw.setPower(CLAW_POWER);
        }
        else {
            claw.setPower(0);
        }

        if (gamepad2.right_stick_y > JOYSTICK_ERROR_RANGE) {
            arm.setPower(-ARM_POWER);
        }
        else if (gamepad2.right_stick_y < -JOYSTICK_ERROR_RANGE) {
            arm.setPower(ARM_POWER);
        }
        else {
            arm.setPower(0);
        }
    }*/

    //When right joystick on the second controller is pushed to the left, claw closes.  When pushed to right, claw opens.
    protected void claw()
    {
        if (gamepad2.right_stick_x > JOYSTICK_ERROR_RANGE) {
          if (claw.getPosition() < 1) {
              claw.setPosition(claw.getPosition()+0.1);
          }
        }
        if (gamepad2.right_stick_x < -JOYSTICK_ERROR_RANGE) {
          if (claw.getPosition() > 0) {
              claw.setPosition(claw.getPosition()-0.1);
          }
        }
    }

    protected void arm() {
        if(armManual)
            manualArmControl();
        else
            automaticArmControl();
    }

    //When x is pressed, arm estends.  When b is pressed, arm retracts.
    protected void automaticArmControl()
    {
        if(gamepad2.x)
        {
            armMotor.setPower(ARM_MOTOR_POWER);
            armControlServo.setPower(ARM_SERVO_POWER);
        }
        else if(gamepad2.b)
        {
            armMotor.setPower(-ARM_MOTOR_POWER);
            armControlServo.setPower(-ARM_SERVO_POWER);
        }
        else
        {
            armMotor.setPower(0);
            armControlServo.setPower(0);
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
