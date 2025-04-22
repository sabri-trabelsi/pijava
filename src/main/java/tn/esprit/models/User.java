package tn.esprit.models;

import tn.esprit.utils.ConnexionDB;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class User {
    private int id;
    private String name;
    private String lastName;
    private String email;
    private String password;
    private String numTel;
    private String role;
    private int age;
    private Date created_at;
    private String specialty;
    private boolean is_banned;
    private boolean enabled;
    private String adresse;

    // Constructeurs
    public User() {}

    public User( String name, String lastName, String email, String password, String numTel, String role, int age, Date created_at, String specialty, boolean is_banned, boolean enabled, String adresse) {

        this.name = name;
        this.lastName = lastName;
        this.email = email;
        this.password = password;
        this.numTel = numTel;
        this.role = role;
        this.age = age;
        this.created_at = created_at;
        this.specialty = specialty;
        this.is_banned = is_banned;
        this.enabled = enabled;
        this.adresse = adresse;
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", lastName='" + lastName + '\'' +
                ", email='" + email + '\'' +
                ", password='" + password + '\'' +
                ", numTel='" + numTel + '\'' +
                ", role='" + role + '\'' +
                ", age=" + age +
                ", created_at=" + created_at +
                ", specialty='" + specialty + '\'' +
                ", is_banned=" + is_banned +
                ", enabled=" + enabled +
                ", adresse='" + adresse + '\'' +
                '}';
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        // Return null instead of empty string for clearer checks
        return (password == null || password.isEmpty()) ? null : password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getNumTel() {
        return numTel;
    }

    public void setNumTel(String numTel) {
        this.numTel = numTel;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public Date getCreated_at() {
        return created_at;
    }

    public void setCreated_at(Date created_at) {
        this.created_at = created_at;
    }

    public String getSpecialty() {
        return specialty;
    }

    public void setSpecialty(String specialty) {
        this.specialty = specialty;
    }

    public boolean isIs_banned() {
        return is_banned;
    }

    public void setIs_banned(boolean is_banned) {
        this.is_banned = is_banned;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public String getAdresse() {
        return adresse;
    }

    public void setAdresse(String adresse) {
        this.adresse = adresse;
    }

    // Add this method to the User class
    public String getFullName() {
        return name + " " + lastName;
    }
    private Connection cnx = ConnexionDB.getInstance().getCnx();
    // Add this to your existing UserService.java
    public List<User> getPatients() {
        List<User> patients = new ArrayList<>();
        String query = "SELECT * FROM user WHERE role = 'Patient'";
        try (Statement st = cnx.createStatement();
             ResultSet rs = st.executeQuery(query)) {
            while (rs.next()) {
                patients.add(mapResultSetToUser(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error fetching patients: " + e.getMessage());
        }
        return patients;
    }

    private User mapResultSetToUser(ResultSet rs) {
        User user = new User();
        try {
            user.setId(rs.getInt("id"));
            user.setName(rs.getString("name"));
            user.setLastName(rs.getString("lastName"));
            user.setEmail(rs.getString("email"));
            user.setPassword(rs.getString("password"));
            user.setNumTel(rs.getString("numTel"));
            user.setRole(rs.getString("role"));
            user.setAge(rs.getInt("age"));
            user.setCreated_at(rs.getDate("created_at"));
            user.setSpecialty(rs.getString("specialty"));
            user.setIs_banned(rs.getBoolean("is_banned"));
            user.setEnabled(rs.getBoolean("enabled"));
            user.setAdresse(rs.getString("adresse"));
        } catch (SQLException e) {
            System.err.println("Error mapping ResultSet to User: " + e.getMessage());
        }
        return user;
    }
}