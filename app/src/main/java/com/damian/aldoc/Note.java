package com.damian.aldoc;

/**
 * Created by dawid on 2017-04-23.
 */

public class Note {
    private String uid;
    private String date;
    private String mood;
    private String symptoms;
    private String medicines;
    private String reaction;

    public Note(){}

    public Note(String m_date, String m_mood, String m_symptoms, String m_medicines, String m_reaction) {
        date = m_date;
        mood = m_mood;
        symptoms = m_symptoms;
        medicines = m_medicines;
        reaction = m_reaction;
    }

    public void setDate(String m_date) {
        date = m_date;
    }
    public String getDate() {
        return date;
    }

    public void setMood(String m_mood) {
        mood = m_mood;
    }
    public String getMood() {
        return mood;
    }

    public void setSymptoms(String m_symptoms) {
        symptoms = m_symptoms;
    }
    public String getSymptoms() {
        return symptoms;
    }

    public void setMedicines(String m_medicines) {
        medicines = m_medicines;
    }
    public String getMedicines() {
        return medicines;
    }

    public void setReaction(String m_reaction) {
        reaction = m_reaction;
    }
    public String getReaction() {
        return reaction;
    }

    public void setUid(String m_uid) { uid = m_uid; }

    public String getUid() { return uid; }
}
