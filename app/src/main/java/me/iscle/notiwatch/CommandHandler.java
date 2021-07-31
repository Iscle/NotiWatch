package me.iscle.notiwatch;

import android.content.Intent;
import android.util.Log;

import me.iscle.notiwatch.activity.NotificationActivity;
import me.iscle.notiwatch.data.LocalNotificationManager;
import me.iscle.notiwatch.model.BatteryStatus;
import me.iscle.notiwatch.model.PhoneNotification;
import me.iscle.notiwatch.service.NotiWatchService;

public class CommandHandler {
    private static final String TAG = "CommandHandler";

    private final NotiWatchService notiPhoneService;
    private final LocalNotificationManager localNotificationManager;

    public CommandHandler(NotiWatchService notiPhoneService) {
        this.notiPhoneService = notiPhoneService;
        this.localNotificationManager = LocalNotificationManager.getInstance();
    }

    public void handleCommand(Capsule capsule) {
        Log.d(TAG, "handleCommand: Got a new command: " + capsule.getCommand());

        switch (capsule.getCommand()) {
            case NOTIFICATION_POSTED:
                LocalNotificationManager.getInstance().addActiveNotification(capsule.getData(PhoneNotification.class));
                Intent in = new Intent(notiPhoneService, NotificationActivity.class);
                in.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                in.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                notiPhoneService.startActivity(in);
                break;
            case NOTIFICATION_REMOVED:
                LocalNotificationManager.getInstance().removeActiveNotification(capsule.getData(String.class));
            case GET_BATTERY_STATUS:
                notiPhoneService.sendBattery();
                break;
            case SET_BATTERY_STATUS:
                BatteryStatus batteryStatus = capsule.getData(BatteryStatus.class);
                Log.d(TAG, "handleMessage: Phone battery: " + batteryStatus.getBatteryPercentage());
                break;
            default:
                Log.d(TAG, "handleMessage: Unknown command: " + capsule.getCommand());
        }
    }
}
