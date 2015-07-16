package com.alorma.github.ui.widget.issues;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.widget.RemoteViews;

import com.alorma.github.R;
import com.alorma.github.basesdk.client.BaseClient;
import com.alorma.github.sdk.bean.dto.response.Repo;
import com.alorma.github.sdk.bean.info.RepoInfo;
import com.alorma.github.sdk.services.repo.GetRepoClient;
import com.alorma.github.ui.widget.RepoWidgetIdentifier;
import com.alorma.github.ui.widget.UpdateWidgetsBroadcastReceiver;

import io.realm.Realm;
import io.realm.RealmResults;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by Bernat on 15/07/2015.
 */
public class RepoIssuesWidget extends AppWidgetProvider {

    @Override
    public void onEnabled(Context context) {
        super.onEnabled(context);

        ComponentName receiver = new ComponentName(context, UpdateWidgetsBroadcastReceiver.class);
        PackageManager pm = context.getPackageManager();

        pm.setComponentEnabledSetting(receiver,
                PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
                PackageManager.DONT_KILL_APP);
    }

    @Override
    public void onDeleted(Context context, int[] appWidgetIds) {
        super.onDeleted(context, appWidgetIds);


        Realm realm = Realm.getInstance(context.getApplicationContext());

        realm.beginTransaction();
        for (int appWidgetId : appWidgetIds) {
            RepoWidgetIdentifier identifier = realm.where(RepoWidgetIdentifier.class).equalTo("widgetId", appWidgetId).findFirst();
            if (identifier != null) {
                identifier.removeFromRealm();
            }
        }
        realm.commitTransaction();
        realm.close();
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        super.onUpdate(context, appWidgetManager, appWidgetIds);

        Realm realm = Realm.getInstance(context.getApplicationContext());

        for (int appWidgetId : appWidgetIds) {
            RepoWidgetIdentifier widgetId = realm.where(RepoWidgetIdentifier.class).equalTo("widgetId", appWidgetId).findFirst();

            if (widgetId != null) {
                RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.repo_issues_widget);

                remoteViews.setTextViewText(R.id.repo_issues_title, String.valueOf(widgetId.getOpenIssuesCount()));

                appWidgetManager.updateAppWidget(widgetId.getWidgetId(), remoteViews);
            }

        }


    }

    @Override
    public void onDisabled(Context context) {
        super.onDisabled(context);

        Intent intentService = new Intent(context, UpdateWidgetsIssuesService.class);
        PendingIntent alarmIntent = PendingIntent.getBroadcast(context, 0, intentService, 0);
        AlarmManager alarmMgr = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarmMgr.cancel(alarmIntent);

        ComponentName receiver = new ComponentName(context, UpdateWidgetsBroadcastReceiver.class);
        PackageManager pm = context.getPackageManager();

        pm.setComponentEnabledSetting(receiver,
                PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
                PackageManager.DONT_KILL_APP);
    }
}
