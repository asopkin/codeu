package com.example.masha.countdowntimer.quotedata;

import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

/**
 * Created by masha on 8/5/2015.
 */
public class QuoteTable {

    // Database table
    public static final String TABLE_QUOTE = "quote";
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_QUOTE = "quote";
    public static final String COLUMN_AUTHOR = "author";
    public static final String COLUMN_DATE = "date";

    // Database creation SQL statement
    private static final String DATABASE_CREATE = "create table "
            + TABLE_QUOTE
            + "("
            + COLUMN_ID + " integer primary key autoincrement, "
            + COLUMN_QUOTE + " text not null, "
            + COLUMN_AUTHOR + " text not null, "
            + COLUMN_DATE + " integer"
            + ");";

    public static void onCreate(SQLiteDatabase database) {
        database.execSQL(DATABASE_CREATE);
    }

    public static void onUpgrade(SQLiteDatabase database, int oldVersion,
                                 int newVersion) {
        Log.w(QuoteTable.class.getName(), "Upgrading database from version "
                + oldVersion + " to " + newVersion
                + ", which will destroy all old data");
        database.execSQL("DROP TABLE IF EXISTS " + TABLE_QUOTE);
        onCreate(database);
    }
}