package me.iscle.notiwatch.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.PagerSnapHelper;
import androidx.recyclerview.widget.RecyclerView;

import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.provider.Settings;
import android.widget.TextView;

import me.iscle.notiwatch.NotiWatch;
import me.iscle.notiwatch.data.LocalNotificationManager;
import me.iscle.notiwatch.R;
import me.iscle.notiwatch.adapter.NotificationAdapter;
import me.iscle.notiwatch.model.PhoneNotification;

import static me.iscle.notiwatch.Constants.BROADCAST_NOTIFICATION_POSTED;
import static me.iscle.notiwatch.Constants.BROADCAST_NOTIFICATION_REMOVED;

public class NotificationsActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private TextView noNotifications;
    private NotificationAdapter notificationAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notifications);

        recyclerView = findViewById(R.id.recycler_view);
        noNotifications = findViewById(R.id.no_notifications_text_view);
        notificationAdapter = new NotificationAdapter(LocalNotificationManager.getInstance().getActiveNotifications(), noNotifications);

        recyclerView.setAdapter(notificationAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        new PagerSnapHelper().attachToRecyclerView(recyclerView);

        LocalNotificationManager.getInstance().addNotificationObserver(notificationObserver);

        startActivity(new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE));

        if (!Settings.canDrawOverlays(this)) {
            Intent myIntent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION);
            startActivity(myIntent);
        }
    }

    private final LocalNotificationManager.NotificationObserver notificationObserver = new LocalNotificationManager.NotificationObserver() {
        @Override
        public void onNotificationPosted(PhoneNotification notification) {
            runOnUiThread(() -> notificationAdapter.updateNotifications());
        }

        @Override
        public void onNotificationRemoved(PhoneNotification notification) {
            runOnUiThread(() -> notificationAdapter.updateNotifications());
        }
    };

    @Override
    protected void onDestroy() {
        LocalNotificationManager.getInstance().removeNotificationObserver(notificationObserver);
        super.onDestroy();
    }
}
