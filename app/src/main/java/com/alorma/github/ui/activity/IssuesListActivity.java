package com.alorma.github.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.widget.CompoundButton;
import android.widget.Switch;

import com.alorma.github.R;
import com.alorma.github.sdk.bean.info.RepoInfo;
import com.alorma.github.ui.activity.base.BackActivity;
import com.alorma.github.ui.fragment.issues.IssuesListFragment;

/**
 * Created by Bernat on 12/06/2015.
 */
public class IssuesListActivity extends BackActivity {

    private static final String REPO_INFO = "REPO_INFO";

    public static Intent createLauncher(Context context, RepoInfo repoInfo) {
        Intent intent = new Intent(context, IssuesListActivity.class);

        Bundle args = new Bundle();
        args.putParcelable(REPO_INFO, repoInfo);
        intent.putExtras(args);

        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.issue_list_activity);

        CompoundButton stateSwitcher = (CompoundButton) findViewById(R.id.stateSwitcher);
        stateSwitcher.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    buttonView.setText("Open issues");
                } else {
                    buttonView.setText("Closed issues");
                }
            }
        });

        if (getIntent() != null && getIntent().getExtras() != null && getIntent().getExtras().containsKey(REPO_INFO)) {
            RepoInfo repoInfo = getIntent().getExtras().getParcelable(REPO_INFO);

            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.content, IssuesListFragment.newInstance(repoInfo, false));
            ft.commit();
        }
    }
}
