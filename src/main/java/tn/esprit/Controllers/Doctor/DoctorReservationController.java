package tn.esprit.Controllers.Doctor;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.util.Callback;
import tn.esprit.models.Reservation;
import tn.esprit.services.ReservationService;

import java.net.URL;
import java.util.ResourceBundle;

public class DoctorReservationController implements Initializable {

    @FXML
    private VBox header;

    @FXML
    private TableView<Reservation> reservationTable;

    @FXML
    private TableColumn<Reservation, String> patientColumn;

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

    @FXML
    private Button btnSearch;

    @FXML
    private Button btnRefresh;

    private ReservationService service;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        service = new ReservationService();

        // Configure columns
        patientColumn.setCellValueFactory(new PropertyValueFactory<>("patientName"));
        subjectColumn.setCellValueFactory(new PropertyValueFactory<>("subject"));
        date_resColumn.setCellValueFactory(new PropertyValueFactory<>("date_res"));
        statutColumn.setCellValueFactory(new PropertyValueFactory<>("statut"));

        // Configure actions column
        setupActionsColumn();

        // Load data
        loadReservations();
    }

    private void setupActionsColumn() {
        Callback<TableColumn<Reservation, Void>, TableCell<Reservation, Void>> cellFactory = new Callback<>() {
            @Override
            public TableCell<Reservation, Void> call(final TableColumn<Reservation, Void> param) {
                return new TableCell<>() {
                    private final Button btnAccept = new Button("Accepter");
                    private final Button btnRefuse = new Button("Refuser");
                    private final Button btnDelete = new Button("Supprimer");
                    private final HBox hbox = new HBox(5, btnAccept, btnRefuse, btnDelete);

                    {
                        btnAccept.getStyleClass().add("accept-button");
                        btnRefuse.getStyleClass().add("refuse-button");
                        btnDelete.getStyleClass().add("delete-button");
                        hbox.setAlignment(javafx.geometry.Pos.CENTER);

                        btnAccept.setOnAction(event -> {
                            Reservation reservation = getTableView().getItems().get(getIndex());
                            if (canModifyReservation(reservation)) {
                                acceptReservation(reservation);
                            } else {
                                showError("Vous ne pouvez pas modifier cette réservation.");
                            }
                        });

                        btnRefuse.setOnAction(event -> {
                            Reservation reservation = getTableView().getItems().get(getIndex());
                            if (canModifyReservation(reservation)) {
                                refuseReservation(reservation);
                            } else {
                                showError("Vous ne pouvez pas modifier cette réservation.");
                            }
                        });

                        btnDelete.setOnAction(event -> {
                            Reservation reservation = getTableView().getItems().get(getIndex());
                            if (canModifyReservation(reservation)) {
                                deleteReservation(reservation);
                            } else {
                                showError("Vous ne pouvez pas supprimer cette réservation.");
                            }
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

    private boolean canModifyReservation(Reservation reservation) {
        // Logic to check if the reservation belongs to the logged-in doctor
        int loggedInDoctorId = getLoggedInDoctorId(); // Replace with actual session management logic to get logged-in doctor ID
        return reservation.getDoctor_id() == loggedInDoctorId;
    }

    private int getLoggedInDoctorId() {
        // Replace this method with actual logic to retrieve the ID of the logged-in doctor
        // For example, from a session management system or authentication service
        return 123; // Placeholder value
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Erreur");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void loadReservations() {
        try {
            var reservations = service.rechercher(); // Fetch all reservations
            reservationTable.setItems(FXCollections.observableArrayList(reservations));
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Erreur lors du chargement des données: " + e.getMessage());
        }
    }

    @FXML
    private void handleSearch() {
        String query = txtSearch.getText().trim();
        if (!query.isEmpty()) {
            var reservations = service.rechercher(query); // Search reservations by query
            reservationTable.setItems(FXCollections.observableArrayList(reservations));
        } else {
            loadReservations(); // If search is empty, load all reservations
        }
    }

    private void acceptReservation(Reservation reservation) {
        reservation.setStatut("ACCEPTED");
        service.updateReservation(reservation);
        loadReservations(); // Refresh the list
    }

    private void refuseReservation(Reservation reservation) {
        reservation.setStatut("REFUSED");
        service.updateReservation(reservation);
        loadReservations(); // Refresh the list
    }

    private void deleteReservation(Reservation reservation) {
        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
        confirmation.setTitle("Confirmation de suppression");
        confirmation.setHeaderText(null);
        confirmation.setContentText("Êtes-vous sûr de vouloir supprimer la réservation pour le patient '"
                + reservation.getPatientName() + "' ?");

        if (confirmation.showAndWait().get() == ButtonType.OK) {
            service.deleteReservation(reservation);
            loadReservations(); // Refresh the list
        }
    }

    @FXML
    private void handleRefresh() {
        txtSearch.clear();
        loadReservations();
    }
}
