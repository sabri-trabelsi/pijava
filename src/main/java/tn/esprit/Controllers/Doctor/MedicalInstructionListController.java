// MedicalInstructionListController.java
package tn.esprit.Controllers.Doctor;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.util.Callback;
import tn.esprit.models.MedicalInstruction;
import tn.esprit.models.User;
import tn.esprit.services.MedicalInstructionService;
import tn.esprit.services.UserService;

import java.net.URL;
import java.util.ResourceBundle;

public class MedicalInstructionListController implements javafx.fxml.Initializable {
    private final ObservableList<MedicalInstruction> instructions = FXCollections.observableArrayList();

    @FXML private TableView<MedicalInstruction> miTable;
    @FXML private TableColumn<MedicalInstruction, String> patientColumn;
    @FXML private TableColumn<MedicalInstruction, String> contentColumn;
    @FXML private TableColumn<MedicalInstruction, String> createdAtColumn;
    @FXML private TableColumn<MedicalInstruction, Void> actionsColumn;

    private final MedicalInstructionService miService = new MedicalInstructionService();
    private final UserService userService = new UserService();
    private User currentDoctor;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        configureColumns();
        refreshTable();
        setupActionColumn();
        miTable.setItems(instructions);
    }

    private void configureColumns() {
        patientColumn.setCellValueFactory(cellData -> {
            User patient = userService.rechercherById(cellData.getValue().getPatientId());
            return new SimpleStringProperty(patient.getFullName());
        });

        contentColumn.setCellValueFactory(new PropertyValueFactory<>("content"));
        createdAtColumn.setCellValueFactory(new PropertyValueFactory<>("createdAt"));
    }

    private void setupActionColumn() {
        actionsColumn.setCellFactory(new Callback<>() {
            @Override
            public TableCell<MedicalInstruction, Void> call(TableColumn<MedicalInstruction, Void> param) {
                return new TableCell<>() {
                    private final Button editBtn = new Button("Edit");
                    private final Button deleteBtn = new Button("Delete");
                    private final HBox container = new HBox(5, editBtn, deleteBtn);

                    {
                        editBtn.getStyleClass().add("edit-button");
                        deleteBtn.getStyleClass().add("delete-button");
                        container.setAlignment(javafx.geometry.Pos.CENTER);

                        editBtn.setOnAction(e -> handleEdit(getTableRow().getItem()));
                        deleteBtn.setOnAction(e -> handleDelete(getTableRow().getItem()));
                    }

                    @Override
                    protected void updateItem(Void item, boolean empty) {
                        super.updateItem(item, empty);
                        setGraphic(empty ? null : container);
                    }
                };
            }
        });
    }

    public void refreshTable() {
        if (currentDoctor != null) {
            instructions.setAll(miService.getByDoctor(currentDoctor.getId()));
        }
    }
    // Call this after successful save in the form controller
    public void onAddSuccess() {
        refreshTable();
    }

    private void handleEdit(MedicalInstruction mi) {
        try {
            if (mi == null || mi.getPrescriptions() == null) {
                showAlert("Invalid medical instruction selected", Alert.AlertType.WARNING);
                return;
            }

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/doctor/MedicalInstructionForm.fxml"));
            Parent view = loader.load();

            MedicalInstructionFormController controller = loader.getController();
            controller.setMedicalInstruction(mi);
            controller.setCurrentDoctor(currentDoctor);

            StackPane contentArea = (StackPane) miTable.getScene().lookup("#contentArea");
            contentArea.getChildren().clear();
            contentArea.getChildren().add(view);
        } catch (Exception e) {
            showAlert("Error loading form: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    private void handleDelete(MedicalInstruction mi) {
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setContentText("Delete this medical instruction?");
        confirm.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                miService.supprimer(mi);
                refreshTable();
            }
        });
    }

    public void setCurrentUser(User user) {
        this.currentDoctor = user;
        refreshTable();
    }

    @FXML
    private void handleAddNew() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/doctor/MedicalInstructionForm.fxml"));
            Parent form = loader.load();

            MedicalInstructionFormController controller = loader.getController();
            controller.setParentController(this); // Pass reference to list controller
            controller.setCurrentDoctor(currentDoctor); // Pass the doctor here

            StackPane contentArea = (StackPane) miTable.getScene().lookup("#contentArea");
            contentArea.getChildren().clear();
            contentArea.getChildren().add(form);
        } catch (Exception e) {
            showAlert("Error loading form: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    private void showAlert(String message, Alert.AlertType type) {
        new Alert(type, message).showAndWait();
    }

    @FXML
    private TextField txtSearch;

    @FXML
    private void handleRefresh() {
        txtSearch.clear();
        refreshTable();
    }

    @FXML
    private void handleSearch() {
        String query = txtSearch.getText().trim();
        if (!query.isEmpty()) {
            var filteredInstructions = instructions.stream()
                    .filter(mi -> mi.getContent().toLowerCase().contains(query.toLowerCase()))
                    .toList();
            miTable.setItems(FXCollections.observableArrayList(filteredInstructions));
        } else {
            refreshTable(); // If search query is empty, refresh the table to show all instructions
        }
    }

}