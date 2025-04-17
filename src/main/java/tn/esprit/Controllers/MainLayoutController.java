package tn.esprit.Controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.StackPane;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class MainLayoutController implements Initializable {

    @FXML
    private StackPane contentArea;

    @FXML
    private Label pageTitleLabel;

    @FXML
    private TitledPane remboursementPanel;

    @FXML
    private TitledPane assurancePanel;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // Charger le dashboard par défaut
        handleNavigateToDashboard();

        // Fermer les panneaux accordéon par défaut
        if (remboursementPanel != null) remboursementPanel.setExpanded(false);
        if (assurancePanel != null) assurancePanel.setExpanded(false);
    }

    @FXML
    private void handleNavigateToDashboard() {
        pageTitleLabel.setText("Tableau de bord");
        loadView("/Dashboard.fxml");
    }

    @FXML
    private void handleNavigateToUsers() {
        pageTitleLabel.setText("Gestion des Utilisateurs");
        loadView("/UsersView.fxml");
    }

    @FXML
    private void handleNavigateToReservations() {
        pageTitleLabel.setText("Gestion des Réservations");
        loadView("/ReservationsView.fxml");
    }

    @FXML
    private void handleNavigateToConsultations() {
        pageTitleLabel.setText("Gestion des Consultations");
        loadView("/ConsultationsView.fxml");
    }

    @FXML
    private void handleNavigateToMedicalInstructions() {
        pageTitleLabel.setText("Consignes Médicales");
        loadView("/MedicalInstructionsView.fxml");
    }

    @FXML
    private void handleNavigateToReimbursements() {
        pageTitleLabel.setText("Gestion des Remboursements");
        loadView("/ReimbursementsView.fxml");
    }

    @FXML
    private void handleNavigateToReimbursementReports() {
        pageTitleLabel.setText("Rapports des Remboursements");
        loadView("/ReimbursementReportsView.fxml");
    }

    @FXML
    private void handleNavigateToAssuranceList() {
        pageTitleLabel.setText("Liste des Assurances");
        loadView("/AssuranceList.fxml");
    }



    @FXML
    private void handleNavigateToAssuranceForm() {
        pageTitleLabel.setText("Nouvelle Assurance");
        loadView("/AssuranceForm.fxml");
    }

    private void loadView(String fxmlPath) {
        try {
            Parent view = FXMLLoader.load(getClass().getResource(fxmlPath));
            contentArea.getChildren().clear();
            contentArea.getChildren().add(view);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}