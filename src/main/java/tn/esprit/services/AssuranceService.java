package tn.esprit.services;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import tn.esprit.interfaces.IService;
import tn.esprit.models.Assurance;
import tn.esprit.utils.ConnexionDB;

public class AssuranceService implements IService<Assurance> {

    private Connection cnx;
    private PreparedStatement pst;

    public AssuranceService() {
        cnx = ConnexionDB.getInstance().getCnx();
    }

    @Override
    public void ajouter(Assurance assurance) {
        String req = "INSERT INTO assurance (nom, type, compagnie, dateDebut, dateExpiration, statut) VALUES (?, ?, ?, ?, ?, ?)";
        try {
            pst = cnx.prepareStatement(req);
            pst.setString(1, assurance.getNom());
            pst.setString(2, assurance.getType());
            pst.setString(3, assurance.getCompagnie());
            pst.setDate(4, new java.sql.Date(assurance.getDateDebut().getTime()));
            pst.setDate(5, new java.sql.Date(assurance.getDateExpiration().getTime()));
            pst.setString(6, assurance.getStatut());

            pst.executeUpdate();
            System.out.println("Assurance ajoutée avec succès");
        } catch (SQLException e) {
            System.err.println("Erreur lors de l'ajout de l'assurance: " + e.getMessage());
        }
    }

    @Override
    public void modifier(Assurance assurance) {
        String req = "UPDATE assurance SET nom=?, type=?, compagnie=?, dateDebut=?, dateExpiration=?, statut=? WHERE id=?";
        try {
            pst = cnx.prepareStatement(req);
            pst.setString(1, assurance.getNom());
            pst.setString(2, assurance.getType());
            pst.setString(3, assurance.getCompagnie());
            pst.setDate(4, new java.sql.Date(assurance.getDateDebut().getTime()));
            pst.setDate(5, new java.sql.Date(assurance.getDateExpiration().getTime()));
            pst.setString(6, assurance.getStatut());
            pst.setInt(7, assurance.getId());

            pst.executeUpdate();
            System.out.println("Assurance modifiée avec succès");
        } catch (SQLException e) {
            System.err.println("Erreur lors de la modification de l'assurance: " + e.getMessage());
        }
    }

    @Override
    public void supprimer(Assurance assurance) {
        String req = "DELETE FROM assurance WHERE id=?";
        try {
            pst = cnx.prepareStatement(req);
            pst.setInt(1, assurance.getId());

            pst.executeUpdate();
            System.out.println("Assurance supprimée avec succès");
        } catch (SQLException e) {
            System.err.println("Erreur lors de la suppression de l'assurance: " + e.getMessage());
        }
    }

    @Override
    public List<Assurance> rechercher() {
        List<Assurance> assurances = new ArrayList<>();
        String req = "SELECT * FROM assurance";
        try {
            Statement st = cnx.createStatement();
            ResultSet rs = st.executeQuery(req);

            while (rs.next()) {
                Assurance assurance = new Assurance();
                assurance.setId(rs.getInt("id"));
                assurance.setNom(rs.getString("nom"));
                assurance.setType(rs.getString("type"));
                assurance.setCompagnie(rs.getString("compagnie"));
                assurance.setDateDebut(rs.getDate("dateDebut"));
                assurance.setDateExpiration(rs.getDate("dateExpiration"));
                assurance.setStatut(rs.getString("statut"));

                assurances.add(assurance);
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la récupération des assurances: " + e.getMessage());
        }
        return assurances;
    }

    @Override
    public List<Assurance> rechercher(String query) {
        List<Assurance> assurances = new ArrayList<>();
        String req = "SELECT * FROM assurance WHERE nom LIKE ? OR type LIKE ? OR compagnie LIKE ? OR statut LIKE ?";
        try {
            pst = cnx.prepareStatement(req);
            String paramQuery = "%" + query + "%";
            pst.setString(1, paramQuery);
            pst.setString(2, paramQuery);
            pst.setString(3, paramQuery);
            pst.setString(4, paramQuery);

            ResultSet rs = pst.executeQuery();

            while (rs.next()) {
                Assurance assurance = new Assurance();
                assurance.setId(rs.getInt("id"));
                assurance.setNom(rs.getString("nom"));
                assurance.setType(rs.getString("type"));
                assurance.setCompagnie(rs.getString("compagnie"));
                assurance.setDateDebut(rs.getDate("dateDebut"));
                assurance.setDateExpiration(rs.getDate("dateExpiration"));
                assurance.setStatut(rs.getString("statut"));

                assurances.add(assurance);
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la recherche d'assurances: " + e.getMessage());
        }
        return assurances;
    }

    @Override
    public Assurance rechercherById(int id) {
        String req = "SELECT * FROM assurance WHERE id=?";
        try {
            pst = cnx.prepareStatement(req);
            pst.setInt(1, id);

            ResultSet rs = pst.executeQuery();

            if (rs.next()) {
                Assurance assurance = new Assurance();
                assurance.setId(rs.getInt("id"));
                assurance.setNom(rs.getString("nom"));
                assurance.setType(rs.getString("type"));
                assurance.setCompagnie(rs.getString("compagnie"));
                assurance.setDateDebut(rs.getDate("dateDebut"));
                assurance.setDateExpiration(rs.getDate("dateExpiration"));
                assurance.setStatut(rs.getString("statut"));

                return assurance;
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la recherche d'assurance par ID: " + e.getMessage());
        }
        return null;
    }
}