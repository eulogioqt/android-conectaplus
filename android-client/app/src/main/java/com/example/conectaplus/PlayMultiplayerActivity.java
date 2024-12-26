package com.example.conectaplus;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.GridLayout;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.conectaplus.conectak.ConectaK;
import com.example.conectaplus.conectak.EvaluadorCK;
import com.example.conectaplus.conectak.Movimiento;
import com.example.conectaplus.jugadores.Evaluador;
import com.example.conectaplus.jugadores.Jugador;
import com.example.conectaplus.jugadores.JugadorAlfaBeta;

import java.util.ArrayList;
import java.util.List;

public class PlayMultiplayerActivity extends AppCompatActivity {

    private List<String> chatMessages = new ArrayList<>();
    private ArrayAdapter<String> chatAdapter;

    private static final int ROWS = 5;
    private static final int COLS = 6;
    private static final int WIN = 4;
    private static int CELL_SIZE = 0;

    private ConectaK conectaK;
    private GridLayout boardLayout;
    private LinearLayout buttonLayout;
    private LinearLayout turnLayout;

    private TextView turnoText;
    private TextView textRoom;
    private int turnoLocal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play_multiplayer);

        Button chatButton = findViewById(R.id.chatButton);
        chatButton.setOnClickListener(v -> openChatDialog());

        int screenWidth = getResources().getDisplayMetrics().widthPixels;
        int screenHeight = getResources().getDisplayMetrics().heightPixels;
        CELL_SIZE = Math.min((int)(0.95 * screenWidth / COLS), (int)(0.95 * (screenHeight * 0.7) / ROWS));

        conectaK = new ConectaK(ROWS, COLS, WIN);

        boardLayout = findViewById(R.id.boardLayout);
        buttonLayout = findViewById(R.id.buttonLayout);
        turnLayout = findViewById(R.id.turnLayout);
        textRoom = findViewById(R.id.textRoom);

        String matchCode = getIntent().getStringExtra("MATCH_CODE");
        turnoLocal = Integer.parseInt(getIntent().getStringExtra("TURNO_LOCAL"));
        textRoom.setText(String.format("Sala %s", matchCode != null ? matchCode : "Error"));

        initializeTurnLayout();
        initializeBoard();
        initializeColumnButtons();

        new Thread(() -> {
            int resultado = jugarPartida();
            runOnUiThread(() -> showGameOverDialog(resultado));
        }).start();

        WebSocketSingleton.getInstance().setOnMessageListener(message -> {
            if (message.startsWith("MOVE")) {
                if (!isLocalTurn()) {
                    String[] parts = message.split(" ");
                    if (parts.length > 1) {
                        int col = Integer.parseInt(parts[1]) - 1;
                        dropFicha(col, turnoLocal != 1);
                    } else {
                        runOnUiThread(() -> Toast.makeText(PlayMultiplayerActivity.this, "Error al mover el otro jugador.", Toast.LENGTH_SHORT).show());
                    }
                }else {
                    runOnUiThread(() -> Toast.makeText(PlayMultiplayerActivity.this, "El otro jugador intento mover cuando no era su turno.", Toast.LENGTH_SHORT).show());
                }
            } else if (message.startsWith("CHAT")) {
                String chatMessage = message.substring(5);
                addChatMessage("Rival: " + chatMessage);
            }
        });
    }

    private void scrollToBottom(ListView listView) {
        listView.post(() -> listView.setSelection(chatAdapter.getCount() - 1));
    }

    private void openChatDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View chatView = getLayoutInflater().inflate(R.layout.dialog_chat, null);

        ListView chatListView = chatView.findViewById(R.id.chatListView);
        EditText chatInput = chatView.findViewById(R.id.chatInput);
        Button sendButton = chatView.findViewById(R.id.sendButton);

        chatAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, chatMessages);
        chatListView.setAdapter(chatAdapter);

        scrollToBottom(chatListView);

        sendButton.setOnClickListener(v -> {
            String message = chatInput.getText().toString().trim();
            if (!message.isEmpty()) {
                sendChatMessage(message);
                chatInput.setText("");
            }
        });

        builder.setView(chatView)
                .setCancelable(true)
                .create()
                .show();
    }

    private void sendChatMessage(String message) {
        WebSocketSingleton.getInstance().sendMessage("CHAT " + message);
        addChatMessage("Tú: " + message);
    }

    private void addChatMessage(String message) {
        runOnUiThread(() -> {
            chatMessages.add(message);
            if (chatAdapter != null) {
                chatAdapter.notifyDataSetChanged();

                ListView chatListView = findViewById(R.id.chatListView);
                if (chatListView != null) {
                    scrollToBottom(chatListView);
                }
            }
        });
    }

    private boolean isLocalTurn() {
        return (turnoLocal == 1) == conectaK.turno1();
    }
    private void updateTurnDisplay() {
        turnoText.setText(String.format("%s", isLocalTurn() ? "Tú turno" : "Turno rival"));

        GradientDrawable circle = (GradientDrawable) turnLayout.getChildAt(0).getBackground();
        circle.setColor((turnoLocal == 1 && isLocalTurn()) || (turnoLocal != 1 && !isLocalTurn()) ? Color.RED : Color.YELLOW);
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
        turnoText.setText(String.format("%s", isLocalTurn() ? "Tú turno" : "Turno rival"));
        turnoText.setTextSize(24);
        turnoText.setPadding(24, 0, 0, 0);
        turnLayout.setPadding(0, 0, 0, 32);

        turnLayout.addView(turnoText);
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
                if (isLocalTurn()) {
                    dropFicha(column, turnoLocal == 1);
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

    private void paintCell(int row, int col, boolean isTurno1) {
        runOnUiThread(() -> {
            int index = row * COLS + col;
            FrameLayout cellContainer = (FrameLayout) boardLayout.getChildAt(index);

            View ficha = new View(this);
            GradientDrawable circle = new GradientDrawable();
            circle.setShape(GradientDrawable.OVAL);
            circle.setColor(isTurno1 ? Color.RED : Color.YELLOW);
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

    private void dropFicha(int colNum, boolean isTurno1) {
        int row = conectaK.siguienteFila(colNum);
        if (row == -1) {
            showMessage("Columna llena");
            return;
        }

        if (isLocalTurn())
            WebSocketSingleton.getInstance().sendMessage("MOVE " + (colNum + 1));

        conectaK = conectaK.mueveHumano(colNum);
        runOnUiThread(this::updateTurnDisplay);
        paintCell(row, colNum, isTurno1);
    }

    private boolean isGameOver() {
        return conectaK.ganaActual() || conectaK.ganaOtro() || conectaK.agotado();
    }

    private int jugarPartida() {
        while (!isGameOver()) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException ignored) {
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
            String message;
            if ((result == 1 && turnoLocal == 1) || (result == -1 && turnoLocal != 1)) {
                message = "Enhorabuena, has ganado!";
            } else if (result == 1 || result == -1) {
                message = "Lo siento, has perdido.";
            } else {
                message = "Es un empate.";
            }

            if (turnoLocal == 1)
                WebSocketSingleton.getInstance().sendMessage("END");

            new AlertDialog.Builder(this)
                    .setTitle("Juego Terminado")
                    .setMessage(message)
                    .setCancelable(false)
                    .setPositiveButton("Aceptar", (dialog, which) -> {
                        Intent intent = new Intent(this, InitialActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                        finish();
                    })
                    .show();
        });
    }

    private Toast currentToast;
    private void showMessage(String message) {
        runOnUiThread(() -> {
            if (currentToast != null) {
                currentToast.cancel();
            }
            currentToast = Toast.makeText(this, message, Toast.LENGTH_SHORT);
            currentToast.show();
        });
    }
}
