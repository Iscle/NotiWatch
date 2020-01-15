package me.iscle.notiwatch.view;

import android.content.Context;
import android.graphics.Color;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import me.iscle.notiwatch.PhoneNotification;

import static me.iscle.notiwatch.Utils.base64ToBitmap;
import static me.iscle.notiwatch.Utils.dpToPixels;

public class NotificationInboxStyle extends LinearLayout {

    private ImageView icon;
    private TextView appName;
    private TextView summaryText;
    private DateTimeView when;
    private View divider;
    private TextView[] textLines;

    public NotificationInboxStyle(Context context) {
        super(context);
        setOrientation(VERTICAL);
        setGravity(Gravity.CENTER);
        
        icon = new ImageView(context);
        appName = new TextView(context);
        summaryText = new TextView(context);
        when = new DateTimeView(context);
        divider = new View(context);
        divider.setBackgroundColor(Color.WHITE);
        LayoutParams dividerParams = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, dpToPixels(context, 1));
        dividerParams.leftMargin = dpToPixels(context, 8);
        dividerParams.rightMargin = dividerParams.leftMargin;
        divider.setLayoutParams(dividerParams);
        textLines = null;
        
        addView(icon);
        addView(appName);
        addView(summaryText);
        addView(when);
    }

    public void setPhoneNotification(PhoneNotification pn) {
        icon.setImageBitmap(base64ToBitmap(pn.getSmallIcon()));
        appName.setText(pn.getAppName());
        summaryText.setText(pn.getSummaryText());
        when.setTime(pn.getWhen());
    }

}
