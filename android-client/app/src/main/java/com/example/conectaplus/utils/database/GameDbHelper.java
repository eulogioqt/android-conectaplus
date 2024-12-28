package com.example.conectaplus.utils.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class GameDbHelper extends SQLiteOpenHelper {

    public static final int DATABASE_VERSION = 2;
    public static final String DATABASE_NAME = "game_results.db";

    public static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE " + GameContract.GameEntry.TABLE_NAME + " (" +
                    GameContract.GameEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                    GameContract.GameEntry.COLUMN_NAME_RESULT + " INTEGER NOT NULL," +
                    GameContract.GameEntry.COLUMN_NAME_TYPE + " INTEGER NOT NULL," +
                    GameContract.GameEntry.COLUMN_NAME_TIMESTAMP + " DATETIME DEFAULT CURRENT_TIMESTAMP" +
                    " )";

    public static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + GameContract.GameEntry.TABLE_NAME;

    public GameDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_ENTRIES);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(SQL_DELETE_ENTRIES);
        onCreate(db);
    }

    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(SQL_DELETE_ENTRIES);
        onCreate(db);
    }
}
