package com.example.conectaplus;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

public class WaitingActivty extends AppCompatActivity {

    private TextView waitingText;
    private TextView roomCodeText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_waiting_activty);

        waitingText = findViewById(R.id.waitingText);
        roomCodeText = findViewById(R.id.roomCodeText);

        String matchCode = getIntent().getStringExtra("MATCH_CODE");

        roomCodeText.setText(String.format("Sala %s", matchCode != null ? matchCode : "Error"));

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
