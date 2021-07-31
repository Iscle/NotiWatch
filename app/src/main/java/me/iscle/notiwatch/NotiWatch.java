package me.iscle.notiwatch;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Build;
import android.os.IBinder;

import me.iscle.notiwatch.service.NotiWatchService;

public class NotiWatch extends Application {
    private static final String TAG = "NotiWatch";

    public static final String SERVICE_CHANNEL_ID = "service_channel";
    public static final String NOTIFICATION_CHANNEL_ID = "notification_channel";
    public static final String MEDIA_CHANNEL_ID = "media_channel";

    public static final String SERVICE_PREFERENCES = "service_preferences";

    private NotiWatchService notiWatchService;

    @Override
    public void onCreate() {
        super.onCreate();

        createNotificationChannels();
        startAndBindService();
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

    private void startAndBindService() {
        Intent serviceIntent = new Intent(this, NotiWatchService.class);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(serviceIntent);
        } else {
            startService(serviceIntent);
        }
        bindService(serviceIntent, notiWatchServiceConnection, Context.BIND_AUTO_CREATE);
    }

    private final ServiceConnection notiWatchServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            notiWatchService = ((NotiWatchService.NotiWatchBinder) service).getService();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            notiWatchService = null;
        }
    };

    public NotiWatchService getNotiWatchService() {
        return notiWatchService;
    }
}
