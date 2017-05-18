package com.damian.aldoc;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.firebase.database.DatabaseReference;

import java.util.HashMap;

/**
 * Created by Andrzej on 2017-05-18.
 */

public class UserProfileDeleteRowDialogClass extends Dialog implements
        View.OnClickListener {

    private Activity c;
    private Dialog d;
    private Button dodaj, anuluj;
    private TextView title;
    private String translation,database_key;

    public Button getButton1() {
        return dodaj;
    }

    public Button getButton2() {
        return anuluj;
    }

    public void setTranslation(String translation) {
        this.translation=translation;
        title.setText(translation);
    }

    public void setDatabase_key(String database_key) {
        this.database_key = database_key;
    }

    public UserProfileDeleteRowDialogClass(Activity a) {
        super(a);
        // TODO Auto-generated constructor stub
        this.c = a;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.user_profile_delete_row_dialog);
        dodaj = (Button) findViewById(R.id.User_profile_delete_row_button1);
        anuluj = (Button) findViewById(R.id.User_profile_delete_row_button2);
        dodaj.setOnClickListener(this);
        anuluj.setOnClickListener(this);
        title = (TextView) findViewById(R.id.User_profile_delete_row_titleText);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.User_profile_delete_row_button1:
            {
                String a[] = Database.GetUserInfo();
                Database.Initialize(true);
                DatabaseReference ref;
                ref = Database.SetLocation("users/" + a[2]);
                HashMap<String,Object> temp = new HashMap<>();
                temp.put(database_key,null);
                ref.updateChildren(temp);
                UserProfileEditActivity.notifyAdapter();
            }
                break;
            case R.id.User_profile_delete_row_button2:
                dismiss();
                break;
            default:
                break;
        }
        dismiss();
    }
}
