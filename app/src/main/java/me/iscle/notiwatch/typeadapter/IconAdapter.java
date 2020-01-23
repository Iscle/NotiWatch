package me.iscle.notiwatch.typeadapter;

import android.graphics.drawable.Icon;
import android.util.Base64;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;

public class IconAdapter extends TypeAdapter<Icon> {
    @Override
    public void write(JsonWriter out, Icon value) throws IOException {

    }

    @Override
    public Icon read(JsonReader in) throws IOException {
        if (in.peek() != JsonToken.STRING)
            return null;

        byte[] data = Base64.decode(in.nextString(), Base64.DEFAULT);
        return Icon.createWithData(data, 0, data.length);
    }
}
