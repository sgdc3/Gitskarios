package com.alorma.github.ui.widget.issues;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.os.Handler;
import android.widget.RemoteViews;

import com.alorma.github.R;
import com.alorma.github.ui.widget.RepoWidgetIdentifier;

import java.util.Random;

import io.realm.Realm;
import io.realm.RealmQuery;

/**
 * Created by Bernat on 15/07/2015.
 */
public class RepoIssuesWidget extends AppWidgetProvider {

    @Override
    public void onEnabled(Context context) {
        super.onEnabled(context);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        super.onUpdate(context, appWidgetManager, appWidgetIds);

        ComponentName thisWidget = new ComponentName(context, RepoIssuesWidget.class);

        int[] allWidgetIds = appWidgetManager.getAppWidgetIds(thisWidget);

        for (int allWidgetId : allWidgetIds) {
            updateAppWidget(context, appWidgetManager, allWidgetId);
        }

    }

    @Override
    public void onDisabled(Context context) {
        super.onDisabled(context);

    }

    @Override
    public void onDeleted(Context context, int[] appWidgetIds) {
        super.onDeleted(context, appWidgetIds);


        Realm realm = Realm.getInstance(context.getApplicationContext());

        realm.beginTransaction();
        for (int appWidgetId : appWidgetIds) {
            RepoWidgetIdentifier identifier = realm.where(RepoWidgetIdentifier.class).equalTo("widgetId", appWidgetId).findFirst();
            identifier.removeFromRealm();
        }
        realm.commitTransaction();
        realm.close();
    }

    public static void updateAppWidget(final Context context, final AppWidgetManager appWidgetManager,
                                       final int appWidgetId) {

        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.repo_issues_widget);
                int number = new Random().nextInt(100);
                remoteViews.setTextViewText(R.id.repo_issues_title, String.valueOf(number) + " issues");

                Realm realm = Realm.getInstance(context.getApplicationContext());

                RepoWidgetIdentifier identifier = realm.where(RepoWidgetIdentifier.class).equalTo("widgetId", appWidgetId).findFirst();

                if (identifier != null) {
                    String text = identifier.getOwner() + "/" + identifier.getRepo();
                    remoteViews.setTextViewText(R.id.repo_issues_text, text);
                }

                realm.close();
                appWidgetManager.updateAppWidget(appWidgetId, remoteViews);
            }
        };

        new Handler().postDelayed(runnable, 1000);
    }

}
