package org.firstinspires.ftc.teamcode;

/**
 * Created by Robotics on 12/7/2017.
 */

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

@Autonomous(name = "TelemetryTest",group = "Autonomous")
public class TelemetryTest extends LinearOpMode {

    @Override
    public void runOpMode() throws InterruptedException {
        waitForStart();
        telemetry.addData("","Hi.");
        telemetry.update();
        //wait(3000);
        sleep(3000);
        telemetry.clear();
        telemetry.update();
    }
}
