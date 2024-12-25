package com.example.conectaplus;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class InitialActivity extends AppCompatActivity {

    private Button chatButton; // referencia al botón

    private Button iaPlayButtom;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_initial);

        // Obtén la referencia al botón
        chatButton = findViewById(R.id.btn_chat);

        iaPlayButtom = findViewById(R.id.btn_play_ai);

        // Asigna un listener al botón para detectar el clic
        chatButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Crea un Intent para ir a la actividad de chat
                Intent intent = new Intent(InitialActivity.this, ChatActivity.class);
                startActivity(intent);
            }
        });
        iaPlayButtom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Crea un Intent para ir a la actividad de chat
                Intent intent = new Intent(InitialActivity.this, PlayAIActivity.class);
                startActivity(intent);
            }
        });
    }
}
