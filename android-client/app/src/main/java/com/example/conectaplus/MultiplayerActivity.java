package com.example.conectaplus;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

public class MultiplayerActivity extends AppCompatActivity {

    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_multiplayer);

        progressBar = findViewById(R.id.progressBar);
        progressBar.setVisibility(android.view.View.GONE);

        showProgressBar();
        connectWebSocket();

        Button createRoomButton = findViewById(R.id.createRoomButton);
        Button joinRoomButton = findViewById(R.id.joinRoomButton);

        createRoomButton.setOnClickListener(v -> {
            // Enviar el mensaje para crear la sala
            createRoomAndNavigate();
        });

        joinRoomButton.setOnClickListener(v -> {
            Intent intent = new Intent(MultiplayerActivity.this, JoinRoomActivity.class);
            startActivity(intent);
        });
    }

    private void showProgressBar() {
        progressBar.setVisibility(android.view.View.VISIBLE); // Mostrar el ProgressBar
    }

    private void hideProgressBar() {
        progressBar.setVisibility(android.view.View.GONE); // Ocultar el ProgressBar
    }

    private void connectWebSocket() {
        WebSocketSingleton webSocketSingleton = WebSocketSingleton.getInstance();

        // Usar el callback para manejar éxito o fallo
        webSocketSingleton.connect(new WebSocketSingleton.Callback() {
            @Override
            public void onSuccess() {
                runOnUiThread(MultiplayerActivity.this::hideProgressBar);  // Ocultar el ProgressBar en el hilo principal
            }

            @Override
            public void onFailure(Throwable t) {
                runOnUiThread(MultiplayerActivity.this::showConnectionFailedDialog);  // Mostrar el diálogo de error
            }
        });
    }

    private void showConnectionFailedDialog() {
        new AlertDialog.Builder(MultiplayerActivity.this)
                .setTitle("Error de conexión")
                .setMessage("No se pudo conectar al servidor. Intente nuevamente.")
                .setPositiveButton("Aceptar", (dialog, which) -> {
                    finish();
                })
                .setCancelable(false)
                .show();
    }

    private void createRoomAndNavigate() {
        WebSocketSingleton webSocketSingleton = WebSocketSingleton.getInstance();

        webSocketSingleton.sendMessage("CREATE");

        webSocketSingleton.setOnMessageListener(message -> {
            if (message.startsWith("CREATE")) {
                String[] parts = message.split(" ");
                if (parts.length > 1) {
                    String matchCode = parts[1];

                    Intent intent = new Intent(MultiplayerActivity.this, WaitingActivty.class);
                    intent.putExtra("MATCH_CODE", matchCode);
                    startActivity(intent);
                } else {
                    runOnUiThread(() -> Toast.makeText(MultiplayerActivity.this, "Error al crear la sala.", Toast.LENGTH_SHORT).show());
                }
            }
        });
    }
}
