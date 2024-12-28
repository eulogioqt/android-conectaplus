package com.example.conectaplus.utils.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class GameDatabase {

    private SQLiteDatabase db;

    public GameDatabase(Context context) {
        GameDbHelper dbHelper = new GameDbHelper(context);
        db = dbHelper.getWritableDatabase();
    }

    public void addResult(int result, int type) {
        ContentValues values = new ContentValues();
        values.put(GameContract.GameEntry.COLUMN_NAME_RESULT, result);
        values.put(GameContract.GameEntry.COLUMN_NAME_TYPE, type);
        db.insert(GameContract.GameEntry.TABLE_NAME, null, values);
    }

    public Cursor getAllResults() {
        String[] columns = {
                GameContract.GameEntry._ID,
                GameContract.GameEntry.COLUMN_NAME_RESULT,
                GameContract.GameEntry.COLUMN_NAME_TYPE,
                GameContract.GameEntry.COLUMN_NAME_TIMESTAMP
        };

        return db.query(
                GameContract.GameEntry.TABLE_NAME,
                columns,
                null,  // No WHERE clause
                null,  // No WHERE arguments
                null,  // No GROUP BY
                null,  // No HAVING
                GameContract.GameEntry.COLUMN_NAME_TIMESTAMP + " DESC"
        );
    }

    public void deleteAllResults() {
        db.delete(GameContract.GameEntry.TABLE_NAME, null, null);
    }

    @Override
    protected void finalize() throws Throwable {
        db.close();
        super.finalize();
    }
}
