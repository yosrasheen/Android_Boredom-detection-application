package com.example.acer.iambored2;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class UserPresentBroadcastReceiver  extends BroadcastReceiver {

    public static int wasScreenOn = 1;

    @Override
    public void onReceive(Context context, Intent intent)
    {
        if (intent.getAction().equals(Intent.ACTION_USER_PRESENT)) {
            wasScreenOn = 0;
        }

        Intent i = new Intent(context, MainActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(i);
    }
}