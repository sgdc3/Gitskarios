package com.alorma.github.ui.widget.issues;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.os.Handler;
import android.widget.RemoteViews;

import com.alorma.github.R;
import com.alorma.github.basesdk.client.BaseClient;
import com.alorma.github.sdk.bean.dto.response.Repo;
import com.alorma.github.sdk.bean.info.RepoInfo;
import com.alorma.github.sdk.services.repo.GetRepoClient;
import com.alorma.github.ui.widget.RepoWidgetIdentifier;

import io.realm.Realm;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by Bernat on 15/07/2015.
 */
public class RepoIssuesWidget extends AppWidgetProvider {

    @Override
    public void onEnabled(Context context) {
        super.onEnabled(context);

        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
        int[] appWidgetIds = appWidgetManager.getAppWidgetIds(new ComponentName(context, RepoIssuesWidget.class));
        updateWidgets(context, appWidgetManager, appWidgetIds);
    }

    @Override
    public void onUpdate(final Context context, final AppWidgetManager appWidgetManager, final int[] appWidgetIds) {
        super.onUpdate(context, appWidgetManager, appWidgetIds);

        Runnable runnable = new Runnable() {

            @Override
            public void run() {
                updateWidgets(context, appWidgetManager, appWidgetIds);


                new Handler().postDelayed(this, 1000);
            }
        };

        new Handler().postDelayed(runnable, 1000);

        updateWidgets(context, appWidgetManager, appWidgetIds);
    }

    private void updateWidgets(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        Realm realm = Realm.getInstance(context);
        for (int appWidgetId : appWidgetIds) {
            RepoWidgetIdentifier identifier = realm.where(RepoWidgetIdentifier.class).equalTo("widgetId", appWidgetId).findFirst();
            if (identifier != null && identifier.getOwner() != null && identifier.getRepo() != null) {
                RepoInfo repoInfo = new RepoInfo();
                repoInfo.owner = identifier.getOwner();
                repoInfo.name = identifier.getRepo();

                GetRepoClient getRepoClient = new GetRepoClient(context, repoInfo);
                getRepoClient.setOnResultCallback(new RepoCallback(context, appWidgetManager, identifier.getWidgetId()));
                getRepoClient.execute();
            }
        }
    }

    private class RepoCallback implements BaseClient.OnResultCallback<Repo> {

        private final Context context;
        private AppWidgetManager appWidgetManager;
        private final int widgetId;

        public RepoCallback(Context context, AppWidgetManager appWidgetManager, int widgetId) {
            this.context = context;
            this.appWidgetManager = appWidgetManager;
            this.widgetId = widgetId;
        }

        @Override
        public void onResponseOk(Repo repo, Response r) {
            RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.repo_issues_widget);

            remoteViews.setTextViewText(R.id.repo_issues_title, String.valueOf(repo.open_issues_count));

            appWidgetManager.updateAppWidget(widgetId, remoteViews);
        }

        @Override
        public void onFail(RetrofitError error) {
            RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.repo_issues_widget);

            remoteViews.setTextViewText(R.id.repo_issues_title, "XX");

            appWidgetManager.updateAppWidget(widgetId, remoteViews);
        }
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
}
