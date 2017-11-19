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

/**
 * Created by Robotics on 10/21/2017.
 */

public class RelicRecoveryLocalizer {
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

    public  RelicRecoveryLocalizer(String vuforiaKey, boolean extendedTracking, boolean cameraPreview) {
        parameters = (cameraPreview ? new VuforiaLocalizer.Parameters(R.id.cameraMonitorViewId) : new VuforiaLocalizer.Parameters());
        parameters.cameraDirection = VuforiaLocalizer.CameraDirection.BACK;
        parameters.vuforiaLicenseKey = vuforiaKey;
        parameters.useExtendedTracking = extendedTracking;
        parameters.cameraMonitorFeedback = VuforiaLocalizer.Parameters.CameraMonitorFeedback.AXES;

        vuforia = ClassFactory.createVuforiaLocalizer(parameters);
        cryptoBoxTape = vuforia.loadTrackablesFromAsset("RelicRecoveryTapeLinesSmall");
        blueTape = cryptoBoxTape.get(0);
        blueTape.setName("blueSmall");
        blueTape.setLocation(createMatrix(0, 0, 0, 90, 0, 0));
        redTape = cryptoBoxTape.get(1);
        redTape.setName("redSmall");
        redTape.setLocation(createMatrix(0, 0, 0, 90, 0, 0));
        relicTrackables = this.vuforia.loadTrackablesFromAsset("RelicVuMark");
        cryptoKey = relicTrackables.get(0);
        cryptoKey.setName("Relic");
        cryptoKey.setLocation(createMatrix(0, 0, 0, 90, 0, 0));

        OpenGLMatrix phoneLoc = createMatrix(0, 0, 0, -90, 0, 0);

        blueListener = ((VuforiaTrackableDefaultListener)blueTape.getListener());
        blueListener.setPhoneInformation(phoneLoc, parameters.cameraDirection);

        redListener = ((VuforiaTrackableDefaultListener)redTape.getListener());
        redListener.setPhoneInformation(phoneLoc, parameters.cameraDirection);

        cryptoKeyListener = ((VuforiaTrackableDefaultListener) cryptoKey.getListener());
        cryptoKeyListener.setPhoneInformation(phoneLoc, parameters.cameraDirection);

        Vuforia.setHint(HINT.HINT_MAX_SIMULTANEOUS_IMAGE_TARGETS, 1);
    }
    
    public  RelicRecoveryLocalizer(String vuforiaKey, boolean extendedTracking) {
        this(vuforiaKey, extendedTracking, true);
    }
    
    public  RelicRecoveryLocalizer(String vuforiaKey) {
        this(vuforiaKey, true, true);
    }

    public void activate() {
        cryptoBoxTape.activate();
        relicTrackables.activate();
    }
    
    public class MatrixPosition {
        OpenGLMatrix matrix;
        public MatrixPosition(OpenGLMatrix matrix) {
            this.matrix = matrix;
        }
        
        public float getX() {
            return matrix.getTranslation().getData()[0];
        }
        
        public float getY() {
            return matrix.getTranslation().getData()[1];
        }
        
        public float getAngle() {
            return (float)(Math.toDegrees(Math.atan(getX() / getY())));
        }
    }
    
    public MatrixPosition getUpdatedRedPosition() {
        return new MatrixPosition(redListener.getUpdatedRobotLocation());
    }
    
    public boolean redIsVisible() {
        return redListener.isVisible();
    }
    
    public MatrixPosition getUpdatedBluePosition() {
        return new MatrixPosition(blueListener.getUpdatedRobotLocation());
    }
    
    public boolean blueIsVisible() {
        return blueListener.isVisible();
    }

    public MatrixPosition getUpdatedCryptoKeyPosition() {
        return new MatrixPosition(cryptoKeyListener.getUpdatedRobotLocation());
    }
    
    public boolean cryptoKeyIsVisible() {
        return (RelicRecoveryVuMark.from(cryptoKey) != RelicRecoveryVuMark.UNKNOWN);
    }
    
    public RelicRecoveryVuMark cryptoKey() {
        return RelicRecoveryVuMark.from(cryptoKey);
    }

    private OpenGLMatrix createMatrix (float x, float y, float z, float u, float v, float w) {
        return OpenGLMatrix.translation(x, y, z).rotated(AxesReference.EXTRINSIC, AxesOrder.XYZ, AngleUnit.DEGREES, u, v, w);
    }
}
