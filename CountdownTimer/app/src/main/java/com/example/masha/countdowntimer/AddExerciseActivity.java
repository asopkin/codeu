package com.example.masha.countdowntimer;

import android.content.ContentValues;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;


public class AddExerciseActivity extends ActionBarActivity {
    private Button addButton;
    public int num_exercises = 11;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_exercise);

    }


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
                /**
                 Intent i = new Intent(this, MyPreferenceFragment.class);
                 startActivity(i);**/
                Intent intent = new Intent();
                intent.setClass(AddExerciseActivity.this, SetPreferenceActivity.class);
                startActivityForResult(intent, 0);
                break;

        }

        return super.onOptionsItemSelected(item);
    }

    public void onClickAddName(View view) {
        ContentValues values = new ContentValues();
        values.put(MyProvider.name, ((EditText) findViewById(R.id.txtName))
                .getText().toString());
        Uri uri = getContentResolver().insert(MyProvider.CONTENT_URI, values);
        Toast.makeText(getBaseContext(), "New exercise inserted", Toast.LENGTH_LONG)
                .show();
        num_exercises++;
    }

    public void onClickDeleteExercise(View view){
        String where = "_id < 1";
        String gimme = "0";
        int ret_val = getContentResolver().delete(MyProvider.CONTENT_URI,MyProvider.id + " = " + num_exercises, null);
        Toast.makeText(getBaseContext(), "First exercise deleted", Toast.LENGTH_LONG).show();
        num_exercises--;
    }
    public void onClickDeleteAll(View view){
        while(num_exercises>0){
            int ret_val = getContentResolver().delete(MyProvider.CONTENT_URI,MyProvider.id + " = " + num_exercises, null);
            num_exercises--;

        }
        Toast.makeText(getBaseContext(), "All done", Toast.LENGTH_LONG).show();
    }
}
