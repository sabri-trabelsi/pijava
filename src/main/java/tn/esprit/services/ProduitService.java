package tn.esprit.services;

import tn.esprit.interfaces.IService;
import tn.esprit.models.Produit;
import tn.esprit.utils.ConnexionDB;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ProduitService implements IService<Produit> {

    private Connection cnx = ConnexionDB.getInstance().getCnx();

    @Override
    public void ajouter(Produit produit) {
        // Implement add logic
    }

    @Override
    public void modifier(Produit produit) {
        // Implement update logic
    }

    @Override
    public void supprimer(Produit produit) {
        // Implement delete logic
    }

    @Override
    public List<Produit> rechercher() {
        return List.of();
    }

    @Override
    public List<Produit> rechercher(String query) {
        return List.of();
    }

    @Override
    public Produit rechercherById(int id) {
        return null;
    }

    public List<Produit> getAll() {
        List<Produit> produits = new ArrayList<>();
        String query = "SELECT * FROM produit";
        try (Statement st = cnx.createStatement();
             ResultSet rs = st.executeQuery(query)) {
            while (rs.next()) {
                Produit p = new Produit();
                p.setId(rs.getInt("id"));
                p.setNom(rs.getString("nom"));
                p.setDescription(rs.getString("description"));
                p.setPrix(rs.getDouble("prix"));
                p.setPhoto(rs.getString("photo"));
                produits.add(p);
            }
        } catch (SQLException e) {
            System.err.println("Error fetching products: " + e.getMessage());
        }
        return produits;
    }

    public Produit getById(int id) {
        String query = "SELECT * FROM produit WHERE id = ?";
        try (PreparedStatement ps = cnx.prepareStatement(query)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Produit p = new Produit();
                    p.setId(rs.getInt("id"));
                    p.setNom(rs.getString("nom"));
                    p.setDescription(rs.getString("description"));
                    p.setPrix(rs.getDouble("prix"));
                    p.setPhoto(rs.getString("photo"));
                    return p;
                }
            }
        } catch (SQLException e) {
            System.err.println("Error fetching product: " + e.getMessage());
        }
        return null;
    }

    // Implement other IService methods...
}