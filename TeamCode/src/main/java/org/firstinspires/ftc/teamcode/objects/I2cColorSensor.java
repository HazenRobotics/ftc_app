package org.firstinspires.ftc.teamcode.objects;

import com.qualcomm.robotcore.hardware.I2cAddr;
import com.qualcomm.robotcore.hardware.I2cDevice;

/**
 * Created by Robotics on 2/23/2017.
 */
public class I2cColorSensor extends I2cSensor {
    protected static final int DEFAULT_ADDRESS = 0x3C;
    protected static final int COLOR_REG_START = 0x04; //Register to start reading
    protected static final int COLOR_READ_LENGTH = 1; //Number of byte to read

    /**
     * Creates a sensor with an address of {@link #DEFAULT_ADDRESS}
     * @param sensor The sensor object on the hardware map to be used
     */
    public I2cColorSensor(I2cDevice sensor) {
        super(sensor, I2cAddr.create7bit((DEFAULT_ADDRESS)));
    }

    /**
     * Creates a sensor with the given address
     * @param address The address which references the given sensor
     * @param sensor The sensor object on the hardware map to be used
     */
    public I2cColorSensor(I2cDevice sensor, I2cAddr address) {
        super(sensor, address);
    }

    //returns color
    //key:
    /* 0 - black
     * 1 - purple
     * 2 - violet/dark blue
     * 3 - blue
     * 4 - teal/light blue
     * 5 - green
     * 6 - lime
     * 7 - yellow
     * 8 - and yellow?
     * 9 - orange
     * 10 - red
     * 11 - redder red?
     * 12 - magenta
     * 13 - red but white (pink?)
     * 14 - green but white (new word?)
     * 15 - blue but white (isn't that just light blue?)
     * 16 - white (finally)
     */

    /**
     * @return Returns a number between 0 and 16. See <a href="http://www.modernroboticsinc.com/Content/Images/uploaded/ColorNumber.png">Color Values Here</a>
     */
    public int readColor() {
        cache = SENSORReader.read(COLOR_REG_START, COLOR_READ_LENGTH);
        return cache[0] & 0xFF;
    }

    public int red()
    {
        cache = SENSORReader.read(0x05, COLOR_READ_LENGTH);
        return cache[0]&0xFF;
    }

    public int green(){
        cache = SENSORReader.read(0x06, COLOR_READ_LENGTH);
        return cache[0]&0xFF;
    }

    public int blue()
    {
        cache = SENSORReader.read(0x07, COLOR_READ_LENGTH);
        return cache[0]&0xFF;
    }

    public int white()
    {
        cache = SENSORReader.read(0x08, COLOR_READ_LENGTH);
        return cache[0]&0xFF;
    }

    public void enableLed(boolean state) {
        if (state) {
            //0 == LED on
            SENSORReader.write8(3, 0);
        } else {
            //1 == LED off
            SENSORReader.write8(3, 1);
        }
    }
}
