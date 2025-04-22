package tn.esprit.Controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.geometry.Pos;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import javafx.stage.Stage;
import tn.esprit.models.User;

public class PatientDashboardController implements Initializable {

    private User currentUser;
    @FXML
    private StackPane contentArea;

    @FXML
    private Button btnAccueil, btnReservation, btnConsultation, btnConsignes,
            btnAssurance, btnRemboursement, btnPaiement;

    private Button currentActiveButton;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // Par défaut, sélectionner l'accueil
        setActiveButton(btnAccueil);

        try {
            // Charger la page d'accueil par défaut
            loadPage("/patient/PatientAccueil.fxml");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleAccueil() {
        setActiveButton(btnAccueil);
        loadPage("/patient/PatientAccueil.fxml");
    }

    @FXML
    private void handleReservation() {
        setActiveButton(btnReservation);
        loadPage("/patient/PatientReservation.fxml");
    }

    @FXML
    private void handleConsultation() {
        setActiveButton(btnConsultation);
        loadPage("/patient/Consultation.fxml");
    }

    @FXML
    private void handleConsignes() {
        setActiveButton(btnConsignes);
        loadPage("/patient/ConsignesMedicales.fxml");
    }

    @FXML
    private void handleAssurance() {
        setActiveButton(btnAssurance);
        loadPage("/patient/Assurance.fxml");
    }

    @FXML
    private void handleRemboursement() {
        setActiveButton(btnRemboursement);
        loadPage("/patient/Remboursement.fxml");
    }

    @FXML
    private void handlePaiement() {
        setActiveButton(btnPaiement);
        loadPage("/patient/Paiement.fxml");
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


    public void setCurrentUser(User user) {
        this.currentUser = user;
        // Initialize dashboard with user data
    }
    @FXML
    private void handleLogout() {
        try {
            Stage currentStage = (Stage) contentArea.getScene().getWindow();
            currentStage.close();

            Parent root = FXMLLoader.load(getClass().getResource("/Login.fxml"));
            Stage loginStage = new Stage();
            loginStage.setScene(new Scene(root));
            loginStage.setTitle("DOC4U - Login");
            loginStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}