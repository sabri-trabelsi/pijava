package tn.esprit.models;

import java.sql.Date;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class MedicalInstruction {
    private int id;
    private int doctorId;
    private int patientId;
    private String content;
    private Timestamp createdAt;
    private Timestamp updatedAt;
    private List<Prescription> prescriptions = new ArrayList<>();


    public MedicalInstruction(int doctorId, int patientId, String content, Timestamp createdAt, Timestamp updatedAt) {
        this.doctorId = doctorId;
        this.patientId = patientId;
        this.content = content;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public MedicalInstruction() {

    }

    @Override
    public String toString() {
        return "MedicalInstruction{" +
                "id=" + id +
                ", doctorId=" + doctorId +
                ", patientId=" + patientId +
                ", content='" + content + '\'' +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getDoctorId() {
        return doctorId;
    }

    public void setDoctorId(int doctorId) {
        this.doctorId = doctorId;
    }

    public int getPatientId() {
        return patientId;
    }

    public void setPatientId(int patientId) {
        this.patientId = patientId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    public Timestamp getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Timestamp updatedAt) {
        this.updatedAt = updatedAt;
    }

    public List<Prescription> getPrescriptions() {
        return prescriptions;
    }

    public void setPrescriptions(List<Prescription> prescriptions) {
        this.prescriptions = prescriptions != null ? prescriptions : new ArrayList<>();
    }

}