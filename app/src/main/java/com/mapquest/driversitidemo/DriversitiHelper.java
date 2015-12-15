package com.mapquest.driversitidemo;

import android.content.Context;
import android.os.Handler;
import android.widget.TextView;

import com.apiosystems.apiosdk.driversiti.Driversiti;
import com.apiosystems.apiosdk.driversiti.DriversitiConfiguration;
import com.apiosystems.apiosdk.driversiti.DriversitiEventListener;
import com.apiosystems.apiosdk.driversiti.DriversitiException;
import com.apiosystems.apiosdk.driversiti.DriversitiSDK;
import com.apiosystems.apiosdk.event.CarModeEvent;
import com.apiosystems.apiosdk.event.CrashDetectedEvent;
import com.apiosystems.apiosdk.event.DriverDeviceHandlingEvent;
import com.apiosystems.apiosdk.event.HardBrakeEvent;
import com.apiosystems.apiosdk.event.NoOpEvent;
import com.apiosystems.apiosdk.event.PassengerDeviceHandlingEvent;
import com.apiosystems.apiosdk.event.RapidAccelerationEvent;
import com.apiosystems.apiosdk.event.SpeedExceededEvent;
import com.apiosystems.apiosdk.event.SpeedRestoredEvent;
import com.apiosystems.apiosdk.event.TripEndEvent;
import com.apiosystems.apiosdk.event.TripStartEvent;

public class DriversitiHelper {
    private static final String DRIVERSITI_APPLICATION_ID = "";

    public static DriversitiSDK setupDriversitiSDK(Context context, DriversitiConfiguration.SetupHandler setupHandler){
        DriversitiConfiguration driversitiConfiguration = new DriversitiConfiguration.ConfigurationBuilder()
                .setContext(context)
                .setApplicationId(DRIVERSITI_APPLICATION_ID)
                .setDetectionMode(DriversitiConfiguration.DetectionMode.AUTO_ON)
                .setEnabledEvents(DriversitiConfiguration.getEnabledEvents())
                .setSetupHandler(setupHandler)
                .build();

        Driversiti.setConfiguration(driversitiConfiguration);
        return Driversiti.getSDK();
    }

    public static DriversitiConfiguration.SetupHandler getSetupHandler(final MainActivity mainActivity, final TextView textView){
        return new DriversitiConfiguration.SetupHandler() {
            @Override
            public void onSetupSuccess() {
                LogHelper.updateStatus(textView, Utils.htmlGreen("Driversiti setup successfull"));

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mainActivity.checkRegistrationAndTakeAction();
                    }
                }, 1);
            }

            @Override
            public void onSetupFailure(DriversitiException errorMessage) {
                LogHelper.updateStatus(textView, Utils.htmlRed("Driversiti setup error: " + errorMessage));
            }
        };
    }

    public static DriversitiEventListener getDriversitiEventListener(final TextView textView){
        return new DriversitiEventListener("DriversitiDemo") {
            @Override
            public void onCarModeStatusChange(final CarModeEvent event) {
                LogHelper.updateStatus(textView, Utils.htmlGreen(event.getEventType().toString()));
            }

            @Override
            public void onRapidAccelerationDetected(final RapidAccelerationEvent event) {
                LogHelper.updateStatus(textView, Utils.htmlRed(event.getEventType().toString()));
            }

            @Override
            public void onHardBrakingDetected(final HardBrakeEvent event) {
                LogHelper.updateStatus(textView, Utils.htmlRed(event.getEventType().toString()));
            }

            @Override
            public void onNoOpEvent(final NoOpEvent event){
                LogHelper.updateStatus(textView, Utils.htmlBlue(event.getEventType().toString() + " speed:" + String.format("%.2f", event.getSpeed())));
            }

            @Override
            public void onCrashDetected(final CrashDetectedEvent event) {
                LogHelper.updateStatus(textView, Utils.htmlRed(event.getEventType().toString()));
            }

            @Override
            public void onDriverDeviceHandlingEvent(final DriverDeviceHandlingEvent event) {
                super.onDriverDeviceHandlingEvent(event);
                LogHelper.updateStatus(textView, Utils.htmlRed(event.getEventType().toString()));
            }

            @Override
            public void onPassengerDeviceHandlingEvent(final PassengerDeviceHandlingEvent event) {
                super.onPassengerDeviceHandlingEvent(event);
                LogHelper.updateStatus(textView, Utils.htmlBlue(event.getEventType().toString()));
            }

            @Override
            public void onSpeedExceeded(final SpeedExceededEvent event) {
                super.onSpeedExceeded(event);
                LogHelper.updateStatus(textView, Utils.htmlYellow(event.getEventType().toString()));
            }

            @Override
            public void onSafeSpeedRestored(final SpeedRestoredEvent event) {
                super.onSafeSpeedRestored(event);
                LogHelper.updateStatus(textView, Utils.htmlYellow(event.getEventType().toString()));
            }

            @Override
            public void onTripStart(final TripStartEvent event){
                super.onTripStart(event);
                LogHelper.updateStatus(textView, Utils.htmlBlue(event.getEventType().toString()));
            }

            @Override
            public void onTripEnd(final TripEndEvent event){
                super.onTripEnd(event);
                LogHelper.updateStatus(textView, Utils.htmlBlue(event.getEventType().toString()));
            }

            @Override
            public void onError(DriversitiException e) {
                LogHelper.updateStatus(textView, Utils.htmlRed("Error Encountered"));
            }
        };
    }
}