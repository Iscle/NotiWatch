package me.iscle.notiwatch.data;

public class DataManager {
    private LocalNotificationManager localNotificationManager;

    public DataManager() {
        localNotificationManager = new LocalNotificationManager();
    }

    public LocalNotificationManager getLocalNotificationManager() {
        return localNotificationManager;
    }
}
