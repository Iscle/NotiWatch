package me.iscle.notiwatch;

public class PhoneNotification {
    private static final String TAG = "PhoneNotification";

    private String appName;

    // StatusBarNotification data
    private String groupKey;
    private String id;
    private String key;
    private String opPkg;
    private String overrideGroupKey;
    private String packageName;
    private long postTime;
    private String tag;
    private int uid;
    private boolean isClearable;
    private boolean isGroup;
    private boolean isOngoing;

    // Notification data
    private NotificationAction[] actions;
    private String category;
    private int color;
    private int flags;
    private int number;
    private String tickerText;
    private int visibility;
    private long when;
    private String group;
    private String largeIcon;
    private String smallIcon;
    private String sortKey;

    // Notification data extras
    private String bigText;
    private boolean chronometerCountDown;
    private int[] compactActions;
    private String conversationTitle;
    private String infoText;
    private String largeIconBig;
    private Integer mediaSession;
    //private Bundle[] messages;
    private String[] people;
    private String picture;
    private int progress;
    private boolean progressIndeterminate;
    private int progressMax;
    private boolean showChronometer;
    private boolean showWhen;
    private String subText;
    private String summaryText;
    private String template;
    private String text;
    private String[] textLines;
    private String title;
    private String titleBig;

    public String getAppName() {
        return appName;
    }

    public String getGroupKey() {
        return groupKey;
    }

    public String getId() {
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

    public String getTemplate() {
        return template;
    }

    public int[] getCompactActions() {
        return compactActions;
    }
}
