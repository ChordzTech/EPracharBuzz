package com.chordz.epracharbuzz;

import android.accessibilityservice.AccessibilityService;
import android.accessibilityservice.AccessibilityServiceInfo;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;

import androidx.core.view.accessibility.AccessibilityNodeInfoCompat;

import com.chordz.epracharbuzz.preferences.AppPreferences;

import java.util.List;

public class MyAccessibilityService extends AccessibilityService {

    private static final String TAG = "MyAccessibilityService";

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        String packageName = event.getPackageName().toString();
        if (packageName.equals("com.whatsapp.w4b")) {
            PackageManager packageManager = this.getPackageManager();
            try {
                ApplicationInfo info = packageManager.getApplicationInfo(packageName, 0);
                String name = packageManager.getApplicationLabel(info).toString();
                try {
                    AccessibilityNodeInfoCompat rootNodeInfo = AccessibilityNodeInfoCompat.wrap(getRootInActiveWindow());

                    List<AccessibilityNodeInfoCompat> messageNodeList = rootNodeInfo.findAccessibilityNodeInfosByViewId("com.whatsapp.w4b:id/entry");
                    if (messageNodeList != null && !messageNodeList.isEmpty()) {
                        AccessibilityNodeInfoCompat messageField = messageNodeList.get(0);
                        if (messageField.getText() == null || messageField.getText().length() == 0
                                || !messageField.getText().toString().endsWith(getApplicationContext().getString(R.string.whatsapp_suffix))) { // So your service doesn't process any message, but the ones ending your apps suffix
                            return;
                        }
                    }
                    // check if the whatsapp message EditText field is filled with text and ending with your suffix (explanation above)


                    List<AccessibilityNodeInfoCompat> sendMessageNodeList = rootNodeInfo.findAccessibilityNodeInfosByViewId("com.whatsapp.w4b:id/send");
                    if (sendMessageNodeList == null || sendMessageNodeList.isEmpty()) {
                        return;
                    }
                    AccessibilityNodeInfoCompat sendMessage = sendMessageNodeList.get(0);
                    if (!sendMessage.isVisibleToUser()) {
                        return;
                    }
                    if (!AppPreferences.INSTANCE.getBooleanValueFromSharedPreferences(AppPreferences.WHATSAPP_ON_OFF)) {
                       return;
                    }
                    sendMessage.performAction(AccessibilityNodeInfo.ACTION_CLICK);
                    try {
                        Thread.sleep(2000);
                        if (!AppPreferences.INSTANCE.getBooleanValueFromSharedPreferences(AppPreferences.WHATSAPP_ON_OFF)
                                || !AppPreferences.INSTANCE.getBooleanValueFromSharedPreferences(AppPreferences.WHATSAPP_ON_OFF)) {
                            // Now fire a click on the send button
                            return;
                        }
                        if (AppPreferences.INSTANCE.getBooleanValueFromSharedPreferences(AppPreferences.WHATSAPP_ON_OFF)
                        ) {
                            performGlobalAction(GLOBAL_ACTION_BACK);
                            performGlobalAction(GLOBAL_ACTION_BACK);
                        }
                        //add below line, if u want to close whatsApp;
                        //performGlobalAction(GLOBAL_ACTION_BACK);
                        Thread.sleep(2000);
                    } catch (Exception e) {
                        Log.e("onAccessibilityEvent", "onAccessibilityEvent: ", e);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                Log.e(TAG, "onAccessibilityEvent: " + name);
                if (!AppPreferences.INSTANCE.getBooleanValueFromSharedPreferences(AppPreferences.WHATSAPP_ON_OFF)
                        || !AppPreferences.INSTANCE.getBooleanValueFromSharedPreferences(AppPreferences.WHATSAPP_ON_OFF)) {
                    // Now fire a click on the send button
                    return;
                }
//                performGlobalAction(GLOBAL_ACTION_BACK);
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onInterrupt() {

    }

    @Override
    protected void onServiceConnected() {
        super.onServiceConnected();
        AccessibilityServiceInfo info = new AccessibilityServiceInfo();
        info.eventTypes = AccessibilityEvent.TYPE_VIEW_CLICKED |
                AccessibilityEvent.TYPE_VIEW_FOCUSED;
        info.packageNames = new String[]
                {"com.whatsapp.w4b"};
        info.feedbackType = AccessibilityServiceInfo.FEEDBACK_SPOKEN;
        this.setServiceInfo(info);
        Log.d(TAG, "Accessibility service connected");
    }

}
