package me.iscle.notiwatch;

import java.util.HashMap;

public class NotificationManager {
    private static final String TAG = "NotificationManager";

    private HashMap<String, HashMap<Integer, Integer>> currentNotifications;

    private NotificationManager() {
        // Empty constructor
    }

    private static class Holder {
        private static final NotificationManager instance = new NotificationManager();
    }

    public static NotificationManager getInstance() {
        return Holder.instance;
    }

    public boolean notificationPosted(String packageName, int notificationId) {
        return false;
    }

    public boolean notificationRemoved(String packageName, int notificationId) {
        return false;
    }

}
