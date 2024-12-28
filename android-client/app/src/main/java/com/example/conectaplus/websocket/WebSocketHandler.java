package com.example.conectaplus.websocket;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.conectaplus.InitialActivity;
import com.example.conectaplus.R;

public class WebSocketHandler {

    public static void showDisconnectedDialog(Context context) {
        new AlertDialog.Builder(context)
                .setTitle(context.getString(R.string.ad_server_disconnected))
                .setMessage(context.getString(R.string.ad_server_disconnected_desc))
                .setPositiveButton(context.getString(R.string.btn_accept), (dialog, which) -> {
                    Intent intent = new Intent(context, InitialActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(intent);
                })
                .setCancelable(false)
                .show();
    }

    public static void claimDisconnectionAlert(Context context) {
        WebSocketSingleton.getInstance().setConnectionCallback(new ConnectionCallback() {
            @Override
            public void onSuccess() { }

            @Override
            public void onFailure(Throwable t) { }

            @Override
            public void onDisconnect() {
                Log.d("WebSocket", "Disconnected");
                if (context instanceof AppCompatActivity) {
                    ((AppCompatActivity) context).runOnUiThread(() -> showDisconnectedDialog(context));
                }
            }
        });
    }
}
