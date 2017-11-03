package org.firstinspires.ftc.teamcode.objects;

/**
 * Created by Robotics on 2/16/2017.
 */
import com.qualcomm.robotcore.hardware.I2cAddr;
import com.qualcomm.robotcore.hardware.I2cDevice;

import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;

//mock class of 12c range sensor
//custom for simplifying range sensor functions
public class I2cRangeSensor extends I2cSensor {
    protected static final int DEFAULT_ADDRESS = 0x28;
    protected static final int RANGE_REG_START = 0x04; //Register to start reading
    protected static final int RANGE_READ_LENGTH = 2; //Number of byte to read

    /**
     * Creates a sensor with an address of {@link #DEFAULT_ADDRESS}
     * @param sensor The sensor object on the hardware map to be used
     */
    public I2cRangeSensor(I2cDevice sensor) {
        super(sensor, I2cAddr.create7bit(DEFAULT_ADDRESS));
    }

    /**
     * Creates a sensor with the given address
     * @param address The address which references the given sensor
     * @param sensor The sensor object on the hardware map to be used
     */
    public I2cRangeSensor(I2cDevice sensor, I2cAddr address) {
        super(sensor, address);
    }

    //returns ultrasonic reading in desired unit
    public double readUltrasonic(DistanceUnit unit) {
        cache = SENSORReader.read(RANGE_REG_START, RANGE_READ_LENGTH);
        return unit.fromUnit(DistanceUnit.CM, cache[0] & 0xFF);
    }
    //returns optical reading for
    public double readOptical(DistanceUnit unit) {
        cache = SENSORReader.read(RANGE_REG_START, RANGE_READ_LENGTH);
        return unit.fromUnit(DistanceUnit.CM, cache[1] & 0xFF);
    }
}
