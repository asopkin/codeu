package com.example.masha.countdowntimer;

/**
 * Created by asopkin on 7/30/2015.
 */

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.Menu;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.Transformation;
import android.widget.AbsoluteLayout;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ExpandableListView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.view.View;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class DisplayExercises extends FragmentActivity implements LoaderManager.LoaderCallbacks<Cursor> {
    ExpandableListView resultView=null;
    CursorLoader cursorLoader;
    ListView listView;
    TextView txt_hidden;
    TextView txt_button;
    ExpandableListAdapter listAdapter;
    ExpandableListView expListView;
    List<String> listDataHeader;
    HashMap<String, List<String>> listDataChild;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.exercises);
        //expListView = (ExpandableListView)findViewById(R.id.list);
        txt_button = (TextView)findViewById(R.id.text1);
        txt_hidden = (TextView)findViewById(R.id.text2);
        //txt_hidden.setVisibility(View.GONE);
        resultView= (ExpandableListView) findViewById(R.id.list);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.activity_main, menu);
        return true;
    }

    public void onClickDisplayNames(View view) {
        getSupportLoaderManager().initLoader(1, null, this);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int arg0, Bundle arg1) {
        cursorLoader= new CursorLoader(this, Uri.parse("content://com.example.contentproviderexample.MyProvider/cte"), null, null, null, null);
        return cursorLoader;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> arg0, Cursor cursor) {
        cursor.moveToFirst();

        // get the listview
        expListView = (ExpandableListView) findViewById(R.id.list);

        ArrayList<String> listItems = new ArrayList<String>();
        ArrayList<String> listItems2 = new ArrayList<String>();
        //  listItems = new ArrayList<ListItem>();

        do{
            String id=cursor.getString(cursor.getColumnIndex("id"));
            String name=cursor.getString(cursor.getColumnIndex("name"));
            // String name_str = id +" : "+ name;
            listItems2.add(id);
            listItems.add(name);
            cursor.moveToNext();
        }while(!cursor.isAfterLast());

        // preparing list data
        listDataHeader = new ArrayList<String>();
        listDataChild = new HashMap<String, List<String>>();

        // Adding child data
        for(int i = 0; i<listItems.size(); i++){
            String temp = "Exercise: " + listItems2.get(i);
            listDataHeader.add(temp);
        }

        for(int j=0; j<listItems.size(); j++){
            List<String> temp = new ArrayList<String>();
            temp.add(listItems.get(j));
            listDataChild.put(listDataHeader.get(j), temp);
        }
        /**
        // Adding child data
        List<String> ex1 = new ArrayList<String>();
        ex1.add("push-ups");

        List<String> ex2 = new ArrayList<String>();
        ex2.add("gimme");

        List<String> ex3 = new ArrayList<String>();
        ex3.add("workout");

        listDataChild.put(listDataHeader.get(0), ex1); // Header, Child data
        listDataChild.put(listDataHeader.get(1), ex2);
        listDataChild.put(listDataHeader.get(2), ex3);
        **/
        listAdapter = new ExpandableListAdapter(this, listDataHeader, listDataChild);

        // setting list adapter
        expListView.setAdapter(listAdapter);

        /**
        ArrayList<String> listItems = new ArrayList<String>();
        ArrayList<String> listItems2 = new ArrayList<String>();
       //  listItems = new ArrayList<ListItem>();
        ListView mylist=(ListView) findViewById(R.id.list);


        do{
            String id=cursor.getString(cursor.getColumnIndex("id"));
            String name=cursor.getString(cursor.getColumnIndex("name"));
           // String name_str = id +" : "+ name;
            listItems2.add(id);
            listItems.add(name);
            cursor.moveToNext();
        }while(!cursor.isAfterLast());

        CustomListAdapter adapter=new CustomListAdapter(this, listItems2, listItems);
        mylist.setAdapter(adapter);**/

    }


    @Override
    public void onLoaderReset(Loader<Cursor> arg0) {
        // TODO Auto-generated method stub

    }


    public void toggleContents(View v){
        if(txt_hidden.isShown()){
            slide_up(this, txt_hidden);
            txt_hidden.setVisibility(View.GONE);
        }
        else{
            txt_hidden.setVisibility(View.VISIBLE);
            slide_down(this, txt_hidden);
        }
        //txt_hidden.setVisibility(txt_hidden.isShown()? View.GONE : View.VISIBLE);
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    public static void slide_down(Context ctx, View v){

        Animation a = AnimationUtils.loadAnimation(ctx, R.anim.slide_down);
        if(a != null){
            a.reset();
            if(v != null){
                v.clearAnimation();
                v.startAnimation(a);
            }
        }
    }

    public static void slide_up(Context ctx, View v){

        Animation a = AnimationUtils.loadAnimation(ctx, R.anim.slide_up);
        if(a != null){
            a.reset();
            if(v != null){
                v.clearAnimation();
                v.startAnimation(a);
            }
        }
    }






}