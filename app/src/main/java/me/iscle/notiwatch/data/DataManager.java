package me.iscle.notiwatch.data;

public class DataManager {
    private NotificationManager notificationManager;

    public DataManager() {
        notificationManager = new NotificationManager();
    }

    public NotificationManager getNotificationManager() {
        return notificationManager;
    }
}
