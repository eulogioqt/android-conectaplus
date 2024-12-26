package com.example.conectaplus;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class InitialActivity extends AppCompatActivity {

    private Button chatButton;
    private Button iaPlayButton;
    private Button multiplayerPlayButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_initial);

        chatButton = findViewById(R.id.btn_chat);
        iaPlayButton = findViewById(R.id.btn_play_ai);
        multiplayerPlayButton = findViewById(R.id.btn_play_multiplayer);

        chatButton.setOnClickListener(view -> {
            Intent intent = new Intent(InitialActivity.this, ChatActivity.class);
            startActivity(intent);
        });

        iaPlayButton.setOnClickListener(view -> {
            Intent intent = new Intent(InitialActivity.this, PlayAIActivity.class);
            startActivity(intent);
        });

        multiplayerPlayButton.setOnClickListener(view -> {
            Intent intent = new Intent(InitialActivity.this, MultiplayerActivity.class);
            startActivity(intent);
        });
    }
}
