package org.firstinspires.ftc.teamcode;

import com.vuforia.HINT;
import com.vuforia.Vuforia;

import org.firstinspires.ftc.robotcore.external.ClassFactory;
import org.firstinspires.ftc.robotcore.external.matrices.OpenGLMatrix;
import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.AxesOrder;
import org.firstinspires.ftc.robotcore.external.navigation.AxesReference;
import org.firstinspires.ftc.robotcore.external.navigation.RelicRecoveryVuMark;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaLocalizer;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaTrackable;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaTrackableDefaultListener;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaTrackables;



public class RelicRecoveryLocalizer {
    //variables
    protected VuforiaLocalizer vuforia;
    protected VuforiaLocalizer.Parameters parameters;
    protected VuforiaTrackableDefaultListener blueListener;
    protected VuforiaTrackableDefaultListener redListener;
    protected VuforiaTrackableDefaultListener cryptoKeyListener;
    protected VuforiaTrackables cryptoBoxTape;
    protected VuforiaTrackables relicTrackables;
    protected VuforiaTrackable cryptoKey;
    protected VuforiaTrackable blueTape;
    protected VuforiaTrackable redTape;

    /**
     * Initializes the localizer using the specified vuforia key
     * @param vuforiaKey the registered key for vuforia to be used
     * @param extendedTracking if vuforia will attempt to estimate position base on relative motion when object is no longer visible
     * @param cameraPreview if the robot controller will show a camera preview of vuforia's vision
     */
    public  RelicRecoveryLocalizer(String vuforiaKey, boolean extendedTracking, boolean cameraPreview) {
        //Creates a parameter object either using or not using camera preview
        parameters = (cameraPreview ? new VuforiaLocalizer.Parameters(R.id.cameraMonitorViewId) : new VuforiaLocalizer.Parameters());

        //Initializes parameter settings
        parameters.cameraDirection = VuforiaLocalizer.CameraDirection.BACK;
        parameters.vuforiaLicenseKey = vuforiaKey;
        parameters.useExtendedTracking = extendedTracking;
        parameters.cameraMonitorFeedback = VuforiaLocalizer.Parameters.CameraMonitorFeedback.AXES;

        //Creates vuforia object with specified parameters
        vuforia = ClassFactory.createVuforiaLocalizer(parameters);

        //Imports tracking data into objects
        cryptoBoxTape = vuforia.loadTrackablesFromAsset("RelicRecoveryTapeLinesSmall");
        relicTrackables = vuforia.loadTrackablesFromAsset("RelicVuMark");

        //Initializes object positional data
        blueTape = cryptoBoxTape.get(1);
        blueTape.setName("blueSmall");
        blueTape.setLocation(createMatrix(0, 0, 0, 90, 0, 0));
        redTape = cryptoBoxTape.get(0);
        redTape.setName("redSmall");
        redTape.setLocation(createMatrix(0, 0, 0, 90, 0, 0));
        cryptoKey = relicTrackables.get(0);
        cryptoKey.setName("Relic");
        cryptoKey.setLocation(createMatrix(0, 0, 0, -90, 0, 0));

        //Grabs the listeners from the trackables (Used to get the position of the trackable from)
        blueListener = ((VuforiaTrackableDefaultListener)blueTape.getListener());
        redListener = ((VuforiaTrackableDefaultListener)redTape.getListener());
        cryptoKeyListener = ((VuforiaTrackableDefaultListener) cryptoKey.getListener());

        //Tells object the location of phone to correctly allow tracking
        OpenGLMatrix phoneLoc = createMatrix(0, 0, 0, -90, 0, 0);
        blueListener.setPhoneInformation(phoneLoc, parameters.cameraDirection);
        redListener.setPhoneInformation(phoneLoc, parameters.cameraDirection);
        cryptoKeyListener.setPhoneInformation(phoneLoc, parameters.cameraDirection);

        //Sets the max number of objects which can be simultaneous
        Vuforia.setHint(HINT.HINT_MAX_SIMULTANEOUS_IMAGE_TARGETS, 1);
    }

