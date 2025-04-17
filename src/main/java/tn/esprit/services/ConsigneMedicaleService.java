package tn.esprit.services;

import tn.esprit.models.ConsigneMedicale;
import tn.esprit.interfaces.IService;
import tn.esprit.utils.ConnexionDB;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ConsigneMedicaleService implements IService<ConsigneMedicale> {

    private final Connection cnx = ConnexionDB.getInstance().getCnx();
    private final UserService userService = new UserService(); // Reference to UserService

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
                String patientFullName = userService.getPatientFullNameById(consigne.getPatient_id());
                System.out.println("Patient: " + patientFullName);
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
                String patientFullName = userService.getPatientFullNameById(consigne.getPatient_id());
                System.out.println("Patient: " + patientFullName);
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
                String patientFullName = userService.getPatientFullNameById(consigne.getPatient_id());
                System.out.println("Patient: " + patientFullName);
                return consigne;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<String> getAllPatients() {
        return userService.getAllPatientFullNames();
    }

    public void save(ConsigneMedicale consigne) {
        String req = "INSERT INTO medical_instruction (doctor_id, patient_id, content, created_at, updated_at) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement pst = cnx.prepareStatement(req, Statement.RETURN_GENERATED_KEYS)) {
            pst.setInt(1, consigne.getDoctor_id());
            pst.setInt(2, consigne.getPatient_id());
            pst.setString(3, consigne.getContent());
            pst.setTimestamp(4, new Timestamp(consigne.getCreated_at().getTime()));
            pst.setTimestamp(5, new Timestamp(consigne.getUpdated_at().getTime()));

            pst.executeUpdate();

            // Retrieve and set the generated ID
            ResultSet generatedKeys = pst.getGeneratedKeys();
            if (generatedKeys.next()) {
                consigne.setId(generatedKeys.getInt(1));
            }

            System.out.println("✅ Consigne médicale sauvegardée avec succès.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public int getPatientIdByName(String patientFullName) {
        return userService.getPatientIdByName(patientFullName);
    }

    public String getPatientNameById(int patientId) {
        return userService.getPatientFullNameById(patientId);
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

            // Retrieve and return the generated ID
            ResultSet generatedKeys = pst.getGeneratedKeys();
            if (generatedKeys.next()) {
                return generatedKeys.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1; // Return -1 if insertion or ID retrieval fails
    }

    public boolean isDoctorAssignedToPatient(int doctorId, int patientId) {
        String query = "SELECT EXISTS(SELECT 1 FROM medical_instruction WHERE doctor_id = ? AND patient_id = ?)";
        try (PreparedStatement pst = cnx.prepareStatement(query)) {
            pst.setInt(1, doctorId);
            pst.setInt(2, patientId);
            ResultSet rs = pst.executeQuery();
            if (rs.next()) {
                return rs.getBoolean(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
}
