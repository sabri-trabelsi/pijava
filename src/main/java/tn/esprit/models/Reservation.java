package tn.esprit.models;

import java.sql.Date;
import java.sql.Timestamp;

public class Reservation {
    private int id;
    private int doctor_id;
    private int patient_id;
    private String subject;
    private Timestamp date_res;
    private String statut; // PENDING, ACCEPTED, REFUSED

    // Constructors
    public Reservation() {
    }

    public Reservation(int doctor_id, int patient_id, String subject, Timestamp date_res) {
        this.doctor_id = doctor_id;
        this.patient_id = patient_id;
        this.subject = subject;
        this.date_res = date_res;
        this.statut = "PENDING";
    }

    @Override
    public String toString() {
        return "Reservation{" +
                "id=" + id +
                ", doctor_id=" + doctor_id +
                ", patient_id=" + patient_id +
                ", subject='" + subject + '\'' +
                ", date_res=" + date_res +
                ", statut='" + statut + '\'' +
                '}';
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getDoctor_id() {
        return doctor_id;
    }

    public void setDoctor_id(int doctor_id) {
        this.doctor_id = doctor_id;
    }

    public int getPatient_id() {
        return patient_id;
    }

    public void setPatient_id(int patient_id) {
        this.patient_id = patient_id;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public Timestamp getDate_res() {
        return date_res;
    }

    public void setDate_res(Timestamp date_res) {
        this.date_res = date_res;
    }

    public String getStatut() {
        return statut;
    }

    public void setStatut(String statut) {
        this.statut = statut;
    }

    public String getDoctor() {
        return "Dr. " + doctor_id; // Replace or enhance this logic based on the actual requirement for fetching doctor details
    }

    public void setDoctor(String value) {
        try {
            if (value != null && !value.isEmpty() && value.startsWith("Dr. ")) {
                this.doctor_id = Integer.parseInt(value.replace("Dr. ", "").trim());
            } else {
                throw new IllegalArgumentException("Invalid doctor value: " + value);
            }
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Doctor ID must be numeric: " + value, e);
        }
    }


    public String getDoctorName() {
        // Simulating fetching the doctor's name
        // Replace this with actual logic to retrieve the name from the database or service
        return "Doctor Name for ID: " + doctor_id;
    }
}
