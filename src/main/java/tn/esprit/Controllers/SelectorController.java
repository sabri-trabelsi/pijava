package tn.esprit.Controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.stage.Stage;

public class SelectorController {
    @FXML
    private Button btnAdmin, btnDoctor, btnPatient;

    @FXML
    private void navigateToAdmin() {
        loadInterface("/MainLayout.fxml", "DOC4U - Administration");
    }

    @FXML
    private void navigateToDoctor() {
        loadInterface("/DoctorLayout.fxml", "DOC4U - Espace Médecin");
    }

    @FXML
    private void navigateToPatient() {
        loadInterface("/PatientLayout.fxml", "DOC4U - Espace Patient");
    }

    private void loadInterface(String fxmlPath, String title) {
        try {
            System.out.println("Chargement de l'interface : " + fxmlPath);

            // Charger le FXML
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent root = loader.load();

            // Configurer la scène
            Scene scene = new Scene(root);

            // Ajouter les styles CSS
            String cssPath = "/styles/styles.css";
            if (getClass().getResource(cssPath) != null) {
                scene.getStylesheets().add(getClass().getResource(cssPath).toExternalForm());
            } else {
                System.err.println("Fichier CSS non trouvé : " + cssPath);
            }

            // Créer et configurer la nouvelle fenêtre
            Stage stage = new Stage();
            stage.setTitle(title);
            stage.setScene(scene);
            stage.setMaximized(true);
            stage.show();

            // Fermer l'écran de sélection
            ((Stage) btnAdmin.getScene().getWindow()).close();

        } catch (Exception e) {
            System.err.println("Erreur lors du chargement de l'interface : " + fxmlPath);
            e.printStackTrace();

            // Afficher une alerte d'erreur
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erreur");
            alert.setHeaderText("Impossible de charger l'interface");
            alert.setContentText("Erreur : " + e.getMessage());
            alert.showAndWait();
        }
    }
}