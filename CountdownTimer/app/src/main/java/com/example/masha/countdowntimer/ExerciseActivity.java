package com.example.masha.countdowntimer;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;
import java.util.Random;


public class ExerciseActivity extends ActionBarActivity {

    private Button doneButton;
    private Button skipButton;
    private CommentsDataSource datasource;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exercise);
        datasource = new CommentsDataSource(this);
        datasource.open();

        List<Comment> values = datasource.getAllComments();
        String[] comments = new String[]{"Dumbbells Exercise: lift 3", "Intervals: run for 20 minutes", "Ab crunches" };
        int nextInt = new Random().nextInt(3);
        // globally
        TextView myAwesomeTextView = (TextView)findViewById(R.id.timer_view);

//in your OnCreate() method
        Comment comment = null;
        comment = datasource.createComment(comments[nextInt]);
        //adapter.add(comment);

        //adapter.notifyDataSetChanged();
        //Toast toast = Toast.makeText(getApplicationContext(), R.string.exercise, duration);
        String tester = comment.toString();
        myAwesomeTextView.setText(tester);

        doneButton = (Button) findViewById(R.id.done_button);

        doneButton.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {
                finish();

            }
        });

        skipButton = (Button) findViewById(R.id.skip_button);

        skipButton.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {
                finish();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_exercise, menu);

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

}
