package com.example.conectaplus;

import android.app.AlertDialog;
import android.database.Cursor;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.conectaplus.database.GameDatabaseHelper;

public class HistoryActivity extends AppCompatActivity {

    private Cursor cursor;
    private GameDatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        try {
            ListView historyListView = findViewById(R.id.history_list_view);
            dbHelper = new GameDatabaseHelper(this);

            cursor = dbHelper.getAllResults();

            if (cursor == null || cursor.getCount() == 0) {
                Toast.makeText(this, "No hay resultados para mostrar.", Toast.LENGTH_SHORT).show();
                return; // Salimos si no hay datos.
            }

            String[] fromColumns = {"result", "timestamp"};
            int[] toViews = {R.id.result_text, R.id.timestamp_text};

            SimpleCursorAdapter adapter = new SimpleCursorAdapter(
                    this,
                    R.layout.history_item,
                    cursor,
                    fromColumns,
                    toViews,
                    0
            );

            historyListView.setAdapter(adapter);

        } catch (Exception e) {
            showErrorDialog(e);
        }
        Button clearHistoryButton = findViewById(R.id.clear_history_button);
        clearHistoryButton.setOnClickListener(v -> {
            new AlertDialog.Builder(this)
                    .setTitle("Borrar Historial")
                    .setMessage("¿Estás seguro de que deseas borrar todo el historial de partidas?")
                    .setPositiveButton("Sí", (dialog, which) -> {
                        dbHelper.getWritableDatabase().delete("game_history", null, null);
                        cursor = dbHelper.getAllResults();
                        ((SimpleCursorAdapter) ((ListView) findViewById(R.id.history_list_view)).getAdapter()).changeCursor(cursor);
                        Toast.makeText(this, "Historial borrado con éxito.", Toast.LENGTH_SHORT).show();
                    })
                    .setNegativeButton("Cancelar", null)
                    .show();
        });

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (cursor != null && !cursor.isClosed()) {
            cursor.close();
        }
        if (dbHelper != null) {
            dbHelper.close();
        }
    }

    /**
     * Muestra un cuadro de diálogo con el mensaje del error.
     *
     * @param e La excepción capturada.
     */
    private void showErrorDialog(Exception e) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Error")
                .setMessage("Se produjo un error: " + e.getMessage())
                .setPositiveButton("Aceptar", null)
                .setNegativeButton("Detalles", (dialog, which) -> {
                    showDetailedErrorDialog(e);
                })
                .setCancelable(false)
                .show();
    }

    /**
     * Muestra detalles más profundos del error en un cuadro de diálogo.
     *
     * @param e La excepción capturada.
     */
    private void showDetailedErrorDialog(Exception e) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Detalles del Error")
                .setMessage(e.toString()) // Muestra el stack trace completo.
                .setPositiveButton("Cerrar", null)
                .setCancelable(false)
                .show();
    }
}
