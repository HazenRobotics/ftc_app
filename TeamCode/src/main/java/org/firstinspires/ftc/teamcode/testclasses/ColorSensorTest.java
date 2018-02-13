package org.firstinspires.ftc.teamcode.testclasses;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.I2cDevice;

import org.firstinspires.ftc.teamcode.objects.I2cColorSensor;
@Disabled
@Autonomous(group = "Test Classes", name = "Color Sensor Test")
/**
 * Class to debug the output of the Color Sensor
 * <br></br>Must Use 'colorSensor' as name of device in configuration
 */
public class ColorSensorTest extends LinearOpMode {

    @Override
    public void runOpMode() throws InterruptedException {
        I2cDevice color = hardwareMap.i2cDevice.get("colorSensor");
        I2cColorSensor colorSensor = new I2cColorSensor(color);
        waitForStart();
        while (opModeIsActive()) {
            telemetry.addData("Color Sensor Output:", colorSensor.readColor());
            telemetry.update();
            idle();
        }
       }


}
