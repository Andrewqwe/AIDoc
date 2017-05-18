package com.damian.aldoc;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Comparator;

/**
 * Created by Andrzej on 2017-05-16.
 */

public class UserProfileEditListAdapter extends BaseAdapter {

    public static final int TYPE_EMPTY = 0;
    public static final int TYPE_NOT_EMPTY = 1;

    private TextView name,value;
    private ImageButton button1,button2;
    private ArrayList<UserProfileEditListItem> list;
    public Activity activity;

    public UserProfileEditListAdapter(ArrayList<UserProfileEditListItem> list, Activity activity) {
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
    public View getView(final int position, View convertView, ViewGroup parent) {
        UserProfileEditListItem listItem = list.get(position);
        int listViewItemType = list.get(position).getRowType();
        LayoutInflater inflater = activity.getLayoutInflater();
        if(convertView == null){

            if(listViewItemType == TYPE_EMPTY){
                convertView = inflater.inflate(R.layout.useremptyrecord,null);
                name = (TextView) convertView.findViewById(R.id.emptyRowTextView);
                name.setText(list.get(position).getTranslation());
                button1 = (ImageButton) convertView.findViewById(R.id.emptyRowAddButton);
                button1.setColorFilter(Color.rgb(0,153,76));
                button1.setBackgroundColor(Color.TRANSPARENT);
                button1.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        UserProfileAddRowDialogClass addDialog = new UserProfileAddRowDialogClass(activity);
                        UserProfileEditListItem item = list.get(position);
                        addDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                        addDialog.show();
                        addDialog.setDatabase_key(item.getDatabase_key());
                        addDialog.setTranslation(item.getTranslation());
                    }
                });

            }else if(listViewItemType == TYPE_NOT_EMPTY){
                convertView = inflater.inflate(R.layout.usernotemptyrecord,null);
                name = (TextView) convertView.findViewById(R.id.notEmptyRowTextView);
                value = (TextView) convertView.findViewById(R.id.notEmptyRowTextView2);
                name.setText(list.get(position).getTranslation());
                value.setText(list.get(position).getValue());
                button1 = (ImageButton) convertView.findViewById(R.id.notEmptyRowEditButton);
                button2 = (ImageButton) convertView.findViewById(R.id.notEmptyRowDeleteButton);
                button1.setColorFilter(Color.BLUE);
                button2.setColorFilter(Color.RED);
                button1.setBackgroundColor(Color.TRANSPARENT);
                button2.setBackgroundColor(Color.TRANSPARENT);
                //TODO: listenery guzikow
            }

        }

        return convertView;
    }
}
