package com.example.conectaplus;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.conectaplus.websocket.WebSocketSingleton;

public class MultiplayerActivity extends AppCompatActivity {

    private ProgressBar progressBar;
    private Button createRoomButton;
    private Button joinRoomButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_multiplayer);

        progressBar = findViewById(R.id.progress_bar);
        createRoomButton = findViewById(R.id.create_match_button);
        joinRoomButton = findViewById(R.id.join_match_button);

        showProgressBar();
        connectWebSocket();

        createRoomButton.setOnClickListener(v -> {
            createRoomAndNavigate();
        });

        joinRoomButton.setOnClickListener(v -> {
            Intent intent = new Intent(MultiplayerActivity.this, JoinMatchActivity.class);
            startActivity(intent);
        });
    }

    private void showProgressBar() {
        progressBar.setVisibility(android.view.View.VISIBLE);
        createRoomButton.setVisibility(android.view.View.GONE);
        joinRoomButton.setVisibility(android.view.View.GONE);
    }

    private void hideProgressBar() {
        progressBar.setVisibility(android.view.View.GONE);
        createRoomButton.setVisibility(android.view.View.VISIBLE);
        joinRoomButton.setVisibility(android.view.View.VISIBLE);
    }

    private void connectWebSocket() {
        WebSocketSingleton webSocketSingleton = WebSocketSingleton.getInstance();

        webSocketSingleton.connect(new WebSocketSingleton.Callback() {
            @Override
            public void onSuccess() {
                runOnUiThread(MultiplayerActivity.this::hideProgressBar);
            }

            @Override
            public void onFailure(Throwable t) {
                runOnUiThread(MultiplayerActivity.this::showConnectionFailedDialog);
            }
        });
    }

    private void showConnectionFailedDialog() {
        new AlertDialog.Builder(MultiplayerActivity.this)
                .setTitle(getString(R.string.ad_connection_error))
                .setMessage(getString(R.string.ad_connection_error_desc))
                .setPositiveButton(getString(R.string.btn_accept), (dialog, which) -> {
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
                    runOnUiThread(() -> Toast.makeText(MultiplayerActivity.this, getString(R.string.cannot_create_match), Toast.LENGTH_SHORT).show());
                }
            }
        });
    }
}
