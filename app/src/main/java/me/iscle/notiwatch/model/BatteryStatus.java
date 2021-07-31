package me.iscle.notiwatch.model;

public class BatteryStatus {
    private final int batteryPercentage;
    private final int chargeStatus;

    public BatteryStatus(int batteryPercentage, int chargeStatus) {
        this.batteryPercentage = batteryPercentage;
        this.chargeStatus = chargeStatus;
    }

    public int getBatteryPercentage() {
        return batteryPercentage;
    }

    public int getChargeStatus() {
        return chargeStatus;
    }
}
