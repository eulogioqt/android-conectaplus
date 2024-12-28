package com.example.conectaplus;

import android.app.AlertDialog;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.conectaplus.database.GameContract;
import com.example.conectaplus.database.GameDatabase;
import com.example.conectaplus.database.GameDbHelper;
import com.example.conectaplus.database.SingletonMap;
import com.example.conectaplus.database.DatabaseValues; // Importar la clase DatabaseValues
import com.google.android.material.snackbar.Snackbar;

public class HistoryActivity extends AppCompatActivity {

    private Cursor cursor;
    private GameDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        initGameDatabase();

        try {
            ListView historyListView = findViewById(R.id.history_list_view);

            cursor = db.getAllResults();
            if (cursor == null || cursor.getCount() == 0) {
                Toast.makeText(this, getString(R.string.no_results_message), Toast.LENGTH_SHORT).show();
                return;
            }

            String[] fromColumns = {GameContract.GameEntry.COLUMN_NAME_RESULT, GameContract.GameEntry.COLUMN_NAME_TIMESTAMP, GameContract.GameEntry.COLUMN_NAME_TYPE};
            int[] toViews = {R.id.result_text, R.id.timestamp_text, R.id.type_text};

            SimpleCursorAdapter adapter = new SimpleCursorAdapter(
                    this,
                    R.layout.history_item,
                    cursor,
                    fromColumns,
                    toViews,
                    0
            ) {
                @Override
                public void bindView(View view, android.content.Context context, Cursor cursor) {
                    super.bindView(view, context, cursor);

                    int resultColumnIndex = cursor.getColumnIndex(GameContract.GameEntry.COLUMN_NAME_RESULT);
                    int typeColumnIndex = cursor.getColumnIndex(GameContract.GameEntry.COLUMN_NAME_TYPE);

                    if (resultColumnIndex != -1 && typeColumnIndex != -1) {
                        int result = cursor.getInt(resultColumnIndex);
                        int type = cursor.getInt(typeColumnIndex);

                        String transformedResult = DatabaseValues.getString(GameContract.GameEntry.COLUMN_NAME_RESULT, result);
                        String transformedType =  DatabaseValues.getString(GameContract.GameEntry.COLUMN_NAME_TYPE, type);

                        ((TextView) view.findViewById(R.id.result_text)).setText(transformedResult);
                        ((TextView) view.findViewById(R.id.type_text)).setText(transformedType);
                    } else {
                        Log.e("HistoryActivity", "Columnas no encontradas");
                    }
                }
            };

            historyListView.setAdapter(adapter);

        } catch (Exception e) {
            showErrorDialog(e);
        }

        Button clearHistoryButton = findViewById(R.id.clear_history_button);
        clearHistoryButton.setOnClickListener(v -> {
            new AlertDialog.Builder(this)
                    .setTitle(getString(R.string.clear_history_title))
                    .setMessage(getString(R.string.clear_history_message))
                    .setPositiveButton("Sí", (dialog, which) -> {
                        deleteHistory();
                        cursor = db.getAllResults();
                        ((SimpleCursorAdapter) ((ListView) findViewById(R.id.history_list_view)).getAdapter()).changeCursor(cursor);
                        Snackbar.make(findViewById(android.R.id.content), getString(R.string.clear_history_success), Snackbar.LENGTH_SHORT).show();
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
    }

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

    private void showDetailedErrorDialog(Exception e) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Detalles del Error")
                .setMessage(e.toString())
                .setPositiveButton("Cerrar", null)
                .setCancelable(false)
                .show();
    }

    private void initGameDatabase() {
        db = (GameDatabase) SingletonMap.getInstance().get(GameDbHelper.DATABASE_NAME);
        if (db == null) {
            db = new GameDatabase(getApplicationContext());
            SingletonMap.getInstance().put(GameDbHelper.DATABASE_NAME, db);
        }
    }

    private void deleteHistory() {
        db.deleteAllResults();
    }
}
