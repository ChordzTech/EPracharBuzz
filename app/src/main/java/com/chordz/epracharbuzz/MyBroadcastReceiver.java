package com.chordz.epracharbuzz;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;import com.chordz.epracharbuzz.MyForegroundService;

public class MyBroadcastReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED)){
            Intent serviceIntent=new Intent(context, MyForegroundService.class);
            context.startForegroundService(serviceIntent);
        }
    }
}
