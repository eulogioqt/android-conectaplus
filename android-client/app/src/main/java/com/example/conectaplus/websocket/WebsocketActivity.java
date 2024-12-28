package com.example.conectaplus.websocket;

import android.content.Intent;
import android.util.Log;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.conectaplus.InitialActivity;
import com.example.conectaplus.MultiplayerActivity;
import com.example.conectaplus.R;

public abstract class WebsocketActivity extends AppCompatActivity {
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
                Log.d("xD", "xDDD");
                runOnUiThread(() -> showDisconnectedDialog());
            }
        });
    }
}
