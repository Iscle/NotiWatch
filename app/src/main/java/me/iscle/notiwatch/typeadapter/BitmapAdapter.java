package me.iscle.notiwatch.typeadapter;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class BitmapAdapter extends TypeAdapter<Bitmap> {

    @Override
    public void write(JsonWriter out, Bitmap value) throws IOException {
        String base64 = bitmapToBase64(value);
        out.value(base64);
    }

    @Override
    public Bitmap read(JsonReader in) throws IOException {
        if (in.peek() != JsonToken.STRING)
            return null;

        String base64 = in.nextString();
        return base64ToBitmap(base64);
    }

    private static String bitmapToBase64(Bitmap b) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        b.compress(Bitmap.CompressFormat.WEBP, 25, baos);

        return Base64.encodeToString(baos.toByteArray(), Base64.DEFAULT);
    }

    private static Bitmap base64ToBitmap(String s) {
        byte[] imageBytes = Base64.decode(s, Base64.DEFAULT);

        return BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
    }
}
