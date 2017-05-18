package com.damian.aldoc;

/**
 * Created by dawid on 2017-04-23.
 */

public class Note {
    private String nid;
    private String date;
    private String mood;
    private String symptoms;
    private String medicines;
    private String reaction;
    private String disease;
    private Long timestamp;

    public Note(){}

    public Note(String n_date, String n_mood, String n_symptoms, String n_medicines, String n_reaction, String n_disease, Long n_timestamp) {
        date = n_date;
        mood = n_mood;
        symptoms = n_symptoms;
        medicines = n_medicines;
        reaction = n_reaction;
        disease = n_disease;
        timestamp = n_timestamp;
    }

    @Override
    public String toString()
    {
        return date + "\n" + symptoms;
    }

    public void setNid(String n_nid) { nid = n_nid; }
    public String getNid() { return nid; }

    public void setDate(String n_date) {
        date = n_date;
    }
    public String getDate() {
        return date;
    }

    public void setMood(String n_mood) {
        mood = n_mood;
    }
    public String getMood() {
        return mood;
    }

    public void setSymptoms(String n_symptoms) {
        symptoms = n_symptoms;
    }
    public String getSymptoms() {
        return symptoms;
    }

    public void setMedicines(String n_medicines) {
        medicines = n_medicines;
    }
    public String getMedicines() {
        return medicines;
    }

    public void setReaction(String n_reaction) {
        reaction = n_reaction;
    }
    public String getReaction() {
        return reaction;
    }

    public void setDisease(String n_disease) { disease = n_disease; }
    public String getDisease() { return disease; }

    public void setTimestamp(Long n_timestamp) { timestamp = n_timestamp; }
    public Long getTimestamp() { return timestamp; }
}
