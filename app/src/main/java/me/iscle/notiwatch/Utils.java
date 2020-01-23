package me.iscle.notiwatch;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.util.DisplayMetrics;

import androidx.annotation.Nullable;

import java.io.ByteArrayOutputStream;
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

    public static void writeLength(OutputStream os, int length) throws IOException {
        os.write(length >> 24);
        os.write(length >> 16);
        os.write(length >> 8);
        os.write(length);
    }

    public static int readLength(InputStream is) throws IOException {
        int length = 0;

        length |= (is.read() << 24);
        length |= (is.read() << 16);
        length |= (is.read() << 8);
        length |= is.read();

        return length;
    }

    public static String readString(InputStream is, int length) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int currentLength = 0;

        while (currentLength != length) {
            int bufLen = is.read(buffer, 0, Math.min(buffer.length, length - currentLength));
            currentLength += bufLen;
            baos.write(buffer, 0, bufLen);
        }

        return new String(baos.toByteArray(), StandardCharsets.UTF_8);
    }
}
