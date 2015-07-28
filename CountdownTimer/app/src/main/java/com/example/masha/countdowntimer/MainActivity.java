package com.example.masha.countdowntimer;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;
import java.util.Random;


public class MainActivity extends ActionBarActivity {

    //countdown timer needs to be put in a service so it can run in the background
    private CountDownTimerWithPause mCountDownTimer;
    private Button startButton;
    private Button pauseButton;
    private CommentsDataSource datasource;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        datasource = new CommentsDataSource(this);
        datasource.open();

        List<Comment> values = datasource.getAllComments();
        /**
        // use the SimpleCursorAdapter to show the
        // elements in a ListView
        ArrayAdapter<Comment> adapter = new ArrayAdapter<Comment>(this,
                android.R.layout.simple_list_item_1, values);
        setListAdapter(adapter);**/
        //3600000 is an hour
        //60000 <- is a minute, for testing purposes
        //10000 <- ~8 seconds
        mCountDownTimer = new CountDownTimerWithPause(10000,1000,true) {

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
                int duration = Toast.LENGTH_SHORT;
                @SuppressWarnings("unchecked")
                //ArrayAdapter<Comment> adapter = (ArrayAdapter<Comment>) getListAdapter();
                Comment comment = null;
                String[] comments = new String[]{"Dumbbells Exercise: lift 3", "Intervals: run for 20 minutes", "Ab crunches" };
                int nextInt = new Random().nextInt(3);
                comment = datasource.createComment(comments[nextInt]);
                //adapter.add(comment);

                //adapter.notifyDataSetChanged();
                //Toast toast = Toast.makeText(getApplicationContext(), R.string.exercise, duration);
                String tester = comment.toString();
                Toast toast = Toast.makeText(getApplicationContext(), tester, duration);
                sendMessage();
                toast.show();

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

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
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

}
