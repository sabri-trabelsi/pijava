package tn.esprit.services;

import tn.esprit.interfaces.IService;
import tn.esprit.models.Reservation;
import tn.esprit.utils.ConnexionDB;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ReservationService implements IService<Reservation> {
    private Connection cnx = ConnexionDB.getInstance().getCnx();

    @Override
    public void ajouter(Reservation reservation) {
        String req = "INSERT INTO reservation(doctor_id, patient_id, subject, date_res, statut) VALUES (?,?,?,?,?)";
        try (PreparedStatement ps = cnx.prepareStatement(req)) {
            ps.setInt(1, reservation.getDoctor_id());
            ps.setInt(2, reservation.getPatient_id());
            ps.setString(3, reservation.getSubject());
            ps.setTimestamp(4, reservation.getDate_res());
            ps.setString(5, reservation.getStatut());
            ps.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error adding reservation: " + e.getMessage());
        }
    }

    @Override
    public void modifier(Reservation reservation) {
        String req = "UPDATE reservation SET doctor_id=?, patient_id=?, subject=?, date_res=?, statut=? WHERE id=?";
        try (PreparedStatement ps = cnx.prepareStatement(req)) {
            ps.setInt(1, reservation.getDoctor_id());
            ps.setInt(2, reservation.getPatient_id());
            ps.setString(3, reservation.getSubject());
            ps.setTimestamp(4, reservation.getDate_res());
            ps.setString(5, reservation.getStatut());
            ps.setInt(6, reservation.getId());
            ps.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error updating reservation: " + e.getMessage());
        }
    }

    @Override
    public void supprimer(Reservation reservation) {
        String req = "DELETE FROM reservation WHERE id=?";
        try (PreparedStatement ps = cnx.prepareStatement(req)) {
            ps.setInt(1, reservation.getId());
            ps.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error deleting reservation: " + e.getMessage());
        }
    }

    @Override
    public List<Reservation> rechercher() {
        List<Reservation> reservations = new ArrayList<>();
        String req = "SELECT * FROM reservation";
        try (Statement st = cnx.createStatement();
             ResultSet rs = st.executeQuery(req)) {
            while (rs.next()) {
                Reservation reservation = new Reservation();
                reservation.setId(rs.getInt("id"));
                reservation.setDoctor_id(rs.getInt("doctor_id"));
                reservation.setPatient_id(rs.getInt("patient_id"));
                reservation.setSubject(rs.getString("subject"));
                reservation.setDate_res(rs.getTimestamp("date_res"));
                reservation.setStatut(rs.getString("statut"));
                reservations.add(reservation);
            }
        } catch (SQLException e) {
            System.err.println("Error fetching reservations: " + e.getMessage());
        }
        return reservations;
    }

    @Override
    public List<Reservation> rechercher(String query) {
        List<Reservation> reservations = new ArrayList<>();
        String req = "SELECT * FROM reservation WHERE subject LIKE ? OR statut LIKE ?";
        try (PreparedStatement ps = cnx.prepareStatement(req)) {
            ps.setString(1, "%" + query + "%");
            ps.setString(2, "%" + query + "%");
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Reservation reservation = new Reservation();
                reservation.setId(rs.getInt("id"));
                reservation.setDoctor_id(rs.getInt("doctor_id"));
                reservation.setPatient_id(rs.getInt("patient_id"));
                reservation.setSubject(rs.getString("subject"));
                reservation.setDate_res(rs.getTimestamp("date_res"));
                reservation.setStatut(rs.getString("statut"));
                reservations.add(reservation);
            }
        } catch (SQLException e) {
            System.err.println("Error searching reservations with query: " + e.getMessage());
        }
        return reservations;
    }

    @Override
    public Reservation rechercherById(int id) {
        String req = "SELECT * FROM reservation WHERE id=?";
        try (PreparedStatement ps = cnx.prepareStatement(req)) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                Reservation reservation = new Reservation();
                reservation.setId(rs.getInt("id"));
                reservation.setDoctor_id(rs.getInt("doctor_id"));
                reservation.setPatient_id(rs.getInt("patient_id"));
                reservation.setSubject(rs.getString("subject"));
                reservation.setDate_res(rs.getTimestamp("date_res"));
                reservation.setStatut(rs.getString("statut"));
                return reservation;
            }
        } catch (SQLException e) {
            System.err.println("Error searching reservation by ID: " + e.getMessage());
        }
        return null;
    }



    public boolean isSlotAvailable(int doctorId, Timestamp date) {
        String req = "SELECT COUNT(*) FROM reservation WHERE doctor_id=? AND date_res=? AND statut != 'REFUSED'";
        try (PreparedStatement ps = cnx.prepareStatement(req)) {
            ps.setInt(1, doctorId);
            ps.setTimestamp(2, date);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getInt(1) == 0;
        } catch (SQLException e) {
            System.err.println("Error checking slot: " + e.getMessage());
        }
        return false;
    }



    public void deleteReservation(Reservation reservation) {
        String req = "DELETE FROM reservation WHERE id=?";
        try (PreparedStatement ps = cnx.prepareStatement(req)) {
            ps.setInt(1, reservation.getId());
            ps.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error deleting reservation: " + e.getMessage());
        }
    }



    public void updateReservation(Reservation reservation) {
        String req = "UPDATE reservation SET doctor_id=?, patient_id=?, subject=?, date_res=?, statut=? WHERE id=?";
        try (PreparedStatement ps = cnx.prepareStatement(req)) {
            ps.setInt(1, reservation.getDoctor_id());
            ps.setInt(2, reservation.getPatient_id());
            ps.setString(3, reservation.getSubject());
            ps.setTimestamp(4, reservation.getDate_res());
            ps.setString(5, reservation.getStatut());
            ps.setInt(6, reservation.getId());
            ps.executeUpdate();
            System.out.println("Reservation updated successfully with ID: " + reservation.getId());
        } catch (SQLException e) {
            System.err.println("Error updating reservation: " + e.getMessage());
        }
    }

    public void addReservation(Reservation reservation) {
        if (reservation == null) {
            System.err.println("Error: reservation object is null");
            return;
        }

        // Validate input fields
        if (reservation.getDoctor_id() <= 0 || reservation.getPatient_id() <= 0 ||
                reservation.getSubject() == null || reservation.getSubject().isEmpty() ||
                reservation.getDate_res() == null) {
            System.err.println("Error: Missing or invalid reservation details");
            return;
        }

        // Check availability
        if (!isSlotAvailable(reservation.getDoctor_id(), reservation.getDate_res())) {
            System.err.println("Error: Slot is not available for the selected doctor and date");
            return;
        }

        // Insert reservation into the database
        String req = "INSERT INTO reservation(doctor_id, patient_id, subject, date_res, statut) VALUES (?,?,?,?,?)";
        try (PreparedStatement ps = cnx.prepareStatement(req)) {
            ps.setInt(1, reservation.getDoctor_id());
            ps.setInt(2, reservation.getPatient_id());
            ps.setString(3, reservation.getSubject());
            ps.setTimestamp(4, reservation.getDate_res());
            ps.setString(5, reservation.getStatut());
            ps.executeUpdate();
            System.out.println("Reservation added successfully with subject: " + reservation.getSubject());
        } catch (SQLException e) {
            System.err.println("Error adding reservation: " + e.getMessage());
        }
    }

    public boolean isDoctorAvailable(int doctorId, Date date) {
        String req = "SELECT COUNT(*) FROM reservation WHERE doctor_id = ? AND date_res = ? AND statut != 'REFUSED'";
        try (PreparedStatement ps = cnx.prepareStatement(req)) {
            ps.setInt(1, doctorId);
            ps.setTimestamp(2, new Timestamp(date.getTime()));
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) == 0; // If count is 0, the slot is available
            }
        } catch (SQLException e) {
            System.err.println("Error checking doctor availability: " + e.getMessage());
        }
        return false;
    }

    public Object getAllReservationsWithDoctorDetails() {
        List<Reservation> reservations = new ArrayList<>();
        String req = "SELECT r.*, d.name AS doctor_name, d.last_name AS doctor_last_name "
                + "FROM reservation r "
                + "JOIN user d ON r.doctor_id = d.id";
        try (Statement st = cnx.createStatement();
             ResultSet rs = st.executeQuery(req)) {
            while (rs.next()) {
                Reservation reservation = new Reservation();
                reservation.setId(rs.getInt("id"));
                reservation.setDoctor_id(rs.getInt("doctor_id"));
                reservation.setPatient_id(rs.getInt("patient_id"));
                reservation.setSubject(rs.getString("subject"));
                reservation.setDate_res(rs.getTimestamp("date_res"));
                reservation.setStatut(rs.getString("statut"));
                reservation.setDoctor("Dr. " + rs.getString("doctor_name") + " " + rs.getString("doctor_last_name"));
                reservations.add(reservation);
            }
        } catch (SQLException e) {
            System.err.println("Error fetching reservations with doctor details: " + e.getMessage());
        }
        return reservations;
    }





    public Map<String, Integer> getDoctorNameIdMap() {
        Map<String, Integer> doctorNameIdMap = new HashMap<>();
        String req = "SELECT id, name, last_name FROM user WHERE role = 'DOCTOR'";
        try (Statement statement = cnx.createStatement();
             ResultSet resultSet = statement.executeQuery(req)) {
            while (resultSet.next()) {
                String fullName = "Dr. " + resultSet.getString("name") + " " + resultSet.getString("last_name");
                int doctorId = resultSet.getInt("id");
                doctorNameIdMap.put(fullName, doctorId);
            }
        } catch (SQLException e) {
            System.err.println("Error fetching doctor names and IDs: " + e.getMessage());
        }
        return doctorNameIdMap;
    }

    public String getDoctorNameById(int doctorId) {
        String req = "SELECT name, last_name FROM user WHERE id = ? AND role = 'DOCTOR'";
        try (PreparedStatement ps = cnx.prepareStatement(req)) {
            ps.setInt(1, doctorId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return "Dr. " + rs.getString("name") + " " + rs.getString("last_name");
            }
        } catch (SQLException e) {
            System.err.println("Error fetching doctor name by ID: " + e.getMessage());
        }
        return "Unknown Doctor";
    }

    public Object searchReservationsByDoctorName(String query) {
        List<Reservation> reservations = new ArrayList<>();
        String req = "SELECT r.*, u.name AS doctor_name, u.last_name AS doctor_last_name "
                + "FROM reservation r "
                + "JOIN user u ON r.doctor_id = u.id "
                + "WHERE u.role = 'DOCTOR' AND (u.name LIKE ? OR u.last_name LIKE ?)";
        try (PreparedStatement ps = cnx.prepareStatement(req)) {
            ps.setString(1, "%" + query + "%");
            ps.setString(2, "%" + query + "%");
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Reservation reservation = new Reservation();
                reservation.setId(rs.getInt("id"));
                reservation.setDoctor_id(rs.getInt("doctor_id"));
                reservation.setPatient_id(rs.getInt("patient_id"));
                reservation.setSubject(rs.getString("subject"));
                reservation.setDate_res(rs.getTimestamp("date_res"));
                reservation.setStatut(rs.getString("statut"));
                reservation.setDoctor("Dr. " + rs.getString("doctor_name") + " " + rs.getString("doctor_last_name"));
                reservations.add(reservation);
            }
        } catch (SQLException e) {
            System.err.println("Error searching reservations by doctor name: " + e.getMessage());
        }
        return reservations;
    }
}