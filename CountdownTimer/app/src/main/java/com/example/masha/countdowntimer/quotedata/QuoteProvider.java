package com.example.masha.countdowntimer.quotedata;

import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;

import java.util.Arrays;
import java.util.HashSet;

/**
 * Created by masha on 8/5/2015.
 */
public class QuoteProvider extends ContentProvider {

    // database
    private QuoteDbHelper database;

    // used for the UriMacher
    private static final int QUOTES = 10;
    private static final int QUOTE_ID = 20;

    private static final String AUTHORITY = "com.example.android.countdowntimer.app";

    private static final String BASE_PATH = "quotes";
    public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY
            + "/" + BASE_PATH);

    public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE
            + "/quotes";
    public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE
            + "/quote";

    private static final UriMatcher sURIMatcher = new UriMatcher(UriMatcher.NO_MATCH);
    static {
        sURIMatcher.addURI(AUTHORITY, BASE_PATH, QUOTES);
        sURIMatcher.addURI(AUTHORITY, BASE_PATH + "/#", QUOTE_ID);
    }

    @Override
    public boolean onCreate() {
        database = new QuoteDbHelper(getContext());
        return false;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {

        // Uisng SQLiteQueryBuilder instead of query() method
        SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();

        // check if the caller has requested a column which does not exists
        checkColumns(projection);

        // Set the table
        queryBuilder.setTables(QuoteTable.TABLE_QUOTE);

        int uriType = sURIMatcher.match(uri);
        switch (uriType) {
            case QUOTES:
                Log.d("QOUTES", "QUOTES");
                break;
            case QUOTE_ID:
                Log.d("QUOTE_ID", "QUOTE_ID");
                // adding the ID to the original query
                queryBuilder.appendWhere(QuoteTable.COLUMN_ID + "="
                        + uri.getLastPathSegment());
                break;
            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }

        SQLiteDatabase db = database.getWritableDatabase();
        Cursor cursor = queryBuilder.query(db, projection, selection,
                selectionArgs, null, null, sortOrder);
        // make sure that potential listeners are getting notified
        Log.d("QUERYING", "JUST FINISHED");
        //Log.d("PROJECTION", "ELEMENT AT 0 " + projection[0]);
        cursor.setNotificationUri(getContext().getContentResolver(), uri);

        return cursor;
    }

    @Override
    public String getType(Uri uri) {
        return null;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        int uriType = sURIMatcher.match(uri);
        SQLiteDatabase sqlDB = database.getWritableDatabase();
        int rowsDeleted = 0;
        long id = 0;
        switch (uriType) {
            case QUOTES:
                id = sqlDB.insert(QuoteTable.TABLE_QUOTE, null, values);
                break;
            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return Uri.parse(BASE_PATH + "/" + id);
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        int uriType = sURIMatcher.match(uri);
        SQLiteDatabase sqlDB = database.getWritableDatabase();
        int rowsDeleted = 0;
        switch (uriType) {
            case QUOTES:
                rowsDeleted = sqlDB.delete(QuoteTable.TABLE_QUOTE, selection,
                        selectionArgs);
                break;
            case QUOTE_ID:
                String id = uri.getLastPathSegment();
                if (TextUtils.isEmpty(selection)) {
                    rowsDeleted = sqlDB.delete(QuoteTable.TABLE_QUOTE,
                            QuoteTable.COLUMN_ID + "=" + id,
                            null);
                } else {
                    rowsDeleted = sqlDB.delete(QuoteTable.TABLE_QUOTE,
                            QuoteTable.COLUMN_ID + "=" + id
                                    + " and " + selection,
                            selectionArgs);
                }
                break;
            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return rowsDeleted;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection,
                      String[] selectionArgs) {

        int uriType = sURIMatcher.match(uri);
        SQLiteDatabase sqlDB = database.getWritableDatabase();
        int rowsUpdated = 0;
        switch (uriType) {
            case QUOTES:
                rowsUpdated = sqlDB.update(QuoteTable.TABLE_QUOTE,
                        values,
                        selection,
                        selectionArgs);
                break;
            case QUOTE_ID:
                String id = uri.getLastPathSegment();
                if (TextUtils.isEmpty(selection)) {
                    rowsUpdated = sqlDB.update(QuoteTable.TABLE_QUOTE,
                            values,
                            QuoteTable.COLUMN_ID + "=" + id,
                            null);
                } else {
                    rowsUpdated = sqlDB.update(QuoteTable.TABLE_QUOTE,
                            values,
                            QuoteTable.COLUMN_ID + "=" + id
                                    + " and "
                                    + selection,
                            selectionArgs);
                }
                break;
            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return rowsUpdated;
    }

    private void checkColumns(String[] projection) {
        String[] available = { QuoteTable.COLUMN_QUOTE,
                QuoteTable.COLUMN_AUTHOR,
                QuoteTable.COLUMN_ID,
                QuoteTable.COLUMN_DATE};
        if (projection != null) {
            HashSet<String> requestedColumns = new HashSet<String>(Arrays.asList(projection));
            HashSet<String> availableColumns = new HashSet<String>(Arrays.asList(available));
            // check if all columns which are requested are available
            if (!availableColumns.containsAll(requestedColumns)) {
                throw new IllegalArgumentException("Unknown columns in projection");
            }
        }
    }

}