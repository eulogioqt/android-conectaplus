package com.example.conectaplus.utils.database;

import android.content.Context;

import com.example.conectaplus.R;

import java.util.HashMap;
import java.util.Map;

public class DatabaseValues {

    private static Map<String, Map<Integer, String>> columnValues = new HashMap<>();

    public static String getString(String column, int value) {
        if (columnValues.containsKey(column)) {
            Map<Integer, String> values = columnValues.get(column);
            if (values.containsKey(value)) {
                return values.get(value);
            }
        }
        return "Unknown";
    }

    public static void initialize(Context context) {
        Map<Integer, String> resultValues = new HashMap<>();
        resultValues.put(-1, context.getString(R.string.loss));
        resultValues.put(0, context.getString(R.string.draw));
        resultValues.put(1, context.getString(R.string.win));
        columnValues.put(GameContract.GameEntry.COLUMN_NAME_RESULT, resultValues);

        Map<Integer, String> typeValues = new HashMap<>();
        typeValues.put(1, context.getString(R.string.singleplayer));
        typeValues.put(2, context.getString(R.string.multiplayer));
        columnValues.put(GameContract.GameEntry.COLUMN_NAME_TYPE, typeValues);
    }
}
