package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
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
    private Servo claw;
    private DcMotor arm;
    //private DcMotor claw;

    //Add all Constants here
    //EX: protected final double MOTOR_POWER = 0.5;
    private final double CLAW_POSITION_ONE = 0.0;
    private final double CLAW_POSITION_TWO = 0.5;
    private final double ARM_POWER = 0.4;
    //private final double CLAW_POWER = 0.2;

    @Override
    public void runOpMode() {

        setupHardware();
        setupToggleList();
        //Add any further initialization (methods) here
        
        waitForStart();

        while (opModeIsActive()) {
            toggleLogic();
            //clawFunction();

            //Add any non-toggles here
            
            //Arm extension part
            armExtension();

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
        arm = hardwareMap.dcMotor.get("arm");
        arm.setDirection(DcMotor.Direction.FORWARD);

    }

    //claw function, run by servo
    protected void setupToggleList() {
        //Add any mechanics that can be controlled by a toggle here
        /*EX:
        ToggleList.add(new Toggle() {
            protected boolean input() {return gamepad1.a;}
            protected void turnOn() {motor.setPower(MOTOR_POWER);}
            protected void turnOff() {motor.setPower(0);}
            protected void debug() {telemetry.addData("Motor", "On: %b, Power: %.2f", isOn(), (isOn() ? MOTOR_POWER : 0.0));}
        })*/
        ToggleList.add(new Toggle() {
            //The y button on gamepad1 will trigger our toggle
            protected boolean input() {return gamepad1.y;}
            protected void turnOn() {claw.setPosition(CLAW_POSITION_ONE);}
            protected void turnOff() {claw.setPosition(CLAW_POSITION_TWO);}
            protected void debug() {telemetry.addData("Claw", "On: %b, Position: %.2f", isOn(), (isOn() ? CLAW_POSITION_ONE : CLAW_POSITION_TWO));}
        });
    }

    protected void toggleLogic() {
        for(Toggle t: ToggleList) {
            t.logic();
            t.debug();
        }
    }

    //Add new methods for functionality down here

    //scissor lift arm moved by pressing up or down arrows on d-pad.
    protected void armExtension() {
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
    }

    //Back up: Claw with motor
//    protected void clawFunction() {
//        //When up arrow pressed, arm moves forward.  When up arrow released, arm stops moving.
//        if(gamepad2.y == true)
//            claw.setPower(CLAW_POWER);
//
//        else if(gamepad2.y == false)
//            claw.setPower(0.0);
//        //When down arrow pressed, arm retracts.  When down arrow released, arm stops moving
//        if(gamepad2.b == true)
//            claw.setPower(-CLAW_POWER);
//        else if(gamepad2.b == false)
//            claw.setPower(0.0);
//    }
    protected void armPlusClaw()
    {
    }
}
