package com.example.conectaplus;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Spinner;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class OptionsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_options);

        Spinner languageSpinner = findViewById(R.id.spinner_language);
        setSpinnerSelection(languageSpinner);

        languageSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedLanguage = position == 0 ? "es" : "en";
                if (!selectedLanguage.equals(LocaleHelper.getCurrentLanguage(OptionsActivity.this))) {
                    LocaleHelper.setLocale(OptionsActivity.this, selectedLanguage);

                    Intent intent = new Intent(OptionsActivity.this, InitialActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    finish();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // No action needed
            }
        });
    }

    private void setSpinnerSelection(Spinner spinner) {
        String currentLanguage = LocaleHelper.getCurrentLanguage(this);
        int position = "es".equals(currentLanguage) ? 0 : 1;
        spinner.setSelection(position);
    }
}
