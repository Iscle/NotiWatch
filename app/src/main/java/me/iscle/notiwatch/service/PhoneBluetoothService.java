package me.iscle.notiwatch.service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.util.Log;

import androidx.core.app.NotificationCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.google.gson.Gson;

import me.iscle.notiwatch.App;
import me.iscle.notiwatch.Capsule;
import me.iscle.notiwatch.Command;
import me.iscle.notiwatch.activity.NotificationActivity;
import me.iscle.notiwatch.model.PhoneNotification;
import me.iscle.notiwatch.data.DataManager;
import me.iscle.notiwatch.model.Status;
import me.iscle.notiwatch.R;

import static android.os.BatteryManager.EXTRA_LEVEL;
import static android.os.BatteryManager.EXTRA_SCALE;
import static android.os.BatteryManager.EXTRA_STATUS;
import static me.iscle.notiwatch.Constants.BROADCAST_NOTIFICATION_POSTED;

public class PhoneBluetoothService extends PhoneService {
    private static final String TAG = "PhoneBluetoothService";

    public static final int SERVICE_NOTIFICATION_ID = 1;
    public static final int REQUEST_ENABLE_BT = 2;

    private ConnectionThread connectionThread;
    private BluetoothDevice currentDevice;
    private ConnectionState state;

    private Handler handler;
    private LocalBroadcastManager localBroadcastManager;

    private DataManager dataManager;

    @Override
    public void onCreate() {
        super.onCreate();

        // Get the watch's bluetooth adapter
        if (BluetoothAdapter.getDefaultAdapter() == null) {
            Log.e(TAG, "onCreate: BluetoothAdapter is null!");
            return;
        }

        // Create the notification ASAP
        Notification notification = newNotification("No phone connected...", "Click to open the app");
        startForeground(SERVICE_NOTIFICATION_ID, notification);

        dataManager = ((App) getApplication()).getDataManager();
        handler = new Handler();
        localBroadcastManager = LocalBroadcastManager.getInstance(this);

        registerReceiver(batteryChangedReceiver, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));

        setState(ConnectionState.DISCONNECTED);
        startListening();
    }

    @Override
    public void onDestroy() {
        unregisterReceiver(batteryChangedReceiver);
        stopListening();
        stopForeground(true);
        super.onDestroy();
    }

    public void handleMessage(String data) {
        Capsule capsule = new Gson().fromJson(data, Capsule.class);
        Log.d(TAG, "handleMessage: Got a new message with command: " + capsule.getCommand());

        switch (capsule.getCommand()) {
            case NOTIFICATION_POSTED:
                dataManager.getLocalNotificationManager().addActiveNotification(capsule.getData(PhoneNotification.class));
                Intent i = new Intent(BROADCAST_NOTIFICATION_POSTED);
                //i.putExtra("phoneNotification", capsule.getRawData());
                localBroadcastManager.sendBroadcast(i);
                Intent in = new Intent(this, NotificationActivity.class);
                in.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                in.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                in.putExtra("phoneNotification", capsule.getRawData());
                startActivity(in);
                break;
            case GET_BATTERY_STATUS:
                sendBattery();
                break;
            case SET_BATTERY_STATUS:
                Status status = capsule.getData(Status.class);
                Log.d(TAG, "handleMessage: Phone battery: " + status.getBatteryLevel());
                break;
            default:
                Log.d(TAG, "handleMessage: Unknown command: " + capsule.getCommand());
        }
    }

    private void updateNotification(String title, String text) {
        Notification notification = newNotification(title, text);

        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        notificationManager.notify(SERVICE_NOTIFICATION_ID, notification);
    }

    private BroadcastReceiver batteryChangedReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            sendBattery(intent);
        }
    };

    private void sendBattery() {
        Intent intent = registerReceiver(null, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
        sendBattery(intent);
    }

    private void sendBattery(Intent batteryStatus) {
        int level = batteryStatus.getIntExtra(EXTRA_LEVEL, -1);
        int scale = batteryStatus.getIntExtra(EXTRA_SCALE, -1);
        byte batteryLevel = (byte) (level * 100 / (float) scale);

        int chargeStatus = batteryStatus.getIntExtra(EXTRA_STATUS, -1);

        Status status = new Status(batteryLevel, chargeStatus);
        sendCommand(Command.SET_BATTERY_STATUS, status);
    }

    private Notification newNotification(String title, String text) {
        Intent notificationIntent = new Intent(this, PhoneBluetoothService.class);
        PendingIntent pendingIntent =
                PendingIntent.getActivity(this, 0, notificationIntent, 0);

        return new NotificationCompat.Builder(this, App.SERVICE_CHANNEL_ID)
                .setContentTitle(title)
                .setContentText(text)
                .setSmallIcon(R.drawable.ic_smartphone_black_24dp)
                .setContentIntent(pendingIntent)
                .build();
    }

    public void setCurrentDevice(BluetoothDevice device) {
        currentDevice = device;
    }

    /**
     * Start AcceptThread to begin a session in listening (server) mode.
     * Called by the Service onCreate()
     */
    private void startListening() {
        stopListening();

        connectionThread = new ConnectionThread(this);
        connectionThread.start();
    }

    /**
     * Stops all bluetooth threads
     */
    private void stopListening() {
        if (connectionThread != null) {
            connectionThread.cancel();
            connectionThread = null;
        }
    }

    /*
     * Indicate that the connection was lost
     */
    private void bluetoothDisconnected() {
        stopListening();

        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        while (!bluetoothAdapter.isEnabled()) {
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        startListening();
    }

    private void disconnected() {
        updateNotification("No phone connected...", "Click to open the app");
        // TODO: do something (tell the activity, etc)

        handler.postDelayed(() -> {
            if (getState() != ConnectionState.LISTENING)
                startListening();
        }, 1000);
    }

    private void listening() {
        updateNotification("Waiting for connection...",
                "Tap to open the app");
    }

    private void connecting() {
        updateNotification("Connecting to " + (currentDevice.getName() == null ? "No name" : currentDevice.getName()) + "...",
                "Tap to open the app"); // TODO: Improve notifications
    }

    private void connected() {
        sendBattery();
    }

    public void sendCommand(Command command, Object object) {
        if (connectionThread == null) return;
        Log.d(TAG, "sendCommand: " + command + ", state: " + getState());
        connectionThread.write(new Capsule(command, object).toJson());
    }

    public void setState(ConnectionState newState) {
        if (this.state == newState) return;
        this.state = newState;

        Log.d(TAG, "setState: " + newState);

        switch (newState) {
            case DISCONNECTED:
                disconnected();
                break;
            case LISTENING:
                listening();
                break;
            case CONNECTING:
                connecting();
                break;
            case CONNECTED:
                connected();
                break;
        }
    }

    public ConnectionState getState() {
        return state;
    }

}
