package org.firstinspires.ftc.teamcode.objects;

import com.qualcomm.robotcore.hardware.I2cAddr;
import com.qualcomm.robotcore.hardware.I2cDevice;
import com.qualcomm.robotcore.util.TypeConversion;

import java.nio.ByteOrder;


public class I2cGyroSensor extends I2cSensor {
    protected static final int DEFAULT_ADDRESS = 0x20;
    protected static final int GYRO_HEADING_START=0x0;
    protected static final int GYRO_INTEGRATED_START=0x06;
    protected static final int GYRO_READ_LENGTH = 2;
    protected static final int GYRO_CALIBRATE_START = 0x4E;
    protected static final int GYRO_HEADING_RESET_START = 0x52;




    /**
     * Creates a sensor with an address of {@link #DEFAULT_ADDRESS}
     * @param sensor The sensor object on the hardware map to be used
     */
    public I2cGyroSensor(I2cDevice sensor) {
        super(sensor, I2cAddr.create8bit((DEFAULT_ADDRESS)));
    }

    /**
     * Creates a sensor with the given address
     * @param address The address which references the given sensor
     * @param sensor The sensor object on the hardware map to be used
     */
    public I2cGyroSensor(I2cDevice sensor, I2cAddr address) {
        super(sensor, address);
    }
    /**
     * Returns the Integrated Z Value of the Gyro
     */
    public int getIntegratedZ(){
        cache = SENSORReader.read(GYRO_INTEGRATED_START, GYRO_READ_LENGTH);
        return TypeConversion.byteArrayToShort(cache, ByteOrder.LITTLE_ENDIAN);
    }
    /**
     * Returns the Heading Value of the Gyro
     */
    public int getHeading(){
        cache = SENSORReader.read(GYRO_HEADING_START, GYRO_READ_LENGTH);
        return TypeConversion.byteArrayToShort(cache, ByteOrder.LITTLE_ENDIAN);
    }/**
     * Calibrates the Gyro
     */
    public void calibrate(){
        SENSORReader.write8(COMAND_REG_START,GYRO_CALIBRATE_START);
    }/**
     * Resets the Gyro's Z heading
     */
    public void resetHeading(){
        SENSORReader.write8(COMAND_REG_START,GYRO_HEADING_RESET_START);
    }/**
     * Returns whether the Gyro is calibrating
     */
    public boolean isCalibrating(){
        return SENSORReader.read(COMAND_REG_START, 1)[0] != 0x00;
    }



}
