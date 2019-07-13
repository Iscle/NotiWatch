package me.iscle.notiwatch;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import me.iscle.notiwatch.CustomViews.DateTimeView;
import me.iscle.notiwatch.Services.PhoneService;

public class MainActivity extends AppCompatActivity {

    private final ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            PhoneService.PhoneBinder binder = (PhoneService.PhoneBinder) service;
            PhoneService phoneService = binder.getService();
            phoneService.setActivity(MainActivity.this);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };
    private View constraintLayout;
    private ImageView appIcon;
    private TextView appName;
    private DateTimeView notificationTime;
    private TextView notificationTitle;
    private TextView notificationText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        constraintLayout = findViewById(R.id.constraintLayout);
        appIcon = findViewById(R.id.icon);
        appName = findViewById(R.id.app_name);
        notificationTime = findViewById(R.id.notification_time);
        notificationTitle = findViewById(R.id.notification_title);
        notificationText = findViewById(R.id.notification_text);

        adjustInset();

        Intent serviceIntent = new Intent(this, PhoneService.class);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(serviceIntent);
        } else {
            startService(serviceIntent);
        }

        // Bind to WatchService
        bindService(new Intent(this, PhoneService.class), mConnection, Context.BIND_AUTO_CREATE);
    }

    public void newNotification(final String image, final int color, final String name, final long time, final String title, final String text) {
        runOnUiThread(() -> {
            byte[] decodedString = Base64.decode(image, Base64.DEFAULT);
            Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
            appIcon.setImageBitmap(decodedByte);
            appIcon.setColorFilter(color);
            appName.setText(name);
            appName.setTextColor(color);
            notificationTime.setTime(time);
            notificationTitle.setText(title);
            notificationText.setText(text);
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
