package tn.esprit.Controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.StackPane;
import javafx.util.StringConverter;
import tn.esprit.models.Assurance;
import tn.esprit.services.AssuranceService;

import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;

public class AssuranceFormController implements Initializable {

    @FXML
    private ComboBox<String> comboType;

    @FXML
    private ComboBox<String> comboStatut;

    @FXML
    private TextField txtNom;

    @FXML
    private TextField txtCompagnie;

    @FXML
    private DatePicker dpDateDebut;

    @FXML
    private DatePicker dpDateExpiration;

    @FXML
    private Button btnSauvegarder;

    @FXML
    private Button btnAnnuler;

    private AssuranceService service;

    // Variables pour le mode édition
    private int assuranceId;
    private boolean isEditMode = false;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        service = new AssuranceService();

        // Initialisation des combobox
        comboType.getItems().addAll("Maladie", "Vie", "Automobile", "Habitation");
        comboStatut.getItems().addAll("Actif", "Inactif", "En attente");

        // Configuration du format des dates
        configurerFormatDates();

        // Par défaut, initialiser les dates
        dpDateDebut.setValue(LocalDate.now());
        dpDateExpiration.setValue(LocalDate.now().plusYears(1));

        System.out.println("AssuranceFormController initialisé");
    }

    private void configurerFormatDates() {
        // Configuration du format des dates pour les DatePickers
        String pattern = "dd/MM/yyyy";
        StringConverter<LocalDate> converter = new StringConverter<>() {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern);

            @Override
            public String toString(LocalDate date) {
                if (date != null) {
                    return formatter.format(date);
                } else {
                    return "";
                }
            }

            @Override
            public LocalDate fromString(String string) {
                if (string != null && !string.isEmpty()) {
                    return LocalDate.parse(string, formatter);
                } else {
                    return null;
                }
            }
        };

        dpDateDebut.setConverter(converter);
        dpDateExpiration.setConverter(converter);

        // Style visuel pour les prompts
        dpDateDebut.setPromptText("JJ/MM/AAAA");
        dpDateExpiration.setPromptText("JJ/MM/AAAA");
    }

    /**
     * Méthode pour définir les valeurs d'une assurance existante dans le formulaire
     * @param assurance L'assurance à éditer
     */
    public void setAssurance(Assurance assurance) {
        if (assurance == null) {
            System.err.println("Erreur: assurance null");
            return;
        }

        System.out.println("Configuration du formulaire pour l'assurance ID: " + assurance.getId());

        // Stocke l'ID de l'assurance
        this.assuranceId = assurance.getId();
        this.isEditMode = true;

        // Changer le titre du bouton en mode édition
        btnSauvegarder.setText("Mettre à jour");

        // Définit les valeurs dans les champs
        txtNom.setText(assurance.getNom());
        txtCompagnie.setText(assurance.getCompagnie());

        // Configuration des combobox
        comboType.setValue(assurance.getType());
        comboStatut.setValue(assurance.getStatut());

        // Conversion des dates (java.util.Date → java.time.LocalDate)
        if (assurance.getDateDebut() != null) {
            LocalDate dateDebut = new java.sql.Date(assurance.getDateDebut().getTime()).toLocalDate();
            dpDateDebut.setValue(dateDebut);
        }

        if (assurance.getDateExpiration() != null) {
            LocalDate dateExpiration = new java.sql.Date(assurance.getDateExpiration().getTime()).toLocalDate();
            dpDateExpiration.setValue(dateExpiration);
        }
    }

    @FXML
    private void handleSauvegarder() {
        if (validerChamps()) {
            try {
                // Création ou récupération de l'objet Assurance
                Assurance assurance = new Assurance();

                // Si en mode édition, définir l'ID
                if (isEditMode) {
                    assurance.setId(assuranceId);
                }

                // Définition des propriétés
                assurance.setNom(txtNom.getText());
                assurance.setType(comboType.getValue());
                assurance.setCompagnie(txtCompagnie.getText());

                // Conversion LocalDate -> java.util.Date
                java.util.Date dateDebut = java.sql.Date.valueOf(dpDateDebut.getValue());
                java.util.Date dateExpiration = java.sql.Date.valueOf(dpDateExpiration.getValue());

                assurance.setDateDebut(dateDebut);
                assurance.setDateExpiration(dateExpiration);
                assurance.setStatut(comboStatut.getValue());

                // Ajout ou modification selon le mode
                if (isEditMode) {
                    System.out.println("Modification de l'assurance ID: " + assuranceId);
                    service.modifier(assurance);
                    afficherMessage("Assurance modifiée avec succès", Alert.AlertType.INFORMATION);
                } else {
                    System.out.println("Ajout d'une nouvelle assurance");
                    service.ajouter(assurance);
                    afficherMessage("Assurance ajoutée avec succès", Alert.AlertType.INFORMATION);
                }

                // Retour à la liste
                navigateToList();
            } catch (Exception e) {
                afficherMessage("Erreur lors de l'opération: " + e.getMessage(), Alert.AlertType.ERROR);
                e.printStackTrace();
            }
        }
    }

    private boolean validerChamps() {
        StringBuilder erreurs = new StringBuilder();

        // Validation des champs obligatoires
        if (txtNom.getText().isEmpty())
            erreurs.append("- Le nom est obligatoire\n");

        if (comboType.getValue() == null)
            erreurs.append("- Le type est obligatoire\n");

        if (txtCompagnie.getText().isEmpty())
            erreurs.append("- La compagnie est obligatoire\n");

        if (dpDateDebut.getValue() == null)
            erreurs.append("- La date de début est obligatoire\n");

        if (dpDateExpiration.getValue() == null)
            erreurs.append("- La date d'expiration est obligatoire\n");

        if (comboStatut.getValue() == null)
            erreurs.append("- Le statut est obligatoire\n");

        // Si des erreurs ont été identifiées
        if (erreurs.length() > 0) {
            afficherMessage("Veuillez corriger les erreurs suivantes:\n" + erreurs.toString(), Alert.AlertType.ERROR);
            return false;
        }

        // Vérification des dates
        if (dpDateDebut.getValue().isAfter(dpDateExpiration.getValue())) {
            afficherMessage("La date de début doit être antérieure à la date d'expiration", Alert.AlertType.ERROR);
            return false;
        }

        return true;
    }

    private void afficherMessage(String message, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(type == Alert.AlertType.ERROR ? "Erreur" : "Succès");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    @FXML
    private void handleAnnuler() {
        navigateToList();
    }

    private void navigateToList() {
        try {
            StackPane contentArea = (StackPane) txtNom.getScene().lookup("#contentArea");
            Label pageTitleLabel = (Label) txtNom.getScene().lookup("#pageTitleLabel");

            if (pageTitleLabel != null) {
                pageTitleLabel.setText("Liste des Assurances");
            }

            if (contentArea != null) {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/AssuranceList.fxml"));
                Parent view = loader.load();

                // Récupérer le contrôleur de liste et rafraîchir les données
                AssuranceListController controller = loader.getController();
                controller.chargerDonnees();  // Rafraîchir les données

                contentArea.getChildren().clear();
                contentArea.getChildren().add(view);
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Erreur de navigation: " + e.getMessage());
        }
    }
}