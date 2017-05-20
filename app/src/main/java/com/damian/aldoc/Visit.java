package com.damian.aldoc;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Comparator;

public class Visit {

    /*Komparator do sortowania wizyt po nazwisku lekarza*/
    public static class DoctorComparator implements Comparator<Visit>
    {
        @Override
        public int compare(Visit o1, Visit o2) {
            return o1.getDoctor().compareTo(o2.getDoctor());
        }
    }

    /*Komparator do sortowania wizyt po dacie*/
    public static class DateComparator implements Comparator<Visit>
    {
        @Override
        public int compare(Visit o1, Visit o2) {
            return  o1.createCalendar().before(o2.createCalendar()) ? 1 : -1;
        }
    }

    /*Komparator do sortowania wizyt po nazwie miejsca wizyty*/
    public static class LocationComparator implements Comparator<Visit>
    {
        @Override
        public int compare(Visit o1, Visit o2) {
            return o1.getLocation().compareTo(o2.getLocation());
        }
    }

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
    * @param time - format hh:mm
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

    public Calendar createCalendar()
    {
        String date = m_date;
        String[] split_date = date.split("-");

        /*Daty sa zapisane do bazy z miesiacem powiekszonym o 1
        * wiec teraz trzeba wprowadzic poprawke*/

        Integer month = Integer.valueOf(split_date[1]);
        month -= 1;

        date = split_date[0] + "-" + month.toString() + "-" + split_date[2];

        SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyyhh:mm");

        Calendar c = Calendar.getInstance();
        try {
            c.setTime(format.parse(date + m_time));
        }catch (ParseException e){e.printStackTrace();}

        return c;
    }

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
}
