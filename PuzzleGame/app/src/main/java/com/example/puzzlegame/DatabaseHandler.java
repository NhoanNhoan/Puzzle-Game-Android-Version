package com.example.puzzlegame;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.content.Context;

public class DatabaseHandler extends SQLiteOpenHelper {
    static final String DB_NAME="PUZZLE MANAGEMENT";
    static final int DB_VERSION = 1;
    static final String TABLE_NAME = "ROUND";

    static final String KEY_NUM_ROUND = "num_round";
    static final String KEY_NUM_PIECES_IN_ROW = "num_pieces_in_row";
    static final String KEY_NUM_PIECES_IN_COLUMN = "num_pieces_in_column";
    static final String KEY_ADDITION_POINT = "addition_point";
    static final String KEY_MINUS_POINT = "minus_point";
    static final String KEY_IMAGE = "image";

    DatabaseHandler(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createRoundTableStatement = "CREATE TABLE " + TABLE_NAME + " ( " +
                KEY_NUM_ROUND + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                KEY_NUM_PIECES_IN_ROW + " INTEGER, " +
                KEY_NUM_PIECES_IN_COLUMN + " INTEGER, " +
                KEY_ADDITION_POINT + " INTEGER, " +
                KEY_MINUS_POINT + " INTEGER, " +
                KEY_IMAGE + " BLOB " + " ) ";
        db.execSQL(createRoundTableStatement);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        String dropRoundTableStatement = String.format("DROP TABLE IF EXISTS %s", TABLE_NAME);
        db.execSQL(dropRoundTableStatement);
        onCreate(db);
    }

    Round getRound(int numRound) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.query(TABLE_NAME, null, KEY_NUM_ROUND + " = ?", new String[] {String.valueOf(numRound)}, null, null, null);
        if (null != cursor) {
            cursor.moveToFirst();
        }

        Round round = new Round(cursor.getInt(0),
                cursor.getInt(1),
                cursor.getInt(2),
                cursor.getInt(3),
                cursor.getInt(4),
                cursor.getInt(5));

        return round;
    }

    Round getFirstRound() {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.query(TABLE_NAME, null, null, null, null, null, null, "1");
        if (null != cursor) {
            cursor.moveToFirst();
        }

        Round round = new Round(cursor.getInt(0),
                cursor.getInt(1),
                cursor.getInt(2),
                cursor.getInt(3),
                cursor.getInt(4),
                cursor.getInt(5));

        return round;
    }

    int getTotalRounds() {
        SQLiteDatabase db = getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT COUNT(*) FROM " + TABLE_NAME, null);
        if (null != cursor) {
            cursor.moveToFirst();
        }

        return cursor.getInt(0);
    }

    void insertRound(Round round) {
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL("INSERT INTO ROUND (num_pieces_in_row, num_pieces_in_column, addition_point, minus_point, image) VALUES(?, ?, ?, ?, ?)",
                new String[] {String.valueOf(round.numPiecesInRow), String.valueOf(round.numPiecesInColumn),
                        String.valueOf(round.additionPoint), String.valueOf(round.minusPoint), String.valueOf(round.image)});
    }
}
