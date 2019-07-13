package me.iscle.notiwatch.Services;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.UUID;

import me.iscle.notiwatch.App;
import me.iscle.notiwatch.Capsule;
import me.iscle.notiwatch.Command;
import me.iscle.notiwatch.MainActivity;
import me.iscle.notiwatch.PhoneNotification;
import me.iscle.notiwatch.R;

import static android.os.BatteryManager.EXTRA_LEVEL;
import static android.os.BatteryManager.EXTRA_SCALE;

public class PhoneService extends Service {
    public static final int SERVICE_NOTIFICATION_ID = 1;
    public static final int REQUEST_ENABLE_BT = 2;
    private static final String TAG = "PhoneService";
    private static final UUID MY_UUID = UUID.fromString("c4547ff6-e6e4-4ccd-9a30-4cdce6249d19");
    private final IBinder mBinder = new PhoneBinder();
    private BluetoothAdapter mBluetoothAdapter;
    private Handler mHandler = null;
    private AcceptThread mAcceptThread;
    private ConnectedThread mConnectedThread;
    private ConnectionState mState = ConnectionState.NONE;
    final BroadcastReceiver batteryChangedReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            sendBattery(intent);
        }
    };
    private MainActivity activity;

    private void sendBattery(Intent i) {
        int batteryLevel = (int) (100 * (((float) i.getIntExtra(EXTRA_LEVEL, 0)) / ((float) i.getIntExtra(EXTRA_SCALE, 1))));
        write(new Capsule(Command.SET_BATTERY_STATUS, batteryLevel).toJson());
    }

    private void sendBattery() {
        Intent intent = registerReceiver(null, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
        sendBattery(intent);
    }

    @Override
    public void onCreate() {
        super.onCreate();

        // Create the notification ASAP
        Notification notification = newNotification("No watch connected...", "Click to open the app");
        startForeground(SERVICE_NOTIFICATION_ID, notification);

        // Get the watch's bluetooth adapter
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (mBluetoothAdapter == null) {
            Log.e(TAG, "onCreate: BluetoothAdapter is null!");
            return;
        }

        registerReceiver(batteryChangedReceiver, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));

        // Start the AcceptThread
        startListening();
    }

    public void setActivity(MainActivity a) {
        this.activity = a;
    }

    public void setHandler(Handler mHandler) {
        this.mHandler = mHandler;
    }

    private void handleMessage(String data) {
        Capsule capsule = new Gson().fromJson(data, Capsule.class);
        Log.d(TAG, "handleMessage: " + capsule.getCommand());

        switch (capsule.getCommand()) {
            case NOTIFICATION_POSTED:
                PhoneNotification pn = capsule.getData(PhoneNotification.class);
                Log.d(TAG, "handleMessage: " + pn.toString());

                activity.newNotification(pn.getSmallIcon(), pn.getColor(), pn.getAppName(), pn.getWhen(), pn.getTitle(), pn.getText());
                break;
            case GET_BATTERY_STATUS:
                sendBattery();
                break;
            case SET_BATTERY_STATUS:
                Log.d(TAG, "handleMessage: Phone battery: " + capsule.getData(int.class));
                break;
            default:

                break;
        }

        if (mHandler != null) {
            // Do UI work
        }
    }

    public void updateNotification(String title, String text) {
        Notification notification = newNotification(title, text);

        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        notificationManager.notify(SERVICE_NOTIFICATION_ID, notification);
    }

    @Override
    public void onDestroy() {
        stopListening();
        unregisterReceiver(batteryChangedReceiver);
        stopForeground(true);
        super.onDestroy();
    }

    public Notification newNotification(String title, String text) {
        Intent notificationIntent = new Intent(this, PhoneService.class);
        PendingIntent pendingIntent =
                PendingIntent.getActivity(this, 0, notificationIntent, 0);

        return new NotificationCompat.Builder(this, App.SERVICE_CHANNEL_ID)
                .setContentTitle(title)
                .setContentText(text)
                .setSmallIcon(R.drawable.ic_smartphone_black_24dp)
                .setContentIntent(pendingIntent)
                .build();
    }

    /**
     * Start the ConnectedThread to begin managing a Bluetooth connection
     *
     * @param socket The BluetoothSocket on which the connection was made
     */
    public synchronized void connected(BluetoothSocket socket) {
        BluetoothDevice device = socket.getRemoteDevice();
        Log.d(TAG, "Connected to: " + device.getName() + " (" + device.getAddress() + ")");

        updateNotification("Connecting to: " + device.getName(),
                "Address: " + device.getAddress());

        // Cancel the thread that completed the connection
        if (mAcceptThread != null) {
            mAcceptThread.cancel();
            mAcceptThread = null;
        }

        // Cancel any thread currently running a connection
        if (mConnectedThread != null) {
            mConnectedThread.cancel();
            mConnectedThread = null;
        }

        // Start the thread to manage the connection and perform transmissions
        mConnectedThread = new ConnectedThread(socket);
        mConnectedThread.start();
    }

    /**
     * Start AcceptThread to begin a session in listening (server) mode.
     * Called by the Service onCreate()
     */
    public synchronized void startListening() {
        // Cancel any thread currently running a connection
        if (mConnectedThread != null) {
            mConnectedThread.cancel();
            mConnectedThread = null;
        }

        // Start the thread to listen on a BluetoothServerSocket
        if (mAcceptThread == null) {
            mAcceptThread = new AcceptThread();
            mAcceptThread.start();
        }
    }

    /**
     * Stops all bluetooth threads
     */
    public synchronized void stopListening() {
        if (mConnectedThread != null) {
            mConnectedThread.cancel();
            mConnectedThread = null;
        }

        if (mAcceptThread != null) {
            mAcceptThread.cancel();
            mAcceptThread = null;
        }

        mState = ConnectionState.NONE;
    }

    /**
     * Write to the ConnectedThread in an unsynchronized manner
     */
    public void write(String data) {
        // Create temporary object
        ConnectedThread r;
        // Synchronize a copy of the ConnectedThread
        synchronized (this) {
            if (mState != ConnectionState.CONNECTED) return;
            r = mConnectedThread;
        }
        // Perform the write unsynchronized
        r.write(data);
    }

    /*
     * Indicate that the connection was lost
     */
    public synchronized void bluetoothDisconnected() {
        mState = ConnectionState.NONE;

        while (!mBluetoothAdapter.isEnabled()) {
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        startListening();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        // Set the Handler to null as we don't have any activity attached anymore
        mHandler = null;
        return super.onUnbind(intent);
    }

    private enum ConnectionState {
        NONE, // We're doing nothing
        CONNECTING, // Initiating an outgoing connection
        CONNECTED, // Connected to a remote device
        LISTENING // We're waiting for an incoming connection
    }

    /**
     * This thread runs while listening for incoming connections. It behaves
     * like a server-side client. It runs until a connection is accepted
     * (or until cancelled).
     */
    private class AcceptThread extends Thread {
        // The local server socket
        private final BluetoothServerSocket mmServerSocket;

        public AcceptThread() {
            BluetoothServerSocket tmp = null;

            // Create a new listening server socket
            try {
                tmp = mBluetoothAdapter.listenUsingRfcommWithServiceRecord("NotiWatch", MY_UUID);
            } catch (IOException e) {
                Log.e(TAG, "Error while creating the BluetoothServerSocket!", e);
            }
            mmServerSocket = tmp;
            mState = ConnectionState.LISTENING;
            updateNotification("Waiting for connection...",
                    "Tap to open the app");
        }

        public void run() {
            BluetoothSocket socket;

            // Listen to the server socket if we're not connected
            while (mState != ConnectionState.CONNECTED) {
                try {
                    // This is a blocking call and will only return on a
                    // successful connection or an exception
                    socket = mmServerSocket.accept();
                } catch (IOException e) {
                    Log.e(TAG, "Error while creating the BluetoothSocket!", e);
                    break;
                }

                // If a connection was accepted
                if (socket != null) {
                    synchronized (this) {
                        switch (mState) {
                            case LISTENING:
                                // Situation normal. Start the connected thread.
                                connected(socket);
                                break;
                            case NONE:
                            case CONNECTED:
                                // Either not ready or already connected. Terminate new socket.
                                try {
                                    socket.close();
                                } catch (IOException e) {
                                    Log.e(TAG, "Could not close the unwanted socket!", e);
                                }
                                break;
                        }
                    }
                }
            }
        }

        void cancel() {
            try {
                mmServerSocket.close();
            } catch (IOException e) {
                Log.e(TAG, "Couuld not close the socket!", e);
            }
        }
    }

    /**
     * This thread runs during a connection with a remote device.
     * It handles all incoming and outgoing transmissions.
     */
    private class ConnectedThread extends Thread {
        private final BluetoothSocket mmSocket;
        private final BufferedWriter mmOutStream;
        private final BufferedReader mmInStream;

        ConnectedThread(BluetoothSocket socket) {
            mmSocket = socket;
            OutputStream tmpOut = null;
            InputStream tmpIn = null;

            // Get the BluetoothSocket input and output streams
            try {
                tmpOut = socket.getOutputStream();
                tmpIn = socket.getInputStream();
            } catch (IOException e) {
                Log.e(TAG, "Error while creating temporary streams!", e);
            }

            mmOutStream = new BufferedWriter(new OutputStreamWriter(tmpOut));
            mmInStream = new BufferedReader(new InputStreamReader(tmpIn));

            mState = ConnectionState.CONNECTED;
            updateNotification("Connected to: " + socket.getRemoteDevice().getName(),
                    "Address: " + socket.getRemoteDevice().getAddress());
        }

        public void run() {
            sendBattery();

            // Keep listening to the InputStream while connected
            while (mState == ConnectionState.CONNECTED) {
                try {
                    // Read from the InputStream
                    handleMessage(mmInStream.readLine());
                } catch (IOException e) {
                    Log.d(TAG, "Disconnected from remote device!");
                    startListening();
                    break;
                }
            }
        }

        /**
         * Write to the connected OutStream.
         *
         * @param data The string to write
         */
        public void write(String data) {
            try {
                mmOutStream.write(data);
                // Send a new line after the data
                mmOutStream.newLine();
                // Flush the stream to send the data
                mmOutStream.flush();
            } catch (IOException e) {
                Log.e(TAG, "Error while writing the data!", e);
            }
        }

        void cancel() {
            try {
                mmInStream.close();
            } catch (IOException e) {
                Log.e(TAG, "Error while closing mmInStream!", e);
            }

            try {
                mmOutStream.close();
            } catch (IOException e) {
                Log.e(TAG, "Error while closing mmOutStream!", e);
            }

            try {
                mmSocket.close();
            } catch (IOException e) {
                Log.e(TAG, "Error while closing mmSocket!", e);
            }
        }
    }

    public class PhoneBinder extends Binder {
        public PhoneService getService() {
            return PhoneService.this;
        }
    }

}
