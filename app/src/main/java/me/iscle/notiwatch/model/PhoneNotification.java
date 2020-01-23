package me.iscle.notiwatch.model;

import android.graphics.Bitmap;
import android.graphics.drawable.Icon;
import android.os.Build;
import android.os.Bundle;

import me.iscle.notiwatch.NotificationAction;

public class PhoneNotification {
    private static final String TAG = "PhoneNotification";

    // Custom
    private String appName;
    private long chronometerBase;
    private Bitmap profileBadge;

    // StatusBarNotification data
    private String groupKey;
    private int id;
    private String key;
    private String overrideGroupKey;
    private String packageName;
    private long postTime;
    private String tag;
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
    private Icon largeIcon;
    private Icon smallIcon;
    private String sortKey;
    private int iconLevel;

    // Notification data extras
    private String bigText;
    private boolean chronometerCountDown;
    private int[] compactActions;
    private String conversationTitle;
    private String infoText;
    private Icon largeIconBig;
    private Integer mediaSession;
    private Bundle[] messages;
    private Bitmap picture;
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

    public String getId() {
        return packageName + ":" + id;
    }

    public NotificationAction[] getActions() {
        return actions;
    }

    public Long getWhen() {
        return when;
    }

    public Icon getLargeIcon() {
        return largeIcon;
    }

    public Icon getLargeIconBig() {
        return largeIconBig;
    }

    public Bitmap getPicture() {
        return picture;
    }

    public Icon getSmallIcon() {
        return smallIcon;
    }

    public String getSummaryText() {
        return summaryText;
    }

    public String getTitleBig() {
        return titleBig;
    }

    public String getBigText() {
        return bigText;
    }

    public void setColor(int color) {
        this.color = color;
    }

    public int getFlags() {
        return flags;
    }

    public boolean isLegacy() {
        return false;
    }

    public int getTargetSdkVersion() {
        return Build.VERSION_CODES.Q;
    }

    public String getInfoText() {
        return infoText;
    }

    public long getCreationTime() {
        return 1;
    }

    public CharSequence[] getRemoteInputHistory() {
        return null;
    }

    public boolean getShowRemoteInputSpinner() {
        return false;
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

    public String getTemplate() {
        return template;
    }

    public int[] getCompactActions() {
        return compactActions;
    }

    public String getTag() {
        return tag;
    }

    public int getVisibility() {
        return visibility;
    }

    public boolean getChronometerCountDown() {
        return chronometerCountDown;
    }

    public boolean showChronometer() {
        return showChronometer;
    }

    public int getIconLevel() {
        return iconLevel;
    }

    public int getColor() {
        return color;
    }

    public long getChronometerBase() {
        return chronometerBase;
    }

    public Bitmap getProfileBadge() {
        return profileBadge;
    }

    public void setLargeIcon(Icon largeIcon) {
        this.largeIcon = largeIcon;
    }

    /**
     * @return true if the notification will show the time; false otherwise
     */
    public boolean showsTime() {
        return when != 0 && showWhen;
    }

    /**
     * @return true if the notification will show a chronometer; false otherwise
     */
    public boolean showsChronometer() {
        return when != 0 && showChronometer;
    }
}
