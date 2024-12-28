package com.example.conectaplus.websocket;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import androidx.appcompat.app.AlertDialog;

import com.example.conectaplus.InitialActivity;
import com.example.conectaplus.R;

import okhttp3.Request;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;
import okhttp3.Response;

public class WebSocketSingleton {

    private static final String TAG = "WebSocketSingleton";
    private static WebSocketSingleton instance;
    private WebSocket webSocket;
    private static final String WS_URL = "ws://eulogioqt-raspberry.jumpingcrab.com:8765";
    private ConnectionCallback callback;
    private boolean isConnected = false;

    private MessageListener messageListener;

    private WebSocketSingleton() {}

    public static synchronized WebSocketSingleton getInstance() {
        if (instance == null) {
            instance = new WebSocketSingleton();
        }
        return instance;
    }

    public void connect() {
        if (isConnected) {
            Log.d(TAG, "Ya está conectado");
            if (callback != null) callback.onSuccess();
            return;
        }

        // "ws://192.168.100.2:8765"
        Request request = new Request.Builder().url(WS_URL).build();

        WebSocketListener listener = new WebSocketListener() {
            @Override
            public void onOpen(WebSocket webSocket, Response response) {
                super.onOpen(webSocket, response);
                WebSocketSingleton.this.webSocket = webSocket;
                isConnected = true;
                Log.d(TAG, "WebSocket abierto");
                if (callback != null) callback.onSuccess();
            }

            @Override
            public void onMessage(WebSocket webSocket, String text) {
                super.onMessage(webSocket, text);
                Log.d(TAG, "Mensaje recibido: " + text);
                if (messageListener != null) {
                    messageListener.onMessage(text);  // Llamar al listener cuando se recibe un mensaje
                }
            }

            @Override
            public void onFailure(WebSocket webSocket, Throwable t, Response response) {
                super.onFailure(webSocket, t, response);
                Log.e(TAG, "Error en WebSocket: " + t.getMessage());

                if (callback != null) {
                    if (isConnected)
                        callback.onDisconnect();
                    else
                        callback.onFailure(t);
                }

                isConnected = false;
            }

            @Override
            public void onClosing(WebSocket webSocket, int code, String reason) {
                super.onClosing(webSocket, code, reason);
                if (callback != null) callback.onDisconnect();
                isConnected = false;
                Log.d(TAG, "WebSocket cerrando: " + reason);
            }
        };

        new okhttp3.OkHttpClient().newWebSocket(request, listener);
    }

    public void sendMessage(String message) {
        if (webSocket != null && isConnected) {
            Log.d(TAG, "Enviando mensaje: " + message);
            webSocket.send(message);
        } else {
            Log.d(TAG, "No se puede enviar el mensaje, WebSocket no está conectado.");
        }
    }

    public void close() {
        if (webSocket != null && isConnected) {
            webSocket.close(1000, "Cerrando conexión");
            isConnected = false;
            Log.d(TAG, "Conexión WebSocket cerrada");
        }
    }

    public boolean isConnected() {return isConnected;}
    public void setConnectionCallback(ConnectionCallback callback) {this.callback = callback;}
    public void setOnMessageListener(MessageListener listener) {
        this.messageListener = listener;
    }
}
