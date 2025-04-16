package tn.esprit;

import tn.esprit.tests.MainFX;
import tn.esprit.utils.ConnexionDB;

public class main {
    public static void main(String[] args) {
        // Initialize the database connection
        ConnexionDB db = ConnexionDB.getInstance();

        // Launch the JavaFX application
        MainFX.main(args);
    }
}