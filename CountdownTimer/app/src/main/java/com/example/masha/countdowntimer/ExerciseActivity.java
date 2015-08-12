package com.example.masha.countdowntimer;

import android.content.ContentProvider;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import com.example.masha.countdowntimer.MyProvider;
import com.example.masha.countdowntimer.TodoDatabaseHelper;


public class ExerciseActivity extends ActionBarActivity implements LoaderManager.LoaderCallbacks<Cursor>{

    private Button doneButton;
    private Button skipButton;
    private Button addButton;
    private String exerciseName = "";
    private CommentsDataSource datasource;
    CursorLoader cursorLoader;
    TextView mylistpref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exercise);
        getSupportLoaderManager().initLoader(1, null, this);
        /**
        datasource = new CommentsDataSource(this);
        datasource.open();

        List<Comment> values = datasource.getAllComments();
        final String[] comments = new String[]{"Dumbbells Exercise: lift 3", "Intervals: run for 20 minutes", "Ab crunches" };


        int nextInt = new Random().nextInt(3);**/
        // globally
        TextView myAwesomeTextView = (TextView)findViewById(R.id.timer_view);
/**
        Comment comment = null;
        comment = datasource.createComment(comments[nextInt]);**/

        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);
        String gimme = sharedPrefs.getString("edittext_preference", "Obama");
        String congratulatory = "Keep it up " + gimme;
        Toast toast = Toast.makeText(getApplicationContext(), congratulatory, Toast.LENGTH_LONG);
        toast.show();

        myAwesomeTextView.setText(exerciseName);



        doneButton = (Button) findViewById(R.id.done_button);

        doneButton.setOnClickListener(doneHandler);


        skipButton = (Button) findViewById(R.id.skip_button);

        skipButton.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {
                finish();
            }
        });

        addButton = (Button) findViewById(R.id.addButton);
        addButton.setOnClickListener(myhandler1);
    }


    View.OnClickListener myhandler1 = new View.OnClickListener() {
        public void onClick(View v) {
            // it was the 1st button
            Intent intentMain = new Intent(ExerciseActivity.this ,
                    AddExerciseActivity.class);
            ExerciseActivity.this.startActivity(intentMain);
        }
    };

    View.OnClickListener doneHandler = new View.OnClickListener() {
        public void onClick(View v) {
            // it was the 1st button
            Intent intentMain = new Intent(ExerciseActivity.this ,
                    RssFeedActivity.class);
            ExerciseActivity.this.startActivity(intentMain);
        }
    };
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
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

                Intent intent = new Intent();
                intent.setClass(ExerciseActivity.this, SetPreferenceActivity.class);
                startActivityForResult(intent, 0);
                break;

        }
        return super.onOptionsItemSelected(item);
    }




    @Override
    public Loader<Cursor> onCreateLoader(int arg0, Bundle arg1) {
        cursorLoader= new CursorLoader(this, Uri.parse("content://com.example.contentproviderexample.MyProvider/cte"), null, null, null, null);
        return cursorLoader;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> arg0, Cursor cursor) {
       // cursor.moveToFirst();
        int gimme = cursor.getCount();
        cursor.moveToPosition(gimme-1);
      //  if(cursor.isAfterLast()){
            String name = cursor.getString(cursor.getColumnIndex("name"));
            exerciseName = name;
            TextView myAwesomeTextView = (TextView)findViewById(R.id.timer_view);
            myAwesomeTextView.setText(exerciseName);
            cursor.close();
       // }



    }


    @Override
    public void onLoaderReset(Loader<Cursor> arg0) {
        // TODO Auto-generated method stub

    }








}