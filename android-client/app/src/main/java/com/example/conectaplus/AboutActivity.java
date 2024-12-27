package com.example.conectaplus;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class AboutActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        // Configura el primer creador
        TextView creator1Link = findViewById(R.id.creator1_link);
        creator1Link.setOnClickListener(v -> openLink("https://github.com/antbaena")); // Cambia por tu enlace

        // Configura el segundo creador
        TextView creator2Link = findViewById(R.id.creator2_link);
        creator2Link.setOnClickListener(v -> openLink("https://github.com/eulogioqt")); // Cambia por tu enlace
    }

    /**
     * Abre un enlace en el navegador predeterminado
     */
    private void openLink(String url) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(url));
        startActivity(intent);
    }
}
