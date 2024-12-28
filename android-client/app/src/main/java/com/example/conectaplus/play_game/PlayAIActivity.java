package com.example.conectaplus.play_game;

import android.os.Bundle;
import android.util.Log;

import com.example.conectaplus.R;
import com.example.conectaplus.game_conectak.conectak.ConectaK;
import com.example.conectaplus.game_conectak.jugadores.JugadorAleatorio;
import com.example.conectaplus.game_conectak.jugadores.JugadorAlfaBeta;
import com.example.conectaplus.game_conectak.conectak.EvaluadorCK;
import com.example.conectaplus.game_conectak.jugadores.Evaluador;
import com.example.conectaplus.game_conectak.jugadores.Jugador;

public class PlayAIActivity extends PlayBaseActivity {

    private Jugador<ConectaK> jugadorIA;

    private static int PROFUNDIDAD = 1;
    private boolean isHumanTurn = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_play_ai);

        ROWS = Integer.parseInt(getIntent().getStringExtra("ROWS"));
        COLS = Integer.parseInt(getIntent().getStringExtra("COLS"));
        WIN = Integer.parseInt(getIntent().getStringExtra("WIN"));

        int dificulty = Integer.parseInt(getIntent().getStringExtra("DIFICULTY"));

        if (dificulty == 1) jugadorIA = new JugadorAleatorio<>();
        else {
            if (dificulty == 2) PROFUNDIDAD = WIN;
            else PROFUNDIDAD = WIN * 2;

            Evaluador<ConectaK> evaluador = new EvaluadorCK();
            jugadorIA = new JugadorAlfaBeta<>(evaluador, PROFUNDIDAD);
        }

        Log.d("Profundidad", String.valueOf(PROFUNDIDAD));

        super.onCreate(savedInstanceState);

        startMatch();
    }

    private void handleAITurn(Jugador<ConectaK> jugadorIA) {
        conectaK = jugadorIA.mueve(conectaK);
        if (conectaK.getUltimoMov() != null) {
            paintCell(conectaK.getUltimoMov().f(), conectaK.getUltimoMov().c(), false);
            runOnUiThread(this::updateTurnDisplay);
        }

        if (!isGameOver())
            isHumanTurn = true;
    }

    @Override
    protected void dropFicha(int colNum, boolean isMainTurn) {
        isHumanTurn = false;
        super.dropFicha(colNum, isMainTurn);
    }

    @Override
    protected int getGameResult() {
        if (conectaK.ganaActual()) {
            return conectaK.turno1() ? 1 : -1;
        } else if (conectaK.ganaOtro()) {
            return conectaK.turno1() ? -1 : 1;
        } else {
            return 0;
        }
    }

    @Override
    protected void jugarPartida(Jugador<ConectaK> jugadorIA) {
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
    }

    @Override
    protected void showGameOverDialog(int result) {
        super.showGameOverDialog(result, 1);
    }

    @Override
    protected Jugador<ConectaK> getOtherPlayer() {
        return jugadorIA;
    }

    @Override
    protected boolean isLocalTurn() {
        return isHumanTurn;
    }

    @Override
    protected boolean isMainPlayer() {
        return true;
    }
}
