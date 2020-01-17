package me.iscle.notiwatch.data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import me.iscle.notiwatch.PhoneNotification;

public class NotificationManager {
    private static final String TAG = "NotificationManager";

    private Map<String, PhoneNotification> activeNotifications;
    private Map<String, PhoneNotification> removedNotifications;
    private PhoneNotification lastActiveNotification;

    public NotificationManager() {
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
