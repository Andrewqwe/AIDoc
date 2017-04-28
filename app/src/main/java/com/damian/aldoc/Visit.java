package com.damian.aldoc;

import java.util.Calendar;
import java.util.ArrayList;


public class Visit {



    public Visit(){}


    public Visit(String doctor, String place, Calendar time)
    {
        m_doctor = m_doctor;
        m_place = m_place;
        m_time = time;
    }

    public Calendar getTime() {
        return m_time;
    }

    public void setTime(Calendar time) {
        m_time = time;
    }

    public String getPlace() {
        return m_place;
    }

    public void setPlace(String m_place) {
        this.m_place = m_place;
    }

    public String getDoctor() {
        return m_doctor;
    }

    public void setDoctor(String m_doctor) {
        this.m_doctor = m_doctor;
    }

    private Calendar m_time;
    private String m_place;
    private String m_doctor;
    private ArrayList<Prescription> m_prescriptions = new ArrayList<>();
}
