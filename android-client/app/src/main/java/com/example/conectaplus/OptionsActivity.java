package com.example.conectaplus;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Spinner;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.snackbar.Snackbar;

import com.example.conectaplus.utils.LocaleHelper;

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
                String selectedLanguage = "en";
                String message = "Language changed to English";

                switch (position) {
                    case 0:
                        selectedLanguage = "es";
                        message = "Idioma cambiado a Español";
                        break;
                    case 1:
                        break;
                    case 2:
                        selectedLanguage = "ar";
                        message = "العرالعرعرية";
                        break;
                    default:
                        break;
                }

                if (!selectedLanguage.equals(LocaleHelper.getCurrentLanguage(OptionsActivity.this))) {
                    LocaleHelper.setLocale(OptionsActivity.this, selectedLanguage);

                    Intent intent = new Intent(OptionsActivity.this, InitialActivity.class);
                    intent.putExtra("SNACKBAR", message);
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
        int position = "es".equals(currentLanguage) ? 0 : ("ar".equals(currentLanguage) ? 2 : 1);
        spinner.setSelection(position);
    }
}
