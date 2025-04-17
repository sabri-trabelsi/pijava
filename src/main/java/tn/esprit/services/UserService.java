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

    public int fetchAndValidateDoctorId(int i) {
        try {
            String req = "SELECT id FROM user WHERE id = ? AND role = 'doctor' AND enabled = true";
            try (PreparedStatement ps = cnx.prepareStatement(req)) {
                ps.setInt(1, i);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        return rs.getInt("id");
                    } else {
                        throw new IllegalArgumentException("Doctor ID not found or invalid.");
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Error fetching and validating doctor ID: " + e.getMessage());
            throw new RuntimeException("Database error while validating doctor ID.", e);
        }
    }
}
    