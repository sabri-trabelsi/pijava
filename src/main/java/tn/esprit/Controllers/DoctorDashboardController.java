package tn.esprit.Controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import javafx.stage.Stage;
import tn.esprit.Controllers.Doctor.MedicalInstructionListController;
import tn.esprit.models.User;


public class DoctorDashboardController implements Initializable{
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
            loadPage("/doctor/DoctorAccueil.fxml");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleAccueil() {
        setActiveButton(btnAccueil);
        loadPage("/doctor/DoctorAccueil.fxml");
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
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/doctor/MedicalInstructionView.fxml"));
            Parent view = loader.load();

            // Pass the current doctor to the controller
            MedicalInstructionListController controller = loader.getController();
            controller.setCurrentUser(currentUser);
            // Store controller reference in content area
            contentArea.setUserData(controller);

            contentArea.getChildren().clear();
            contentArea.getChildren().add(view);
        } catch (IOException e) {
            showError("Error loading medical instructions: " + e.getMessage());
            e.printStackTrace();
        }
    }
    private void loadPageWithController(String fxmlPath, Runnable controllerSetup) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent view = loader.load();

            if (controllerSetup != null) {
                controllerSetup.run();
            }

            contentArea.getChildren().clear();
            contentArea.getChildren().add(view);
        } catch (IOException e) {
            showError("Error loading page: " + e.getMessage());
            e.printStackTrace();
        }
    }


    // Keep your existing showError method or add this if you don't have it
    private void showError(String message) {
        VBox errorContainer = new VBox();
        errorContainer.setAlignment(Pos.CENTER);
        errorContainer.setSpacing(10);

        Label errorTitle = new Label("Error Loading Page");
        errorTitle.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");

        Label errorMessage = new Label(message);
        errorMessage.setStyle("-fx-text-fill: red;");

        errorContainer.getChildren().addAll(errorTitle, errorMessage);
        contentArea.getChildren().clear();
        contentArea.getChildren().add(errorContainer);
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

    }
    @FXML
    private void handleLogout() {
        try {
            // Close current window
            Stage currentStage = (Stage) contentArea.getScene().getWindow();
            currentStage.close();

            // Show login screen
            Parent root = FXMLLoader.load(getClass().getResource("/Login.fxml"));
            Stage loginStage = new Stage();
            loginStage.setScene(new Scene(root));
            loginStage.setTitle("DOC4U - Login");
            loginStage.show();
        } catch (IOException e) {
            System.err.println("Error loading login screen: " + e.getMessage());
            e.printStackTrace();
        }
    }
}