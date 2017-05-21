package com.damian.aldoc.visits;

public class PrescriptionEntry
{
    public PrescriptionEntry(){}

    public PrescriptionEntry(String prescription_uid, String medicine_name, String dose_rate)
    {
        m_prescription_uid = prescription_uid;
        m_medicine_name = medicine_name;
        m_dose_rate = dose_rate;
    }

    public String getUid() { return m_uid; }

    public void setUid(String uid) { m_uid = uid; }

    public String getPrescriptionUid() { return m_prescription_uid; }

    public void setPrescriptionUid(String prescription_uid) { m_prescription_uid = prescription_uid; }

    public String getMedicineName() { return m_medicine_name; }

    public void setMedicineName(String medicine_name) { m_medicine_name = medicine_name; }

    public String getDoseRate() { return m_dose_rate; }

    public void setDoseRate(String dose_rate) { m_dose_rate = dose_rate; }

    @Override
    public String toString() {
        return m_medicine_name + "\n" + m_dose_rate;
    }

    private String m_uid; // uid wpisu w bazie
    private String m_prescription_uid; // uid recepty (w bazie), ktorej dotyczy dany wpis
    private String m_medicine_name; // nazwa leku z wpisu
    private String m_dose_rate; // dawkowanie leku
}

