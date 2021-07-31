package me.iscle.notiwatch;

import android.graphics.Bitmap;
import android.graphics.drawable.Icon;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.nio.charset.StandardCharsets;

import me.iscle.notiwatch.typeadapter.BitmapAdapter;
import me.iscle.notiwatch.typeadapter.CharSequenceAdapter;
import me.iscle.notiwatch.typeadapter.IconAdapter;

public final class Capsule {
    private static final transient Gson gson = new GsonBuilder()
            .registerTypeAdapter(Bitmap.class, new BitmapAdapter())
            .registerTypeAdapter(CharSequence.class, new CharSequenceAdapter())
            .registerTypeAdapter(Icon.class, new IconAdapter())
            .create();

    private final Command command;
    private final String data;

    public Capsule(Command command, Object data) {
        this.command = command;
        this.data = gson.toJson(data);
    }

    public String toJson() {
        return gson.toJson(this);
    }

    public Command getCommand() {
        return command;
    }

    public <T> T getData(Class<T> type) {
        return gson.fromJson(data, type);
    }

    public static Capsule fromBytes(byte[] data) {
        return gson.fromJson(new String(data, StandardCharsets.UTF_8), Capsule.class);
    }
}
