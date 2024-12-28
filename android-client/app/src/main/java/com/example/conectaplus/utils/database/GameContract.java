package com.example.conectaplus.utils.database;

import android.provider.BaseColumns;

public final class GameContract {

    private GameContract() {}

    public static abstract class GameEntry implements BaseColumns {
        public static final String TABLE_NAME = "game_history";
        public static final String COLUMN_NAME_RESULT = "result";
        public static final String COLUMN_NAME_TYPE = "type";
        public static final String COLUMN_NAME_TIMESTAMP = "timestamp";
    }
}
