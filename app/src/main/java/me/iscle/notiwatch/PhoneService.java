package me.iscle.notiwatch;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.UUID;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import static me.iscle.notiwatch.App.NOTIFICATION_CHANNEL_ID;

public class PhoneService extends Service {
    private static final String TAG = "PhoneService";

    // Constants that indicate the current connection state
    public static final int STATE_NONE = 0; // we're doing nothing
    public static final int STATE_LISTEN = 1; // now listening for incoming connections
    public static final int STATE_CONNECTED = 3; // now connected to a remote device

    public static final int SERVICE_NOTIFICATION_ID = 1;
    public static final int REQUEST_ENABLE_BT = 2;

    private static final UUID MY_UUID = UUID.fromString("c4547ff6-e6e4-4ccd-9a30-4cdce6249d19");
    private final IBinder mBinder = new PhoneBinder();

    private BluetoothAdapter mBluetoothAdapter;
    private Handler mHandler;
    private AcceptThread mAcceptThread;
    private ConnectedThread mConnectedThread;
    private int mState;

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "onCreate");

        // Create the notification ASAP
        Notification notification = newNotification("No watch connected...", "Click to open the app");
        startForeground(SERVICE_NOTIFICATION_ID, notification);

        // Set the initial state
        mState = STATE_NONE;
        mHandler = null;

        // Get the watch's bluetooth adapter
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (mBluetoothAdapter == null) {
            Log.e(TAG, "onCreate: BluetoothAdapter is null!");
            return;
        }

        // Start the AcceptThread
        startBluetoothConnection();
    }

    public void setHandler(Handler mHandler) {
        this.mHandler = mHandler;
    }

    private void handleMessage(String data) {
        Log.d(TAG, "handleMessage: " + data);
        Capsule capsule = new Gson().fromJson(data, Capsule.class);

        switch (capsule.getCommand()) {
            case 1:
                PhoneNotification pn = new Gson().fromJson(capsule.getData(), PhoneNotification.class);

                Notification notification = new NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
                        .setSmallIcon(R.drawable.ic_smartphone_black_24dp)
                        .setContentTitle(pn.getTitle())
                        .setContentText(pn.getText())
                        .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                        .addAction(R.drawable.ic_smartphone_black_24dp, "Test button", null)
                        .build();

                NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
                notificationManager.notify(pn.getId(), notification);
                break;
            default:

                break;
        }

        if (mHandler != null) {
            // Do UI work
        }
    }

    public void updateNotification(String title, String text) {
        Log.d(TAG, "updateNotification");

        Notification notification = newNotification(title, text);

        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        notificationManager.notify(SERVICE_NOTIFICATION_ID, notification);
    }

    @Override
    public void onDestroy() {
        Log.d(TAG, "onDestroy");
        stopBluetoothConnection();
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
    public synchronized void startBluetoothConnection() {
        Log.d(TAG, "startBluetoothConnection");

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
    public synchronized void stopBluetoothConnection() {
        Log.d(TAG, "stopBluetoothConnection");

        if (mConnectedThread != null) {
            mConnectedThread.cancel();
            mConnectedThread = null;
        }

        if (mAcceptThread != null) {
            mAcceptThread.cancel();
            mAcceptThread = null;
        }

        mState = STATE_NONE;
    }

    /**
     * Write to the ConnectedThread in an unsynchronized manner
     *
     * @param data The bytes to bluetoothWrite
     * @see ConnectedThread#write(String)
     */
    public void bluetoothWrite(String data) {
        // Create temporary object
        ConnectedThread r;
        // Synchronize a copy of the ConnectedThread
        synchronized (this) {
            if (mState != STATE_CONNECTED) return;
            r = mConnectedThread;
        }
        // Perform the bluetoothWrite unsynchronized
        r.write(data);
    }

    /*
     * Indicate that the connection was lost
     */
    public synchronized void bluetoothDisconnected() {
        mState = STATE_NONE;
        startBluetoothConnection();
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
            mState = STATE_LISTEN;
            updateNotification("Waiting for connection...",
                    "Tap to open the app");
        }

        public void run() {
            Log.d(TAG, "Starting AcceptThread");

            BluetoothSocket socket;

            // Listen to the server socket if we're not connected
            while (mState != STATE_CONNECTED) {
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
                            case STATE_LISTEN:
                                // Situation normal. Start the connected thread.
                                connected(socket);
                                break;
                            case STATE_NONE:
                            case STATE_CONNECTED:
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
            Log.i(TAG, "Finishing AcceptThread");
        }

        void cancel() {
            Log.i(TAG, "Closing sockets");
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
        private final BufferedReader mmInStream;
        private final BufferedWriter mmOutStream;

        ConnectedThread(BluetoothSocket socket) {
            Log.d(TAG, "Created ConnectedThread");
            mmSocket = socket;
            InputStream tmpIn = null;
            OutputStream tmpOut = null;

            // Get the BluetoothSocket input and output streams
            try {
                tmpIn = socket.getInputStream();
                tmpOut = socket.getOutputStream();
            } catch (IOException e) {
                Log.e(TAG, "Error while creating temporary streams!", e);
            }

            mmInStream = new BufferedReader(new InputStreamReader(tmpIn));
            mmOutStream = new BufferedWriter(new OutputStreamWriter(tmpOut));
            mState = STATE_CONNECTED;
            updateNotification("Connected to: " + socket.getRemoteDevice().getName(),
                    "Address: " + socket.getRemoteDevice().getAddress());
        }

        public void run() {
            Log.i(TAG, "Started ConnectedThread");

            // Keep listening to the InputStream while connected
            while (mState == STATE_CONNECTED) {
                try {
                    // Read from the InputStream
                    handleMessage(mmInStream.readLine());
                } catch (IOException e) {
                    Log.e(TAG, "Disconnected from remote device!", e);
                    bluetoothDisconnected();
                    break;
                }
            }
        }

        /**
         * Write to the connected OutStream.
         *
         * @param data The string to bluetoothWrite
         */
        public void write(String data) {
            try {
                mmOutStream.write(data);
                // Write a carriage return after the data to indicate we've
                // finished sending
                mmOutStream.write("\r");
                // Flush the stream to send the data
                mmOutStream.flush();
            } catch (IOException e) {
                Log.e(TAG, "Error while writing the data!", e);
            }
        }

        void cancel() {
            try {
                mmInStream.close();
                mmOutStream.close();
                mmSocket.close();
            } catch (IOException e) {
                Log.e(TAG, "Error while closing the streams and socket!", e);
            }
        }
    }

    public class PhoneBinder extends Binder {
        public PhoneService getService() {
            return PhoneService.this;
        }
    }

}
