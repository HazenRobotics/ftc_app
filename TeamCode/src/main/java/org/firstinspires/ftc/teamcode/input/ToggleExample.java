package org.firstinspires.ftc.teamcode.input;

import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;

import org.firstinspires.ftc.teamcode.output.Message;
import org.firstinspires.ftc.teamcode.output.Telemetry;

@TeleOp(name="Toggle", group="Example")
@Disabled
public class ToggleExample extends LinearOpMode {
    protected ButtonManager buttons = new ButtonManager();
    protected DcMotor motor;
    protected final double MOTOR_POWER = 0.5;
    protected final Telemetry telemetry = new Telemetry(super.telemetry);

    //Initializes the motor variable from the hardwareMap and sets its direction
    protected void setupHardware() {
        motor = hardwareMap.dcMotor.get("motor");
        motor.setDirection(DcMotor.Direction.FORWARD);
    }

    protected void setupButtons() {
    	buttons.add(new Toggle() {
    		public boolean isInputPressed() { return gamepad1.a; }
    		public boolean onActivate() { motor.setPower(MOTOR_POWER);
                return false;
            }
    		public void onDeactivate() { motor.setPower(0); }
    	});
    }

    @Override
    public void runOpMode() {
        setupHardware();
        setupButtons();
        waitForStart();
        
        telemetry.add("Motor Power", new Message.IMessageData() {
            @Override
            public String getMessage() {
                return String.format("%.2d", motor.getPower());
            }
        });

        while (opModeIsActive()) {
            buttons.update();
            telemetry.update();
        }
    }
}
