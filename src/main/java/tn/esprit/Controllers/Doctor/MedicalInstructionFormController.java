// MedicalInstructionFormController.java
package tn.esprit.Controllers.Doctor;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.StackPane;
import javafx.util.StringConverter;
import tn.esprit.models.*;
import tn.esprit.services.*;

import java.util.ArrayList;
import java.util.List;

public class MedicalInstructionFormController {
    private MedicalInstructionListController parentController;

    public void setParentController(MedicalInstructionListController controller) {
        this.parentController = controller;
    }

    @FXML private ComboBox<User> cbPatients;
    @FXML private ComboBox<Produit> cbProducts;
    @FXML private TextArea taContent;
    @FXML private TextField tfDosage, tfDuration;
    @FXML private TableView<Prescription> tvPrescriptions;

    private final MedicalInstructionService miService = new MedicalInstructionService();
    private final UserService userService = new UserService();
    private final ProduitService produitService = new ProduitService();

    private MedicalInstruction currentMI;
    private User currentDoctor;
    private List<Prescription> prescriptions = new ArrayList<>();

    @FXML
    public void initialize() {
        setupPatientsCombo();
        setupProductsCombo();
        configurePrescriptionsTable();
    }

    private void setupPatientsCombo() {
        cbPatients.getItems().addAll(userService.getPatients());
        cbPatients.setConverter(new StringConverter<>() {
            @Override
            public String toString(User user) {
                return user != null ? user.getFullName() : "";
            }

            @Override
            public User fromString(String string) {
                return null;
            }
        });
    }

    private void setupProductsCombo() {
        cbProducts.getItems().addAll(produitService.getAll());
        cbProducts.setConverter(new StringConverter<>() {
            @Override
            public String toString(Produit produit) {
                return produit != null ? produit.getNom() : "";
            }

            @Override
            public Produit fromString(String string) {
                return null;
            }
        });
    }

    private void configurePrescriptionsTable() {
        TableColumn<Prescription, String> productCol = new TableColumn<>("Product");
        productCol.setCellValueFactory(cellData -> {
            Produit p = produitService.getById(cellData.getValue().getProduitId());
            return javafx.beans.binding.Bindings.createStringBinding(() -> p != null ? p.getNom() : "");
        });

        TableColumn<Prescription, String> dosageCol = new TableColumn<>("Dosage");
        dosageCol.setCellValueFactory(new PropertyValueFactory<>("dosage"));

        TableColumn<Prescription, String> durationCol = new TableColumn<>("Duration");
        durationCol.setCellValueFactory(new PropertyValueFactory<>("duration"));

        TableColumn<Prescription, Void> actionCol = new TableColumn<>("Actions");
        actionCol.setCellFactory(param -> new TableCell<>() {
            private final Button removeBtn = new Button("Remove");

            {
                removeBtn.setOnAction(e -> {
                    Prescription p = getTableRow().getItem();
                    prescriptions.remove(p);
                    tvPrescriptions.getItems().remove(p);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : removeBtn);
            }
        });

        tvPrescriptions.getColumns().addAll(productCol, dosageCol, durationCol, actionCol);
    }

    @FXML
    private void handleAddPrescription() {
        Produit product = cbProducts.getValue();
        String dosage = tfDosage.getText();
        String duration = tfDuration.getText();

        if (product == null || dosage.isEmpty() || duration.isEmpty()) {
            showAlert("Please select a product and fill dosage/duration", Alert.AlertType.WARNING);
            return;
        }

        Prescription p = new Prescription();
        p.setProduitId(product.getId());
        p.setDosage(dosage);
        p.setDuration(duration);

        prescriptions.add(p);
        tvPrescriptions.getItems().add(p);
        clearPrescriptionFields();
    }

    @FXML
    private void handleSave() {
        if (currentDoctor == null) {
            showAlert("Doctor context missing!", Alert.AlertType.ERROR);
            return;
        }
        if (!validateInput()) return;

        MedicalInstruction mi = currentMI != null ? currentMI : new MedicalInstruction();
        mi.setDoctorId(currentDoctor.getId());
        mi.setPatientId(cbPatients.getValue().getId());
        mi.setContent(taContent.getText());
        mi.setPrescriptions(prescriptions);

        try {
            if (currentMI == null) {
                miService.ajouter(mi);
            } else {
                miService.modifier(mi);
            }

            // Refresh parent controller
            if (parentController != null) {
                parentController.refreshTable();
            }


            navigateBack();
        } catch (Exception e) {
            showAlert("Error saving: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    @FXML
    private void handleCancel() {
        navigateBack();
    }

    private boolean validateInput() {
        StringBuilder errors = new StringBuilder();

        if (cbPatients.getValue() == null) errors.append("- Select a patient\n");

        if (taContent.getText().length() < 10 || taContent.getText().length() > 200)
            errors.append("- Content must be between 10 and 200 characters\n");
        if (prescriptions.isEmpty())
            errors.append("- Add at least one prescription\n");
        

        if (errors.length() > 0) {
            showAlert("Validation errors:\n" + errors, Alert.AlertType.ERROR);
            return false;
        }
        return true;
    }

    public void setMedicalInstruction(MedicalInstruction mi) {
        this.currentMI = mi;
        try {
            User patient = userService.rechercherById(mi.getPatientId());
            cbPatients.getSelectionModel().select(patient);
            taContent.setText(mi.getContent());

            // Handle null prescriptions
            prescriptions = mi.getPrescriptions() != null ? mi.getPrescriptions() : new ArrayList<>();
            tvPrescriptions.getItems().setAll(prescriptions);

        } catch (Exception e) {
            showAlert("Error loading medical instruction: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    public void setCurrentDoctor(User doctor) {
        if (doctor == null || !"Doctor".equals(doctor.getRole())) {
            throw new IllegalArgumentException("Invalid doctor context");
        }
        this.currentDoctor = doctor;
    }

    private void clearPrescriptionFields() {
        cbProducts.getSelectionModel().clearSelection();
        tfDosage.clear();
        tfDuration.clear();
    }

    private void navigateBack() {
        try {
            StackPane contentArea = (StackPane) tvPrescriptions.getScene().lookup("#contentArea");
            contentArea.getChildren().clear();
            contentArea.getChildren().add(FXMLLoader.load(getClass().getResource("/doctor/MedicalInstructionView.fxml")));
        } catch (Exception e) {
            showAlert("Navigation error: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    private void showAlert(String message, Alert.AlertType type) {
        new Alert(type, message).showAndWait();
    }
}