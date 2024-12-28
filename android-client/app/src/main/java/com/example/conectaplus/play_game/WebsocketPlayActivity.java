package com.example.conectaplus.play_game;

import android.content.Intent;

import androidx.appcompat.app.AlertDialog;

import com.example.conectaplus.InitialActivity;
import com.example.conectaplus.R;
import com.example.conectaplus.websocket.ConnectionCallback;
import com.example.conectaplus.websocket.WebSocketSingleton;

public abstract class WebsocketPlayActivity extends PlayBaseActivity {
    protected void showDisconnectedDialog() {
        new AlertDialog.Builder(this)
                .setTitle(getString(R.string.ad_server_disconnected))
                .setMessage(getString(R.string.ad_server_disconnected_desc))
                .setPositiveButton(getString(R.string.btn_accept), (dialog, which) -> {
                    Intent intent = new Intent(this, InitialActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    finish();
                })
                .setCancelable(false)
                .show();
    }

    protected void claimDisconnectionAlert() {
        WebSocketSingleton.getInstance().setConnectionCallback(new ConnectionCallback() {
            @Override
            public void onSuccess() { }

            @Override
            public void onFailure(Throwable t) {  }

            @Override
            public void onDisconnect() {
                runOnUiThread(() -> showDisconnectedDialog());
            }
        });
    }
}
