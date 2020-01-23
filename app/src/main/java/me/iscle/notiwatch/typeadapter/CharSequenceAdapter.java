package me.iscle.notiwatch.typeadapter;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;

public class CharSequenceAdapter extends TypeAdapter<CharSequence> {
    @Override
    public void write(JsonWriter out, CharSequence value) throws IOException {

    }

    @Override
    public CharSequence read(JsonReader in) throws IOException {
        return null;
    }
}
