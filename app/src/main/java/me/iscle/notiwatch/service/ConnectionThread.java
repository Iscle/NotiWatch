package me.iscle.notiwatch.service;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.util.Log;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

import static me.iscle.notiwatch.Utils.readLength;
import static me.iscle.notiwatch.Utils.readString;
import static me.iscle.notiwatch.Utils.writeLength;

/**
 * This thread runs during a connection with a remote device.
 * It handles all incoming and outgoing transmissions.
 */
public class ConnectionThread extends Thread {
    private static final String TAG = "ConnectionThread";

    private static final UUID NOTI_UUID = UUID.fromString("c4547ff6-e6e4-4ccd-9a30-4cdce6249d19");

    private final PhoneBluetoothService phoneService;

    private BluetoothServerSocket bluetoothServerSocket;
    private BluetoothSocket bluetoothSocket;
    private OutputStream outputStream;
    private InputStream inputStream;

    private boolean isCanceled;

    public ConnectionThread(PhoneBluetoothService watchService) {
        this.phoneService = watchService;
        this.isCanceled = false;
    }

    public void run() {
        synchronized (phoneService.getBluetoothLock()) {
            // Create a new listening server socket
            try {
                bluetoothServerSocket = BluetoothAdapter.getDefaultAdapter()
                        .listenUsingRfcommWithServiceRecord("NotiWatch", NOTI_UUID);
            } catch (IOException e) {
                Log.e(TAG, "Error while creating bluetoothServerSocket!", e);
                cancel();
                return;
            }

            phoneService.setState(ConnectionState.LISTENING);

            try {
                // This is a blocking call and will only return on a
                // successful connection or an exception
                bluetoothSocket = bluetoothServerSocket.accept();
                closeBluetoothServerSocket();
            } catch (IOException e) {
                Log.e(TAG, "Error while creating bluetoothSocket!", e);
                cancel();
                return;
            }

            phoneService.setCurrentDevice(bluetoothSocket.getRemoteDevice());
            phoneService.setState(ConnectionState.CONNECTING);

            // Get the BluetoothSocket input and output streams
            try {
                outputStream = bluetoothSocket.getOutputStream();
                inputStream = bluetoothSocket.getInputStream();
            } catch (IOException e) {
                Log.e(TAG, "Error while creating bluetoothSocket streams!", e);
                cancel();
                return;
            }

            phoneService.setState(ConnectionState.CONNECTED);
        }

        // Keep listening to the InputStream while connected
        while (phoneService.getState() == ConnectionState.CONNECTED && !isCanceled) {
            try {
                int length = readLength(inputStream);
                String data = readString(inputStream, length);

                phoneService.handleMessage(data);
            } catch (IOException e) {
                Log.e(TAG, "Disconnected from remote device!", e);
                cancel();
                return;
            }
        }

        cancel();
    }

    /**
     * Write to the connected OutStream.
     *
     * @param data The string to write
     */
    public void write(String data) {
        if (isCanceled) return;

        byte[] bytes = data.getBytes(StandardCharsets.UTF_8);

        synchronized (phoneService.getWriteLock()) {
            if (phoneService.getState() == ConnectionState.CONNECTED) {
                try {
                    writeLength(outputStream, bytes.length);
                    outputStream.write(bytes);
                    outputStream.flush();
                } catch (IOException e) {
                    Log.e(TAG, "Error while writing data to outputStream!", e);
                    cancel();
                    return;
                }
            }
        }
    }

    private void closeBluetoothServerSocket() {
        if (bluetoothServerSocket != null) {
            try {
                bluetoothServerSocket.close();
            } catch (IOException e) {
                Log.e(TAG, "Error while closing bluetoothServerSocket!", e);
            }
            bluetoothServerSocket = null;
        }
    }

    void cancel() {
        if (isCanceled) return;
        isCanceled = true;

        if (outputStream != null) {
            try {
                outputStream.close();
            } catch (IOException e) {
                Log.e(TAG, "Error while closing outputStream!", e);
            }
            outputStream = null;
        }

        if (inputStream != null) {
            try {
                inputStream.close();
            } catch (IOException e) {
                Log.e(TAG, "Error while closing inputStream!", e);
            }
            inputStream = null;
        }

        if (bluetoothSocket != null) {
            try {
                bluetoothSocket.close();
            } catch (IOException e) {
                Log.e(TAG, "Error while closing bluetoothSocket!", e);
            }
            bluetoothSocket = null;
        }

        if (bluetoothServerSocket != null) {
            try {
                bluetoothServerSocket.close();
            } catch (IOException e) {
                Log.e(TAG, "Error while closing bluetoothServerSocket!", e);
            }
            bluetoothServerSocket = null;
        }

        phoneService.setState(ConnectionState.DISCONNECTED);
    }
}
