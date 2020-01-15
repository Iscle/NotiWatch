package me.iscle.notiwatch.service;

public enum ConnectionState {
    DISCONNECTED, // We're doing nothing
    LISTENING,
    CONNECTING, // Initiating an outgoing connection
    CONNECTED // Connected to a remote device
}
