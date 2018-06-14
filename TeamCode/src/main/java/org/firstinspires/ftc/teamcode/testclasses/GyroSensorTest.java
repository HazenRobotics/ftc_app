package org.firstinspires.ftc.teamcode.testclasses;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.I2cDevice;

import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;
import org.firstinspires.ftc.teamcode.objects.I2cGyroSensor;
import org.firstinspires.ftc.teamcode.objects.I2cRangeSensor;

@Disabled
@Autonomous(group = "Test Classes", name = "Gyro Sensor Test")
/**
 * Class to debug the output of the Gyro Sensor
 * <br></br>Must Use 'gyroSensor' as name of device in configuration
 */
public class GyroSensorTest extends LinearOpMode {

    @Override
    public void runOpMode() throws InterruptedException {
        I2cDevice gyro = hardwareMap.i2cDevice.get("gyroSensor");
        I2cGyroSensor gyroSensor = new I2cGyroSensor(gyro);
        sleep(500);
        gyroSensor.calibrate();
        while(gyroSensor.isCalibrating()) {
            telemetry.addData(">", "Calibrating");
            telemetry.update();
            idle();
        }
        telemetry.clear();
        waitForStart();
        gyroSensor.calibrate();
        while (opModeIsActive()) {
            telemetry.addData("Gyro Sensor Heading:", gyroSensor.getHeading());
            telemetry.addData("Gyro Sensor Integrated Z Value:", gyroSensor.getIntegratedZ());
            telemetry.update();
            idle();
        }
       }


}
