package me.iscle.notiwatch.model;

import android.content.Intent;
import android.graphics.drawable.Drawable;

public class Functionality {
    private Drawable icon;
    private String name;
    private Intent action;

    public Functionality(Drawable icon, String name, Intent action) {
        this.icon = icon;
        this.name = name;
        this.action = action;
    }

    public Drawable getIcon() {
        return icon;
    }

    public String getName() {
        return name;
    }

    public Intent getAction() {
        return action;
    }
}
