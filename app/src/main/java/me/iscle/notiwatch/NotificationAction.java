package me.iscle.notiwatch;

public class NotificationAction {
    private String title;
    private String icon;
    private int hashCode;

    public String getTitle() {
        return title;
    }

    public String getIcon() {
        return icon;
    }

    public int getHashCode() {
        return hashCode;
    }

    public static class Callback {
        private String notificationId;
        private int hashCode;

        public Callback(String notificationId, int hashCode) {
            this.notificationId = notificationId;
            this.hashCode = hashCode;
        }
    }
}
