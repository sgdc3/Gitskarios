package com.alorma.github.ui.widget;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by Bernat on 15/07/2015.
 */
public class RepoWidgetIdentifier extends RealmObject{

    @PrimaryKey
    private int widgetId;
    private String owner;
    private String repo;

    public int getWidgetId() {
        return widgetId;
    }

    public void setWidgetId(int widgetId) {
        this.widgetId = widgetId;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public String getRepo() {
        return repo;
    }

    public void setRepo(String repo) {
        this.repo = repo;
    }
}
