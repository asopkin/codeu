package com.example.masha.countdowntimer;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;
import java.util.Random;


public class MainActivity extends ActionBarActivity {

    //countdown timer needs to be put in a service so it can run in the background
    private CountDownTimerWithPause mCountDownTimer;
    private Button startButton;
    private Button pauseButton;
    private Button addButton;
    private Button showButton;
    private CommentsDataSource datasource;
    private static final int RESULT_SETTINGS = 1;
    protected boolean mbActive;
    protected ProgressBar mProgressBar;
    protected static final int TIMER_RUNTIME = 10000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mProgressBar = (ProgressBar)findViewById(R.id.adprogress_progressBar);
        //showUserSettings();

        /**datasource = new CommentsDataSource(this);
        datasource.open();**/
        /**
        List<Comment> values = datasource.getAllComments();

        // use the SimpleCursorAdapter to show the
        // elements in a ListView
        ArrayAdapter<Comment> adapter = new ArrayAdapter<Comment>(this,
                android.R.layout.simple_list_item_1, values);
        setListAdapter(adapter);**/
        //3600000 is an hour
        //60000 <- is a minute, for testing purposes
        //10000 <- ~8 seconds
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
                            updateProgress(waited);
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

        mCountDownTimer = new CountDownTimerWithPause(TIMER_RUNTIME,1000,true) {

            TextView mTextField =  (TextView) findViewById(R.id.timer_view);

            public void onTick(long millisUntilFinished) {
                int seconds = (int) (millisUntilFinished / 1000);
                int minutes = seconds / 60;
                seconds = seconds % 60;

                if (seconds < 10) {
                    mTextField.setText("" + minutes + ":0" + seconds);
                } else {
                    mTextField.setText("" + minutes + ":" + seconds);
                }

            }

            public void onFinish() {
                /**
                NotificationCompat.Builder mBuilder =
                        new NotificationCompat.Builder(this)
                                .setSmallIcon(R.mipmap.ic_launcher)
                                .setContentTitle("My notification")
                                .setContentText("Hello World!");
                // Creates an explicit intent for an Activity in your app
                Intent resultIntent = new Intent(this, MainActivity.class);

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
                **/
                sendMessage();
            }


        }.create();

        startButton = (Button) findViewById(R.id.startButton);

        startButton.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {
                mCountDownTimer.resume();

            }
        });

        pauseButton = (Button) findViewById(R.id.pauseButton);

        pauseButton.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {
                mCountDownTimer.pause();

            }
        });

        addButton = (Button) findViewById(R.id.addButton);
        addButton.setOnClickListener(myhandler1);

        showButton = (Button)findViewById(R.id.showButton);
        showButton.setOnClickListener(myhandler2);


    }


    View.OnClickListener myhandler1 = new View.OnClickListener() {
        public void onClick(View v) {
            // it was the 1st button
            Intent intentMain = new Intent(MainActivity.this ,
                    AddExerciseActivity.class);
            MainActivity.this.startActivity(intentMain);
        }
    };

    View.OnClickListener myhandler2 = new View.OnClickListener() {
        public void onClick(View v) {
            // it was the 1st button
            Intent intentMain = new Intent(MainActivity.this ,
                    DisplayExercises.class);
            MainActivity.this.startActivity(intentMain);
        }
    };

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
                Intent intent = new Intent();
                intent.setClass(MainActivity.this, SetPreferenceActivity.class);
                startActivityForResult(intent, 0);
                break;

        }

        return super.onOptionsItemSelected(item);
    }

    private void sendMessage() {
        Intent intent = new Intent(this, ExerciseActivity.class);
        startActivity(intent);
    }

    @Override
    protected void onResume() {
      super.onResume();
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

    public void updateProgress(final int timePassed) {
        if(null != mProgressBar) {
            // Ignore rounding error here
            final int progress = mProgressBar.getMax() * timePassed / TIMER_RUNTIME;
            mProgressBar.setProgress(progress);
        }
    }

    public void onContinue() {
        // perform any final actions here
    }




}
