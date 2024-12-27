package com.example.conectaplus;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class InitialActivity extends AppCompatActivity {

    private Button iaPlayButton;
    private Button multiplayerPlayButton;

    private Button optionsButton;
    private Button historyButton;

    private Button aboutButton;
    private Button exitButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_initial);

        iaPlayButton = findViewById(R.id.btn_play_ai);
        multiplayerPlayButton = findViewById(R.id.btn_play_multiplayer);

        optionsButton = findViewById(R.id.btn_options);
        historyButton = findViewById(R.id.btn_history);
        aboutButton = findViewById(R.id.btn_about);


        exitButton = findViewById(R.id.btn_exit);

        iaPlayButton.setOnClickListener(view -> {
            Intent intent = new Intent(InitialActivity.this, PlayAIActivity.class);
            startActivity(intent);
        });

        multiplayerPlayButton.setOnClickListener(view -> {
            Intent intent = new Intent(InitialActivity.this, MultiplayerActivity.class);
            startActivity(intent);
        });

        optionsButton.setOnClickListener(view -> {
            Intent intent = new Intent(InitialActivity.this, OptionsActivity.class);
            startActivity(intent);
        });

        historyButton.setOnClickListener(view -> {
            Intent intent = new Intent(InitialActivity.this, HistoryActivity.class);
            startActivity(intent);
        });

        aboutButton.setOnClickListener(view -> {
            Intent intent = new Intent(InitialActivity.this, AboutActivity.class);
            startActivity(intent);
        });

        exitButton.setOnClickListener(view -> {
            finish();
            System.exit(0);
        });
    }
}
