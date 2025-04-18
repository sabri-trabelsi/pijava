package tn.esprit.models;

import java.util.Date;

public class Assurance {
    private int id;
    private String nom;
    private String type;
    private String compagnie;
    private Date dateDebut;
    private Date dateExpiration;
    private String statut;

    // Constructeur par défaut
    public Assurance() {
    }

    // Constructeur avec paramètres
    public Assurance(int id, String nom, String type, String compagnie, Date dateDebut, Date dateExpiration, String statut) {
        this.id = id;
        this.nom = nom;
        this.type = type;
        this.compagnie = compagnie;
        this.dateDebut = dateDebut;
        this.dateExpiration = dateExpiration;
        this.statut = statut;
    }

    // Constructeur sans id (pour nouvelles entrées)
    public Assurance(String nom, String type, String compagnie, Date dateDebut, Date dateExpiration, String statut) {
        this.nom = nom;
        this.type = type;
        this.compagnie = compagnie;
        this.dateDebut = dateDebut;
        this.dateExpiration = dateExpiration;
        this.statut = statut;
    }

    // Getters et Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getCompagnie() {
        return compagnie;
    }

    public void setCompagnie(String compagnie) {
        this.compagnie = compagnie;
    }

    public Date getDateDebut() {
        return dateDebut;
    }

    public void setDateDebut(Date dateDebut) {
        this.dateDebut = dateDebut;
    }

    public Date getDateExpiration() {
        return dateExpiration;
    }

    public void setDateExpiration(Date dateExpiration) {
        this.dateExpiration = dateExpiration;
    }

    public String getStatut() {
        return statut;
    }

    public void setStatut(String statut) {
        this.statut = statut;
    }

    @Override
    public String toString() {
        return "Assurance{" +
                "id=" + id +
                ", nom='" + nom + '\'' +
                ", type='" + type + '\'' +
                ", compagnie='" + compagnie + '\'' +
                ", dateDebut=" + dateDebut +
                ", dateExpiration=" + dateExpiration +
                ", statut='" + statut + '\'' +
                '}';
    }
}