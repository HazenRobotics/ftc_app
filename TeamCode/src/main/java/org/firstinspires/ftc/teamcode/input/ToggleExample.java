package org.firstinspires.ftc.teamcode.input;

import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import java.util.ArrayList;

@TeleOp(name="Toggle", group="Example")
@Disabled
public class ToggleExample extends LinearOpMode {
    protected ButtonManager buttons = new ButtonManager();
    protected DcMotor motor;
    protected final double MOTOR_POWER = 0.5;

    //Initializes the motor variable from the hardwareMap and sets its direction
    protected void setupHardware() {
        motor = hardwareMap.dcMotor.get("motor");
        motor.setDirection(DcMotor.Direction.FORWARD);
    }

    protected void setupButtons() {
    	buttons.add(new Toggle() {
    		public boolean isInputPressed() { return gamepad1.a; }
    		public void onActivate() { motor.setPower(MOTOR_POWER); }
    		public void onDeactivate() { motor.setPower(0); }
    	});
    }

    @Override
    public void runOpMode() {
        setupHardware();
        setupButtons();
        waitForStart();

        while (opModeIsActive()) {
            buttons.update();
            telemetry.addData("Motor", "On: %b, Power: %.2f", isOn(), (isOn() ? MOTOR_POWER : 0.0));
            telemetry.update();
        }
    }
}