    /**
     * Initializes the localizer using the specified vuforia key, with camera preview enabled as default
     * @param vuforiaKey the registered key for vuforia to be used
     * @param extendedTracking if vuforia will attempt to estimate position base on relative motion when object is no longer visible
     */
    public  RelicRecoveryLocalizer(String vuforiaKey, boolean extendedTracking) {
        this(vuforiaKey, extendedTracking, true);
    }

    /**
     * Initializes the localizer using the specified vuforia key, with both camera preview and extended tracking enabled as default
     * @param vuforiaKey the registered key for vuforia to be used
     */
    public  RelicRecoveryLocalizer(String vuforiaKey) {
        this(vuforiaKey, true, true);
    }

    public void activate() {
        cryptoBoxTape.activate();
        relicTrackables.activate();
    }


    /**
     * Positional data returned by tracked Vuforia objects
     */
    public class MatrixPosition {
        protected OpenGLMatrix matrix;
        public MatrixPosition(OpenGLMatrix matrix) {
            this.matrix = matrix;
        }

        /**
         * Gets the X location of the object
         * @return X location
         */
        public float getX() {
            if(matrix == null)
                return 0;
            else
                return matrix.getTranslation().getData()[0];
        }

        /**
         * Gets the Y location of the object
         * @return Y location
         */
        public float getY() {
            if(matrix == null)
                return 0;
            else
                return matrix.getTranslation().getData()[1];
        }

        /**
         * Calculates the angle from the phone to the object
         * @return the calculated angle
         */
        public float getAngle() {
            if (getY() == 0 )
                return 90.0f;
            else
                return (float)(Math.toDegrees(Math.atan(getX() / getY())));
        }
    }

    /**
     * Gets the position of the red tape
     * @return a MatrixPosition of the red tape location
     */
    public MatrixPosition getUpdatedRedPosition() {
        return new MatrixPosition(redListener.getRobotLocation());
    }

    /**
     * Determines if the red tape is visible to the camera
     * @return if the red tape is visible
     */
    public boolean redIsVisible() {
        return redListener.isVisible();
    }

    /**
     * Gets the position of the blue tape
     * @return a MatrixPosition of the blue tape location
     */
    public MatrixPosition getUpdatedBluePosition() {
        return new MatrixPosition(blueListener.getRobotLocation());
    }

    /**
     * Determines if the blue tape is visible to the camera
     * @return if the blue tape is visible
     */
    public boolean blueIsVisible() {
        return blueListener.isVisible();
    }

    /**
     * Gets the position of the cryptokey
     * @return a MatrixPosition of the cryptokey location
     */
    public MatrixPosition getUpdatedCryptoKeyPosition() {
        return new MatrixPosition(cryptoKeyListener.getRobotLocation());
    }

    /**
     * Determines if the cryptokey is visible to the camera
     * @return if the cryptokey is visible
     */
    public boolean cryptoKeyIsVisible() {
        return (RelicRecoveryVuMark.from(cryptoKey) != RelicRecoveryVuMark.UNKNOWN);
    }

    /**
     * Determines the cryptokey (or pictograph) seen by the camera
     * @return the seen pictograph, is UNKNOWN if none is seen
     */
    public RelicRecoveryVuMark cryptoKey() {
        return RelicRecoveryVuMark.from(cryptoKey);
    }

    //Helper function to more easily create matrices with given position and roation
    private OpenGLMatrix createMatrix (float x, float y, float z, float u, float v, float w) {
        return OpenGLMatrix.translation(x, y, z).rotated(AxesReference.EXTRINSIC, AxesOrder.XYZ, AngleUnit.DEGREES, u, v, w);
    }
}/*

Examples for better optimizing vuforia to be more univeral

tracables t = localize.load trackablesa

t.get(1).getpos


localzie.laodtracables()

localise.trackarray[""].get(1).getPos*/