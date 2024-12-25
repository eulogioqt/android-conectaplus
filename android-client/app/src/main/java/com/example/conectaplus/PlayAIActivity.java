package com.example.conectaplus;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.conectaplus.conectak.ConectaK;
import com.example.conectaplus.conectak.EvaluadorCK;
import com.example.conectaplus.conectak.Movimiento;
import com.example.conectaplus.jugadores.Evaluador;
import com.example.conectaplus.jugadores.Jugador;
import com.example.conectaplus.jugadores.JugadorAleatorio;
import com.example.conectaplus.jugadores.JugadorAlfaBeta;

public class PlayAIActivity extends AppCompatActivity {

    private ConectaK conectaK;
    private static final int ROWS = 3;
    private static final int COLS = 3;
    private GridLayout boardLayout;
    private LinearLayout buttonLayout;
    private boolean isHumanTurn = false;

    private int profundidad = 8;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play_ai);

        conectaK = new ConectaK(ROWS, COLS, 3);

        boardLayout = findViewById(R.id.boardLayout);
        buttonLayout = findViewById(R.id.buttonLayout);

        initializeBoard();
        initializeColumnButtons();

        //Jugador<ConectaK> jugadorIA = new JugadorAleatorio<>();

        Evaluador<ConectaK> evaluador = new EvaluadorCK();

        Jugador<ConectaK> jugadorIA = new JugadorAlfaBeta<>(evaluador, profundidad);

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
                TextView cell = new TextView(this);
                cell.setText("");
                cell.setBackgroundResource(R.drawable.cell_border);
                GridLayout.LayoutParams params = new GridLayout.LayoutParams();
                params.rowSpec = GridLayout.spec(i);
                params.columnSpec = GridLayout.spec(j);
                params.width = 200;
                params.height = 200;
                params.setMargins(2, 2, 2, 2);
                cell.setLayoutParams(params);
                boardLayout.addView(cell);
            }
        }
    }

    private void initializeColumnButtons() {
        for (int col = 0; col < COLS; col++) {
            Button button = new Button(this);
            button.setText("Col " + (col + 1));
            final int column = col;
            button.setOnClickListener(v -> {
                if (isHumanTurn) {
                    dropFicha(column);
                } else {
                    showMessage("No es tu turno");
                }
            });

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );
            params.setMargins(10, 10, 10, 10);
            button.setLayoutParams(params);
            buttonLayout.addView(button);
        }
    }

    public void paintCell(int x, int y, String value) {
        int index = x * COLS + y;
        TextView cell = (TextView) boardLayout.getChildAt(index);
        cell.setText(value);
    }

    public void dropFicha(int colNum) {
        int row = conectaK.siguienteFila(colNum);
        if (row == -1) {
            showMessage("Columna llena");
            return;
        }

        conectaK = conectaK.mueveHumano(colNum);
        paintCell(row, colNum, "X");

        if (isGameOver(conectaK)) {
            showGameOverDialog(getGameResult(conectaK));
        } else {
            isHumanTurn = false;
        }
    }

    private void handleAITurn(Jugador<ConectaK> jugadorIA, ConectaK e) {
        conectaK = jugadorIA.mueve(e);

        Movimiento lastMove = conectaK.getUltimoMov();
        if (lastMove != null) {
            paintCell(lastMove.f(), lastMove.c(), "O");
        }

        if (isGameOver(conectaK)) {
            showGameOverDialog(getGameResult(conectaK));
        } else {
            isHumanTurn = true;
        }
    }

    private boolean isGameOver(ConectaK e) {
        return e.ganaActual() || e.ganaOtro() || e.agotado();
    }

    private int jugarPartida(Jugador<ConectaK> jugadorIA) {
        while (!isGameOver(conectaK)) {
            if (conectaK.turno1()) {
                isHumanTurn = true;
                while (isHumanTurn) {
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException ignored) {
                    }
                }
            } else {
                handleAITurn(jugadorIA, conectaK);
            }
        }
        return getGameResult(conectaK);
    }

    private int getGameResult(ConectaK e) {
        if (e.ganaActual()) {
            return e.turno1() ? 1 : -1;
        } else if (e.ganaOtro()) {
            return e.turno1() ? -1 : 1;
        } else {
            return 0;
        }
    }

    private void showGameOverDialog(int result) {
        runOnUiThread(() -> {
            String message;
            if (result == 1) {
                message = "Enhorabuena, has ganado!";
            } else if (result == -1) {
                message = "Lo siento, has perdido.";
            } else {
                message = "Es un empate.";
            }

            new AlertDialog.Builder(this)
                    .setTitle("Juego Terminado")
                    .setMessage(message)
                    .setCancelable(false) // Evita que el diálogo se cierre accidentalmente
                    .setPositiveButton("Aceptar", (dialog, which) -> finish())
                    .show();
        });
    }


    private void showMessage(String message) {
        runOnUiThread(() -> Toast.makeText(this, message, Toast.LENGTH_SHORT).show());
    }
}
