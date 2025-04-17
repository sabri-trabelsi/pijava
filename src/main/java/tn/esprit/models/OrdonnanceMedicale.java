package tn.esprit.models;

import java.util.Date;

public class OrdonnanceMedicale {
    private int id, medical_instruction_id, produit_id;
    private String dosage, duration, product_name;

    public OrdonnanceMedicale(int medical_instruction_id, int produit_id, String dosage, String duration, String product_name) {
        this.medical_instruction_id = medical_instruction_id;
        this.produit_id = produit_id;
        this.dosage = dosage;
        this.duration = duration;
        this.product_name = product_name;
    }

    public OrdonnanceMedicale() {

    }

    public OrdonnanceMedicale(int medical_instruction_id, int produit_id, String dosage, String duration) {
    }

    @Override
    public String toString() {
        return "OrdonnanceMedicale{" +
                "id=" + id +
                ", medical_instruction_id=" + medical_instruction_id +
                ", produit_id=" + produit_id +
                ", dosage='" + dosage + '\'' +
                ", duration='" + duration + '\'' +
                ", product_name='" + product_name + '\'' +
                '}';
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getMedical_instruction_id() {
        return medical_instruction_id;
    }

    public void setMedical_instruction_id(int medical_instruction_id) {
        this.medical_instruction_id = medical_instruction_id;
    }

    public int getProduit_id() {
        return produit_id;
    }

    public void setProduit_id(int produit_id) {
        this.produit_id = produit_id;
    }

    public String getDosage() {
        return dosage;
    }

    public void setDosage(String dosage) {
        this.dosage = dosage;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public String getProduct_name() {
        return product_name;
    }

    public void setProduct_name(String product_name) {
        this.product_name = product_name;
    }

    public String getProduit() {
        return product_name;
    }

    public Date getDate() {
        return new Date(); // Replace with appropriate logic to return the correct date
    }

    public String getDuree() {
        return duration;
    }

    public String getIdPatient() {
        return String.valueOf(medical_instruction_id); // Replace with correct logic if needed
    }

    public void setProduit(String text) {
        if (text == null || text.isEmpty()) {
            throw new IllegalArgumentException("Le champ 'Produit' ne peut pas être vide");
        }
        this.product_name = text.trim();
    }

    public void setDuree(String text) {
        if (text == null || text.isEmpty()) {
            throw new IllegalArgumentException("Duration cannot be null or empty");
        }
        this.duration = text;
    }

    public void setIdPatient(int i) {
        if (i <= 0) {
            throw new IllegalArgumentException("Patient ID must be a positive integer");
        }
        this.medical_instruction_id = i;
    }

    public void setIdDocteur(int idDocteur) {
        if (idDocteur <= 0) {
            throw new IllegalArgumentException("Doctor ID must be a positive integer");
        }
        this.medical_instruction_id = idDocteur;
    }

    public void setDate(Date date) {
        if (date == null) {
            throw new IllegalArgumentException("Date cannot be null");
        }
        this.id = new Date().hashCode(); // Example logic to associate the date uniquely
    }
}
