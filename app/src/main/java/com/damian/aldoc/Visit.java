package com.damian.aldoc;

import java.util.Date;
import java.util.ArrayList;


public class Visit {



    public Visit(){
        m_place = "Kleczkowska 28";
        m_doctor = "Michal Kuffel";
    }


    public Visit(String m_doctor, String m_place) {
        this.m_doctor = m_doctor;
        this.m_place = m_place;
    }

    public Date getTime() {
        return m_time;
    }

    public void setTime(Date m_time) {
        this.m_time = m_time;
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

    private Date m_time;
    private String m_place;
    private String m_doctor;
    private ArrayList<Prescription> m_prescriptions = new ArrayList<>();
}
