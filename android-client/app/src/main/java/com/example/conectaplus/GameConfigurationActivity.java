package com.example.conectaplus;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;

import androidx.appcompat.app.AppCompatActivity;

import com.example.conectaplus.play_game.PlayAIActivity;
import com.google.android.material.snackbar.Snackbar;

public class GameConfigurationActivity extends AppCompatActivity {

    private EditText inputRows, inputColumns, inputWin;
    private RadioGroup difficultyGroup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_configuration);

        inputRows = findViewById(R.id.input_rows);
        inputColumns = findViewById(R.id.input_columns);
        inputWin = findViewById(R.id.input_win);
        difficultyGroup = findViewById(R.id.difficulty_group);
        Button btnContinue = findViewById(R.id.btn_continue);

        btnContinue.setOnClickListener(v -> {
            if (validateInputs()) {
                int rows = Integer.parseInt(inputRows.getText().toString());
                int cols = Integer.parseInt(inputColumns.getText().toString());
                int win = Integer.parseInt(inputWin.getText().toString());
                int difficulty = getSelectedDifficulty();

                Intent intent = new Intent(GameConfigurationActivity.this, PlayAIActivity.class);
                intent.putExtra("ROWS", rows + ""); // Lo quiero como String porque con Int me pide un defaultValue y lo quiero obligatorio
                intent.putExtra("COLS", cols + "");
                intent.putExtra("WIN", win + "");
                intent.putExtra("DIFICULTY", difficulty + "");
                startActivity(intent);
            }
        });
    }

    private boolean validateInputs() {
        String rowsText = inputRows.getText().toString();
        String colsText = inputColumns.getText().toString();
        String winText = inputWin.getText().toString();

        if (TextUtils.isEmpty(rowsText) || TextUtils.isEmpty(colsText) || TextUtils.isEmpty(winText)) {
            showError(getString(R.string.error_invalid_input));
            return false;
        }

        int rows = Integer.parseInt(rowsText);
        int cols = Integer.parseInt(colsText);
        int win = Integer.parseInt(winText);

        if (rows < 3 || cols < 3 || rows > 8 || cols > 8) {
            showError(getString(R.string.error_invalid_input));
            return false;
        }

        if (win <= 1 || win > Math.min(rows, cols)) {
            showError(getString(R.string.error_invalid_win_condition));
            return false;
        }

        return true;
    }

    private int getSelectedDifficulty() {
        int selectedId = difficultyGroup.getCheckedRadioButtonId();

        if (selectedId == R.id.difficulty_easy) return 1;
        if (selectedId == R.id.difficulty_medium) return 2;
        if (selectedId == R.id.difficulty_hard) return 3;

        return 1;
    }

    private void showError(String message) {
        Snackbar.make(findViewById(android.R.id.content), message, Snackbar.LENGTH_LONG).show();
    }
}
