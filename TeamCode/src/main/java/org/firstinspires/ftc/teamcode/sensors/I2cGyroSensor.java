package org.firstinspires.ftc.teamcode.sensors;

import com.qualcomm.robotcore.hardware.I2cAddr;
import com.qualcomm.robotcore.hardware.I2cDevice;
import com.qualcomm.robotcore.util.TypeConversion;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;

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
     * Gets the current integrated Z value held by the sensor, which counts up or down as you rotate
     * @return The Integrated Z value in degrees with the same range as a short
     */
    public short getIntegratedZValue() {
        cache = SENSORReader.read(GYRO_INTEGRATED_START, GYRO_READ_LENGTH);
        return TypeConversion.byteArrayToShort(cache, ByteOrder.LITTLE_ENDIAN);
    }

    /**
     * Gets the current integrated Z value held by the sensor, which counts up or down as you rotate
     * @param unit The unit type in which the angle will be returned
     * @return The Integrated Z value in the specified units
     */
    public float getIntegratedZValue(AngleUnit unit) {
        return unit.fromDegrees(getIntegratedZValue());
    }

    /**
     * Gets the current heading value held by the sensor which loops back to 0 after you pass 360 degrees
     * @return The heading value of the gyro in degrees, between 0 and 359
     */
    public short getHeading() {
        cache = SENSORReader.read(GYRO_HEADING_START, GYRO_READ_LENGTH);
        return TypeConversion.byteArrayToShort(cache, ByteOrder.LITTLE_ENDIAN);
    }

    /**
     * Gets the current heading value held by the sensor which loops back to 0 after you make one full rotation
     * @param unit The unit type in which the angle will be returned
     * @return The heading value of the gyro in the specified angle unit
     */
    public float getHeading(AngleUnit unit) {
        return unit.fromDegrees(getHeading());
    }

    /**
     * Calibrates the Gyro
     */
    public void calibrate() {
        SENSORReader.write8(COMAND_REG_START,GYRO_CALIBRATE_START);
    }

    /**
     * Resets the Gyro's Z heading
     */
    public void resetHeading() {
        SENSORReader.write8(COMAND_REG_START,GYRO_HEADING_RESET_START);
    }

    /**
     * Checks if the gyro is still calibrating, either by a full calibration or heading reset
     * @return If the Gyro is currently calibrating
     */
    public boolean isCalibrating() {
        return SENSORReader.read(COMAND_REG_START, 1)[0] != 0x00;
    }
}
