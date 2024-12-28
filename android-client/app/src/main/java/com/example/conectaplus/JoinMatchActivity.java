package com.example.conectaplus;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.conectaplus.play_game.PlayMultiplayerActivity;
import com.example.conectaplus.websocket.WebSocketSingleton;

public class JoinMatchActivity extends AppCompatActivity {

    private EditText roomCodeInput;
    private Button acceptButton;
    private TextView errorText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join_match);

        roomCodeInput = findViewById(R.id.match_code_input);
        acceptButton = findViewById(R.id.accept_button);
        errorText = findViewById(R.id.error_text);

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
                Intent intent = new Intent(JoinMatchActivity.this, PlayMultiplayerActivity.class);
                intent.putExtra("MATCH_CODE", matchCode);
                intent.putExtra("TURNO_LOCAL", "2");
                startActivity(intent);

                finish();
            } else if (message.startsWith("DENY")) {
                runOnUiThread(() -> {
                    errorText.setText(String.format("%s %s", getString(R.string.cannot_join_match), matchCode));
                    errorText.setVisibility(TextView.VISIBLE);
                });
            }
        });
    }
}
