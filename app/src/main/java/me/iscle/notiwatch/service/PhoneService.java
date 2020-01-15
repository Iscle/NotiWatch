package me.iscle.notiwatch.service;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;

import androidx.annotation.Nullable;

import me.iscle.notiwatch.Command;

public abstract class PhoneService extends Service {
    private final Object bluetoothLock = new Object();
    private final Object writeLock = new Object();

    private final IBinder binder = new PhoneBinder();

    public abstract void sendCommand(Command command, Object object);

    public Object getBluetoothLock() {
        return bluetoothLock;
    }

    public Object getWriteLock() {
        return writeLock;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    public class PhoneBinder extends Binder {
        private PhoneBinder() {
            // Empty constructor
        }

        public PhoneService getService() {
            return PhoneService.this;
        }
    }
}
