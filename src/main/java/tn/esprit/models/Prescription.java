package tn.esprit.models;

public class Prescription {
    private int id;
    private int medicalInstructionId;
    private int productId;
    private String dosage;
    private String duration;
    private int produitId;


    public Prescription(int medicalInstructionId, int productId, String dosage, String duration) {
        this.medicalInstructionId = medicalInstructionId;
        this.productId = productId;
        this.dosage = dosage;
        this.duration = duration;
    }

    public Prescription() {

    }

    @Override
    public String toString() {
        return "Prescription{" +
                "id=" + id +
                ", medicalInstructionId=" + medicalInstructionId +
                ", productId=" + productId +
                ", dosage='" + dosage + '\'' +
                ", duration='" + duration + '\'' +
                '}';
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getMedicalInstructionId() {
        return medicalInstructionId;
    }

    public void setMedicalInstructionId(int medicalInstructionId) {
        this.medicalInstructionId = medicalInstructionId;
    }

    public int getProductId() {
        return productId;
    }

    public void setProductId(int productId) {
        this.productId = productId;
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
    public int getProduitId() {
        return produitId;
    }

    public void setProduitId(int produitId) {
        this.produitId = produitId;
    }
}