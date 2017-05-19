package com.damian.aldoc.userProfile;

/**
 * Created by Andrzej on 2017-05-16.
 */

public class UserProfileEditListItem {
    private String translation,value,database_key,data_type;
    private int rowType;

    public String getDatabase_key() {
        return database_key;
    }

    public void setDatabase_key(String database_key) {
        this.database_key = database_key;
    }

    public String getTranslation() {
        return translation;
    }

    public void setTranslation(String key) {
        this.translation = translation;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public UserProfileEditListItem(String translation, String value, String database_key, String data_type) {
        this.translation = translation;
        this.value = value;
        this.database_key = database_key;
        this.data_type = data_type;
        this.rowType = UserProfileEditListAdapter.TYPE_NOT_EMPTY;
    }


    public UserProfileEditListItem(String translation, String database_key, String data_type) {
        this.translation = translation;
        this.database_key = database_key;
        this.data_type = data_type;
        this.rowType = UserProfileEditListAdapter.TYPE_EMPTY;
        // TODO:rozwiazanie tymczasowe, w przyszlosci mozna przeniesc do adaptera
    }

    public int getRowType() {
        return rowType;
    }

    public void setRowType(int type) {
        this.rowType = type;
    }
}
