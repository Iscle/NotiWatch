package me.iscle.notiwatch;

import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.FrameLayout;

import me.iscle.notiwatch.data.LocalNotificationManager;
import me.iscle.notiwatch.model.PhoneNotification;

import static me.iscle.notiwatch.Constants.BROADCAST_NOTIFICATION_POSTED;

public class TestActivityNew extends AppCompatActivity {

    private LocalNotificationManager lnm;
    private FrameLayout frame;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.notification_layout);

        lnm = ((App) getApplication()).getDataManager().getLocalNotificationManager();

        frame = findViewById(R.id.frame);
        adjustInset();

        IntentFilter filter = new IntentFilter();
        filter.addAction(BROADCAST_NOTIFICATION_POSTED);
        LocalBroadcastManager.getInstance(this).registerReceiver(receiver, filter);
    }

    private void adjustInset() {
        Resources res = getResources();
        //if (res.getConfiguration().isScreenRound()) {
        DisplayMetrics dm = res.getDisplayMetrics();
        int inset = (int) (0.146467f * (float) dm.widthPixels);
        frame.setPadding(inset, 0, inset, inset);
        //}
    }

    @Override
    protected void onDestroy() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(receiver);
        super.onDestroy();
    }

    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            PhoneNotification pn = lnm.getLastActiveNotification();
            if (!TextUtils.isEmpty(pn.getTemplate())) return;

            View v = new NotiDrawer(TestActivityNew.this, pn).createContentView();
            frame.removeAllViews();
            frame.addView(v);
        }
    };
}
