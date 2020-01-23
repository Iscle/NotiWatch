package me.iscle.notiwatch.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.PagerSnapHelper;
import androidx.recyclerview.widget.RecyclerView;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.widget.TextView;

import me.iscle.notiwatch.App;
import me.iscle.notiwatch.data.DataManager;
import me.iscle.notiwatch.data.LocalNotificationManager;
import me.iscle.notiwatch.R;
import me.iscle.notiwatch.adapter.NotificationAdapter;

import static me.iscle.notiwatch.Constants.BROADCAST_NOTIFICATION_POSTED;
import static me.iscle.notiwatch.Constants.BROADCAST_NOTIFICATION_REMOVED;

public class NotificationsActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private TextView noNotifications;
    private NotificationAdapter notificationAdapter;
    private LocalNotificationManager localNotificationManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notifications);

        DataManager dataManager = ((App) getApplication()).getDataManager();
        localNotificationManager = dataManager.getLocalNotificationManager();

        recyclerView = findViewById(R.id.recycler_view);
        noNotifications = findViewById(R.id.no_notifications_text_view);
        notificationAdapter = new NotificationAdapter(localNotificationManager.getActiveNotifications(), noNotifications);

        recyclerView.setAdapter(notificationAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        new PagerSnapHelper().attachToRecyclerView(recyclerView);

        IntentFilter filter = new IntentFilter();
        filter.addAction(BROADCAST_NOTIFICATION_POSTED);
        filter.addAction(BROADCAST_NOTIFICATION_REMOVED);
        LocalBroadcastManager.getInstance(this).registerReceiver(receiver, filter);
    }

    @Override
    protected void onDestroy() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(receiver);
        super.onDestroy();
    }

    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            notificationAdapter.setPhoneNotifications(localNotificationManager.getActiveNotifications());
        }
    };
}
