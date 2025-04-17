package tn.esprit.services;

import tn.esprit.models.ConsigneMedicale;
import tn.esprit.interfaces.IService;
import tn.esprit.utils.ConnexionDB;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ConsigneMedicaleService implements IService<ConsigneMedicale> {

    private final Connection cnx = ConnexionDB.getInstance().getCnx();

    @Override
    public void ajouter(ConsigneMedicale consigne) {
        String req = "INSERT INTO medical_instruction (doctor_id, patient_id, content, created_at, updated_at) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement pst = cnx.prepareStatement(req)) {
            pst.setInt(1, consigne.getDoctor_id());
            pst.setInt(2, consigne.getPatient_id());
            pst.setString(3, consigne.getContent());
            pst.setTimestamp(4, new Timestamp(consigne.getCreated_at().getTime()));
            pst.setTimestamp(5, new Timestamp(consigne.getUpdated_at().getTime()));
            pst.executeUpdate();
            System.out.println("✅ Consigne ajoutée.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void modifier(ConsigneMedicale consigne) {
        String req = "UPDATE medical_instruction SET doctor_id=?, patient_id=?, content=?, created_at=?, updated_at=? WHERE id=?";
        try (PreparedStatement pst = cnx.prepareStatement(req)) {
            pst.setInt(1, consigne.getDoctor_id());
            pst.setInt(2, consigne.getPatient_id());
            pst.setString(3, consigne.getContent());
            pst.setTimestamp(4, new Timestamp(consigne.getCreated_at().getTime()));
            pst.setTimestamp(5, new Timestamp(consigne.getUpdated_at().getTime()));
            pst.setInt(6, consigne.getId());
            pst.executeUpdate();
            System.out.println("✅ Consigne modifiée.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void supprimer(ConsigneMedicale consigne) {
        try (PreparedStatement pst = cnx.prepareStatement("DELETE FROM medical_instruction WHERE id=?")) {
            pst.setInt(1, consigne.getId());
            pst.executeUpdate();
            System.out.println("✅ Consigne supprimée.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public List<ConsigneMedicale> rechercher() {
        List<ConsigneMedicale> list = new ArrayList<>();
        String req = "SELECT mi.* FROM medical_instruction mi";
        try (Statement st = cnx.createStatement(); ResultSet rs = st.executeQuery(req)) {
            while (rs.next()) {
                ConsigneMedicale consigne = new ConsigneMedicale(
                        rs.getInt("doctor_id"),
                        rs.getInt("patient_id"),
                        rs.getString("content"),
                        rs.getTimestamp("created_at"),
                        rs.getTimestamp("updated_at")
                );
                list.add(consigne);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    @Override
    public List<ConsigneMedicale> rechercher(String query) {
        List<ConsigneMedicale> list = new ArrayList<>();
        String req = "SELECT mi.* FROM medical_instruction mi WHERE content LIKE ?";
        try (PreparedStatement pst = cnx.prepareStatement(req)) {
            pst.setString(1, "%" + query + "%");
            ResultSet rs = pst.executeQuery();
            while (rs.next()) {
                ConsigneMedicale consigne = new ConsigneMedicale(
                        rs.getInt("doctor_id"),
                        rs.getInt("patient_id"),
                        rs.getString("content"),
                        rs.getTimestamp("created_at"),
                        rs.getTimestamp("updated_at")
                );
                list.add(consigne);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    @Override
    public ConsigneMedicale rechercherById(int id) {
        String req = "SELECT mi.* FROM medical_instruction mi WHERE mi.id = ?";
        try (PreparedStatement pst = cnx.prepareStatement(req)) {
            pst.setInt(1, id);
            ResultSet rs = pst.executeQuery();
            if (rs.next()) {
                ConsigneMedicale consigne = new ConsigneMedicale(
                        rs.getInt("doctor_id"),
                        rs.getInt("patient_id"),
                        rs.getString("content"),
                        rs.getTimestamp("created_at"),
                        rs.getTimestamp("updated_at")
                );
                return consigne;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public int getPatientIdByName(String patient) {
        String req = "SELECT id FROM users WHERE CONCAT(first_name, ' ', last_name) LIKE ?";
        try (PreparedStatement pst = cnx.prepareStatement(req)) {
            pst.setString(1, patient);
            ResultSet rs = pst.executeQuery();
            if (rs.next()) {
                return rs.getInt("id");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public int saveAndReturnGeneratedId(ConsigneMedicale consigne) {
        String req = "INSERT INTO medical_instruction (doctor_id, patient_id, content, created_at, updated_at) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement pst = cnx.prepareStatement(req, Statement.RETURN_GENERATED_KEYS)) {
            pst.setInt(1, consigne.getDoctor_id());
            pst.setInt(2, consigne.getPatient_id());
            pst.setString(3, consigne.getContent());
            pst.setTimestamp(4, new Timestamp(consigne.getCreated_at().getTime()));
            pst.setTimestamp(5, new Timestamp(consigne.getUpdated_at().getTime()));
            pst.executeUpdate();

            ResultSet rs = pst.getGeneratedKeys();
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }


    public List<String> getAllPatients() {
        List<String> patients = new ArrayList<>();
        String req = "SELECT CONCAT(name, ' ', last_name) AS full_name FROM user WHERE role = 'Patient'";
        try (Statement st = cnx.createStatement(); ResultSet rs = st.executeQuery(req)) {
            while (rs.next()) {
                patients.add(rs.getString("full_name"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return patients;
    }
}
