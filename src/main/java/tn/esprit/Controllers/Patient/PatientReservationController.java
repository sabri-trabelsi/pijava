package tn.esprit.Controllers.Patient;

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
import tn.esprit.models.Reservation;
import tn.esprit.services.ReservationService;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class PatientReservationController implements Initializable {

    @FXML
    private TableView<Reservation> reservationTable;

    @FXML
    private TableColumn<Reservation, String> doctorColumn;

    @FXML
    private TableColumn<Reservation, String> subjectColumn;

    @FXML
    private TableColumn<Reservation, String> date_resColumn;

    @FXML
    private TableColumn<Reservation, String> statutColumn;

    @FXML
    private TableColumn<Reservation, Void> actionsColumn;

    @FXML
    private TextField txtSearch;

    private ReservationService service;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        System.out.println("Initialisation de PatientReservationController");
        service = new ReservationService();

        // Configuration des colonnes
        doctorColumn.setCellValueFactory(new PropertyValueFactory<>("doctorName")); // Fetch doctor's name from the User table
        subjectColumn.setCellValueFactory(new PropertyValueFactory<>("subject"));
        date_resColumn.setCellValueFactory(new PropertyValueFactory<>("date_res"));
        statutColumn.setCellValueFactory(new PropertyValueFactory<>("statut"));

        // Configuration de la colonne d'actions
        configurerColonneActions();

        // Chargement des données
        chargerDonnees();
    }

    private void configurerColonneActions() {
        Callback<TableColumn<Reservation, Void>, TableCell<Reservation, Void>> cellFactory = new Callback<>() {
            @Override
            public TableCell<Reservation, Void> call(final TableColumn<Reservation, Void> param) {
                return new TableCell<>() {
                    private final Button btnModifier = new Button("Modifier");
                    private final Button btnSupprimer = new Button("Supprimer");
                    private final HBox hbox = new HBox(5, btnModifier, btnSupprimer);

                    {
                        btnModifier.getStyleClass().add("edit-button");
                        btnSupprimer.getStyleClass().add("delete-button");
                        hbox.setAlignment(javafx.geometry.Pos.CENTER);

                        btnModifier.setOnAction(event -> {
                            Reservation reservation = getTableView().getItems().get(getIndex());
                            modifierReservation(reservation);
                        });

                        btnSupprimer.setOnAction(event -> {
                            Reservation reservation = getTableView().getItems().get(getIndex());
                            supprimerReservation(reservation);
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
            var reservations = service.getAllReservationsWithDoctorDetails(); // Fetch reservations with doctor names from User table
            if (reservations instanceof List) {
                System.out.println("Nombre de réservations trouvées: " + ((List<?>) reservations).size());
                reservationTable.setItems(FXCollections.observableArrayList((List<Reservation>) reservations));
            } else {
                System.err.println("Erreur: Le résultat n'est pas une liste de réservations");
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Erreur lors du chargement des données: " + e.getMessage());
        }
    }

    @FXML
    private void handleSearch() {
        String query = txtSearch.getText().trim();
        if (!query.isEmpty()) {
            var reservations = service.searchReservationsByDoctorName(query); // Search based on doctor's name from User table
            reservationTable.setItems(FXCollections.observableArrayList((List<Reservation>) reservations));
        } else {
            chargerDonnees(); // Si la recherche est vide, afficher toutes les réservations
        }
    }

    private void modifierReservation(Reservation reservation) {
        try {
            StackPane contentArea = (StackPane) reservationTable.getScene().lookup("#contentArea");
            Label pageTitleLabel = (Label) reservationTable.getScene().lookup("#pageTitleLabel");

            if (pageTitleLabel != null) {
                pageTitleLabel.setText("Modifier Reservation");
            }

            if (contentArea != null) {
                FXMLLoader loader = new FXMLLoader();
                loader.setLocation(getClass().getResource("/patient/PatientReservationForm.fxml"));
                Parent view = loader.load();

                PatientReservationFormController formController = loader.getController();
                formController.setReservation(reservation);

                contentArea.getChildren().clear();
                contentArea.getChildren().add(view);
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Erreur de navigation: " + e.getMessage());
        }
    }

    private void supprimerReservation(Reservation reservation) {
        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
        confirmation.setTitle("Confirmation de suppression");
        confirmation.setHeaderText(null);
        confirmation.setContentText("Êtes-vous sûr de vouloir supprimer la réservation pour le docteur '"
                + reservation.getDoctorName() + "' ?"); // Fetch the doctor's name from User table

        if (confirmation.showAndWait().get() == ButtonType.OK) {
            service.deleteReservation(reservation);
            chargerDonnees(); // Rafraîchir la liste
        }
    }

    @FXML
    private void handleRefresh() {
        txtSearch.clear();
        chargerDonnees();
    }

    @FXML
    private void handleAddReservation() {
        try {
            StackPane contentArea = (StackPane) reservationTable.getScene().lookup("#contentArea");
            Label pageTitleLabel = (Label) reservationTable.getScene().lookup("#pageTitleLabel");

            if (pageTitleLabel != null) {
                pageTitleLabel.setText("Ajouter Reservation");
            }

            if (contentArea != null) {
                FXMLLoader loader = new FXMLLoader();
                loader.setLocation(getClass().getResource("/patient/PatientReservationForm.fxml"));
                Parent view = loader.load();

                contentArea.getChildren().clear();
                contentArea.getChildren().add(view);
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Erreur de navigation: " + e.getMessage());
        }
    }
}
