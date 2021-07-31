package me.iscle.notiwatch.data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import me.iscle.notiwatch.model.PhoneNotification;

public class LocalNotificationManager {
    private static final String TAG = "LocalNotificationManager";

    private static volatile LocalNotificationManager instance;

    private final Map<String, PhoneNotification> activeNotificationsMap;
    private final List<PhoneNotification> activeNotificationsList;
    private final List<NotificationObserver> notificationObservers;

    private LocalNotificationManager() {
        this.activeNotificationsMap = new HashMap<>();
        this.activeNotificationsList = new ArrayList<>();
        this.notificationObservers = new ArrayList<>();
    }

    public static LocalNotificationManager getInstance() {
        if (instance == null) {
            synchronized (LocalNotificationManager.class) {
                if (instance == null) {
                    instance = new LocalNotificationManager();
                }
            }
        }

        return instance;
    }

    public List<PhoneNotification> getActiveNotifications() {
        return activeNotificationsList;
    }

    public void addActiveNotification(PhoneNotification pn) {
        activeNotificationsMap.put(pn.getId(), pn);
        activeNotificationsList.add(pn);
        for (NotificationObserver notificationObserver : notificationObservers) {
            notificationObserver.onNotificationPosted(pn);
        }
    }

    public PhoneNotification getActiveNotification(String id) {
        return activeNotificationsMap.get(id);
    }

    public PhoneNotification removeActiveNotification(String id) {
        PhoneNotification pn = activeNotificationsMap.remove(id);
        activeNotificationsList.remove(pn);
        for (NotificationObserver notificationObserver : notificationObservers) {
            notificationObserver.onNotificationPosted(pn);
        }
        return pn;
    }

    public void addNotificationObserver(NotificationObserver observer) {
        notificationObservers.add(observer);
    }

    public void removeNotificationObserver(NotificationObserver observer) {
        notificationObservers.remove(observer);
    }

    public PhoneNotification getLastActiveNotification() {
        if (activeNotificationsList.size() == 0) {
            return null;
        }

        return activeNotificationsList.get(activeNotificationsList.size() - 1);
    }

    public void clear() {
        activeNotificationsMap.clear();
        activeNotificationsList.clear();
    }

    public interface NotificationObserver {
        void onNotificationPosted(PhoneNotification notification);

        void onNotificationRemoved(PhoneNotification notification);
    }
}
