package com.example.masha.countdowntimer;

/**
 * Created by asopkin on 8/1/2015.
 */
import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

public class CustomListAdapter extends ArrayAdapter<String> {

    private final Activity context;
    //private final String[] itemname;
    //private final String[] imgid;
    private final ArrayList<String> itemname;
    private final ArrayList<String> imgid;

    public CustomListAdapter(Activity context, ArrayList<String> itemname, ArrayList<String> imgid) {
        super(context, R.layout.list_item, itemname);
        // TODO Auto-generated constructor stub

        this.context=context;
        this.itemname=itemname;
        this.imgid=imgid;
    }

    public View getView(int position,View view,ViewGroup parent) {
        LayoutInflater inflater=context.getLayoutInflater();
        View rowView=inflater.inflate(R.layout.list_item, null,true);

        TextView txtTitle = (TextView) rowView.findViewById(R.id.text1);
        TextView txt2 = (TextView) rowView.findViewById(R.id.text2);
        txtTitle.setText(itemname.get(position));
        txt2.setText(imgid.get(position));
        return rowView;

    };
}