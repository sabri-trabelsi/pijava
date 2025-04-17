package tn.esprit.services;

import tn.esprit.models.OrdonnanceMedicale;
import tn.esprit.interfaces.IService;
import tn.esprit.utils.ConnexionDB;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class OrdonnanceMedicaleService implements IService<OrdonnanceMedicale> {

    private final Connection cnx = ConnexionDB.getInstance().getCnx();

    @Override
    public void ajouter(OrdonnanceMedicale o) {
        String req = "INSERT INTO prescription (medical_instruction_id, produit_id, dosage, duration) VALUES (?, ?, ?, ?)";
        try (PreparedStatement pst = cnx.prepareStatement(req)) {
            pst.setInt(1, o.getMedical_instruction_id());
            pst.setInt(2, o.getProduit_id());
            pst.setString(3, o.getDosage());
            pst.setString(4, o.getDuration());
            pst.executeUpdate();
            System.out.println("✅ Ordonnance ajoutée.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void modifier(OrdonnanceMedicale o) {
        String req = "UPDATE prescription SET medical_instruction_id=?, produit_id=?, dosage=?, duration=? WHERE id=?";
        try (PreparedStatement pst = cnx.prepareStatement(req)) {
            pst.setInt(1, o.getMedical_instruction_id());
            pst.setInt(2, o.getProduit_id());
            pst.setString(3, o.getDosage());
            pst.setString(4, o.getDuration());
            pst.setInt(5, o.getId());
            pst.executeUpdate();
            System.out.println("✅ Ordonnance modifiée.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void supprimer(OrdonnanceMedicale o) {
        try (PreparedStatement pst = cnx.prepareStatement("DELETE FROM prescription WHERE id=?")) {
            pst.setInt(1, o.getId());
            pst.executeUpdate();
            System.out.println("✅ Ordonnance supprimée.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public List<OrdonnanceMedicale> rechercher() {
        List<OrdonnanceMedicale> list = new ArrayList<>();
        String req = "SELECT * FROM prescription";
        try (Statement st = cnx.createStatement(); ResultSet rs = st.executeQuery(req)) {
            while (rs.next()) {
                list.add(new OrdonnanceMedicale(
                        rs.getInt("medical_instruction_id"),
                        rs.getInt("produit_id"),
                        rs.getString("dosage"),
                        rs.getString("duration")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    @Override
    public List<OrdonnanceMedicale> rechercher(String query) {
        List<OrdonnanceMedicale> list = new ArrayList<>();
        String req = "SELECT * FROM prescription WHERE dosage LIKE ? OR duration LIKE ?";
        try (PreparedStatement pst = cnx.prepareStatement(req)) {
            pst.setString(1, "%" + query + "%");
            pst.setString(2, "%" + query + "%");
            ResultSet rs = pst.executeQuery();
            while (rs.next()) {
                list.add(new OrdonnanceMedicale(
                        rs.getInt("medical_instruction_id"),
                        rs.getInt("produit_id"),
                        rs.getString("dosage"),
                        rs.getString("duration")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    @Override
    public OrdonnanceMedicale rechercherById(int id) {
        try (PreparedStatement pst = cnx.prepareStatement("SELECT * FROM prescription WHERE id=?")) {
            pst.setInt(1, id);
            ResultSet rs = pst.executeQuery();
            if (rs.next()) {
                return new OrdonnanceMedicale(
                        rs.getInt("medical_instruction_id"),
                        rs.getInt("produit_id"),
                        rs.getString("dosage"),
                        rs.getString("duration")
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<String> getAllProducts() {
        List<String> products = new ArrayList<>();
        String req = "SELECT nom FROM produit";
        try (Statement st = cnx.createStatement(); ResultSet rs = st.executeQuery(req)) {
            while (rs.next()) {
                products.add(rs.getString("nom"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return products;
    }

    public void save(OrdonnanceMedicale ordonnance) {
        String req = "INSERT INTO prescription (medical_instruction_id, produit_id, dosage, duration) VALUES (?, ?, ?, ?)";
        try (PreparedStatement pst = cnx.prepareStatement(req, Statement.RETURN_GENERATED_KEYS)) {
            pst.setInt(1, ordonnance.getMedical_instruction_id());
            pst.setInt(2, ordonnance.getProduit_id());
            pst.setString(3, ordonnance.getDosage());
            pst.setString(4, ordonnance.getDuration());
            pst.executeUpdate();

            ResultSet rs = pst.getGeneratedKeys();
            if (rs.next()) {
                ordonnance.setId(rs.getInt(1));
            }
            System.out.println("✅ Ordonnance sauvegardée avec succès.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public int getProductIdByName(String product) {
        String req = "SELECT id FROM produit WHERE nom = ?";
        try (PreparedStatement pst = cnx.prepareStatement(req)) {
            pst.setString(1, product);
            ResultSet rs = pst.executeQuery();
            if (rs.next()) {
                return rs.getInt("id");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1; // Return -1 if the product is not found
    }
}
