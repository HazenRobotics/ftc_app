package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import java.util.ArrayList;

/**
 * Created by Alex on 9/9/2017.
 */

@TeleOp(name="Toggle", group="Example")
@Disabled
public class ToggleExample extends LinearOpMode {
    protected ArrayList<Toggle> ToggleList = new ArrayList<Toggle>();
    protected DcMotor motor;
    protected final double MOTOR_POWER = 0.5;

    //Initializes the motor variable from the hardwareMap and sets its direction
    protected void setupHardware() {
        motor = hardwareMap.dcMotor.get("motor");
        motor.setDirection(DcMotor.Direction.FORWARD);
    }

    protected void setupToggleList() {
        ToggleList.add(new Toggle() {
            //The a button on gamepad1 will trigger our toggle
            protected boolean input() {return gamepad1.a;}
            //Based on if the motor is currently on or off, we will set the motor Power
            protected void turnOn() {motor.setPower(MOTOR_POWER);}
            protected void turnOff() {motor.setPower(0);}
            //Shows debug telemetry of if the motor is on
            protected void debug() {telemetry.addData("Motor", "On: %b, Power: %.2f", isOn(), (isOn() ? MOTOR_POWER : 0.0));}
        });

    }

    @Override
    public void runOpMode() {

        setupHardware();
        setupToggleList();
        waitForStart();

        while (opModeIsActive()) {
            toggleLogic();
            telemetry.update();
        }
    }

    protected void toggleLogic() {
        for(Toggle t: ToggleList) {
            t.logic();
            t.debug();
        }
    }
}
