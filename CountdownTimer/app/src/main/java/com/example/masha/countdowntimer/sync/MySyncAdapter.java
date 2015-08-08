package com.example.masha.countdowntimer.sync;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.SyncRequest;
import android.content.SyncResult;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.format.Time;
import android.util.Log;

import com.example.masha.countdowntimer.R;
import com.example.masha.countdowntimer.quotedata.QuoteProvider;
import com.example.masha.countdowntimer.quotedata.QuoteTable;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class MySyncAdapter extends AbstractThreadedSyncAdapter {
    public final String LOG_TAG = MySyncAdapter.class.getSimpleName();
    // Interval at which to get the new quote, in seconds.
    // 60 seconds (1 minute) * 180 * 8 = 24 hours
    // we want a new quote every day
    public static final int SYNC_INTERVAL = 60 * 180 * 8;
    public static final int SYNC_FLEXTIME = SYNC_INTERVAL/3;
    private static final long DAY_IN_MILLIS = 1000 * 60 * 60 * 24;

    ContentResolver mContentResolver;

    public MySyncAdapter(Context context, boolean autoInitialize) {
        super(context, autoInitialize);
        mContentResolver = context.getContentResolver();
    }

    @Override
    public void onPerformSync(Account account, Bundle extras, String authority, ContentProviderClient provider, SyncResult syncResult) {
        Log.d(LOG_TAG, "Starting sync");

        // These two need to be declared outside the try/catch
        // so that they can be closed in the finally block.
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;

        // Will contain the raw JSON response as a string.
        String quoteJsonStr = null;

        try {
            // Construct the URL for the OpenWeatherMap query
            // Possible parameters are avaiable at OWM's forecast API page, at
            // http://openweathermap.org/API#forecast
            //final String QUOTES_BASE_URL =
             //       "http://api.theysaidso.com/qod?category=inspire";

            //Uri builtUri = Uri.parse(QUOTES_BASE_URL);

            URL url = new URL("http://api.theysaidso.com/qod.json?category=inspire");

            // Create the request to the quotes API, and open the connection
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            // Read the input stream into a String
            InputStream inputStream = urlConnection.getInputStream();
            StringBuffer buffer = new StringBuffer();
            if (inputStream == null) {
                // Nothing to do.
                return;
            }
            reader = new BufferedReader(new InputStreamReader(inputStream));

            String line;
            while ((line = reader.readLine()) != null) {
                // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                // But it does make debugging a *lot* easier if you print out the completed
                // buffer for debugging.
                buffer.append(line + "\n");
            }

            if (buffer.length() == 0) {
                // Stream was empty.  No point in parsing.
                return;
            }
            quoteJsonStr = buffer.toString();
            Log.d(LOG_TAG, "DEBUG" + quoteJsonStr);
            getQuoteDataFromJson(quoteJsonStr);
        } catch (IOException e) {
            Log.e(LOG_TAG, "Error ", e);
            // If the code didn't successfully get the weather data, there's no point in attempting
            // to parse it.
        } catch (JSONException e) {
            Log.e(LOG_TAG, e.getMessage(), e);
            e.printStackTrace();
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (reader != null) {
                try {
                    reader.close();
                } catch (final IOException e) {
                    Log.e(LOG_TAG, "Error closing stream", e);
                }
            }
        }
        return;
    }

    /**
     * Take the String representing the complete quote in JSON Format and
     * pull out the data we need to construct the Strings needed for the wireframes.
     *
     * Fortunately parsing is easy:  constructor takes the JSON string and converts it
     * into an Object hierarchy for us.
     */
    private void getQuoteDataFromJson(String quoteJsonStr)
            throws JSONException {

        // Now we have a String representing the complete quote in JSON Format.
        // Fortunately parsing is easy:  constructor takes the JSON string and converts it
        // into an Object hierarchy for us.

        try {
            JSONObject quoteJson = new JSONObject(quoteJsonStr);
            JSONObject quoteContainer = quoteJson.getJSONObject("contents");

            JSONArray qodArrayJson = quoteContainer.getJSONArray("quotes");

            JSONObject qodJson = qodArrayJson.getJSONObject(0);

            String quotename = qodJson.getString("quote");

            int quoteLength = qodJson.getInt("length");

            String author = qodJson.getString("author");

            Time dayTime = new Time();
            dayTime.setToNow();
            // we start at the day returned by local time. Otherwise this is a mess.
            int today = Time.getJulianDay(System.currentTimeMillis(), dayTime.gmtoff);

            addQuote(quotename, author, today);

            Log.d(LOG_TAG, "Quote name :" + quotename);
            Log.d(LOG_TAG, "Quote length :" + quoteLength);
            Log.d(LOG_TAG, "Quote author :" + author);
            Log.d(LOG_TAG, "Sync Complete. ");

        } catch (JSONException e) {
            Log.e(LOG_TAG, e.getMessage(), e);
            e.printStackTrace();
        }
    }

    /**
     * Helper method to handle insertion of a new location in the quote database
     *
     */

    void addQuote(String quoteName, String author, int date) {

        Time dayTime = new Time();
        dayTime.setToNow();
        int julianStartDay = Time.getJulianDay(System.currentTimeMillis(), dayTime.gmtoff);
        dayTime = new Time();

        ContentValues quoteValues = new ContentValues();
        quoteValues.put(QuoteTable.COLUMN_QUOTE, quoteName);
        quoteValues.put(QuoteTable.COLUMN_AUTHOR, author);
        quoteValues.put(QuoteTable.COLUMN_DATE, date);

        Uri insertedUri = getContext().getContentResolver().insert(QuoteProvider.CONTENT_URI, quoteValues);
        long quoteId = ContentUris.parseId(insertedUri);

        Cursor cursor = getContext().getContentResolver().query(
                QuoteProvider.CONTENT_URI,
                new String[]{QuoteTable.COLUMN_QUOTE},
                QuoteTable.COLUMN_ID + " = ?",
                new String[]{quoteId+""},
                null);

        if (cursor != null) {
            Log.d(LOG_TAG, "CURSOR IS NOT NULL");
            if (cursor.moveToFirst()) {
                Log.d(LOG_TAG, "SUCCESSFULLY STORED IN DATABASE");
                String quote = cursor.getString(0);
                Log.d(LOG_TAG, "IT WORKS! IT WORKS! AHAHAHAA!");
            }
        }

        cursor.close();
        //deletes all quotes that may have been inserted on a previous day
        /*
        getContext().getContentResolver().delete(QuoteProvider.CONTENT_URI,
                QuoteTable.COLUMN_DATE + " <= ?",
                new String[]{Long.toString(dayTime.setJulianDay(julianStartDay - 1))});
*/
        Log.d(LOG_TAG, quoteId + "");
    }


    /**
     * Helper method to schedule the sync adapter periodic execution
     */
    public static void configurePeriodicSync(Context context, int syncInterval, int flexTime) {
        Account account = getSyncAccount(context);
        String authority = context.getString(R.string.content_authority);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            // we can enable inexact timers in our periodic sync
            SyncRequest request = new SyncRequest.Builder().
                    syncPeriodic(syncInterval, flexTime).
                    setSyncAdapter(account, authority).
                    setExtras(new Bundle()).build();
            ContentResolver.requestSync(request);
        } else {
            ContentResolver.addPeriodicSync(account,
                    authority, new Bundle(), syncInterval);
        }
    }

    /**
     * Helper method to have the sync adapter sync immediately
     * @param context The context used to access the account service
     */
    public static void syncImmediately(Context context) {
        Bundle bundle = new Bundle();
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_EXPEDITED, true);
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, true);
        ContentResolver.requestSync(getSyncAccount(context),
                context.getString(R.string.content_authority), bundle);
    }

    /**
     * Helper method to get the fake account to be used with SyncAdapter, or make a new one
     * if the fake account doesn't exist yet.  If we make a new account, we call the
     * onAccountCreated method so we can initialize things.
     *
     * @param context The context used to access the account service
     * @return a fake account.
     */
    public static Account getSyncAccount(Context context) {
        // Get an instance of the Android account manager
        AccountManager accountManager =
                (AccountManager) context.getSystemService(Context.ACCOUNT_SERVICE);

        // Create the account type and default account
        Account newAccount = new Account(
                context.getString(R.string.app_name), context.getString(R.string.sync_account_type));

        // If the password doesn't exist, the account doesn't exist
        if ( null == accountManager.getPassword(newAccount) ) {

        /*
         * Add the account and account type, no password or user data
         * If successful, return the Account object, otherwise report an error.
         */
            if (!accountManager.addAccountExplicitly(newAccount, "", null)) {
                return null;
            }
            /*
             * If you don't set android:syncable="true" in
             * in your <provider> element in the manifest,
             * then call ContentResolver.setIsSyncable(account, AUTHORITY, 1)
             * here.
             */

            onAccountCreated(newAccount, context);
        }
        return newAccount;
    }

    private static void onAccountCreated(Account newAccount, Context context) {
        /*
         * Since we've created an account
         */
        MySyncAdapter.configurePeriodicSync(context, SYNC_INTERVAL, SYNC_FLEXTIME);

        /*
         * Without calling setSyncAutomatically, our periodic sync will not be enabled.
         */
        ContentResolver.setSyncAutomatically(newAccount, context.getString(R.string.content_authority), true);

        /*
         * Finally, let's do a sync to get things started
         */
        syncImmediately(context);
    }

    public static void initializeSyncAdapter(Context context) {
        getSyncAccount(context);
    }
}