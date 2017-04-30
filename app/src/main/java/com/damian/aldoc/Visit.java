package com.damian.aldoc;

import java.util.ArrayList;


public class Visit {



    public Visit(){}


    public Visit(String doctor, String location, String date, String time)
    {
        m_doctor = doctor;
        m_location = location;
        m_date = date;
        m_time = time;
    }

    public String getTime() {
        return m_time;
    }

    /*
    * @param time - format hh::mm
    * */
    public void setTime(String time) {
        m_time = time;
    }

    public String getDate() {
        return m_date;
    }

    /*
    * @param date - format dd-MM-yyyy
    * */
    public void setDate(String date) {
        m_date = date;
    }

    public String getLocation() {
        return m_location;
    }

    public void setLocation(String location) {
        m_location = location;
    }

    public String getDoctor() {
        return m_doctor;
    }

    public void setDoctor(String doctor) {
        m_doctor = doctor;
    }

    public void setUid(String uid) { m_uid = uid; }

    public String getUid() { return m_uid; }

    @Override
    public String toString()
    {
        return m_date + " " + m_time + "\n" + m_location + "\n" + m_doctor;
    }

    private String m_uid;
    private String m_time;
    private String m_date;
    private String m_location;
    private String m_doctor;
    private ArrayList<Prescription> m_prescriptions = new ArrayList<>();
}
