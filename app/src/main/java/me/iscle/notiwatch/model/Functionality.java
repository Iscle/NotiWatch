package me.iscle.notiwatch.model;

import android.app.Activity;

public class Functionality {
    private int icon;
    private String name;
    private Class<? extends Activity> activity;

    public Functionality(int icon, String name, Class<? extends Activity> activity) {
        this.icon = icon;
        this.name = name;
        this.activity = activity;
    }

    public int getIcon() {
        return icon;
    }

    public String getName() {
        return name;
    }

    public Class<? extends Activity> getActivity() {
        return activity;
    }
}
