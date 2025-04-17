package tn.esprit.models;

import java.util.Date;

public class ConsigneMedicale {
    private int id, doctor_id, patient_id;
    private String content;
    private Date created_at = new Date(); // Initialize with the current date by default
    private Date updated_at = new Date(); // Initialize with the current date by default

    public ConsigneMedicale(int doctor_id, int patient_id, String content, Date created_at, Date updated_at) {
        this.doctor_id = doctor_id;
        this.patient_id = patient_id;
        this.content = content;
        this.created_at = (created_at != null) ? created_at : new Date(); // Ensure it's not null
        this.updated_at = (updated_at != null) ? updated_at : new Date(); // Ensure it's not null
    }

    @Override
    public String toString() {
        return "ConsigneMedicale{" +
                "id=" + id +
                ", doctor_id=" + doctor_id +
                ", patient_id=" + patient_id +
                ", content='" + content + '\'' +
                ", created_at=" + created_at +
                ", updated_at=" + updated_at +
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

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Date getCreated_at() {
        return created_at;
    }

    public void setCreated_at(Date created_at) {
        this.created_at = (created_at != null) ? created_at : new Date(); // Ensure it's not null
    }

    public Date getUpdated_at() {
        return updated_at;
    }

    public void setUpdated_at(Date updated_at) {
        this.updated_at = (updated_at != null) ? updated_at : new Date(); // Ensure it's not null
    }
}
