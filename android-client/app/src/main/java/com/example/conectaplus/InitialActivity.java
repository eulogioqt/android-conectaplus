package com.example.conectaplus;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class InitialActivity extends AppCompatActivity {

    private Button iaPlayButton;
    private Button multiplayerPlayButton;
    private Button historyButton;
    private Button exitButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_initial);

        iaPlayButton = findViewById(R.id.btn_play_ai);
        multiplayerPlayButton = findViewById(R.id.btn_play_multiplayer);
        historyButton = findViewById(R.id.btn_history);
        exitButton = findViewById(R.id.btn_exit);

        iaPlayButton.setOnClickListener(view -> {
            Intent intent = new Intent(InitialActivity.this, PlayAIActivity.class);
            startActivity(intent);
        });

        multiplayerPlayButton.setOnClickListener(view -> {
            Intent intent = new Intent(InitialActivity.this, MultiplayerActivity.class);
            startActivity(intent);
        });

        historyButton.setOnClickListener(view -> {
            Intent intent = new Intent(InitialActivity.this, ChatActivity.class);
            startActivity(intent);
        });

        exitButton.setOnClickListener(view -> {
            finish();
            System.exit(0);
        });
    }
}
