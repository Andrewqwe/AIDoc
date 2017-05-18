package com.damian.aldoc;

import java.util.ArrayList;
import android.media.Image;

public class Prescription {

    public Prescription() {}

    public Prescription(String visit_uid, String name, String photo)
    {
        m_visit_uid = visit_uid;
        m_name = name;
        m_photo_uri = photo;
    }

    @Override
    public String toString()
    {
        return m_name;
    }

    public void setName(String name) { m_name = name; }
    public String getName() { return m_name; }
    public void setVisitUid(String visit_uid) { m_visit_uid = visit_uid; }
    public String getVisitUid() { return m_visit_uid; }
    public void setUid(String uid) { m_uid = uid; }
    public String getUid() { return m_uid; }
    public void setPhoto(String photo) { m_photo_uri = photo; }
    public String getPhoto() { return m_photo_uri; }

    private String m_uid;
    private String m_photo_uri;
    private String m_visit_uid;
    private String m_name;
}
