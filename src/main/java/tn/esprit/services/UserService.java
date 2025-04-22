package tn.esprit.services;

import tn.esprit.interfaces.IService;
import tn.esprit.models.User;
import tn.esprit.utils.ConnexionDB;
import org.mindrot.jbcrypt.BCrypt;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UserService implements IService<User> {
    private Connection cnx = ConnexionDB.getInstance().getCnx();

    public static int getDoctorIdByFullName(String firstName, String lastName) {
        String req = "SELECT id FROM user WHERE role = 'Doctor' AND name = ? AND last_name = ?";
        try (PreparedStatement ps = ConnexionDB.getInstance().getCnx().prepareStatement(req)) {
            ps.setString(1, firstName);
            ps.setString(2, lastName);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("id");
                }
            }
        } catch (SQLException e) {
            System.err.println("Error fetching doctor ID: " + e.getMessage());
        }
        return -1; // Return -1 if no match is found
    }

    public static String getDoctorFullNameById(int doctorId) {
        String req = "SELECT CONCAT(name, ' ', last_name) AS full_name FROM user WHERE role = 'Doctor' AND id = ?";
        try (PreparedStatement ps = ConnexionDB.getInstance().getCnx().prepareStatement(req)) {
            ps.setInt(1, doctorId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("full_name");
                }
            }
        } catch (SQLException e) {
            System.err.println("Error fetching doctor's full name: " + e.getMessage());
        }
        return null; // Return null if no match is found
    }


    @Override
    public void ajouter(User user) {
        String req = "INSERT INTO user(name, last_name, email, password, num_tel, role, age, " +
                "adresse, specialty, is_banned, enabled) VALUES (?,?,?,?,?,?,?,?,?,?,?)";
        try (PreparedStatement ps = cnx.prepareStatement(req)) {
            ps.setString(1, user.getName());
            ps.setString(2, user.getLastName());
            ps.setString(3, user.getEmail());
            ps.setString(4, BCrypt.hashpw(user.getPassword(), BCrypt.gensalt()));
            ps.setString(5, user.getNumTel());
            ps.setString(6, user.getRole());
            ps.setInt(7, user.getAge());
            ps.setString(8, user.getAdresse());
            ps.setString(9, user.getSpecialty());
            ps.setBoolean(10, user.isIs_banned());
            ps.setBoolean(11, user.isEnabled());

            ps.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error adding user: " + e.getMessage());
        }
    }

    @Override
    public void modifier(User user) {
        StringBuilder req = new StringBuilder("UPDATE user SET " +
                "name=?, last_name=?, email=?, num_tel=?, role=?, age=?, " +
                "adresse=?, specialty=?, is_banned=?, enabled=?");

        List<Object> parameters = new ArrayList<>();

        // Add base parameters
        parameters.add(user.getName());
        parameters.add(user.getLastName());
        parameters.add(user.getEmail());
        parameters.add(user.getNumTel());
        parameters.add(user.getRole());
        parameters.add(user.getAge());
        parameters.add(user.getAdresse());
        parameters.add(user.getSpecialty());
        parameters.add(user.isIs_banned());
        parameters.add(user.isEnabled());

        // Conditionally update password
        if (user.getPassword() != null && !user.getPassword().isEmpty()) {
            req.append(", password=?");
            parameters.add(BCrypt.hashpw(user.getPassword(), BCrypt.gensalt()));
        }

        req.append(" WHERE id=?");
        parameters.add(user.getId());

        try (PreparedStatement ps = cnx.prepareStatement(req.toString())) {
            for (int i = 0; i < parameters.size(); i++) {
                ps.setObject(i + 1, parameters.get(i));
            }
            ps.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error updating user: " + e.getMessage());
        }
    }

    @Override
    public void supprimer(User user) {
        String req = "DELETE FROM user WHERE id=?";
        try (PreparedStatement ps = cnx.prepareStatement(req)) {
            ps.setInt(1, user.getId());
            if (ps.executeUpdate() > 0) {
                System.out.println("Utilisateur supprimé avec succès.");
            } else {
                System.err.println("Échec de la suppression de l'utilisateur.");
            }
        } catch (SQLException e) {
            System.err.println("Erreur de suppression utilisateur: " + e.getMessage());
        }
    }

    @Override
    public List<User> rechercher() {
        List<User> users = new ArrayList<>();
        String req = "SELECT * FROM user";
        try (Statement st = cnx.createStatement(); ResultSet rs = st.executeQuery(req)) {
            while (rs.next()) {
                User user = new User();
                user.setId(rs.getInt("id"));
                user.setName(rs.getString("name"));
                user.setLastName(rs.getString("last_name"));
                user.setEmail(rs.getString("email"));
                user.setNumTel(rs.getString("num_tel"));
                user.setRole(rs.getString("role"));
                user.setAge(rs.getInt("age"));
                user.setAdresse(rs.getString("adresse"));
                user.setSpecialty(rs.getString("specialty"));
                user.setCreated_at(rs.getDate("created_at"));
                user.setIs_banned(rs.getBoolean("is_banned"));
                users.add(user);
            }
        } catch (SQLException e) {
            System.err.println("Erreur de recherche des utilisateurs: " + e.getMessage());
        }
        return users;
    }

    @Override
    public List<User> rechercher(String query) {
        List<User> users = new ArrayList<>();
        String req = "SELECT * FROM user WHERE name LIKE ? OR last_name LIKE ?";
        try (PreparedStatement ps = cnx.prepareStatement(req)) {
            ps.setString(1, "%" + query + "%");
            ps.setString(2, "%" + query + "%");
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    User user = new User();
                    user.setId(rs.getInt("id"));
                    user.setName(rs.getString("name"));
                    user.setLastName(rs.getString("last_name"));
                    user.setEmail(rs.getString("email"));
                    user.setNumTel(rs.getString("num_tel"));
                    user.setRole(rs.getString("role"));
                    user.setAge(rs.getInt("age"));
                    user.setAdresse(rs.getString("adresse"));
                    user.setSpecialty(rs.getString("specialty"));
                    users.add(user);
                }
            }
        } catch (SQLException e) {
            System.err.println("Erreur de recherche des utilisateurs avec requête: " + e.getMessage());
        }
        return users;
    }

    @Override
    public User rechercherById(int id) {
        User user = null;
        String req = "SELECT * FROM user WHERE id=?";
        try (PreparedStatement ps = cnx.prepareStatement(req)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    user = new User();
                    user.setId(rs.getInt("id"));
                    user.setName(rs.getString("name"));
                    user.setLastName(rs.getString("last_name"));
                    user.setEmail(rs.getString("email"));
                    user.setNumTel(rs.getString("num_tel"));
                    user.setRole(rs.getString("role"));
                    user.setAge(rs.getInt("age"));
                    user.setAdresse(rs.getString("adresse"));
                    user.setSpecialty(rs.getString("specialty"));
                }
            }
        } catch (SQLException e) {
            System.err.println("Erreur de recherche utilisateur par ID: " + e.getMessage());
        }
        return user;
    }

    public List<User> getDoctors() {
        List<User> doctors = new ArrayList<>();
        String req = "SELECT * FROM user WHERE role = 'Doctor'";
        try (Statement st = cnx.createStatement(); ResultSet rs = st.executeQuery(req)) {
            while (rs.next()) {
                User doctor = new User();
                doctor.setId(rs.getInt("id"));
                doctor.setName(rs.getString("name"));
                doctor.setLastName(rs.getString("last_name"));
                doctor.setEmail(rs.getString("email"));
                doctor.setNumTel(rs.getString("num_tel"));
                doctor.setRole(rs.getString("role"));
                doctor.setAge(rs.getInt("age"));
                doctor.setAdresse(rs.getString("adresse"));
                doctor.setSpecialty(rs.getString("specialty"));
                doctor.setCreated_at(rs.getDate("created_at"));
                doctor.setIs_banned(rs.getBoolean("is_banned"));
                doctor.setEnabled(rs.getBoolean("enabled"));
                doctors.add(doctor);
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la récupération des docteurs: " + e.getMessage());
        }
        return doctors;
    }
    public User authenticate(String email, String password) {
        String query = "SELECT * FROM user WHERE email = ?";
        try (PreparedStatement ps = cnx.prepareStatement(query)) {
            ps.setString(1, email);
            ResultSet rs = ps.executeQuery();
            if (rs.next() && BCrypt.checkpw(password, rs.getString("password"))) {
                User user = mapResultSetToUser(rs);
                return (user.isEnabled() && !user.isIs_banned()) ? user : null;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    private User mapResultSetToUser(ResultSet rs) {
        User user = new User();
        try {
            user.setId(rs.getInt("id"));
            user.setName(rs.getString("name"));
            user.setLastName(rs.getString("last_name"));
            user.setEmail(rs.getString("email"));
            user.setPassword(rs.getString("password"));
            user.setNumTel(rs.getString("num_tel"));
            user.setRole(rs.getString("role"));
            user.setAge(rs.getInt("age"));
            user.setAdresse(rs.getString("adresse"));
            user.setSpecialty(rs.getString("specialty"));
            user.setCreated_at(rs.getDate("created_at"));
            user.setIs_banned(rs.getBoolean("is_banned"));
            user.setEnabled(rs.getBoolean("enabled"));
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return user;
    }

    public boolean emailExists(String email) {
        String query = "SELECT COUNT(*) FROM user WHERE email = ?";
        try (PreparedStatement ps = cnx.prepareStatement(query)) {
            ps.setString(1, email);
            ResultSet rs = ps.executeQuery();
            return rs.next() && rs.getInt(1) > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public List<User> getPatients() {
        List<User> patients = new ArrayList<>();
        String query = "SELECT * FROM user WHERE role = 'Patient'";
        try (Statement st = cnx.createStatement(); ResultSet rs = st.executeQuery(query)) {
            while (rs.next()) {
                User user = new User();
                user.setId(rs.getInt("id"));
                user.setName(rs.getString("name"));
                user.setLastName(rs.getString("last_name"));
                user.setEmail(rs.getString("email"));
                user.setNumTel(rs.getString("num_tel"));
                user.setRole(rs.getString("role"));
                user.setAge(rs.getInt("age"));
                user.setAdresse(rs.getString("adresse"));
                user.setSpecialty(rs.getString("specialty"));
                user.setCreated_at(rs.getDate("created_at"));
                user.setIs_banned(rs.getBoolean("is_banned"));
                user.setEnabled(rs.getBoolean("enabled"));
                patients.add(user);
            }
        } catch (SQLException e) {
            System.err.println("Error fetching patients: " + e.getMessage());
        }
        return patients;
    }
}