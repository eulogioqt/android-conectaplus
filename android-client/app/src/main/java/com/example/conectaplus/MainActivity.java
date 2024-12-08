package com.example.conectaplus;

import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "WebSocket";
    private WebSocket webSocket;
    private MessageAdapter messageAdapter;
    private List<String> messageList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setSupportActionBar(findViewById(R.id.toolbar));

        EditText messageEditText = findViewById(R.id.messageEditText);
        Button sendButton = findViewById(R.id.sendButton);
        RecyclerView recyclerView = findViewById(R.id.recyclerView);

        messageList = new ArrayList<>();
        messageAdapter = new MessageAdapter(messageList);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(messageAdapter);

        startWebSocket();

        sendButton.setOnClickListener(v -> {
            String message = messageEditText.getText().toString();
            if (!message.isEmpty()) {
                sendMessage(message);
                messageEditText.setText("");
                addMessage("[Tú] " + message);
            } else {
                Toast.makeText(MainActivity.this, "Por favor, ingresa un mensaje", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void startWebSocket() {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder().url("ws://eulogioqt-raspberry.jumpingcrab.com:8765").build();
        WebSocketListener listener = new WebSocketListener() {
            @Override
            public void onOpen(WebSocket webSocket, Response response) {
                Log.d(TAG, "WebSocket abierto");
            }

            @Override
            public void onMessage(WebSocket webSocket, String text) {
                Log.d(TAG, "Mensaje recibido: " + text);
                runOnUiThread(() -> addMessage(text));
            }

            @Override
            public void onFailure(WebSocket webSocket, Throwable t, Response response) {
                Log.e(TAG, "Error en WebSocket: " + t.getMessage());
            }

            @Override
            public void onClosing(WebSocket webSocket, int code, String reason) {
                Log.d(TAG, "WebSocket cerrando: " + reason);
            }
        };
        webSocket = client.newWebSocket(request, listener);
    }

    private void sendMessage(String message) {
        if (webSocket != null) {
            Log.d(TAG, "Enviando mensaje: " + message);
            webSocket.send(message);
        }
    }

    private void addMessage(String message) {
        messageList.add(message);
        messageAdapter.notifyItemInserted(messageList.size() - 1);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (webSocket != null) {
            webSocket.close(1000, "Cerrando conexión");
        }
    }
}
