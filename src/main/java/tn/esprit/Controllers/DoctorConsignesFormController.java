package tn.esprit.Controllers;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import tn.esprit.models.ConsigneMedicale;
import tn.esprit.models.OrdonnanceMedicale;
import tn.esprit.services.ConsigneMedicaleService;
import tn.esprit.services.OrdonnanceMedicaleService;
import tn.esprit.services.UserService;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DoctorConsignesFormController implements Initializable {

    @FXML
    private ComboBox<String> comboPatient;

    @FXML
    private TextField txtContent;

    @FXML
    private ComboBox<String> comboProduct;

    @FXML
    private TextField txtDosage;

    @FXML
    private TextField txtDuration;

    @FXML
    private Button btnSauvegarder;

    @FXML
    private Button btnAnnuler;

    private ConsigneMedicaleService consigneService;
    private OrdonnanceMedicaleService ordonnanceService;
    private UserService userService;

    private static final Logger LOGGER = Logger.getLogger(DoctorConsignesFormController.class.getName());

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        consigneService = new ConsigneMedicaleService();
        ordonnanceService = new OrdonnanceMedicaleService();
        userService = new UserService();

        loadPatients();
        loadProducts();
    }

    private void loadPatients() {
        List<String> patients = consigneService.getAllPatients();
        comboPatient.getItems().addAll(patients);
    }

    private void loadProducts() {
        List<String> products = ordonnanceService.getAllProducts();
        comboProduct.getItems().addAll(products);
    }

    @FXML
    private void handleSauvegarder() {
        try {
            String patient = comboPatient.getValue();
            String content = txtContent.getText();
            String product = comboProduct.getValue();
            String dosage = txtDosage.getText();
            String duration = txtDuration.getText();

            if (patient == null) {
                showAlert("Erreur", "Veuillez sélectionner un patient.", Alert.AlertType.ERROR);
                return;
            } else if (content.isEmpty()) {
                showAlert("Erreur", "Veuillez remplir le contenu.", Alert.AlertType.ERROR);
                return;
            } else if (product == null) {
                showAlert("Erreur", "Veuillez sélectionner un produit.", Alert.AlertType.ERROR);
                return;
            } else if (dosage.isEmpty()) {
                showAlert("Erreur", "Veuillez remplir le dosage.", Alert.AlertType.ERROR);
                return;
            } else if (duration.isEmpty()) {
                showAlert("Erreur", "Veuillez remplir la durée.", Alert.AlertType.ERROR);
                return;
            }

            if (!dosage.matches("\\d+.*") || dosage.length() < 5 || dosage.length() > 20) {
                showAlert("Erreur", "Le dosage doit commencer par des chiffres, contenir entre 5 et 20 caractères.", Alert.AlertType.ERROR);
                return;
            } else if (!duration.matches("\\d+.*") || duration.length() < 5 || duration.length() > 20) {
                showAlert("Erreur", "La durée doit commencer par des chiffres, contenir entre 5 et 20 caractères.", Alert.AlertType.ERROR);
                return;
            }

            int doctorId = userService.fetchAndValidateDoctorId(0);
            int patientId = consigneService.getPatientIdByName(patient);

            ConsigneMedicale consigne = new ConsigneMedicale(doctorId, patientId, content, null, null);
            int consigneId = consigneService.saveAndReturnGeneratedId(consigne);

            if (consigneId <= 0) {
                showAlert("Erreur", "Échec de la création de la consigne médicale.", Alert.AlertType.ERROR);
                return;
            }

            int produitId = ordonnanceService.getProductIdByName(product);
            if (produitId > 0) {
                OrdonnanceMedicale ordonnance = new OrdonnanceMedicale(consigneId, produitId, dosage, duration);
                ordonnanceService.save(ordonnance);
                showAlert("Succès", "Consigne médicale et ordonnance sauvegardées avec succès.", Alert.AlertType.INFORMATION);
                clearFields();
            } else {
                showAlert("Erreur", "Produit introuvable.", Alert.AlertType.ERROR);
            }

        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Une erreur s'est produite lors de la sauvegarde: ", e);
            showAlert("Erreur", "Une erreur interne est survenue. Veuillez réessayer ultérieurement.", Alert.AlertType.ERROR);
        }
    }

    @FXML
    private void handleAnnuler() {
        clearFields();
    }

    private void clearFields() {
        comboPatient.setValue(null);
        txtContent.clear();
        comboProduct.setValue(null);
        txtDosage.clear();
        txtDuration.clear();
    }

    private void showAlert(String title, String message, Alert.AlertType alertType) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public void setConsigne(ConsigneMedicale consigne) {
        if (consigne != null) {
            comboPatient.setValue(String.valueOf(consigne.getPatient_id()));
            txtContent.setText(consigne.getContent());
        } else {
            clearFields();
        }
    }
}
