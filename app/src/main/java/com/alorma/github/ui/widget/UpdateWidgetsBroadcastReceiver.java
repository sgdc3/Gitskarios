package com.alorma.github.ui.widget;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.alorma.github.ui.widget.issues.UpdateWidgetsIssuesService;

import java.util.concurrent.TimeUnit;

/**
 * Created by Bernat on 16/07/2015.
 */
public class UpdateWidgetsBroadcastReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i("ALORMA-WIDGET", "Widget : " + intent.getAction());
        if (intent.getAction().equals("android.intent.action.BOOT_COMPLETED")
                || intent.getAction().equals("alorma.github.WIDGET_CREATED")
                || intent.getAction().equals("android.intent.action.PACKAGE_ADDED")) {
            Intent intentService = new Intent(context, UpdateWidgetsIssuesService.class);
            PendingIntent alarmIntent = PendingIntent.getBroadcast(context, 0, intentService, PendingIntent.FLAG_NO_CREATE);

            if (alarmIntent == null) {
                alarmIntent = PendingIntent.getBroadcast(context, 0, intentService, PendingIntent.FLAG_CANCEL_CURRENT);
                AlarmManager alarmMgr = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
                alarmMgr.setInexactRepeating(AlarmManager.ELAPSED_REALTIME,
                        TimeUnit.MINUTES.toMillis(1),
                        TimeUnit.MINUTES.toMillis(1), alarmIntent);

            }
            context.startService(intentService);
        }
    }
}
