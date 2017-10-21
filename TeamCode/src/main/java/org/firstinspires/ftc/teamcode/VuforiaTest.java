package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.vuforia.HINT;
import com.vuforia.Vuforia;

import org.firstinspires.ftc.robotcore.external.ClassFactory;
import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.robotcore.external.matrices.OpenGLMatrix;
import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.AxesOrder;
import org.firstinspires.ftc.robotcore.external.navigation.AxesReference;
import org.firstinspires.ftc.robotcore.external.navigation.Orientation;
import org.firstinspires.ftc.robotcore.external.navigation.RelicRecoveryVuMark;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaLocalizer;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaTrackable;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaTrackableDefaultListener;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaTrackables;

/**
 * Created by Robotics on 10/17/2017.
 */


@TeleOp(name = "Vuforia Test", group = "Test")
public class VuforiaTest extends LinearOpMode {

    protected VuforiaLocalizer vuforia;
    protected VuforiaLocalizer.Parameters parameters;
    protected OpenGLMatrix lastLocation;
    protected VuforiaTrackable obj;
    protected VuforiaTrackable blueTape;
    protected VuforiaTrackableDefaultListener listener;
    protected VuforiaTrackableDefaultListener relicListener;
    protected VuforiaTrackables objects;
    protected VuforiaTrackables tapes;
    protected VuforiaTrackables relicTrackables;
    protected VuforiaTrackable relicTemplate;

    protected float x = 0;
    protected float y = 0;
    protected float thirdAngle = 0;

    protected static final String VUFORIA_KEY = "AeCNMrn/////AAAAGRlPvGpkjUVapbG0iA01W9pxODQbY2cczmmaGy8CmYxrxKgX4Vf4DTayzCXCJeYBCtDVd5iWQFKFtnbAlSlvIqJmcUnLOF79x5QwSpMX9hJER259y94/" +
            "bdZGZYj9XRg07DZZOpFwAERjcIH6HBVJcTG6/M+oLw4ObLbiY0EqZhZA6app2Tep5BDzsDSI9DwWrR2LqqPxJSRwwGqxqlkja+u3ggLEQmWalqr2n20ywTZUpHvqtBuP53AgnJZCs4HNc57+XhhjkJWLIBnb3HBPZAZMA4uZfAq" +
            "I1uP8E1L+wgiAGretWwRrO3X/frXXIi5IJU9JDx52szfHeOr8kYBekeA/Ir5RygBs6yUNDPsepHkq";

    public void runOpMode() throws InterruptedException {
        vuforiaSetUp();

        waitForStart();
        objects.activate();
        relicTrackables.activate();

        x = 0;
        y = 0;
        thirdAngle = 0;


        while (opModeIsActive()) {

            telemetry.addData(obj.getName(), listener.isVisible() ? "Visible" : "Not Visible");

            OpenGLMatrix robotLocationTransform = listener.getUpdatedRobotLocation();
            if (robotLocationTransform != null) {
                lastLocation = robotLocationTransform;
                float[] coords = lastLocation.getTranslation().getData();

                x = Orientation.getOrientation(lastLocation, AxesReference.EXTRINSIC, AxesOrder.XYZ, AngleUnit.DEGREES).firstAngle;
                y = Orientation.getOrientation(lastLocation, AxesReference.EXTRINSIC, AxesOrder.XYZ, AngleUnit.DEGREES).secondAngle;
                thirdAngle = Orientation.getOrientation(lastLocation, AxesReference.EXTRINSIC, AxesOrder.XYZ, AngleUnit.DEGREES).thirdAngle;

            }

            telemetry.addData(obj.getName(), "x: " + x + " y: " + y + " angle: " + thirdAngle);

            if (lastLocation != null) {
                telemetry.addData("Pos", lastLocation.formatAsTransform());
            } else {
                telemetry.addData("Pos", "Unknown");
            }

            RelicRecoveryVuMark vuMark = RelicRecoveryVuMark.from(relicTemplate);
            if (vuMark != RelicRecoveryVuMark.UNKNOWN) {
                telemetry.addData("VuMark", "%s visible", vuMark);
            } else
                telemetry.addData("VuMark", "not visible");
            telemetry.update();
            idle();
        }
    }

    public void vuforiaSetUp() {
        parameters = new VuforiaLocalizer.Parameters(R.id.cameraMonitorViewId);
        parameters.cameraDirection = VuforiaLocalizer.CameraDirection.BACK;
        parameters.vuforiaLicenseKey = VUFORIA_KEY;
        parameters.useExtendedTracking = true;

        vuforia = ClassFactory.createVuforiaLocalizer(parameters);
        objects = vuforia.loadTrackablesFromAsset("FTC_2016-17");
        obj = objects.get(0);
        obj.setName("Test");
        obj.setLocation(createMatrix(100, 100, 100, 90, 0, 0));

        relicTrackables = this.vuforia.loadTrackablesFromAsset("RelicVuMark");
        relicTemplate = relicTrackables.get(0);
        relicTemplate.setName("Relic");
        relicTemplate.setLocation(createMatrix(0, 0, 0, 90, 0, 0));

        OpenGLMatrix phoneLoc = createMatrix(0, 0, 0, -90, 0, 0);
        listener = ((VuforiaTrackableDefaultListener)obj.getListener());
        listener.setPhoneInformation(phoneLoc, parameters.cameraDirection);

        relicListener = ((VuforiaTrackableDefaultListener)relicTemplate.getListener());
        relicListener.setPhoneInformation(phoneLoc, parameters.cameraDirection);

        Vuforia.setHint(HINT.HINT_MAX_SIMULTANEOUS_IMAGE_TARGETS, 1);
    }

    public OpenGLMatrix createMatrix (float x, float y, float z, float u, float v, float w) {
        return OpenGLMatrix.translation(x, y, z).rotated(AxesReference.EXTRINSIC, AxesOrder.XYZ, AngleUnit.DEGREES, u, v, w);
    }


    //TODO:get x y and angle and is visible(relative to blue tape or red tape)get currently seen cryptokey as well as is visible function

    public void setupBlueTape() {
        tapes = vuforia.loadTrackablesFromAsset("FTC_2016-17"); //Upload Images of Red and Blue tape
        blueTape = objects.get(0);
        blueTape.setName("BlueTape");
        blueTape.setLocation(createMatrix(100, 100, 100, 90, 0, 0));
    }

    public void getBlueX(){
        x = Orientation.getOrientation(lastLocation, AxesReference.EXTRINSIC, AxesOrder.XYZ, AngleUnit.DEGREES).firstAngle;
        telemetry.addData(Bl)
    }
    public void getBlueY(){

    }
    public void getBlueAngle(){

    }
    public void getRedX(){

    }
    public void getRedy(){

    }
    public void getRedAngle(){

    }
}






