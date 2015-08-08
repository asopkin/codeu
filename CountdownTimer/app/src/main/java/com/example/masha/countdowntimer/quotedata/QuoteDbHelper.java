package com.example.masha.countdowntimer.quotedata;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by masha on 8/5/2015.
 */
public class QuoteDbHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "quotetable.db";
    private static final int DATABASE_VERSION = 1;

    public QuoteDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // Method is called during creation of the database
    @Override
    public void onCreate(SQLiteDatabase database) {
        QuoteTable.onCreate(database);
    }

    // Method is called during an upgrade of the database,
    // e.g. if you increase the database version
    @Override
    public void onUpgrade(SQLiteDatabase database, int oldVersion,
                          int newVersion) {
        QuoteTable.onUpgrade(database, oldVersion, newVersion);
    }
}
