package com.example.conectaplus.play_game;

import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.conectaplus.R;
import com.example.conectaplus.game_conectak.conectak.ConectaK;
import com.example.conectaplus.game_conectak.jugadores.Jugador;
import com.example.conectaplus.websocket.WebSocketSingleton;


public class PlayMultiplayerActivity extends PlayBaseActivity {

    private LinearLayout turnLayout;
    private ChatManager chatManager;

    private TextView turnoText;
    private TextView textRoom;
    private int turnoLocal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_play_multiplayer);
        super.onCreate(savedInstanceState);

        chatManager = new ChatManager(this);

        Button chatButton = findViewById(R.id.chat_button);
        chatButton.setOnClickListener(v -> chatManager.openChatDialog());

        turnLayout = findViewById(R.id.turn_layout);
        textRoom = findViewById(R.id.match_text);

        String matchCode = getIntent().getStringExtra("MATCH_CODE");
        turnoLocal = Integer.parseInt(getIntent().getStringExtra("TURNO_LOCAL"));
        textRoom.setText(String.format("%s %s", getString(R.string.match_string), matchCode != null ? matchCode : "?"));

        initializeTurnLayout();

        WebSocketSingleton.getInstance().setOnMessageListener(message -> {
            if (message.startsWith("CHAT"))
                handleChatMessage(message);
            else if (message.startsWith("MOVE"))
                handleMoveMessage(message);
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

    private void initializeTurnLayout() {
        View ficha = new View(this);
        GradientDrawable circle = new GradientDrawable();
        circle.setShape(GradientDrawable.OVAL);
        circle.setColor((turnoLocal == 1 && isLocalTurn()) || (turnoLocal != 1 && !isLocalTurn()) ? Color.RED : Color.YELLOW);
        circle.setStroke(4, Color.BLACK);
        ficha.setBackground(circle);

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(CELL_SIZE, LinearLayout.LayoutParams.WRAP_CONTENT);
        params.width = (int) (CELL_SIZE * 0.75);
        params.height = (int) (CELL_SIZE * 0.75);
        ficha.setLayoutParams(params);

        turnLayout.addView(ficha);

        turnoText = new TextView(this);
        turnoText.setText(String.format("%s", getString(isLocalTurn() ? R.string.turn_you_string : R.string.turn_rival_string)));
        turnoText.setTextSize(24);
        turnoText.setPadding(24, 0, 0, 0);
        turnLayout.setPadding(0, 0, 0, 32);

        turnLayout.addView(turnoText);
    }
    private void updateTurnDisplay() {
        turnoText.setText(String.format("%s", getString(isLocalTurn() ? R.string.turn_you_string : R.string.turn_rival_string)));

        GradientDrawable circle = (GradientDrawable) turnLayout.getChildAt(0).getBackground();
        circle.setColor((turnoLocal == 1 && isLocalTurn()) || (turnoLocal != 1 && !isLocalTurn()) ? Color.RED : Color.YELLOW);
    }

    @Override
    protected void dropFicha(int colNum, boolean isMainTurn) {
        super.dropFicha(colNum, isMainTurn);

        if (isLocalTurn())
            WebSocketSingleton.getInstance().sendMessage("MOVE " + (colNum + 1));

        runOnUiThread(this::updateTurnDisplay);
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
