package com.example.conectaplus.websocket;

public interface ConnectionCallback {
    void onSuccess();
    void onDisconnect();
    void onFailure(Throwable t);
}
