package org.firstinspires.ftc.teamcode;

import com.vuforia.HINT;
import com.vuforia.Vuforia;

import org.firstinspires.ftc.robotcore.external.ClassFactory;
import org.firstinspires.ftc.robotcore.external.matrices.OpenGLMatrix;
import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.AxesOrder;
import org.firstinspires.ftc.robotcore.external.navigation.AxesReference;
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
    protected VuforiaTrackables CryptoBoxTape;
    protected VuforiaTrackables relicTrackables;
    protected VuforiaTrackable cryptoKey;
    protected VuforiaTrackable blueTape;
    protected VuforiaTrackable redTape;

    public  RelicRecoveryLocalizer(String vuforiaKey, boolean extendedTracking) {
        parameters = new VuforiaLocalizer.Parameters(R.id.cameraMonitorViewId);
        parameters.cameraDirection = VuforiaLocalizer.CameraDirection.BACK;
        parameters.vuforiaLicenseKey = vuforiaKey;
        parameters.useExtendedTracking = extendedTracking;

        vuforia = ClassFactory.createVuforiaLocalizer(parameters);
        CryptoBoxTape = vuforia.loadTrackablesFromAsset("FTC_2016-17");
        blueTape = CryptoBoxTape.get(0);
        blueTape.setName("Blue Cryptobox Tape");
        blueTape.setLocation(createMatrix(0, 0, 0, 90, 0, 0));
        redTape = CryptoBoxTape.get(1);
        redTape.setName("Red Cryptobox Tape");
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

    public OpenGLMatrix createMatrix (float x, float y, float z, float u, float v, float w) {
        return OpenGLMatrix.translation(x, y, z).rotated(AxesReference.EXTRINSIC, AxesOrder.XYZ, AngleUnit.DEGREES, u, v, w);
    }
}
