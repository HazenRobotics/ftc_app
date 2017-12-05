package org.firstinspires.ftc.teamcode.models;

/**
 * An RGB 0-255 color.
 */
public class Color {
    /**
     * This is the default margin of error used by {@link #approximatelyEquals(Color)}.
     */
    private static final int DEFAULT_MAXIMUM_FUZZY_EQUALS_DELTA = 40;

    public static final Color RED = new Color(255, 0, 0);
    public static final Color GREEN = new Color(0, 255, 0);
    public static final Color BLUE = new Color(0, 0, 255);

    private final int red, blue, green;

    /**
     * An RGB 0-255 color.
     * @param red Between 0 and 255.
     * @param green Between 0 and 255.
     * @param blue Between 0 and 255.
     */
    public Color(int red, int green, int blue) {
        if(red < 0 || green < 0 || blue < 0)
            throw new IllegalArgumentException("A color cannot have values outside the range of an unsigned byte (0-255). Was negative.");
        if(red > 255 || green > 255 || blue > 255)
            throw new IllegalArgumentException("A color cannot have values outside the range of an unsigned byte (0-255). Was above 255.");
        this.red = red;
        this.green = green;
        this.blue = blue;
    }

    /**
     * @return Between 0 and 255
     */
    public int red() {
        return red;
    }

    /**
     * @return Between 0 and 255
     */
    public int green() {
        return green;
    }

    /**
     * @return Between 0 and 255
     */
    public int blue() {
        return blue;
    }

    /**
     * The difference between two colors.
     * @param other The color to compare to.
     * @return The sum of the differences in each color aspect.
     */
    public int delta(Color other) {
        return Math.abs(red - other.red) + Math.abs(green - other.green) + Math.abs(blue - other.blue);
    }

    /**
     * Compares two colors to see if they are within a margin of error in the sensor.
     * @param other The color to compare to.
     * @return Is this color within a certain difference of another color?
     */
    public boolean approximatelyEquals(Color other) {
        return this.approximatelyEquals(other, DEFAULT_MAXIMUM_FUZZY_EQUALS_DELTA);
    }

    /**
     * Compares two colors to see if they are within a margin of error in the sensor.
     * @param other The color to compare to.
     * @param maximumDifference The maximum margin of error.
     * @return Is this color within a certain difference of another color?
     */
    public boolean approximatelyEquals(Color other, int maximumDifference) {
        return this.delta(other) <= maximumDifference;
    }
    @Override
    public String toString(){

        return "";
    }

}
