package com.alorma.github.ui.widget.issues;

import android.app.Service;
import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.RemoteViews;

import com.alorma.github.R;
import com.alorma.github.basesdk.client.BaseClient;
import com.alorma.github.sdk.bean.dto.response.Repo;
import com.alorma.github.sdk.bean.info.RepoInfo;
import com.alorma.github.sdk.services.repo.GetRepoClient;
import com.alorma.github.ui.widget.RepoWidgetIdentifier;

import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmResults;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by Bernat on 16/07/2015.
 */
public class UpdateWidgetsIssuesService extends Service {
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        init();
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        init();
    }

    private void init() {
        Realm realm = Realm.getInstance(getApplicationContext());
        RealmResults<RepoWidgetIdentifier> widgetIds = realm.where(RepoWidgetIdentifier.class).findAll();


        List<RepoWidgetIdentifier> repoWidgetIdentifiers = new ArrayList<>();
        for (RepoWidgetIdentifier widgetId : widgetIds) {

            if (widgetId.getOwner() != null && widgetId.getRepo() != null) {
                RepoWidgetIdentifier identifier = new RepoWidgetIdentifier();
                identifier.setWidgetId(widgetId.getWidgetId());
                identifier.setOwner(widgetId.getOwner());
                identifier.setRepo(widgetId.getRepo());
                repoWidgetIdentifiers.add(identifier);
            }
        }

        realm.close();

        for (RepoWidgetIdentifier widgetId : repoWidgetIdentifiers) {

            Log.i("ALORMA-WIDGET", "Widget : " + widgetToString(widgetId));

            if (widgetId.getOwner() != null && widgetId.getRepo() != null) {
                RepoInfo repoInfo = new RepoInfo();
                repoInfo.owner = widgetId.getOwner();
                repoInfo.name = widgetId.getRepo();

                GetRepoClient getRepoClient = new GetRepoClient(this, repoInfo);
                getRepoClient.setOnResultCallback(new RepoCallback(this, widgetId.getWidgetId()));
                getRepoClient.execute();
            }
        }
    }

    public String widgetToString(RepoWidgetIdentifier widgetId) {
        final StringBuffer sb = new StringBuffer("RepoWidgetIdentifier{");
        sb.append("widgetId=").append(widgetId.getWidgetId());
        sb.append(", owner='").append(widgetId.getOwner()).append('\'');
        sb.append(", repo='").append(widgetId.getRepo()).append('\'');
        sb.append('}');
        return sb.toString();
    }

    private class RepoCallback implements BaseClient.OnResultCallback<Repo> {

        private final Context context;
        private final int widgetId;

        public RepoCallback(Context context, int widgetId) {
            this.context = context;
            this.widgetId = widgetId;
        }

        @Override
        public void onResponseOk(Repo repo, Response r) {
            AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
            RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.repo_issues_widget);

            remoteViews.setTextViewText(R.id.repo_issues_title, String.valueOf(repo.open_issues_count));

            Realm realm = Realm.getInstance(context.getApplicationContext());
            RepoWidgetIdentifier first = realm.where(RepoWidgetIdentifier.class).equalTo("widgetId", widgetId).findFirst();

            if (first != null) {
                RepoWidgetIdentifier newItem = new RepoWidgetIdentifier();
                newItem.setWidgetId(first.getWidgetId());
                newItem.setRepo(first.getRepo());
                newItem.setOwner(first.getOwner());
                newItem.setOpenIssuesCount(repo.open_issues_count);

                realm.beginTransaction();
                realm.copyToRealmOrUpdate(newItem);
                realm.commitTransaction();
                realm.close();
            }

            appWidgetManager.updateAppWidget(this.widgetId, remoteViews);
        }

        @Override
        public void onFail(RetrofitError error) {
            AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
            RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.repo_issues_widget);

            remoteViews.setTextViewText(R.id.repo_issues_title, "XX");

            appWidgetManager.updateAppWidget(widgetId, remoteViews);
        }
    }
}
