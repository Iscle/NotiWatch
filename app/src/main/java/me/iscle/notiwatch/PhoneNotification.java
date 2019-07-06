package me.iscle.notiwatch;

import java.util.Arrays;

public class PhoneNotification {
    private static final String TAG = "PhoneNotification";

    // StatusBarNotification data
    private String groupKey;
    private int id;
    private String key;
    private String packageName;
    private boolean isClearable;
    private boolean isGroup;
    private boolean isOngoing;

    // Notification data
    private NotificationAction[] actions;
    private int color;
    //private Bundle extras;
    private Long when;
    private String largeIcon;
    private String smallIcon;
    private String title;
    private String text;
    private String subText;
    private String bigText;
    private String conversationTitle;
    private String largeIconBig;
    private String picture;
    private String summaryText;
    private String[] textLines;

    public String getGroupKey() {
        return groupKey;
    }

    public int getId() {
        return id;
    }

    public String getKey() {
        return key;
    }

    public String getPackageName() {
        return packageName;
    }

    public boolean isClearable() {
        return isClearable;
    }

    public boolean isGroup() {
        return isGroup;
    }

    public boolean isOngoing() {
        return isOngoing;
    }

    public NotificationAction[] getActions() {
        return actions;
    }

    public int getColor() {
        return color;
    }

    public Long getWhen() {
        return when;
    }

    public String getLargeIcon() {
        return largeIcon;
    }

    public String getSmallIcon() {
        return smallIcon;
    }

    public String getTitle() {
        return title;
    }

    public String getText() {
        return text;
    }

    public String getSubText() {
        return subText;
    }

    public String getBigText() {
        return bigText;
    }

    public String getConversationTitle() {
        return conversationTitle;
    }

    public String getLargeIconBig() {
        return largeIconBig;
    }

    public String getPicture() {
        return picture;
    }

    public String getSummaryText() {
        return summaryText;
    }

    public String[] getTextLines() {
        return textLines;
    }

    @Override
    public String toString() {
        return "PhoneNotification{" +
                "groupKey='" + groupKey + '\'' +
                ", id=" + id +
                ", key='" + key + '\'' +
                ", packageName='" + packageName + '\'' +
                ", isClearable=" + isClearable +
                ", isGroup=" + isGroup +
                ", isOngoing=" + isOngoing +
                ", color=" + color +
                ", when=" + when +
                ", title='" + title + '\'' +
                ", text='" + text + '\'' +
                ", subText='" + subText + '\'' +
                ", bigText='" + bigText + '\'' +
                ", conversationTitle='" + conversationTitle + '\'' +
                ", largeIconBig='" + largeIconBig + '\'' +
                ", picture='" + picture + '\'' +
                ", summaryText='" + summaryText + '\'' +
                ", textLines=" + Arrays.toString(textLines) +
                '}';
    }
}
