package me.iscle.notiwatch;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;

import me.iscle.notiwatch.data.DataManager;

public class App extends Application {
    public static final String SERVICE_CHANNEL_ID = "service_channel";
    public static final String NOTIFICATION_CHANNEL_ID = "notification_channel";
    public static final String MEDIA_CHANNEL_ID = "media_channel";

    public static final String SERVICE_PREFERENCES = "service_preferences";

    private DataManager dataManager;

    @Override
    public void onCreate() {
        super.onCreate();

        createNotificationChannels();

        dataManager = new DataManager();
    }

    public DataManager getDataManager() {
        return dataManager;
    }

    private void createNotificationChannels() {
        // Create the NotificationChannels if we are on API 26+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel serviceChannel = new NotificationChannel(SERVICE_CHANNEL_ID, "Services", NotificationManager.IMPORTANCE_LOW);
            NotificationChannel notificationChannel = new NotificationChannel(NOTIFICATION_CHANNEL_ID, "Notifications", NotificationManager.IMPORTANCE_DEFAULT);
            NotificationChannel mediaChannel = new NotificationChannel(MEDIA_CHANNEL_ID, "Media controls", NotificationManager.IMPORTANCE_LOW);

            // Register the channel with the system
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(serviceChannel);
            notificationManager.createNotificationChannel(notificationChannel);
            notificationManager.createNotificationChannel(mediaChannel);
        }
    }
}
