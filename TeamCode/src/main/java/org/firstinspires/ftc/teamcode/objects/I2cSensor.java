package org.firstinspires.ftc.teamcode.objects;

import com.qualcomm.robotcore.hardware.I2cAddr;
import com.qualcomm.robotcore.hardware.I2cDevice;
import com.qualcomm.robotcore.hardware.I2cDeviceSynch;
import com.qualcomm.robotcore.hardware.I2cDeviceSynchImpl;



public abstract class I2cSensor {
    protected I2cAddr address; //address in 7 bits
    protected byte[] cache; //storage of information
    protected I2cDevice SENSOR; //legit sensor
    protected I2cDeviceSynch SENSORReader; //reader to read and write
    protected int COMAND_REG_START = 0x03;



    I2cSensor(I2cDevice sensor, I2cAddr address) {
        SENSOR = sensor;
        changeAddress(address);
    }

    /**
     * Changes the sensor to use the given address
     * @param address The new address to be used
     */
    public void changeAddress(I2cAddr address) {
        this.address = address;
        SENSORReader = new I2cDeviceSynchImpl(SENSOR, address, false);
        SENSORReader.engage();
    }

}
