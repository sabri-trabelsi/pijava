package tn.esprit.Controllers;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.util.Callback;
import tn.esprit.models.ConsigneMedicale;
import tn.esprit.models.OrdonnanceMedicale;
import tn.esprit.services.ConsigneMedicaleService;
import tn.esprit.services.OrdonnanceMedicaleService;

import java.net.URL;
import java.util.Date;
import java.util.ResourceBundle;

public class DoctorConsigneController implements Initializable {

    @FXML
    private TableView<ConsigneMedicale> consignesTable;

    @FXML
    private TableColumn<ConsigneMedicale, Integer> patientColumn;

    @FXML
    private TableColumn<ConsigneMedicale, String> contentColumn;

    @FXML
    private TableColumn<ConsigneMedicale, Date> created_atColumn;

    @FXML
    private TableColumn<ConsigneMedicale, Date> updated_atColumn;

    @FXML
    private TableView<OrdonnanceMedicale> ordonnancesTable;

    @FXML
    private TableColumn<OrdonnanceMedicale, String> productColumn;

    @FXML
    private TableColumn<OrdonnanceMedicale, String> dosageColumn;

    @FXML
    private TableColumn<OrdonnanceMedicale, String> durationColumn;

    @FXML
    private TableColumn<ConsigneMedicale, Void> actionsColumn;

    @FXML
    private TextField txtSearch;

    private ConsigneMedicaleService consigneService;
    private OrdonnanceMedicaleService ordonnanceService;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        System.out.println("Initialisation de ConsignesListController");
        consigneService = new ConsigneMedicaleService();
        ordonnanceService = new OrdonnanceMedicaleService();

        // Configuration des colonnes ConsigneMedicale
        patientColumn.setCellValueFactory(new PropertyValueFactory<>("patient_id"));
        contentColumn.setCellValueFactory(new PropertyValueFactory<>("content"));
        created_atColumn.setCellValueFactory(new PropertyValueFactory<>("created_at"));
        updated_atColumn.setCellValueFactory(new PropertyValueFactory<>("updated_at"));

        // Configuration des colonnes OrdonnanceMedicale
        productColumn.setCellValueFactory(new PropertyValueFactory<>("product_name"));
        dosageColumn.setCellValueFactory(new PropertyValueFactory<>("dosage"));
        durationColumn.setCellValueFactory(new PropertyValueFactory<>("duration"));

        // Configuration de la colonne d'actions
        configurerColonneActions();

        // Chargement des données
        chargerDonnees();
    }

    private void configurerColonneActions() {
        Callback<TableColumn<ConsigneMedicale, Void>, TableCell<ConsigneMedicale, Void>> cellFactory = new Callback<>() {
            @Override
            public TableCell<ConsigneMedicale, Void> call(final TableColumn<ConsigneMedicale, Void> param) {
                return new TableCell<>() {
                    private final Button btnModifier = new Button("Modifier");
                    private final Button btnSupprimer = new Button("Supprimer");
                    private final HBox hbox = new HBox(5, btnModifier, btnSupprimer);

                    {
                        btnModifier.getStyleClass().add("edit-button");
                        btnSupprimer.getStyleClass().add("delete-button");
                        hbox.setAlignment(javafx.geometry.Pos.CENTER);

                        btnModifier.setOnAction(event -> {
                            ConsigneMedicale consigne = getTableView().getItems().get(getIndex());
                            modifierConsigne(consigne);
                        });

                        btnSupprimer.setOnAction(event -> {
                            ConsigneMedicale consigne = getTableView().getItems().get(getIndex());
                            supprimerConsigne(consigne);
                        });
                    }

                    @Override
                    protected void updateItem(Void item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty) {
                            setGraphic(null);
                        } else {
                            setGraphic(hbox);
                        }
                    }
                };
            }
        };

        actionsColumn.setCellFactory(cellFactory);
    }

    public void chargerDonnees() {
        try {
            System.out.println("Chargement des consignes médicales...");
            var consignes = consigneService.rechercher();
            System.out.println("Nombre de consignes trouvées: " + consignes.size());
            consignesTable.setItems(FXCollections.observableArrayList(consignes));

            System.out.println("Chargement des ordonnances médicales...");
            var ordonnances = ordonnanceService.rechercher();
            System.out.println("Nombre d'ordonnances trouvées: " + ordonnances.size());
            ordonnancesTable.setItems(FXCollections.observableArrayList(ordonnances));
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Erreur lors du chargement des données: " + e.getMessage());
        }
    }

    @FXML
    private void handleSearch() {
        String query = txtSearch.getText().trim();
        if (!query.isEmpty()) {
            var consignes = consigneService.rechercher(query);
            consignesTable.setItems(FXCollections.observableArrayList(consignes));

            var ordonnances = ordonnanceService.rechercher(query);
            ordonnancesTable.setItems(FXCollections.observableArrayList(ordonnances));
        } else {
            chargerDonnees(); // Si la recherche est vide, afficher toutes les données
        }
    }

    @FXML
    private void handleAddConsigne() {
        try {
            StackPane contentArea = (StackPane) consignesTable.getScene().lookup("#contentArea");
            Label pageTitleLabel = (Label) consignesTable.getScene().lookup("#pageTitleLabel");

            if (pageTitleLabel != null) {
                pageTitleLabel.setText("Nouvelle Consigne Médicale");
            }

            if (contentArea != null) {
                URL url = getClass().getResource("/ConsignesForm.fxml");
                System.out.println("URL du formulaire: " + url);

                if (url == null) {
                    System.err.println("Erreur: Ressource DoctorConsignesForm.fxml introuvable");
                    return;
                }

                Parent view = FXMLLoader.load(url);
                contentArea.getChildren().clear();
                contentArea.getChildren().add(view);
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Erreur de navigation: " + e.getMessage());
        }
    }

    private void modifierConsigne(ConsigneMedicale consigne) {
        try {
            StackPane contentArea = (StackPane) consignesTable.getScene().lookup("#contentArea");
            Label pageTitleLabel = (Label) consignesTable.getScene().lookup("#pageTitleLabel");

            if (pageTitleLabel != null) {
                pageTitleLabel.setText("Modifier Consigne Médicale");
            }

            if (contentArea != null) {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/DoctorConsignesForm.fxml"));
                Parent view = loader.load();

                // Récupérer le contrôleur et passer la consigne
                DoctorConsignesFormController controller = loader.getController();
                controller.setConsigne(consigne);

                contentArea.getChildren().clear();
                contentArea.getChildren().add(view);
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Erreur de navigation: " + e.getMessage());
        }
    }

    private void supprimerConsigne(ConsigneMedicale consigne) {
        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
        confirmation.setTitle("Confirmation de suppression");
        confirmation.setHeaderText(null);
        confirmation.setContentText("Êtes-vous sûr de vouloir supprimer cette consigne ?");

        if (confirmation.showAndWait().get() == ButtonType.OK) {
            consigneService.supprimer(consigne);
            chargerDonnees(); // Rafraîchir les données
        }
    }

    @FXML
    private void handleRefresh() {
        txtSearch.clear();
        chargerDonnees();
    }
}
