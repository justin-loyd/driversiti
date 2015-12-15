package com.mapquest.driversitidemo;

import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.apiosystems.apiosdk.ApioCloud.OnCompleteTaskHandler;
import com.apiosystems.apiosdk.Enums.ApioEventType;
import com.apiosystems.apiosdk.driversiti.Driversiti;
import com.apiosystems.apiosdk.driversiti.DriversitiEventListener;
import com.apiosystems.apiosdk.driversiti.DriversitiSDK;
import com.apiosystems.apiosdk.driversiti.UserManager;
import com.apiosystems.apiosdk.driversiti.data.User;
import com.apiosystems.apiosdk.driversiti.data.UserBuilder;

public class MainActivity extends AppCompatActivity {
    private static final String LOG_TAG = MainActivity.class.getSimpleName();
    private static final boolean DEMO_FIRE_EVENTS = false;

    private DriversitiSDK mDriversitiSDK;
    private DriversitiEventListener mEventListener;
    private TextView mDetectionsTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.email_fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LogHelper.emailLogFile(MainActivity.this);
            }
        });

        mDetectionsTextView = (TextView) findViewById(R.id.textview_detections);
        mDetectionsTextView.setMovementMethod(new ScrollingMovementMethod());

        mDriversitiSDK = DriversitiHelper.setupDriversitiSDK(this, DriversitiHelper.getSetupHandler(this, mDetectionsTextView));

        mEventListener = DriversitiHelper.getDriversitiEventListener(mDetectionsTextView);
        mDriversitiSDK.addEventListener(mEventListener);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_start) {
            LogHelper.updateStatus(mDetectionsTextView, "User Clicked Start");
            mDriversitiSDK.start();
            mDriversitiSDK.startTrip();
            return true;
        }
        if (id == R.id.action_stop) {
            LogHelper.updateStatus(mDetectionsTextView, "User Clicked Stop");
            mDriversitiSDK.endTrip();
            mDriversitiSDK.stop();
            return true;
        }

        if(id == R.id.action_debug) {
            LogHelper.updateStatus(mDetectionsTextView, "User Clicked Debug");
            mDriversitiSDK.fireEventDebug(ApioEventType.LOCATION_UPDATE);
            mDriversitiSDK.fireEventDebug(ApioEventType.SPEED_THRESHOLD_EXCEEDED);
            mDriversitiSDK.fireEventDebug(ApioEventType.SPEED_THRESHOLD_RESTORED);
            mDriversitiSDK.fireEventDebug(ApioEventType.HARD_ACCELERATION);
            mDriversitiSDK.fireEventDebug(ApioEventType.HARD_DECELERATION);
            mDriversitiSDK.fireEventDebug(ApioEventType.CRASH_DETECTED);
            mDriversitiSDK.fireEventDebug(ApioEventType.DRIVER_DEVICE_HANDLING);
            mDriversitiSDK.fireEventDebug(ApioEventType.PASSENGER_DEVICE_HANDLING);
            mDriversitiSDK.fireEventDebug(ApioEventType.TRIP_END);
        }

        return super.onOptionsItemSelected(item);
    }

    public void checkRegistrationAndTakeAction() {
        if(checkRegistration()){
            if(DEMO_FIRE_EVENTS){
                mDriversitiSDK.start();
                mDriversitiSDK.startTrip();
                fireEventDebug();
            }
        }
    }

    private boolean checkRegistration() {
        if(!Utils.isUserRegistered()) {
            registerUser();
            return false;
        }
        LogHelper.updateStatus(mDetectionsTextView, Utils.htmlGreen("User Already Registered"));
        return true;
    }

    private void fireEventDebug(){
        HandlerThread handlerThread = new HandlerThread("MainActivity Handler Thread");
        handlerThread.start();
        final Handler handler = new Handler(handlerThread.getLooper());
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Log.i(LOG_TAG, "mDriversitiSDK.fireEventDebug: ");
                mDriversitiSDK.fireEventDebug(ApioEventType.HARD_ACCELERATION);
                mDriversitiSDK.fireEventDebug(ApioEventType.HARD_DECELERATION);
                mDriversitiSDK.fireEventDebug(ApioEventType.CRASH_DETECTED);
                mDriversitiSDK.fireEventDebug(ApioEventType.TRIP_END);
                handler.postDelayed(this, 5000);
            }
        }, 5000);
    }

    private UserManager mUserManager;

    private void registerUser(){
        final User userRegistrationRequest = getApioUserData();
        mUserManager = Driversiti.getSDK().getUserManager();

        OnCompleteTaskHandler callback = new OnCompleteTaskHandler() {
            @Override
            public void onSuccess(Object result) {
                User newUser = null;
                if (result != null && result instanceof User) {
                    newUser = (User) result;
                } else {
                    LogHelper.updateStatus(mDetectionsTextView, Utils.htmlRed("User Registration Unsuccessful"));
                    return;
                }
                mUserManager.loginUser(newUser);
                LogHelper.updateStatus(mDetectionsTextView, Utils.htmlGreen("User Registration Successful"));
            }
            @Override
            public void onFailure(Exception errorMessage) {
                LogHelper.updateStatus(mDetectionsTextView, Utils.htmlRed("User Registration Failed: " + errorMessage));
            }
        };
        mUserManager.addUser(userRegistrationRequest, callback);
    }

    private User getApioUserData(){
        return new UserBuilder().create();
    }
}
