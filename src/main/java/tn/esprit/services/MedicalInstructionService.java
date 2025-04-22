package tn.esprit.services;

import tn.esprit.interfaces.IService;
import tn.esprit.models.MedicalInstruction;
import tn.esprit.models.Prescription;
import tn.esprit.utils.ConnexionDB;

import java.sql.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MedicalInstructionService implements IService<MedicalInstruction> {

    private Connection cnx = ConnexionDB.getInstance().getCnx();

    @Override
    public void ajouter(MedicalInstruction mi) {
        String req = "INSERT INTO medical_instruction (doctor_id, patient_id, content, created_at) VALUES (?,?,?,?)";
        try (PreparedStatement ps = cnx.prepareStatement(req, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, mi.getDoctorId());
            ps.setInt(2, mi.getPatientId());
            ps.setString(3, mi.getContent());
            ps.setTimestamp(4, new Timestamp(System.currentTimeMillis()));

            ps.executeUpdate();

            // Get generated ID for prescriptions
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    int generatedId = rs.getInt(1);
                    addPrescriptions(mi.getPrescriptions(), generatedId);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error adding medical instruction: " + e.getMessage());
        }
    }

    @Override
    public void modifier(MedicalInstruction mi) {
        String req = "UPDATE medical_instruction SET content=?, updated_at=? WHERE id=?";
        try (PreparedStatement ps = cnx.prepareStatement(req)) {
            ps.setString(1, mi.getContent());
            ps.setTimestamp(2, new Timestamp(System.currentTimeMillis()));
            ps.setInt(3, mi.getId());
            ps.executeUpdate();

            // Update prescriptions
            updatePrescriptions(mi.getPrescriptions(), mi.getId());
        } catch (SQLException e) {
            System.err.println("Error updating medical instruction: " + e.getMessage());
        }
    }

    @Override
    public void supprimer(MedicalInstruction mi) {
        String req = "DELETE FROM medical_instruction WHERE id=?";
        try (PreparedStatement ps = cnx.prepareStatement(req)) {
            ps.setInt(1, mi.getId());
            ps.executeUpdate();

            // Delete associated prescriptions
            deletePrescriptions(mi.getId());
        } catch (SQLException e) {
            System.err.println("Error deleting medical instruction: " + e.getMessage());
        }
    }

    @Override
    public List<MedicalInstruction> rechercher() {
        List<MedicalInstruction> instructions = new ArrayList<>();
        String req = "SELECT * FROM medical_instruction";
        try (Statement st = cnx.createStatement();
             ResultSet rs = st.executeQuery(req)) {

            while (rs.next()) {
                MedicalInstruction mi = mapResultSetToMI(rs);
                mi.setPrescriptions(getPrescriptionsForMI(mi.getId()));
                instructions.add(mi);
            }
        } catch (SQLException e) {
            System.err.println("Error fetching medical instructions: " + e.getMessage());
        }
        return instructions;
    }

    @Override
    public List<MedicalInstruction> rechercher(String query) {
        List<MedicalInstruction> instructions = new ArrayList<>();
        String req = "SELECT * FROM medical_instruction WHERE content LIKE ?";
        try (PreparedStatement ps = cnx.prepareStatement(req)) {
            ps.setString(1, "%" + query + "%");
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    MedicalInstruction mi = mapResultSetToMI(rs);
                    mi.setPrescriptions(getPrescriptionsForMI(mi.getId()));
                    instructions.add(mi);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error searching medical instructions: " + e.getMessage());
        }
        return instructions;
    }

    @Override
    public MedicalInstruction rechercherById(int id) {
        String req = "SELECT * FROM medical_instruction WHERE id=?";
        try (PreparedStatement ps = cnx.prepareStatement(req)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    MedicalInstruction mi = mapResultSetToMI(rs);
                    mi.setPrescriptions(getPrescriptionsForMI(id));
                    return mi;
                }
            }
        } catch (SQLException e) {
            System.err.println("Error fetching medical instruction by ID: " + e.getMessage());
        }
        return null;
    }

    private MedicalInstruction mapResultSetToMI(ResultSet rs) throws SQLException {
        MedicalInstruction mi = new MedicalInstruction();
        mi.setId(rs.getInt("id"));
        mi.setDoctorId(rs.getInt("doctor_id"));
        mi.setPatientId(rs.getInt("patient_id"));
        mi.setContent(rs.getString("content"));
        mi.setCreatedAt(rs.getTimestamp("created_at"));
        mi.setUpdatedAt(rs.getTimestamp("updated_at"));
        List<Prescription> prescriptions = getPrescriptionsForMI(mi.getId());
        mi.setPrescriptions(prescriptions != null ? prescriptions : new ArrayList<>());
        return mi;
    }

    // Prescription Management
    private void addPrescriptions(List<Prescription> prescriptions, int miId) {
        String req = "INSERT INTO prescription (medical_instruction_id, produit_id, dosage, duration) VALUES (?,?,?,?)";
        try (PreparedStatement ps = cnx.prepareStatement(req)) {
            for (Prescription p : prescriptions) {
                ps.setInt(1, miId);
                ps.setInt(2, p.getProduitId());
                ps.setString(3, p.getDosage());
                ps.setString(4, p.getDuration());
                ps.addBatch();
            }
            ps.executeBatch();
        } catch (SQLException e) {
            System.err.println("Error adding prescriptions: " + e.getMessage());
        }
    }

    private void updatePrescriptions(List<Prescription> prescriptions, int miId) {
        deletePrescriptions(miId);
        addPrescriptions(prescriptions, miId);
    }

    private void deletePrescriptions(int miId) {
        String req = "DELETE FROM prescription WHERE medical_instruction_id=?";
        try (PreparedStatement ps = cnx.prepareStatement(req)) {
            ps.setInt(1, miId);
            ps.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error deleting prescriptions: " + e.getMessage());
        }
    }

    private List<Prescription> getPrescriptionsForMI(int miId) {
        List<Prescription> prescriptions = new ArrayList<>();
        String req = "SELECT * FROM prescription WHERE medical_instruction_id=?";
        try (PreparedStatement ps = cnx.prepareStatement(req)) {
            ps.setInt(1, miId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Prescription p = new Prescription();
                    p.setId(rs.getInt("id"));
                    p.setMedicalInstructionId(miId);
                    p.setProduitId(rs.getInt("produit_id"));
                    p.setDosage(rs.getString("dosage"));
                    p.setDuration(rs.getString("duration"));
                    prescriptions.add(p);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error fetching prescriptions: " + e.getMessage());
        }
        return prescriptions;
    }

    // Additional Doctor-specific methods
    public List<MedicalInstruction> getInstructionsByDoctor(int doctorId) {
        List<MedicalInstruction> instructions = new ArrayList<>();
        String req = "SELECT * FROM medical_instruction WHERE doctor_id=?";
        try (PreparedStatement ps = cnx.prepareStatement(req)) {
            ps.setInt(1, doctorId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    MedicalInstruction mi = mapResultSetToMI(rs);
                    mi.setPrescriptions(getPrescriptionsForMI(mi.getId()));
                    instructions.add(mi);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error fetching doctor's instructions: " + e.getMessage());
        }
        return instructions;
    }
    // Add this method to MedicalInstructionService
    public List<MedicalInstruction> getByDoctor(int doctorId) {
        String query = "SELECT * FROM medical_instruction WHERE doctor_id = ?";
        try (PreparedStatement ps = cnx.prepareStatement(query)) {
            ps.setInt(1, doctorId);
            ResultSet rs = ps.executeQuery();

            List<MedicalInstruction> instructions = new ArrayList<>();
            while (rs.next()) {
                instructions.add(mapResultSetToMI(rs));
            }
            return instructions;
        } catch (SQLException e) {
            System.err.println("Error fetching instructions: " + e.getMessage());
        }
        return Collections.emptyList();
    }
}