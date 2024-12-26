package com.example.conectaplus;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class JoinRoomActivity extends AppCompatActivity {

    private EditText roomCodeInput;
    private Button acceptButton;
    private TextView errorText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join_room);

        roomCodeInput = findViewById(R.id.roomCodeInput);
        acceptButton = findViewById(R.id.acceptButton);
        errorText = findViewById(R.id.errorText);

        acceptButton.setOnClickListener(v -> {
            String roomCode = roomCodeInput.getText().toString();
            if (!roomCode.isEmpty()) {
                joinRoomAndNavigate(roomCode);
            }
        });
    }

    private void joinRoomAndNavigate(String matchCode) {
        WebSocketSingleton webSocketSingleton = WebSocketSingleton.getInstance();

        webSocketSingleton.sendMessage("JOIN " + matchCode);

        webSocketSingleton.setOnMessageListener(message -> {
            if (message.startsWith("START")) {
                Intent intent = new Intent(JoinRoomActivity.this, PlayMultiplayerActivity.class);
                intent.putExtra("MATCH_CODE", matchCode);
                intent.putExtra("TURNO_LOCAL", "2");
                startActivity(intent);

                finish();
            } else if (message.startsWith("DENY")) {
                runOnUiThread(() -> {
                    errorText.setText("No se pudo unir a la sala MatchCode " + matchCode);
                    errorText.setVisibility(TextView.VISIBLE);
                });
            }
        });
    }
}
