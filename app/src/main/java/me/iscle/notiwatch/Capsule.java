package me.iscle.notiwatch;

import com.google.gson.Gson;

public class Capsule {
    private static final transient Gson gson = new Gson();

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
}
