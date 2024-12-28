package com.example.conectaplus.play_game;

import android.os.Bundle;
import android.util.Log;

import com.example.conectaplus.R;
import com.example.conectaplus.game_conectak.conectak.ConectaK;
import com.example.conectaplus.game_conectak.jugadores.JugadorAlfaBeta;
import com.example.conectaplus.database.GameDatabaseHelper;
import com.example.conectaplus.game_conectak.conectak.EvaluadorCK;
import com.example.conectaplus.game_conectak.jugadores.Evaluador;
import com.example.conectaplus.game_conectak.jugadores.Jugador;

public class PlayAIActivity extends PlayBaseActivity {

    private Jugador<ConectaK> jugadorIA;

    private static int PROFUNDIDAD = 1;
    private boolean isHumanTurn = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_play_ai);
        super.onCreate(savedInstanceState);

        Evaluador<ConectaK> evaluador = new EvaluadorCK();
        jugadorIA = new JugadorAlfaBeta<>(evaluador, PROFUNDIDAD);

        startMatch();
    }

    private void handleAITurn(Jugador<ConectaK> jugadorIA) {
        conectaK = jugadorIA.mueve(conectaK);
        if (conectaK.getUltimoMov() != null) {
            paintCell(conectaK.getUltimoMov().f(), conectaK.getUltimoMov().c(), false);
        }

        if (!isGameOver())
            isHumanTurn = true;
    }

    @Override
    protected void dropFicha(int colNum, boolean isMainTurn) {
        super.dropFicha(colNum, isMainTurn);

        isHumanTurn = false;
    }

    @Override
    protected int jugarPartida(Jugador<ConectaK> jugadorIA) {
        while (!isGameOver()) {
            Log.d("Se supone q no game over", "Se supone q no game over");
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
        Log.d("Ahora si", "ahora si");
        return getGameResult();
    }
    @Override
    protected void showGameOverDialog(int result) {
        GameDatabaseHelper dbHelper = new GameDatabaseHelper(this);
        String message;

        if (result == 1) {
            message = getString(R.string.ad_game_finished_win_desc);
            dbHelper.addResult("win");
        } else if (result == -1) {
            message = getString(R.string.ad_game_finished_lose_desc);
            dbHelper.addResult("loss");
        } else {
            message = getString(R.string.ad_game_finished_draw_desc);
            dbHelper.addResult("draw");
        }

        dbHelper.close();

        super.showGameOverDialog(message);
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
