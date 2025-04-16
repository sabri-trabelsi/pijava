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
import tn.esprit.models.Assurance;
import tn.esprit.services.AssuranceService;

import java.net.URL;
import java.util.ResourceBundle;

public class AssuranceListController implements Initializable {

    @FXML
    private TableView<Assurance> assuranceTable;

    @FXML
    private TableColumn<Assurance, String> typeColumn;

    @FXML
    private TableColumn<Assurance, String> nomColumn;

    @FXML
    private TableColumn<Assurance, String> compagnieColumn;

    @FXML
    private TableColumn<Assurance, String> statutColumn;

    @FXML
    private TableColumn<Assurance, Void> actionsColumn;

    @FXML
    private TextField txtSearch;

    private AssuranceService service;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        System.out.println("Initialisation de AssuranceListController");
        service = new AssuranceService();

        // Configuration des colonnes (sans idColumn)
        typeColumn.setCellValueFactory(new PropertyValueFactory<>("type"));
        nomColumn.setCellValueFactory(new PropertyValueFactory<>("nom"));
        compagnieColumn.setCellValueFactory(new PropertyValueFactory<>("compagnie"));
        statutColumn.setCellValueFactory(new PropertyValueFactory<>("statut"));

        // Configuration de la colonne d'actions
        configurerColonneActions();

        // Chargement des données
        chargerDonnees();
    }

    private void configurerColonneActions() {
        Callback<TableColumn<Assurance, Void>, TableCell<Assurance, Void>> cellFactory = new Callback<>() {
            @Override
            public TableCell<Assurance, Void> call(final TableColumn<Assurance, Void> param) {
                return new TableCell<>() {
                    private final Button btnModifier = new Button("Modifier");
                    private final Button btnSupprimer = new Button("Supprimer");
                    private final HBox hbox = new HBox(5, btnModifier, btnSupprimer);

                    {
                        btnModifier.getStyleClass().add("edit-button");
                        btnSupprimer.getStyleClass().add("delete-button");
                        hbox.setAlignment(javafx.geometry.Pos.CENTER);

                        btnModifier.setOnAction(event -> {
                            Assurance assurance = getTableView().getItems().get(getIndex());
                            modifierAssurance(assurance);
                        });

                        btnSupprimer.setOnAction(event -> {
                            Assurance assurance = getTableView().getItems().get(getIndex());
                            supprimerAssurance(assurance);
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
            System.out.println("Chargement des données...");
            var assurances = service.rechercher();
            System.out.println("Nombre d'assurances trouvées: " + assurances.size());
            assuranceTable.setItems(FXCollections.observableArrayList(assurances));
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Erreur lors du chargement des données: " + e.getMessage());
        }
    }

    @FXML
    private void handleSearch() {
        String query = txtSearch.getText().trim();
        if (!query.isEmpty()) {
            var assurances = service.rechercher(query);
            assuranceTable.setItems(FXCollections.observableArrayList(assurances));
        } else {
            chargerDonnees(); // Si la recherche est vide, afficher toutes les assurances
        }
    }

    @FXML
    private void handleAddAssurance() {
        try {
            StackPane contentArea = (StackPane) assuranceTable.getScene().lookup("#contentArea");
            Label pageTitleLabel = (Label) assuranceTable.getScene().lookup("#pageTitleLabel");

            if (pageTitleLabel != null) {
                pageTitleLabel.setText("Nouvelle Assurance");
            }

            if (contentArea != null) {
                URL url = getClass().getResource("/AssuranceForm.fxml");
                System.out.println("URL du formulaire: " + url);

                if (url == null) {
                    System.err.println("Erreur: Ressource AssuranceForm.fxml introuvable");
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

    private void modifierAssurance(Assurance assurance) {
        try {
            StackPane contentArea = (StackPane) assuranceTable.getScene().lookup("#contentArea");
            Label pageTitleLabel = (Label) assuranceTable.getScene().lookup("#pageTitleLabel");

            if (pageTitleLabel != null) {
                pageTitleLabel.setText("Modifier Assurance");
            }

            if (contentArea != null) {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/AssuranceForm.fxml"));
                Parent view = loader.load();

                // Récupérer le contrôleur et passer l'assurance
                AssuranceFormController controller = loader.getController();
                controller.setAssurance(assurance);

                contentArea.getChildren().clear();
                contentArea.getChildren().add(view);
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Erreur de navigation: " + e.getMessage());
        }
    }

    private void supprimerAssurance(Assurance assurance) {
        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
        confirmation.setTitle("Confirmation de suppression");
        confirmation.setHeaderText(null);
        confirmation.setContentText("Êtes-vous sûr de vouloir supprimer l'assurance '" + assurance.getNom() + "' ?");

        if (confirmation.showAndWait().get() == ButtonType.OK) {
            service.supprimer(assurance);
            chargerDonnees(); // Rafraîchir la liste
        }
    }

    @FXML
    private void handleRefresh() {
        txtSearch.clear();
        chargerDonnees();
    }
}