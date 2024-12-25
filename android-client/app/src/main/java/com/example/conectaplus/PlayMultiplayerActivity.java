package com.example.conectaplus;

import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.GridLayout;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.conectaplus.conectak.ConectaK;
import com.example.conectaplus.conectak.EvaluadorCK;
import com.example.conectaplus.conectak.Movimiento;
import com.example.conectaplus.jugadores.Evaluador;
import com.example.conectaplus.jugadores.Jugador;
import com.example.conectaplus.jugadores.JugadorAlfaBeta;

public class PlayMultiplayerActivity extends AppCompatActivity {

    private static final int ROWS = 5;
    private static final int COLS = 6;
    private static final int WIN = 4;
    private static final int PROFUNDIDAD = 8;
    private static int CELL_SIZE = 0;

    private ConectaK conectaK;
    private GridLayout boardLayout;
    private LinearLayout buttonLayout;
    private boolean isHumanTurn = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play_ai);

        int screenWidth = getResources().getDisplayMetrics().widthPixels;
        int screenHeight = getResources().getDisplayMetrics().heightPixels;
        CELL_SIZE = Math.min((int)(0.95 * screenWidth / COLS), (int)(0.95 * (screenHeight * 0.7) / ROWS));

        conectaK = new ConectaK(ROWS, COLS, WIN);

        boardLayout = findViewById(R.id.boardLayout);
        buttonLayout = findViewById(R.id.buttonLayout);

        initializeBoard();
        initializeColumnButtons();

        Evaluador<ConectaK> evaluador = new EvaluadorCK();
        Jugador<ConectaK> jugadorIA = new JugadorAlfaBeta<>(evaluador, PROFUNDIDAD);

        new Thread(() -> {
            int resultado = jugarPartida(jugadorIA);
            runOnUiThread(() -> showGameOverDialog(resultado));
        }).start();
    }

    private void initializeBoard() {
        boardLayout.setRowCount(ROWS);
        boardLayout.setColumnCount(COLS);

        for (int i = 0; i < ROWS; i++) {
            for (int j = 0; j < COLS; j++) {
                FrameLayout cellContainer = new FrameLayout(this);
                cellContainer.setBackgroundResource(R.drawable.cell_border);

                GridLayout.LayoutParams params = new GridLayout.LayoutParams();
                params.rowSpec = GridLayout.spec(i);
                params.columnSpec = GridLayout.spec(j);
                params.width = CELL_SIZE;
                params.height = CELL_SIZE;
                params.setMargins(2, 2, 2, 2);

                cellContainer.setLayoutParams(params);
                boardLayout.addView(cellContainer);
            }
        }
    }

    private void initializeColumnButtons() {
        for (int col = 0; col < COLS; col++) {
            Button button = new Button(this);
            button.setText(String.valueOf(col + 1));

            final int column = col;
            button.setOnClickListener(v -> {
                if (isHumanTurn) {
                    dropFicha(column);
                } else {
                    showMessage("No es tu turno");
                }
            });

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(CELL_SIZE, LinearLayout.LayoutParams.WRAP_CONTENT);
            params.setMargins(2, 2, 2, 2); // Márgenes entre los botones
            button.setLayoutParams(params);

            buttonLayout.addView(button);
        }
    }

    private void paintCell(int row, int col, boolean isHuman) {
        runOnUiThread(() -> {
            int index = row * COLS + col;
            FrameLayout cellContainer = (FrameLayout) boardLayout.getChildAt(index);

            View ficha = new View(this);
            GradientDrawable circle = new GradientDrawable();
            circle.setShape(GradientDrawable.OVAL);
            circle.setColor(isHuman ? Color.RED : Color.YELLOW);
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

    private void dropFicha(int colNum) {
        int row = conectaK.siguienteFila(colNum);
        if (row == -1) {
            showMessage("Columna llena");
            return;
        }

        conectaK = conectaK.mueveHumano(colNum);
        paintCell(row, colNum, true);

        if (isGameOver()) {
            showGameOverDialog(getGameResult());
        } else {
            isHumanTurn = false;
        }
    }

    private void handleAITurn(Jugador<ConectaK> jugadorIA) {
        conectaK = jugadorIA.mueve(conectaK);
        Movimiento lastMove = conectaK.getUltimoMov();

        if (lastMove != null) {
            paintCell(lastMove.f(), lastMove.c(), false);
        }

        runOnUiThread(() -> {
            if (isGameOver()) {
                showGameOverDialog(getGameResult());
            } else {
                isHumanTurn = true;
            }
        });
    }

    private boolean isGameOver() {
        return conectaK.ganaActual() || conectaK.ganaOtro() || conectaK.agotado();
    }

    private int jugarPartida(Jugador<ConectaK> jugadorIA) {
        while (!isGameOver()) {
            if (conectaK.turno1()) {
                isHumanTurn = true;
                while (isHumanTurn) {
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException ignored) {
                    }
                }
            } else {
                handleAITurn(jugadorIA);
            }
        }
        return getGameResult();
    }

    private int getGameResult() {
        if (conectaK.ganaActual()) {
            return conectaK.turno1() ? 1 : -1;
        } else if (conectaK.ganaOtro()) {
            return conectaK.turno1() ? -1 : 1;
        } else {
            return 0;
        }
    }

    private void showGameOverDialog(int result) {
        runOnUiThread(() -> {
            String message = result == 1 ? "Enhorabuena, has ganado!" : result == -1 ? "Lo siento, has perdido." : "Es un empate.";
            new AlertDialog.Builder(this)
                    .setTitle("Juego Terminado")
                    .setMessage(message)
                    .setCancelable(false)
                    .setPositiveButton("Aceptar", (dialog, which) -> finish())
                    .show();
        });
    }

    private void showMessage(String message) {
        runOnUiThread(() -> Toast.makeText(this, message, Toast.LENGTH_SHORT).show());
    }
}
