package org.firstinspires.ftc.teamcode.input;

import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import java.util.ArrayList;

import org.firstinspires.ftc.teamcode.input.gamepad.ButtonEvent;
import org.firstinspires.ftc.teamcode.input.gamepad.annotations.ButtonListener;
import org.firstinspires.ftc.teamcode.output.Message;
import org.firstinspires.ftc.teamcode.output.Telemetry;

@TeleOp(name="Toggle", group="Example")
@Disabled
public class ToggleExample extends OpMode {
    protected ButtonManager buttons = new ButtonManager();
    protected final Telemetry telemetry = new Telemetry(super.telemetry);
    
    protected DcMotor motor;
    protected final double MOTOR_POWER = 0.5;
    
    @Override
    public void init() {
    	setupHardware();
        setupButtons();
    }
    
    @Override
    public void loop() {
    	buttons.update();
        telemetry.update();
    }
    
    protected void setupHardware() {
        motor = hardwareMap.dcMotor.get("motor");
        motor.setDirection(DcMotor.Direction.FORWARD);
    }

    protected void setupButtons() {
    	buttons.registerToggle("motor", gamepad1, "a");
    	buttons.registerListeners(this);
    }
    
    public void setupTelemetry() {
    	telemetry.add("Motor Power", new Message.IMessageData() {
            @Override
            public String getMessage() {
                return String.format("%.2d", motor.getPower());
            }
        });
    }
    
    @ButtonListener(button = "motor")
    public void onMotorOn() {
    	motor.setPower(MOTOR_POWER);
    }
    
    @ButtonListener(button = "motor", event = ButtonEvent.RELEASE)
    public void onMotorRelease() {
    	motor.setPower(0);
    }
}
