package com.alorma.github.ui.widget;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Created by Bernat on 16/07/2015.
 */
public class UpdateWidgetsBroadcastReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Intent intentService = new Intent(context, UpdateWidgetsService.class);
        context.startService(intentService);
    }
}
