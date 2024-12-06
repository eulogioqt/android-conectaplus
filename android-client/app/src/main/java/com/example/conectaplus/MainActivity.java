package com.example.conectaplus;

import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "WebSocket";
    private WebSocket webSocket;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        EditText messageEditText = findViewById(R.id.messageEditText);
        Button sendButton = findViewById(R.id.sendButton);

        startWebSocket();

        sendButton.setOnClickListener(v -> {
            String message = messageEditText.getText().toString();
            if (!message.isEmpty()) {
                sendMessage(message);
            } else {
                Toast.makeText(MainActivity.this, "Por favor, ingresa un mensaje", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void startWebSocket() {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder().url("ws://192.168.1.153:8765").build();
        WebSocketListener listener = new WebSocketListener() {
            @Override
            public void onOpen(WebSocket webSocket, Response response) {
                Log.d(TAG, "WebSocket abierto");
            }

            @Override
            public void onMessage(WebSocket webSocket, String text) {
                Log.d(TAG, "Mensaje recibido: " + text);
                runOnUiThread(() -> {
                    Toast.makeText(MainActivity.this, "Mensaje invertido: " + text, Toast.LENGTH_SHORT).show();
                });
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (webSocket != null) {
            webSocket.close(1000, "Cerrando conexión");
        }
    }
}
