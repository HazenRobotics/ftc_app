package org.firstinspires.ftc.teamcode.objects;

/**
 * Created by Robotics on 2/16/2017.
 */

import com.qualcomm.hardware.modernrobotics.ModernRoboticsI2cRangeSensor;
import com.qualcomm.robotcore.hardware.I2cAddr;
import com.qualcomm.robotcore.hardware.I2cDevice;
import com.qualcomm.robotcore.hardware.I2cDeviceSynch;
import com.qualcomm.robotcore.hardware.I2cDeviceSynchImpl;

import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;

//mock class of 12c range sensor
//custom for simplifying range sensor functions
public class I2cRangeSensor {
    protected static final int RANGE_REG_START = 0x04; //Register to start reading
    protected static final int RANGE_READ_LENGTH = 2; //Number of byte to read

    protected I2cAddr rangeAdress = null;   //address in 7 bits
    protected byte[] rangeCache;    //storage of information
    protected I2cDevice RANGE;     //legit range sensor
    protected I2cDeviceSynch RANGEReader;   //reader to read and write

    //constructor for default
    //DO NOT USE
    public I2cRangeSensor() {
        //rangeAdress = new I2cAddr(0x14);
    }
    //constructor for custum address
    //give 7 bit adress
    public I2cRangeSensor(I2cAddr adress, I2cDevice sensor) {
        RANGE = sensor;
        changeAdress(adress);
    }
    //changes the address of the sensor
    //give 7 bit adress
    public void changeAdress(I2cAddr adress) {
        rangeAdress = adress;
        RANGEReader = new I2cDeviceSynchImpl(RANGE, rangeAdress, false);
        RANGEReader.engage();
    }

    //returns ultrasonic reading in desired unit
    public double readUltrasonic(DistanceUnit unit) {
        rangeCache = RANGEReader.read(RANGE_REG_START, RANGE_READ_LENGTH);
        return unit.fromUnit(DistanceUnit.CM, rangeCache[0] & 0xFF);
    }
    //returns optical reading for
    public double readOptical(DistanceUnit unit) {
        rangeCache = RANGEReader.read(RANGE_REG_START, RANGE_READ_LENGTH);
        return unit.fromUnit(DistanceUnit.CM, rangeCache[1] & 0xFF);
    }
}
