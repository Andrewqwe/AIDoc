package com.damian.aldoc.userProfile;

import android.widget.TextView;

/**
 * Created by Andrzej on 2017-05-16.
 */

public class UserProfileEditViewHolder {
    private TextView text;

    public TextView getText() {
        return text;
    }

    public void setText(TextView text) {
        this.text = text;
    }

    public UserProfileEditViewHolder(TextView text) {
        this.text = text;
    }
}
