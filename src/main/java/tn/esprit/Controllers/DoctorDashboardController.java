package tn.esprit.Controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.geometry.Pos;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class DoctorDashboardController implements Initializable {

    @FXML
    private StackPane contentArea;

    @FXML
    private Button btnAccueil, btnPatients, btnRendezVous, btnConsultations,
            btnOrdonnances, btnPlanning;

    private Button currentActiveButton;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // Par défaut, sélectionner l'accueil
        setActiveButton(btnAccueil);

        try {
            // Charger la page d'accueil par défaut
            loadPage("/doctor/Accueil.fxml");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleAccueil() {
        setActiveButton(btnAccueil);
        loadPage("/doctor/Accueil.fxml");
    }

    @FXML
    private void handlePatients() {
        setActiveButton(btnPatients);
        loadPage("/doctor/Patients.fxml");
    }

    @FXML
    private void handleRendezVous() {
        setActiveButton(btnRendezVous);
        loadPage("/doctor/RendezVous.fxml");
    }

    @FXML
    private void handleConsultations() {
        setActiveButton(btnConsultations);
        loadPage("/doctor/Consultations.fxml");
    }

    @FXML
    private void handleOrdonnances() {
        setActiveButton(btnOrdonnances);
        loadPage("/doctor/Ordonnances.fxml");
    }

    @FXML
    private void handlePlanning() {
        setActiveButton(btnPlanning);
        loadPage("/doctor/Planning.fxml");
    }

    private void setActiveButton(Button button) {
        // Réinitialiser l'état actif du bouton précédent
        if (currentActiveButton != null) {
            currentActiveButton.getStyleClass().remove("sidebar-button-active");
        }

        // Définir le nouveau bouton actif
        button.getStyleClass().add("sidebar-button-active");
        currentActiveButton = button;
    }

    private void loadPage(String fxmlPath) {
        try {
            Parent page = FXMLLoader.load(getClass().getResource(fxmlPath));
            contentArea.getChildren().clear();
            contentArea.getChildren().add(page);
        } catch (IOException e) {
            e.printStackTrace();

            // Afficher un message d'erreur en cas d'échec
            VBox errorContainer = new VBox();
            errorContainer.setAlignment(Pos.CENTER);
            errorContainer.setSpacing(10);

            Label errorTitle = new Label("Impossible de charger la page");
            errorTitle.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");

            Label errorMessage = new Label("Erreur: " + e.getMessage());
            errorMessage.setStyle("-fx-text-fill: red;");

            errorContainer.getChildren().addAll(errorTitle, errorMessage);

            contentArea.getChildren().clear();
            contentArea.getChildren().add(errorContainer);
        }
    }
}