package com.example.conectaplus;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.example.conectaplus.play_game.PlayMultiplayerActivity;
import com.example.conectaplus.websocket.WebSocketSingleton;

public class WaitingActivty extends AppCompatActivity {

    private TextView waitingText;
    private TextView roomCodeText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_waiting_activty);

        waitingText = findViewById(R.id.waiting_text);
        roomCodeText = findViewById(R.id.match_code_text);

        String matchCode = getIntent().getStringExtra("MATCH_CODE");

        roomCodeText.setText(String.format("%s %s", getString(R.string.match_string), matchCode != null ? matchCode : "?"));

        WebSocketSingleton.getInstance().setOnMessageListener(message -> {
            if (message.startsWith("START")) {
                Intent intent = new Intent(WaitingActivty.this, PlayMultiplayerActivity.class);
                intent.putExtra("MATCH_CODE", matchCode);
                intent.putExtra("TURNO_LOCAL", "1");
                startActivity(intent);

                finish();
            }
        });
    }
}
