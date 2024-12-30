package com.example.conectaplus.play_game;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.StateListDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.GridLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.conectaplus.InitialActivity;
import com.example.conectaplus.R;
import com.example.conectaplus.utils.database.GameDatabase;
import com.example.conectaplus.utils.database.GameDbHelper;
import com.example.conectaplus.utils.database.SingletonMap;
import com.example.conectaplus.game_conectak.conectak.ConectaK;
import com.example.conectaplus.game_conectak.jugadores.Jugador;

public abstract class PlayBaseActivity extends AppCompatActivity {

    protected GameDatabase gameDatabase;

    protected static int ROWS = 5;
    protected static int COLS = 6;
    protected static int WIN = 4;
    protected static int CELL_SIZE = 0;

    protected ConectaK conectaK;
    private LinearLayout turnLayout;
    private TextView turnoText;
    protected GridLayout boardLayout;
    protected LinearLayout buttonLayout;
    private Toast currentToast;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        int screenWidth = getResources().getDisplayMetrics().widthPixels;
        int screenHeight = getResources().getDisplayMetrics().heightPixels;
        CELL_SIZE = Math.min((int)(0.85 * screenWidth / COLS), (int)(0.95 * (screenHeight * 0.7) / ROWS));

        conectaK = new ConectaK(ROWS, COLS, WIN);

        turnLayout = findViewById(R.id.turn_layout);
        boardLayout = findViewById(R.id.board_layout);
        buttonLayout = findViewById(R.id.button_layout);

