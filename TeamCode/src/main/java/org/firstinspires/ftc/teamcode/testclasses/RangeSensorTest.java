package org.firstinspires.ftc.teamcode.testclasses;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.I2cDevice;

import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;
import org.firstinspires.ftc.teamcode.objects.I2cColorSensor;
import org.firstinspires.ftc.teamcode.objects.I2cRangeSensor;

@Disabled
@Autonomous(group = "Test Classes", name = "Range Sensor Test")
/**
 * Class to debug the output of the Range Sensor
 * <br></br>Must Use 'rangeSensor' as name of device in configuration
 */
public class RangeSensorTest extends LinearOpMode {

    @Override
    public void runOpMode() throws InterruptedException {
        I2cDevice range = hardwareMap.i2cDevice.get("rangeSensor");
        I2cRangeSensor rangeSensor = new I2cRangeSensor(range);
        waitForStart();
        while (opModeIsActive()) {
            telemetry.addData("Range Sensor CM:", rangeSensor.readUltrasonic(DistanceUnit.CM));
            telemetry.addData("Range Sensor INCHES:", rangeSensor.readUltrasonic(DistanceUnit.INCH));
            telemetry.addData("Range Sensor CM Optical:", rangeSensor.readOptical(DistanceUnit.CM));
            telemetry.addData("Range Sensor INCHES Optical:", rangeSensor.readOptical(DistanceUnit.INCH));
            telemetry.update();
            idle();
        }
       }


}
