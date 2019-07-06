package me.iscle.notiwatch;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Base64;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

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
    private View mainLayout;
    private ImageView imageView;
    private TextView textView;
    private TextView textView2;
    private TextView textView3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mainLayout = findViewById(R.id.mainLayout);
        imageView = findViewById(R.id.imageView);
        textView = findViewById(R.id.textView);
        textView2 = findViewById(R.id.textView2);
        textView3 = findViewById(R.id.textView3);

        Intent serviceIntent = new Intent(this, PhoneService.class);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(serviceIntent);
        } else {
            startService(serviceIntent);
        }

        // Bind to WatchService
        bindService(new Intent(this, PhoneService.class), mConnection, Context.BIND_AUTO_CREATE);
    }

    public void setSmallImage(final String image) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                byte[] decodedString = Base64.decode(image, Base64.DEFAULT);
                Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                imageView.setImageBitmap(decodedByte);
            }
        });
    }

    public void setLargeImage(final String image) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                byte[] decodedString = Base64.decode(image, Base64.DEFAULT);
                Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                mainLayout.setBackground(new BitmapDrawable(getResources(), decodedByte));
            }
        });
    }

    public void setText(final String t1, final String t2, final String t3) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                textView.setText(t1);
                textView2.setText(t2);
                textView3.setText(t3);
            }
        });
    }
}
