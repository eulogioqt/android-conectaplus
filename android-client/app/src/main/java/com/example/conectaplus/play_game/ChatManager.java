package com.example.conectaplus.play_game;

import android.content.Context;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import androidx.appcompat.app.AlertDialog;

import com.example.conectaplus.R;
import com.example.conectaplus.websocket.WebSocketSingleton;

import java.util.ArrayList;
import java.util.List;

public class ChatManager {
    private final Context context;
    private final List<String> chatMessages;
    private ArrayAdapter<String> chatAdapter;

    public ChatManager(Context context) {
        this.context = context;
        this.chatMessages = new ArrayList<>();
    }

    public void openChatDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        View chatView = View.inflate(context, R.layout.dialog_chat, null);

        ListView chatListView = chatView.findViewById(R.id.chat_list_view);
        EditText chatInput = chatView.findViewById(R.id.chat_input);
        Button sendButton = chatView.findViewById(R.id.sendButton);

        chatAdapter = new ArrayAdapter<>(context, android.R.layout.simple_list_item_1, chatMessages);
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

    private void scrollToBottom(ListView listView) {
        listView.post(() -> listView.setSelection(chatAdapter.getCount() - 1));
    }

    private void sendChatMessage(String message) {
        WebSocketSingleton.getInstance().sendMessage("CHAT " + message);
        addChatMessage(context.getString(R.string.you_string) + ": " + message);
    }

    public void addChatMessage(String message) {
        chatMessages.add(message);
        if (chatAdapter != null) {
            chatAdapter.notifyDataSetChanged();
        }
    }
}
