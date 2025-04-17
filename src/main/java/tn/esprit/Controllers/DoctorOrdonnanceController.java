package tn.esprit.Controllers;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import tn.esprit.models.OrdonnanceMedicale;
import tn.esprit.services.OrdonnanceMedicaleService;

import java.util.Date;
import java.util.List;

public class DoctorOrdonnanceController {
    @FXML private TextField txtProduit, txtDosage, txtDuree, txtIdPatient;
    @FXML private TableView<OrdonnanceMedicale> tableOrdonnances;
    @FXML private TableColumn<OrdonnanceMedicale, String> colProduit;
    @FXML private TableColumn<OrdonnanceMedicale, Date> colDate;
    private OrdonnanceMedicaleService service = new OrdonnanceMedicaleService();
    private OrdonnanceMedicale selected;

    @FXML
    public void initialize() {
        colProduit.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getProduit()));
        colDate.setCellValueFactory(data -> new javafx.beans.property.SimpleObjectProperty<>(data.getValue().getDate()));
        chargerTable();

        tableOrdonnances.getSelectionModel().selectedItemProperty().addListener((obs, old, newOrd) -> {
            if (newOrd != null) {
                selected = newOrd;
                txtProduit.setText(newOrd.getProduit());
                txtDosage.setText(newOrd.getDosage());
                txtDuree.setText(newOrd.getDuree());
                txtIdPatient.setText(String.valueOf(newOrd.getIdPatient()));
            }
        });
    }

    private void chargerTable() {
        List<OrdonnanceMedicale> list = service.rechercher();
        tableOrdonnances.setItems(FXCollections.observableArrayList(list));
    }

    @FXML
    private void ajouter() {
        if (txtProduit.getText().isEmpty() || txtDosage.getText().isEmpty() || txtDuree.getText().isEmpty()) {
            showAlert("Tous les champs sont obligatoires", Alert.AlertType.WARNING);
            return;
        }
        OrdonnanceMedicale o = new OrdonnanceMedicale();
        o.setProduit(txtProduit.getText());
        o.setDosage(txtDosage.getText());
        o.setDuree(txtDuree.getText());
        o.setIdPatient(Integer.parseInt(txtIdPatient.getText()));
        o.setIdDocteur(1); // temporaire
        o.setDate(new Date());
        service.ajouter(o);
        clear();
        chargerTable();
    }

    @FXML
    private void modifier() {
        if (selected == null) return;
        selected.setProduit(txtProduit.getText());
        selected.setDosage(txtDosage.getText());
        selected.setDuree(txtDuree.getText());
        selected.setDate(new Date());
        service.modifier(selected);
        clear();
        chargerTable();
    }

    @FXML
    private void supprimer() {
        if (selected != null) {
            service.supprimer(selected);
            clear();
            chargerTable();
        }
    }

    private void clear() {
        txtProduit.clear();
        txtDosage.clear();
        txtDuree.clear();
        txtIdPatient.clear();
        selected = null;
    }

    private void showAlert(String msg, Alert.AlertType type) {
        Alert a = new Alert(type);
        a.setContentText(msg);
        a.showAndWait();
    }
}
