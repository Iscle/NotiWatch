package me.iscle.notiwatch.service;

import static android.os.BatteryManager.EXTRA_LEVEL;
import static android.os.BatteryManager.EXTRA_SCALE;
import static android.os.BatteryManager.EXTRA_STATUS;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import me.iscle.notiwatch.Capsule;
import me.iscle.notiwatch.Command;
import me.iscle.notiwatch.CommandHandler;
import me.iscle.notiwatch.NotiWatch;
import me.iscle.notiwatch.R;
import me.iscle.notiwatch.activity.MainActivity;
import me.iscle.notiwatch.data.LocalNotificationManager;
import me.iscle.notiwatch.model.BatteryStatus;
import me.iscle.notiwatch.model.Phone;

public class NotiWatchService extends Service {
    private static final String TAG = "NotiWatchService";

    public static final int SERVICE_NOTIFICATION_ID = 1;

    private ConnectionState state;
    private CommandHandler commandHandler;
    private ConnectionThread connectionThread;
    private Phone phone;
    private NotificationManager notificationManager;
    private boolean shouldBeDisconnected;

    @Override
    public void onCreate() {
        super.onCreate();

        state = ConnectionState.DISCONNECTED;

        startForeground(SERVICE_NOTIFICATION_ID, getNotification());

        commandHandler = new CommandHandler(this);
        notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        registerReceiver(batteryReceiver, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));

        startListening();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        unregisterReceiver(batteryReceiver);
        stop();
        stopForeground(true);
        super.onDestroy();
    }

    private void updateNotification() {
        notificationManager.notify(SERVICE_NOTIFICATION_ID, getNotification());
    }

    private final BroadcastReceiver batteryReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            sendBattery(intent);
        }
    };

    public void sendBattery() {
        Intent intent = registerReceiver(null, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
        sendBattery(intent);
    }

    private void sendBattery(Intent batteryStatus) {
        int level = batteryStatus.getIntExtra(EXTRA_LEVEL, 50);
        int scale = batteryStatus.getIntExtra(EXTRA_SCALE, 100);
        int chargeStatus = batteryStatus.getIntExtra(EXTRA_STATUS,
                BatteryManager.BATTERY_STATUS_DISCHARGING);
        int batteryLevel = Math.round((float) level / (float) scale * 100.0f);

        sendCommand(Command.SET_BATTERY_STATUS, new BatteryStatus(batteryLevel, chargeStatus));
    }

    private Notification getNotification() {
        Intent notificationIntent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent =
                PendingIntent.getActivity(this, 0, notificationIntent, PendingIntent.FLAG_IMMUTABLE);

        String title;
        String text;
        switch (state) {
            case DISCONNECTED:
                title = "Disconnected";
                text = "Service stopped";
                break;
            case LISTENING:
                title = "Listening";
                text = "Waiting for device";
                break;
            case CONNECTED:
                title = "Connected to " + phone.getName();
                text = "Battery: " + phone.getBatteryPercentage();
                break;
            default:
                throw new RuntimeException("Wrong service state");
        }

        return new NotificationCompat.Builder(this, NotiWatch.SERVICE_CHANNEL_ID)
                .setContentTitle(title)
                .setContentText(text)
                .setSmallIcon(R.drawable.ic_smartphone)
                .setContentIntent(pendingIntent)
                .build();
    }

    private void startListening() {
        stop();

        connectionThread = new ConnectionThread(this, connectionThreadListener);
        connectionThread.start();
    }

    private final ConnectionThread.ConnectionListener connectionThreadListener = new ConnectionThread.ConnectionListener() {
        @Override
        public void onListening() {
            Log.d(TAG, "onListening: ");
            setState(ConnectionState.LISTENING);
        }

        @Override
        public void onConnect(BluetoothDevice device) {
            Log.d(TAG, "onConnect: ");
            phone = new Phone(device);
            setState(ConnectionState.CONNECTED);
            LocalNotificationManager.getInstance().clear();
            sendBattery();
        }

        @Override
        public void onMessage(Capsule capsule) {
            Log.d(TAG, "onMessage: ");
            commandHandler.handleCommand(capsule);
        }

        @Override
        public void onError(Throwable t) {
            Log.e(TAG, "onError: ", t);
        }

        @Override
        public void onDisconnect() {
            Log.d(TAG, "onDisconnect: ");
            setState(ConnectionState.DISCONNECTED);
            if (!shouldBeDisconnected) {
                startListening();
            }
        }
    };

    private void stop() {
        if (connectionThread != null) {
            connectionThread.close();
            connectionThread = null;
        }
    }

    private void setState(ConnectionState state) {
        this.state = state;
        updateNotification();
    }

    public void sendCommand(Command command, Object object) {
        if (state != ConnectionState.CONNECTED) return;
        connectionThread.send(new Capsule(command, object));
    }

    public ConnectionState getState() {
        return state;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return new NotiWatchBinder();
    }

    public class NotiWatchBinder extends Binder {
        public NotiWatchService getService() {
            return NotiWatchService.this;
        }
    }
}