        initializeBoard();
        initializeColumnButtons();
        initializeTurnLayout();
        initGameDatabase();
    }

    private void initializeTurnLayout() {
        View ficha = new View(this);
        GradientDrawable circle = new GradientDrawable();
        circle.setShape(GradientDrawable.OVAL);
        circle.setStroke(4, Color.BLACK);
        ficha.setBackground(circle);

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(CELL_SIZE, LinearLayout.LayoutParams.WRAP_CONTENT);
        params.width = (int) (CELL_SIZE * 0.75);
        params.height = (int) (CELL_SIZE * 0.75);
        ficha.setLayoutParams(params);

        turnLayout.addView(ficha);

        turnoText = new TextView(this);
        turnoText.setTextSize(24);
        turnoText.setPadding(24, 0, 0, 0);
        turnLayout.setPadding(0, 0, 0, 32);

        updateTurnDisplay();

        turnLayout.addView(turnoText);
    }
    protected void updateTurnDisplay() {
        turnoText.setText(String.format("%s", getString(isLocalTurn() ? R.string.turn_you_string : R.string.turn_rival_string)));

        GradientDrawable circle = (GradientDrawable) turnLayout.getChildAt(0).getBackground();
        circle.setColor((isMainPlayer() && isLocalTurn()) || (!isMainPlayer() && !isLocalTurn()) ? Color.RED : Color.YELLOW);
    }

    protected void initializeBoard() {
        boardLayout.setRowCount(ROWS);
        boardLayout.setColumnCount(COLS);

        for (int i = 0; i < ROWS; i++) {
            for (int j = 0; j < COLS; j++) {
                FrameLayout cellContainer = new FrameLayout(this);

                GradientDrawable emptyCircle = new GradientDrawable();
                emptyCircle.setShape(GradientDrawable.OVAL);
                emptyCircle.setColor(Color.rgb(0, 0, 139)); // Azul oscuro
                emptyCircle.setStroke(4, Color.BLACK);

                View emptyView = new View(this);
                emptyView.setBackground(emptyCircle);

                FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
                        FrameLayout.LayoutParams.MATCH_PARENT,
                        FrameLayout.LayoutParams.MATCH_PARENT
                );
                params.setMargins(10, 10, 10, 10);
                emptyView.setLayoutParams(params);

                cellContainer.addView(emptyView);

                GridLayout.LayoutParams cellParams = new GridLayout.LayoutParams();
                cellParams.rowSpec = GridLayout.spec(i);
                cellParams.columnSpec = GridLayout.spec(j);
                cellParams.width = CELL_SIZE;
                cellParams.height = CELL_SIZE;
                cellParams.setMargins(2, 2, 2, 2);

                cellContainer.setLayoutParams(cellParams);
                boardLayout.addView(cellContainer);
            }
        }
    }

    protected void initializeColumnButtons() {
        for (int col = 0; col < COLS; col++) {
            Button button = new Button(this);
            button.setText(String.valueOf(col + 1));
            button.setTextColor(getResources().getColor(R.color.white));

            StateListDrawable stateDrawable = new StateListDrawable();

            GradientDrawable normalDrawable = new GradientDrawable();
            normalDrawable.setShape(GradientDrawable.OVAL);
            normalDrawable.setColor(Color.parseColor("#0000BD"));
            normalDrawable.setStroke(4, Color.BLACK);

            GradientDrawable pressedDrawable = new GradientDrawable();
            pressedDrawable.setShape(GradientDrawable.OVAL);
            pressedDrawable.setColor(Color.parseColor("#00008B"));
            pressedDrawable.setStroke(4, Color.BLACK);

            stateDrawable.addState(new int[]{android.R.attr.state_pressed}, pressedDrawable);
            stateDrawable.addState(new int[]{}, normalDrawable);

            button.setBackground(stateDrawable);

            final int column = col;
            button.setOnClickListener(v -> {
                if (isLocalTurn()) {
                    dropFicha(column, isMainPlayer());
                } else {
                    showMessage(getString(R.string.not_your_turn));
                }
            });

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(CELL_SIZE - 10, CELL_SIZE - 10);
            params.setMargins(8, 8, 8, 8);
            button.setLayoutParams(params);

            buttonLayout.addView(button);
        }
    }



    protected void paintCell(int row, int col, boolean isMainPlayer) {
        runOnUiThread(() -> {
            int index = row * COLS + col;
            FrameLayout cellContainer = (FrameLayout) boardLayout.getChildAt(index);

            // Eliminar cualquier fondo previo (celda azul)
            cellContainer.removeAllViews();

            // Crear una ficha con color rojo o amarillo
            View ficha = new View(this);
            GradientDrawable circle = new GradientDrawable();
            circle.setShape(GradientDrawable.OVAL);
            circle.setColor(isMainPlayer ? Color.RED : Color.YELLOW);
            circle.setStroke(4, Color.BLACK);

            ficha.setBackground(circle);

            FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
                    FrameLayout.LayoutParams.MATCH_PARENT,
                    FrameLayout.LayoutParams.MATCH_PARENT
            );
            params.setMargins(10, 10, 10, 10);
            ficha.setLayoutParams(params);

            cellContainer.addView(ficha);
        });
    }

    protected void dropFicha(int colNum, boolean isMainTurn) {
        int row = conectaK.siguienteFila(colNum);
        if (row == -1) {
            showMessage(getString(R.string.full_column));
            return;
        }

        conectaK = conectaK.mueveHumano(colNum);
        paintCell(row, colNum, isMainTurn);
        runOnUiThread(this::updateTurnDisplay);
    }

    protected boolean isGameOver() {
        return conectaK.ganaActual() || conectaK.ganaOtro() || conectaK.agotado();
    }

    private void showMessage(String message) {
        runOnUiThread(() -> {
            if (currentToast != null) {
                currentToast.cancel();
            }
            currentToast = Toast.makeText(this, message, Toast.LENGTH_SHORT);
            currentToast.show();
        });
    }

    protected void showGameOverDialog(int result, int players) {
        runOnUiThread(() -> {
            String message;

            if (result == 1)
                message = getString(R.string.ad_game_finished_win_desc);
            else if (result == -1)
                message = getString(R.string.ad_game_finished_lose_desc);
            else
                message = getString(R.string.ad_game_finished_draw_desc);

            gameDatabase.addResult(result, players);

            new AlertDialog.Builder(this)
                    .setTitle(getString(R.string.ad_game_finished))
                    .setMessage(message)
                    .setCancelable(false)
                    .setPositiveButton(getString(R.string.btn_accept), (dialog, which) -> {
                        Intent intent = new Intent(this, InitialActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                        finish();
                    })
                    .show();
        });
    }

    protected void startMatch() {
        new Thread(() -> {
            jugarPartida(getOtherPlayer());
            runOnUiThread(() -> showGameOverDialog(getGameResult()));
        }).start();
    }

    private void initGameDatabase() {
        gameDatabase = (GameDatabase) SingletonMap.getInstance().get(GameDbHelper.DATABASE_NAME);
        if(gameDatabase == null) {
            gameDatabase = new GameDatabase(getApplicationContext());
            SingletonMap.getInstance().put(GameDbHelper.DATABASE_NAME, gameDatabase);
        }
    }

    protected abstract int getGameResult();
    protected abstract Jugador<ConectaK> getOtherPlayer();
    protected abstract void jugarPartida(Jugador<ConectaK> jugadorIA);
    protected abstract void showGameOverDialog(int result);
    protected abstract boolean isLocalTurn();
    protected abstract boolean isMainPlayer();
}
