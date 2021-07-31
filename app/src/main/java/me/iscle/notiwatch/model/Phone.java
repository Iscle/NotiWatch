package me.iscle.notiwatch.model;

import android.bluetooth.BluetoothDevice;

public class Phone {
    private BluetoothDevice device;

    public Phone(BluetoothDevice device) {
        this.device = device;
    }

    public String getName() {
        return device.getName();
    }

    public String getAddress() {
        return device.getAddress();
    }

    public int getBatteryPercentage() {
        return 50; // TODO: Implement
    }
}
