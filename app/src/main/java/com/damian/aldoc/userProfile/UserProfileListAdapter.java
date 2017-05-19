package com.damian.aldoc.userProfile;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.damian.aldoc.R;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Andrzej on 2017-04-28.
 */

public class UserProfileListAdapter extends BaseAdapter {

    private TextView name,value;
    private ArrayList<HashMap<String,String>> list;
    public Activity activity;

    public UserProfileListAdapter(ArrayList<HashMap<String, String>> list, Activity activity) {
        super();
        this.list = list;
        this.activity = activity;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = activity.getLayoutInflater();

        if(convertView == null)
        {
            convertView=inflater.inflate(R.layout.simplerow,null);
            name=(TextView) convertView.findViewById(R.id.rowTextView);
            value=(TextView) convertView.findViewById(R.id.rowTextView2);
        }
        HashMap<String,String> map = list.get(position);
        name.setText(map.get("name"));
        value.setText(map.get("value"));

        return convertView;
    }
}
