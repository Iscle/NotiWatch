package me.iscle.notiwatch.activity;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.google.gson.Gson;

import jp.wasabeef.glide.transformations.BlurTransformation;
import me.iscle.notiwatch.App;
import me.iscle.notiwatch.Command;
import me.iscle.notiwatch.NotificationAction;
import me.iscle.notiwatch.PhoneNotification;
import me.iscle.notiwatch.R;
import me.iscle.notiwatch.data.NotificationManager;
import me.iscle.notiwatch.view.DateTimeView;
import me.iscle.notiwatch.service.PhoneBluetoothService;
import me.iscle.notiwatch.service.PhoneService;

import static me.iscle.notiwatch.Constants.BROADCAST_NOTIFICATION_POSTED;
import static me.iscle.notiwatch.Utils.base64ToBitmap;
import static me.iscle.notiwatch.Utils.dpToPixels;

public class TestActivity extends AppCompatActivity {
    private static final String TAG = "TestActivity";

    private PhoneBluetoothService phoneBluetoothService;

    private final ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            PhoneService.PhoneBinder binder = (PhoneService.PhoneBinder) service;
            phoneBluetoothService = (PhoneBluetoothService) binder.getService();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            phoneBluetoothService = null;
        }
    };

    private View mainLayout;
    private View constraintLayout;
    private ImageView appIcon;
    private TextView appName;
    private TextView notificationSummaryText;
    private DateTimeView notificationTime;
    private TextView notificationTitle;
    private TextView notificationText;
    private TextView notificationSubText;
    private LinearLayout actionLayout;

    private LocalBroadcastManager localBroadcastManager;
    private NotificationManager notificationManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);

        notificationManager = ((App) getApplication()).getDataManager().getNotificationManager();

        mainLayout = findViewById(R.id.main_layout);
        constraintLayout = findViewById(R.id.constraintLayout);
        appIcon = findViewById(R.id.icon);
        appName = findViewById(R.id.app_name);
        notificationSummaryText = findViewById(R.id.notification_summary_text);
        notificationTime = findViewById(R.id.notification_time);
        notificationTitle = findViewById(R.id.notification_title);
        notificationText = findViewById(R.id.notification_text);
        notificationSubText = findViewById(R.id.notification_sub_text);
        actionLayout = findViewById(R.id.action_layout);

        localBroadcastManager = LocalBroadcastManager.getInstance(this);

        IntentFilter serviceBroadcastReceiverFilter = new IntentFilter();
        serviceBroadcastReceiverFilter.addAction(BROADCAST_NOTIFICATION_POSTED);
        localBroadcastManager.registerReceiver(serviceBroadcastReceiver, serviceBroadcastReceiverFilter);

        adjustInset();

        Intent serviceIntent = new Intent(this, PhoneBluetoothService.class);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(serviceIntent);
        } else {
            startService(serviceIntent);
        }

        // Bind to WatchService
        bindService(new Intent(this, PhoneBluetoothService.class), mConnection, Context.BIND_AUTO_CREATE);
    }

    private BroadcastReceiver serviceBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            switch (intent.getAction()) {
                case BROADCAST_NOTIFICATION_POSTED:
                    Log.d(TAG, "onReceive: started parsing notification");
                    PhoneNotification pn = notificationManager.getLastActiveNotification();
                    newNotification(pn);
                    Log.d(TAG, "onReceive: finished displaying notification");
                    break;
            }
        }
    };

    private void newNotification(PhoneNotification pn) {
        Log.d(TAG, "newNotification: " + pn.getTemplate());
        Bitmap smallIcon;
        if (pn.getSmallIcon() != null) {
            smallIcon = base64ToBitmap(pn.getSmallIcon());
        } else {
            smallIcon = null;
        }

        Bitmap largeIcon;
        if (pn.getLargeIcon() != null) {
            largeIcon = base64ToBitmap(pn.getLargeIcon());
        } else {
            largeIcon = null;
        }

        runOnUiThread(() -> {
                if (smallIcon != null)
                    appIcon.setImageBitmap(smallIcon);
                if (largeIcon != null) {
                    Glide.with(this)
                            .load(largeIcon)
                            .apply(RequestOptions.bitmapTransform(new BlurTransformation(15)))
                            .into(new CustomTarget<Drawable>() {
                                @Override
                                public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {
                                    mainLayout.setBackground(resource);
                                }

                                @Override
                                public void onLoadCleared(@Nullable Drawable placeholder) {
                                    mainLayout.setBackground(placeholder);
                                }
                            });
                } else {
                    mainLayout.setBackgroundColor(Color.BLACK);
                }
                appIcon.setColorFilter(pn.getColor());
                appName.setText(pn.getAppName());
                appName.setTextColor(pn.getColor());
                if (pn.getSubText() != null) {
                    notificationSubText.setVisibility(View.VISIBLE);
                    notificationTime.setVisibility(View.GONE);
                    notificationSubText.setText(pn.getSubText());
                } else {
                    if (pn.getSummaryText() != null && !(pn.getSummaryText().equals("null"))) {
                        notificationSummaryText.setText(pn.getSummaryText());
                        notificationSummaryText.setVisibility(View.VISIBLE);
                    } else {
                        notificationSummaryText.setVisibility(View.GONE);
                    }
                    notificationTime.setVisibility(View.VISIBLE);
                    notificationSubText.setVisibility(View.GONE);
                    notificationTime.setTime(pn.getWhen());
                }
                notificationTitle.setText(pn.getTitle());
                notificationText.setText(pn.getText());
                actionLayout.removeAllViews();
                if (pn.getActions() != null && pn.getActions().length > 0) {
                    if (pn.getCompactActions() != null && pn.getCompactActions().length > 0) {
                        NotificationAction[] actions = pn.getActions();
                        for (int i : pn.getCompactActions()) {
                            NotificationAction na = actions[i];
                            if (na.getIcon() == null) continue;
                            ImageView iv = new ImageView(this);
                            Bitmap icon = base64ToBitmap(na.getIcon());
                            iv.setImageBitmap(icon);
                            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1.0f);
                            params.height = Math.min(icon.getHeight(), dpToPixels(this, 72));
                            iv.setLayoutParams(params);
                            iv.setAdjustViewBounds(true);
                            iv.setOnClickListener(view ->
                                    phoneBluetoothService.sendCommand(Command.NOTIFICATION_ACTION_CALLBACK,
                                            new NotificationAction.Callback(pn.getId(), na.getHashCode())));
                            actionLayout.addView(iv);
                        }
                    } else {
                        for (NotificationAction na : pn.getActions()) {
                            if (na.getIcon() == null) continue;
                            ImageView iv = new ImageView(this);
                            Bitmap icon = base64ToBitmap(na.getIcon());
                            iv.setImageBitmap(icon);
                            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1.0f);
                            params.height = Math.min(icon.getHeight(), dpToPixels(this, 72));
                            iv.setLayoutParams(params);
                            iv.setAdjustViewBounds(true);
                            iv.setOnClickListener(view ->
                                    phoneBluetoothService.sendCommand(Command.NOTIFICATION_ACTION_CALLBACK,
                                            new NotificationAction.Callback(pn.getId(), na.getHashCode())));
                            actionLayout.addView(iv);
                        }
                    }
                }
            });
    }

    private void adjustInset() {
        Resources res = getResources();
        //if (res.getConfiguration().isScreenRound()) {
        DisplayMetrics dm = res.getDisplayMetrics();
        int inset = (int) (0.146467f * (float) dm.widthPixels);
        constraintLayout.setPadding(inset, 0, inset, inset);
        //}
    }
}
