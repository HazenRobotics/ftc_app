package org.firstinspires.ftc.teamcode.objects;

import com.qualcomm.hardware.modernrobotics.ModernRoboticsI2cColorSensor;
import com.qualcomm.robotcore.hardware.I2cAddr;
import com.qualcomm.robotcore.hardware.I2cDevice;
import com.qualcomm.robotcore.hardware.I2cDeviceSynch;
import com.qualcomm.robotcore.hardware.I2cDeviceSynchImpl;

/**
 * Created by Robotics on 2/23/2017.
 */
public class I2cColorSensor {
    protected static final int COLOR_REG_START = 0x04; //Register to start reading
    protected static final int COLOR_READ_LENGTH = 1; //Number of byte to read

    protected I2cAddr colorAddress = null;   //address in 7 bits
    protected I2cDevice COLOR;     //legit range sensor
    protected I2cDeviceSynch COLORReader;   //reader to read and write
    protected byte[] colorCache;    //storage of information
    //constructor for default
    //DO NOT USE
    public I2cColorSensor() {
        //colorAddress = new I2cAddr(0x3c);
    }
    //constructor for custum address
    //give 7 bit adress
    public I2cColorSensor(I2cAddr adress, I2cDevice sensor) {
        COLOR = sensor;
        changeAdress(adress);
    }
    //changes the address of the sensor
    //give 7 bit adress
    public void changeAdress(I2cAddr adress) {
        colorAddress = adress;
        COLORReader = new I2cDeviceSynchImpl(COLOR, colorAddress, false);
        COLORReader.engage();
    }

    //returns color
    //key:
    /* 0 - black
     * 1 - purple
     * 2 - violet/dark blue
     * 3 - blue
     * 4 - teal/lightblue
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
     * 15 - blue but white (isnt that just light blue?)
     * 16 - white (finally)
     */
    public int readColor() {
        colorCache = COLORReader.read(COLOR_REG_START, COLOR_READ_LENGTH);
        return colorCache[0] & 0xFF;
    }

    public int red()
    {
        colorCache = COLORReader.read(0x05,COLOR_READ_LENGTH);
        return colorCache[0]&0xFF;
    }

    public int green(){
        colorCache = COLORReader.read(0x06,COLOR_READ_LENGTH);
        return colorCache[0]&0xFF;
    }

    public int blue()
    {
        colorCache = COLORReader.read(0x07,COLOR_READ_LENGTH);
        return colorCache[0]&0xFF;
    }

    public int white()
    {
        colorCache = COLORReader.read(0x08,COLOR_READ_LENGTH);
        return colorCache[0]&0xFF;
    }


    //0 = on
    //1 = off
    //DO NOT ENTER HIGHER THAN 1
    public void enableLed(boolean state) {
        if (state) {
            COLORReader.write8(3, 0);
        }
        else COLORReader.write8(3, 1);
    }
}
