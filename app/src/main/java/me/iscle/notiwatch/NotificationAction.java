package me.iscle.notiwatch;

public class NotificationAction {
    private final String title;
    private final int hashCode;

    public NotificationAction(String title, int hashCode) {
        this.title = title;
        this.hashCode = hashCode;
    }

    public String getTitle() {
        return title;
    }

    public int getHashCode() {
        return hashCode;
    }
}
