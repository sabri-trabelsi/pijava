package tn.esprit.utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConnexionDB {
    final  String url = "jdbc:mysql://localhost:3306/pi";
    final  String user = "root";
    final  String password = "";

    private static ConnexionDB instance;
    private Connection cnx;
    public Connection getCnx() {
        return cnx;
    }
    public static ConnexionDB getInstance() {
        if (instance == null) {
            instance = new ConnexionDB();
        }
        return instance;
    }
    public ConnexionDB() {
        try {
            cnx = DriverManager.getConnection(url, user, password);
            System.out.println("Connexion établie avec succès");
        } catch (SQLException e) {
            throw new RuntimeException("Erreur de connexion à la base de données", e);

        }
    }
}
