package com.example.masha.countdowntimer;

import android.animation.ObjectAnimator;
import android.app.LoaderManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.ContentValues;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.ActionBarActivity;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.example.masha.countdowntimer.quotedata.QuoteProvider;
import com.example.masha.countdowntimer.quotedata.QuoteTable;
import com.example.masha.countdowntimer.sync.MySyncAdapter;


public class MainActivity extends ActionBarActivity implements
        LoaderManager.LoaderCallbacks<Cursor> {

    //countdown timer needs to be put in a service so it can run in the background
    private CountDownTimerWithPause mCountDownTimer;
    private Button startButton;
    private Button pauseButton;
    private Button addButton;
    private Button showButton;
    private Button refreshButton;
    private CommentsDataSource datasource;
    private static final int RESULT_SETTINGS = 1;
    protected boolean mbActive = true;
    protected ProgressBar mProgressBar;
    private int TIMER_RUNTIME = 10000;
    private String userName;
    private Boolean progressbar= true;
    boolean resumed = true;
    private int mProgressStatus = 0;
    private int secondsPassed;
    ObjectAnimator animation;
    private SimpleCursorAdapter adapter;
    private int pStatus = 0;



    private Handler mHandler = new Handler();
   // private Context context;

    private static final String[] DETAIL_COLUMNS = {
            QuoteTable.COLUMN_QUOTE,
            QuoteTable.COLUMN_AUTHOR
    };

    // These indices are tied to DETAIL_COLUMNS.  If DETAIL_COLUMNS changes, these
    // must change.
   // public static final int COL_QUOTE_ID = 0;
    public static final int COL_QUOTE = 0;
    public static final int COL_QUOTE_AUTHOR = 1;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);
        String timing = sharedPrefs.getString("select_timing", "11000");
        String congratulatory = "seconds: " + timing;
        Toast toast = Toast.makeText(getApplicationContext(), congratulatory, Toast.LENGTH_LONG);
        toast.show();



        int tracker = 25;

       mProgressBar = (ProgressBar) findViewById(R.id.adprogress_progressBar);
        Resources res = getResources();
        Drawable drawable = res.getDrawable(R.drawable.circular);
        mProgressBar.setProgressDrawable(drawable);
        mProgressBar.setProgress(pStatus);
        //mProgressBar.setSecondaryProgress(50);
        run();
        /**
        new Thread(new Runnable() {
            public void run() {
                while (mProgressStatus < TIMER_RUNTIME) {
                    if(progressbar) {
                        mProgressStatus += 200;
                    }
                    // Update the progress bar
                    mHandler.post(new Runnable() {
                        public void run() {
                            if(progressbar) {
                               //mProgressBar.setProgress(mProgressStatus);
                            }
                        }
                    });
                }
            }
        }).start();**/

        /**
        Resources res = getResources();
        Drawable drawable = res.getDrawable(R.drawable.circular);
        if(progressbar){
            mProgressBar.setProgress(tracker);   // Main Progress
            //mProgressBar.setSecondaryProgress(50); // Secondary Progress
            //mProgressBar.setMax(100); // Maximum Progress
            mProgressBar.setProgressDrawable(drawable);
        }**/


        //3600000 is an hour
        //60000 <- is a minute, for testing purposes
        //10000 <- ~8 seconds
        /**
        final Thread timerThread = new Thread() {
            @Override
            public void run() {
                //mbActive = true;
                try {
                    int waited = 0;
                    while(mbActive && (waited < TIMER_RUNTIME)) {
                        sleep(200);
                        if(mbActive) {
                            waited += 200;
                            if(progressbar) {
                                updateProgress(waited);
                            }
                        }
                    }
                } catch(InterruptedException e) {
                    // do nothing
                } finally {
                    onContinue();
                }
            }
        };
        timerThread.start();**/

        mCountDownTimer = new CountDownTimerWithPause(TIMER_RUNTIME,1000,true) {

            TextView mTextField =  (TextView) findViewById(R.id.timer_view);

            public void onTick(long millisUntilFinished) {
                int seconds = (int) (millisUntilFinished / 1000);
                int minutes = seconds / 60;
                seconds = seconds % 60;
                secondsPassed = seconds;

                if (seconds < 10) {
                    mTextField.setText("" + minutes + ":0" + seconds);
                } else {
                    mTextField.setText("" + minutes + ":" + seconds);
                }

            }

            public void onFinish() {


                sendMessage();
            }


        }.create();

        startButton = (Button) findViewById(R.id.startButton);

        startButton.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {
                mProgressBar.setProgress(pStatus);
                mCountDownTimer.resume();

            }
        });

        pauseButton = (Button) findViewById(R.id.pauseButton);

        pauseButton.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {
                if(resumed){
                    mCountDownTimer.pause();
                    resumed = false;
                }
                else{
                    mCountDownTimer.resume();
                    resumed = true;
                }

            }
        });

        addButton = (Button) findViewById(R.id.addButton);
        addButton.setOnClickListener(myhandler1);

        showButton = (Button)findViewById(R.id.showButton);
        showButton.setOnClickListener(myhandler2);

        refreshButton = (Button)findViewById(R.id.refreshButton);
        refreshButton.setOnClickListener(refreshHandler);

        String[] projection = {QuoteTable.COLUMN_QUOTE};

        //Cursor cursor = getContentResolver().query(QuoteProvider.CONTENT_URI, projection, null, null, null, null);
        Cursor cursor = getContentResolver().query(QuoteProvider.CONTENT_URI, null, null, null, null, null);
        boolean dataExists = cursor.moveToFirst();
        Log.d("DATA EXISTS OR NOT?" , dataExists + " ");

        MySyncAdapter.initializeSyncAdapter(this);
        getLoaderManager().initLoader(0, null, this);
    }

    View.OnClickListener refreshHandler = new View.OnClickListener() {
        public void onClick(View v) {
            // it was the 1st button
            Log.d("MainActivity", "button clicked");
            MySyncAdapter.syncImmediately(MainActivity.this);
        }
    };

    View.OnClickListener myhandler1 = new View.OnClickListener() {
        public void onClick(View v) {
            // it was the 1st button
            mCountDownTimer.pause();
            Intent intentMain = new Intent(MainActivity.this ,
                    AddExerciseActivity.class);
            MainActivity.this.startActivity(intentMain);
        }
    };

    View.OnClickListener myhandler2 = new View.OnClickListener() {
        public void onClick(View v) {
            // it was the 1st button
            mCountDownTimer.pause();
            Intent intentMain = new Intent(MainActivity.this ,
                    DisplayExercises.class);
            MainActivity.this.startActivity(intentMain);
        }
    };

    public void createNotification(){
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setContentTitle("Attention")
                        .setContentText("Do your exercises!");
        // Creates an explicit intent for an Activity in your app
        Intent resultIntent = new Intent(this, ExerciseActivity.class);

        // The stack builder object will contain an artificial back stack for the
        // started Activity.
        // This ensures that navigating backward from the Activity leads out of
        // your application to the Home screen.
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        // Adds the back stack for the Intent (but not the Intent itself)
        stackBuilder.addParentStack(MainActivity.class);
        // Adds the Intent that starts the Activity to the top of the stack
        stackBuilder.addNextIntent(resultIntent);
        PendingIntent resultPendingIntent =
                stackBuilder.getPendingIntent(
                        0,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );
        mBuilder.setContentIntent(resultPendingIntent);
        NotificationManager mNotificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        // mId allows you to update the notification later on.
        mNotificationManager.notify(234234, mBuilder.build());
    }

    public void run() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (pStatus < 100) {
                    if (resumed) {
                        pStatus += 2;
                    }
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            mProgressBar.setProgress(pStatus);
                        }
                    });
                    try {
                        Thread.sleep(200);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.menu_main, menu);
        getMenuInflater().inflate(R.menu.settings, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch (item.getItemId()) {

            case R.id.menu_settings:
                /**
                Intent i = new Intent(this, MyPreferenceFragment.class);
                startActivity(i);**/
                mCountDownTimer.pause();
                Intent intent = new Intent();
                intent.setClass(MainActivity.this, SetPreferenceActivity.class);
                startActivityForResult(intent, 0);
                break;

        }

        return super.onOptionsItemSelected(item);
    }

    private void sendMessage() {

        createNotification();
        Intent intent = new Intent(this, ExerciseActivity.class);
        startActivity(intent);
    }
    @Override
    protected void onPause(){
        mCountDownTimer.pause();
        super.onPause();
    //    animation.cancel();
        progressbar = false;
        mProgressBar.setProgress(0);
        mbActive = false;

    }

    @Override
    protected void onResume() {
      super.onResume();
        progressbar = true;
        mProgressBar.setProgress(pStatus);
        pStatus=0;
        Resources res = getResources();
       // Drawable drawable = res.getDrawable(R.drawable.circular);
       // mProgressBar.setProgress(25);   // Main Progress
       // mProgressBar.setSecondaryProgress(50); // Secondary Progress
       // mProgressBar.setMax(100); // Maximum Progress
       // mProgressBar.setProgressDrawable(drawable);
        //showUserSettings();

        final Thread timerThread = new Thread() {
            @Override
            public void run() {
                mbActive = true;
                try {
                    int waited = 0;
                    while(mbActive && (waited < TIMER_RUNTIME)) {
                        sleep(200);
                        if(mbActive) {
                            waited += 200;
                        }
                    }
                } catch(InterruptedException e) {
                    // do nothing
                } finally {
                    onContinue();
                }
            }
        };
        timerThread.start();
        mCountDownTimer.create();
    }

    public void onClickAddName(View view) {
        ContentValues values = new ContentValues();
        values.put(MyProvider.name, ((EditText) findViewById(R.id.txtName))
                .getText().toString());
        Uri uri = getContentResolver().insert(MyProvider.CONTENT_URI, values);
        Toast.makeText(getBaseContext(), "New record inserted", Toast.LENGTH_LONG)
                .show();
    }


    public void onContinue() {
        // perform any final actions here
    }

    // creates a new loader after the initLoader () call
    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Log.d("CURSOR LOADER CREATE", "Just created the cursor loader");
        //String []  quote = {QuoteTable.COLUMN_QUOTE};
        CursorLoader cursorLoader = new CursorLoader(this,
                QuoteProvider.CONTENT_URI,DETAIL_COLUMNS, null, null, null);
        return cursorLoader;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        Log.d("WHEEE", "I'M STILL NOT WORKING");
        if (data == null) {
            Log.d("NULL", "AH NO THIS ISN'T SUPPOSED TO HAPPEN!");
        }
        if (data != null) {
            Log.d("YAY", "HUZZAH!");
        }
        if (!data.moveToFirst()) {
            Log.d("THIS", "SUCKS");
        }
        if (data != null && data.moveToFirst()) {
            // Read weather condition ID from cursor
            String quote = data.getString(COL_QUOTE);
            String author = data.getString(COL_QUOTE_AUTHOR);

            String display = quote + "\n" + "~" + author;
            Log.d("WHYYOUNODISPLAY", display);
            TextView quoteView = (TextView) findViewById(R.id.quote_view);
            quoteView.setText(display);
            quoteView.setMovementMethod(new ScrollingMovementMethod());
            quoteView.setVerticalScrollbarPosition(View.SCROLLBAR_POSITION_LEFT);
            quoteView.setBackgroundColor(getResources().getColor(R.color.dark_blue));
        }
    }

        @Override
        public void onLoaderReset(Loader<Cursor> loader) {

        }
}
