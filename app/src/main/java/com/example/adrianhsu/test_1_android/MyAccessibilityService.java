package com.example.adrianhsu.test_1_android;

/**
 * Created by AdrianHsu on 15/7/20.
 */
import android.accessibilityservice.AccessibilityService;
import android.accessibilityservice.AccessibilityServiceInfo;
import android.app.Notification;
import android.os.Parcelable;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;
import android.widget.Toast;
import android.os.Build;
import android.content.ComponentName;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;

public class MyAccessibilityService extends AccessibilityService {

    public static String currentPackageName = "";
    public static long beginTime = 0;
    public static long endTime = 0;
    public static long interval = 0;
    public static boolean ignoring = false;
    public static final String TAG = "MyAccessibilityService";

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {

        if (event.getEventType() == AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED) {

            Log.v(TAG, "***** onAccessibilityEvent");
//        Toast.makeText(getApplicationContext(), "Got event from: " + event.getPackageName(), Toast.LENGTH_LONG).show();

            String tempPackageName = event.getPackageName().toString();

            if(currentPackageName.contentEquals("")) { // first time
                currentPackageName = tempPackageName;
                beginTime = System.currentTimeMillis();
                return;
            }
            if (tempPackageName.contentEquals("com.android.systemui") ||
                    tempPackageName.contentEquals("com.asus.launcher")) {

                if (ignoring) {
                    return;
                }
                endTime = System.currentTimeMillis();
                ignoring = true;
            }
            else {
                if (ignoring) {
                    ignoring = false;
                } else {
                    if (tempPackageName.contentEquals(currentPackageName)) {
                        return;
                    }
                    endTime = System.currentTimeMillis();
                }

                interval = endTime - beginTime;
                beginTime = System.currentTimeMillis();
                Log.v(TAG, "PackageName: " + currentPackageName + ", interval: " + (interval / 1000));
                currentPackageName = event.getPackageName().toString();
            }
        }
    }
    @Override
    public void onInterrupt()
    {
        Log.v(TAG, "***** onInterrupt");
    }

    @Override
    public void onServiceConnected()
    {
        Log.v(TAG, "***** onServiceConnected");

        AccessibilityServiceInfo info = new AccessibilityServiceInfo();
        info.eventTypes = AccessibilityEvent.TYPE_NOTIFICATION_STATE_CHANGED;
        info.notificationTimeout = 100;
        info.feedbackType = AccessibilityEvent.TYPES_ALL_MASK;
        setServiceInfo(info);

        super.onServiceConnected();
        //Configure these here for compatibility with API 13 and below.
        AccessibilityServiceInfo config = new AccessibilityServiceInfo();
        config.eventTypes = AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED;
        config.feedbackType = AccessibilityServiceInfo.FEEDBACK_GENERIC;

        if (Build.VERSION.SDK_INT >= 16)
            //Just in case this helps
            config.flags = AccessibilityServiceInfo.FLAG_INCLUDE_NOT_IMPORTANT_VIEWS;

        setServiceInfo(config);
    }
}