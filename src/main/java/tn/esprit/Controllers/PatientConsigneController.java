package tn.esprit.Controllers;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import tn.esprit.models.ConsigneMedicale;
import tn.esprit.services.ConsigneMedicaleService;


import java.util.List;

public class PatientConsigneController {

    @FXML
    private TableView<ConsigneMedicale> tableConsignes;

    @FXML
    private TableColumn<ConsigneMedicale, String> colContenu;

    @FXML
    private TableColumn<ConsigneMedicale, java.util.Date> colDate;

    private final ConsigneMedicaleService service = new ConsigneMedicaleService();

    @FXML
    public void initialize() {
        // Initialisation des colonnes
        colContenu.setCellValueFactory(new PropertyValueFactory<>("contenu"));
        colDate.setCellValueFactory(new PropertyValueFactory<>("date"));

        chargerConsignes();
    }

    private void chargerConsignes() {
        // Ici on peut filtrer par patient connecté (ex: id = 1 pour le test)
        List<ConsigneMedicale> list = service.rechercher();
        tableConsignes.setItems(FXCollections.observableArrayList(list));
    }
}
