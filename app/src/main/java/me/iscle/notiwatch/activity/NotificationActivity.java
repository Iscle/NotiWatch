package me.iscle.notiwatch.activity;

import android.content.res.Resources;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.View;
import android.widget.ScrollView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import me.iscle.notiwatch.NotiDrawer;
import me.iscle.notiwatch.data.LocalNotificationManager;
import me.iscle.notiwatch.model.PhoneNotification;

public class NotificationActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        PhoneNotification pn = LocalNotificationManager.getInstance().getLastActiveNotification();

        View notificationView = new NotiDrawer(this, pn).createContentView();
        adjustInset(notificationView);

        ScrollView scrollView = new ScrollView(this);
        scrollView.addView(notificationView);
        setContentView(scrollView);
    }

    private void adjustInset(View v) {
        Resources res = getResources();
        if (res.getConfiguration().isScreenRound()) {
            DisplayMetrics dm = res.getDisplayMetrics();
            int inset = (int) (0.146467f * (float) dm.widthPixels);
            int top = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 8, res.getDisplayMetrics());
            v.setPadding(inset, top, inset, inset);
        }
    }
}
