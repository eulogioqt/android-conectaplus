package com.example.conectaplus.play_game;

import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;

import com.example.conectaplus.R;
import com.example.conectaplus.game_conectak.conectak.ConectaK;
import com.example.conectaplus.game_conectak.jugadores.Jugador;
import com.example.conectaplus.websocket.WebSocketHandler;
import com.example.conectaplus.websocket.WebSocketSingleton;


public class PlayMultiplayerActivity extends PlayBaseActivity {

    private ChatManager chatManager;
    private TextView textRoom;
    private int turnoLocal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_play_multiplayer);
        WebSocketHandler.claimDisconnectionAlert(this);
        super.onCreate(savedInstanceState);

        chatManager = new ChatManager(this);

        Button chatButton = findViewById(R.id.chat_button);
        chatButton.setOnClickListener(v -> chatManager.openChatDialog());

        textRoom = findViewById(R.id.match_text);

        String matchCode = getIntent().getStringExtra("MATCH_CODE");
        turnoLocal = Integer.parseInt(getIntent().getStringExtra("TURNO_LOCAL"));
        textRoom.setText(String.format("%s %s", getString(R.string.match_string), matchCode != null ? matchCode : "?"));

        WebSocketSingleton.getInstance().setOnMessageListener(message -> {
            if (message.startsWith("CHAT"))
                handleChatMessage(message);
            else if (message.startsWith("MOVE"))
                handleMoveMessage(message);
            else if (message.startsWith("DISCONNECT"))
                handleDisconnectMessage();
        });

        startMatch();
    }

    private void handleChatMessage(String message) {
        String chatMessage = message.substring(5);
        runOnUiThread(() -> chatManager.addChatMessage(getString(R.string.rival_string) + ": " + chatMessage));
    }

    private void handleMoveMessage(String message) {
        if (!isLocalTurn()) {
            String[] parts = message.split(" ");
            if (parts.length > 1) {
                int col = Integer.parseInt(parts[1]) - 1;
                dropFicha(col, turnoLocal != 1);
            } else {
                runOnUiThread(() -> Toast.makeText(this, getString(R.string.error_moving_other_player), Toast.LENGTH_SHORT).show());
            }
        } else {
            runOnUiThread(() -> Toast.makeText(this, getString(R.string.error_moving_other_player_not_his_turn), Toast.LENGTH_SHORT).show());
        }
    }

    private void handleDisconnectMessage() {
        runOnUiThread(() -> {
            new AlertDialog.Builder(this)
                    .setTitle(getString(R.string.ad_player_disconnected))
                    .setMessage(getString(R.string.ad_player_disconnected_desc))
                    .setCancelable(false)
                    .setPositiveButton(getString(R.string.btn_accept), (dialog, which) -> {
                        finish();
                    })
                    .show();
        });
    }

    @Override
    protected void dropFicha(int colNum, boolean isMainTurn) {
        super.dropFicha(colNum, isMainTurn);

        if (isLocalTurn())
            WebSocketSingleton.getInstance().sendMessage("MOVE " + (colNum + 1));
    }

    @Override
    protected void jugarPartida(Jugador<ConectaK> ignore) {
        while (!isGameOver()) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException ignored) {}
        }
    }

    @Override
    protected int getGameResult() {
        int result = 0;
        if (conectaK.ganaActual()) {
            result = conectaK.turno1() ? 1 : -1;
        } else if (conectaK.ganaOtro()) {
            result = conectaK.turno1() ? -1 : 1;
        }

        if ((result == 1 && turnoLocal == 1) || (result == -1 && turnoLocal != 1)) {
            return 1;
        } else if (result != 0) {
            return -1;
        }
        return 0;
    }

    @Override
    protected void showGameOverDialog(int result) {
        if (turnoLocal == 1)
            WebSocketSingleton.getInstance().sendMessage("END");

        super.showGameOverDialog(result, 2);
    }

    @Override
    protected Jugador<ConectaK> getOtherPlayer() {
        return null;
    }

    @Override
    protected boolean isLocalTurn() {
        return (turnoLocal == 1) == conectaK.turno1();
    }

    @Override
    protected boolean isMainPlayer() {
        return turnoLocal == 1;
    }
}
