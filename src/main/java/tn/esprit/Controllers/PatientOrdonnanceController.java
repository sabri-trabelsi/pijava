package tn.esprit.controllers;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import tn.esprit.models.OrdonnanceMedicale;
import tn.esprit.services.OrdonnanceMedicaleService;

import java.util.List;

public class PatientOrdonnanceController {

    @FXML
    private TableView<OrdonnanceMedicale> tableOrdonnances;

    @FXML
    private TableColumn<OrdonnanceMedicale, String> colProduit;

    @FXML
    private TableColumn<OrdonnanceMedicale, String> colDosage;

    @FXML
    private TableColumn<OrdonnanceMedicale, String> colDuree;

    @FXML
    private TableColumn<OrdonnanceMedicale, java.util.Date> colDate;

    private final OrdonnanceMedicaleService service = new OrdonnanceMedicaleService();

    @FXML
    public void initialize() {
        colProduit.setCellValueFactory(new PropertyValueFactory<>("produit"));
        colDosage.setCellValueFactory(new PropertyValueFactory<>("dosage"));
        colDuree.setCellValueFactory(new PropertyValueFactory<>("duree"));
        colDate.setCellValueFactory(new PropertyValueFactory<>("date"));

        chargerOrdonnances();
    }

    private void chargerOrdonnances() {
        // À filtrer par ID patient dans une vraie appli
        List<OrdonnanceMedicale> list = service.rechercher();
        tableOrdonnances.setItems(FXCollections.observableArrayList(list));
    }
}
