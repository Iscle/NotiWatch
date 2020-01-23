package me.iscle.notiwatch.data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import me.iscle.notiwatch.model.PhoneNotification;

public class LocalNotificationManager {
    private static final String TAG = "LocalNotificationManager";

    private Map<String, PhoneNotification> activeNotifications;
    private Map<String, PhoneNotification> removedNotifications;
    private PhoneNotification lastActiveNotification;

    public LocalNotificationManager() {
        activeNotifications = new HashMap<>();
        removedNotifications = new HashMap<>();
    }

    public void addActiveNotification(PhoneNotification pn) {
        activeNotifications.put(pn.getId(), pn);
        lastActiveNotification = pn;
    }

    public PhoneNotification getLastActiveNotification() {
        return lastActiveNotification;
    }

    public void remoteActiveNotification(String id) {
        activeNotifications.remove(id);
    }

    public List<PhoneNotification> getActiveNotifications() {
        return new ArrayList<>(activeNotifications.values());
    }
}
