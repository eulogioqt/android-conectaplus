package com.example.conectaplus;

import android.app.AlertDialog;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;
import okhttp3.Response;

public class ChatActivity extends AppCompatActivity {

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
                Toast.makeText(ChatActivity.this, "Por favor, ingresa un mensaje", Toast.LENGTH_SHORT).show();
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
    private void startWebSocketAuth() {
        String token = obtenerToken();

        if (token == null || token.isEmpty()) {
            // Si no se pudo obtener el token, mostramos un modal de error.
            runOnUiThread(() -> {
                new AlertDialog.Builder(ChatActivity.this)
                        .setTitle("Error de autenticación")
                        .setMessage("No se pudo obtener el token de autenticación. Verifique sus credenciales.")
                        .setPositiveButton("OK", (dialog, which) -> {
                            dialog.dismiss();
                            // Aquí puedes implementar alguna lógica adicional, como reintentar la autenticación
                        })
                        .show();
            });
            return; // No continuamos con la conexión WebSocket
        }
        OkHttpClient client = new OkHttpClient.Builder()
                .build();

        Request request = new Request.Builder()
                .url("wss://eulogioqt-raspberry.jumpingcrab.com:8765") // Asegúrate de usar wss si tu servidor soporta TLS
                .addHeader("Authorization", "Bearer " + token)          // Agregar cabecera de autenticación
                .build();

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

    private String obtenerToken() {
        // Suponiendo que este es tu endpoint de login
        String urlLogin = "http://eulogioqt-raspberry.jumpingcrab.com:5000/login";

        OkHttpClient client = new OkHttpClient();

        // Credenciales a enviar, por ejemplo:
        String json = "{\"username\":\"usuario\",\"password\":\"contraseña\"}";
        RequestBody body = RequestBody.create(
                json,
                MediaType.get("application/json; charset=utf-8")
        );

        Request request = new Request.Builder()
                .url(urlLogin)
                .post(body)
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (response.isSuccessful() && response.body() != null) {
                String responseBody = response.body().string();
                // Asumiendo que el JSON devuelto es del tipo: {"token":"VALOR_DEL_TOKEN"}
                String token = new JSONObject(responseBody).optString("token", null);
                return token; // Retornamos el token obtenido
            } else {
                // Respuesta no satisfactoria del servidor
                Log.e(TAG, "Error al obtener token: " + response.code());
                return null;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
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
