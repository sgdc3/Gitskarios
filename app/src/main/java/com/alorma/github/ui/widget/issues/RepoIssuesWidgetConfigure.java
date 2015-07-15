package com.alorma.github.ui.widget.issues;

import android.appwidget.AppWidgetManager;
import android.content.Intent;
import android.os.Bundle;

import com.alorma.github.R;
import com.alorma.github.ui.activity.base.BackActivity;
import com.alorma.github.ui.widget.RepoWidgetIdentifier;

import io.realm.Realm;
import io.realm.RealmResults;

/**
 * Created by Bernat on 15/07/2015.
 */
public class RepoIssuesWidgetConfigure extends BackActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.repo_issues_widget_configure);

        setResult(RESULT_CANCELED);

        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(this);

        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        int mAppWidgetId = AppWidgetManager.INVALID_APPWIDGET_ID;
        if (extras != null) {
            mAppWidgetId = extras.getInt(
                    AppWidgetManager.EXTRA_APPWIDGET_ID,
                    AppWidgetManager.INVALID_APPWIDGET_ID);
        }

        // If they gave us an intent without the widget id, just bail.
        if (mAppWidgetId == AppWidgetManager.INVALID_APPWIDGET_ID) {
            finish();
        }

        RepoWidgetIdentifier identifier = new RepoWidgetIdentifier();
        identifier.setWidgetId(mAppWidgetId);
        identifier.setOwner("gitskarios");
        identifier.setRepo("Gitskarios");

        Realm realm = Realm.getInstance(getApplicationContext());
        realm.beginTransaction();

        realm.copyToRealmOrUpdate(identifier);

        realm.commitTransaction();

        realm.close();
        realm = null;

        RepoIssuesWidget.updateAppWidget(this, appWidgetManager, mAppWidgetId);

        Intent resultValue = new Intent();
        resultValue.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, mAppWidgetId);
        setResult(RESULT_OK, resultValue);
        finish();
    }
}
