package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.Servo;

import java.util.ArrayList;

/**
 * Created by Robotics(Angel) on 10/3/2017.
 */
@TeleOp (name="ExtendRelic",group="TeleOp")

public class ExtendRelic extends LinearOpMode
{
    private ArrayList<Toggle> ToggleList = new ArrayList<Toggle>();
    private Servo claw;
    private final double CLAW_POSITION = 0.0;
    private DcMotor arm;

    //Initial setup code
    protected void setUpHardware()
    {
        claw = hardwareMap.servo.get("claw");

        arm = hardwareMap.dcMotor.get("arm");
        arm.setDirection(DcMotor.Direction.FORWARD);
    }
    //initializing and setting up toggle to open and close claw by setting position to 0 (closed) and 0.5(all the way open)
    private void setupToggleList()
    {
        ToggleList.add(new Toggle() {
            //The Y button on gamepad1 will trigger our toggle
            protected boolean input() {return gamepad1.y;}
            //Based on if the motor is currently on or off, we will set the motor Power
            protected void turnOn() {claw.setPosition(CLAW_POSITION);}
            protected void turnOff() {claw.setPosition(0.5);}
            protected void debug() {telemetry.addData("Claw", "On: %b, Power: %.2f", isOn(), (isOn() ? CLAW_POSITION : 0.5));}
        });
    }
    //main block of code where everything runs
    public void runOpMode() throws InterruptedException
    {
        setUpHardware();
        setupToggleList();
        waitForStart();
        while(opModeIsActive())
        {
            //arm extension part
            //When up arrow pressed, arm moves forward.  When up arrow released, arm stops moving.
            if(gamepad2.dpad_up=true)
            {
                arm.setPower(0.4);
            }
            else if(gamepad2.dpad_up=false)
            {
                arm.setPower(0.0);
            }
            //When down arrow pressed, arm retracts.  When down arrow released, arm stops moving
            if(gamepad2.dpad_down=true)
            {
                arm.setPower(-0.4);
            }
            else if(gamepad2.dpad_down=false)
            {
                arm.setPower(0.0);
            }
            //claw part
            toggleLogic();
            telemetry.update();
            idle();
        }
    }
    private void toggleLogic() {
        for(Toggle t: ToggleList) {
            t.logic();
            t.debug();
        }
    }
}
