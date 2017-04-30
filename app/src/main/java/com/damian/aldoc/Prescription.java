package com.damian.aldoc;

import java.util.ArrayList;
import android.media.Image;

public class Prescription {

    public Prescription() {}

    public Prescription(String visit_uid, String name)
    {
        m_visit_uid = visit_uid;
        m_name = name;
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

    private String m_visit_uid;
    private String m_name;
    private ArrayList<PrescriptionEntry> m_entries = new ArrayList<>();
}
