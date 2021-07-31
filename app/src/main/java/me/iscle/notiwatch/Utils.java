package me.iscle.notiwatch;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.util.DisplayMetrics;

import androidx.annotation.Nullable;

import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

public class Utils {

    public static int dpToPixels(Context context, int dp) {
        final DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        return (int) ((float) dp * displayMetrics.density);
    }

    public static int pixelsToDp(Context context, int pixels) {
        final DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        return Math.round((float) pixels / displayMetrics.density);
    }

    public static void closeCloseable(Closeable closeable) {
        try {
            closeable.close();
        } catch (IOException e) {
            // Ignored
        }
    }
}
