package tn.esprit.Controllers.Patient;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.StackPane;
import javafx.util.StringConverter;
import tn.esprit.models.Reservation;
import tn.esprit.services.ReservationService;

import java.net.URL;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.ResourceBundle;

public class PatientReservationFormController implements Initializable {

    @FXML
    private ComboBox<String> comboDoctor;

    @FXML
    private TextField txtSubject;

    @FXML
    private DatePicker dpDate_res;

    @FXML
    private Button btnSauvegarder;

    @FXML
    private Button btnAnnuler;

    private ReservationService service;

    // Variables for reservation mode
    private int reservationId;
    private boolean isEditMode = false;

    // Map to hold doctor names and their IDs
    private Map<String, Integer> doctorNameIdMap;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        service = new ReservationService();

        // Initialize doctors list in ComboBox with names fetched from users table
        doctorNameIdMap = service.getDoctorNameIdMap();
        comboDoctor.getItems().addAll(doctorNameIdMap.keySet());

        // Configure date format
        configureDateFormat();

        // Set default date
        dpDate_res.setValue(LocalDate.now());

        System.out.println("PatientReservationFormController initialized");
    }

    private void configureDateFormat() {
        String pattern = "dd/MM/yyyy";
        StringConverter<LocalDate> converter = new StringConverter<>() {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern);

            @Override
            public String toString(LocalDate date) {
                if (date != null) {
                    return formatter.format(date);
                } else {
                    return "";
                }
            }

            @Override
            public LocalDate fromString(String string) {
                if (string != null && !string.isEmpty()) {
                    return LocalDate.parse(string, formatter);
                } else {
                    return null;
                }
            }
        };

        dpDate_res.setConverter(converter);

        dpDate_res.setPromptText("JJ/MM/AAAA");
    }

    /**
     * Method to set the values of an existing reservation in the form
     *
     * @param reservation The reservation to edit
     */
    public void setReservation(Reservation reservation) {
        if (reservation == null) {
            System.err.println("Error: reservation is null");
            return;
        }

        System.out.println("Setting form for reservation ID: " + reservation.getId());

        // Store the ID of the reservation
        this.reservationId = reservation.getId();
        this.isEditMode = true;

        // Change button text in edit mode
        btnSauvegarder.setText("Update");

        // Set values in the fields
        String doctorName = service.getDoctorNameById(reservation.getDoctor_id());
        comboDoctor.setValue(doctorName);
        txtSubject.setText(reservation.getSubject());

        // Convert dates
        if (reservation.getDate_res() != null) {
            LocalDate dateRes = new java.sql.Date(reservation.getDate_res().getTime()).toLocalDate();
            dpDate_res.setValue(dateRes);
        }
    }

    @FXML
    private void handleSauvegarder() {
        if (validateFields() && checkDoctorAvailability()) {
            try {
                Reservation reservation = new Reservation();

                // If in edit mode, set the ID
                if (isEditMode) {
                    reservation.setId(reservationId);
                }

                // Set properties
                String selectedDoctorName = comboDoctor.getValue();
                Integer doctorId = doctorNameIdMap.get(selectedDoctorName);
                reservation.setDoctor_id(doctorId);
                reservation.setSubject(txtSubject.getText());

                // Convert LocalDate to java.util.Date
                java.util.Date dateRes = java.sql.Date.valueOf(dpDate_res.getValue());
                reservation.setDate_res(new Timestamp(dateRes.getTime()));

                // Add or modify based on mode
                if (isEditMode) {
                    System.out.println("Updating reservation ID: " + reservationId);
                    service.updateReservation(reservation);
                    showMessage("Reservation updated successfully", Alert.AlertType.INFORMATION);
                } else {
                    System.out.println("Adding a new reservation");
                    service.addReservation(reservation);
                    showMessage("Reservation added successfully", Alert.AlertType.INFORMATION);
                }

                navigateToList();
            } catch (Exception e) {
                showMessage("Error during operation: " + e.getMessage(), Alert.AlertType.ERROR);
                e.printStackTrace();
            }
        }
    }

    private boolean validateFields() {
        StringBuilder errors = new StringBuilder();

        if (comboDoctor.getValue() == null)
            errors.append("- A doctor must be selected\n");

        if (txtSubject.getText().isEmpty())
            errors.append("- The subject is required\n");

        if (dpDate_res.getValue() == null)
            errors.append("- The reservation date is required\n");

        if (errors.length() > 0) {
            showMessage("Please correct the following errors:\n" + errors.toString(), Alert.AlertType.ERROR);
            return false;
        }

        return true;
    }

    private boolean checkDoctorAvailability() {
        String selectedDoctorName = comboDoctor.getValue();
        Integer doctorId = doctorNameIdMap.get(selectedDoctorName);

        if (doctorId == null) {
            showMessage("Invalid doctor selection. Please choose a valid doctor.", Alert.AlertType.ERROR);
            return false;
        }

        LocalDate date = dpDate_res.getValue();
        boolean isAvailable = service.isDoctorAvailable("Dr. " + doctorId, java.sql.Date.valueOf(date));
        if (!isAvailable) {
            showMessage("The selected doctor is not available on the chosen date. Please select a different date or doctor.", Alert.AlertType.ERROR);
        }

        return isAvailable;
    }

    private void showMessage(String message, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(type == Alert.AlertType.ERROR ? "Error" : "Success");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    @FXML
    private void handleAnnuler() {
        navigateToList();
    }

    private void navigateToList() {
        try {
            StackPane contentArea = (StackPane) comboDoctor.getScene().lookup("#contentArea");
            Label pageTitleLabel = (Label) comboDoctor.getScene().lookup("#pageTitleLabel");

            if (pageTitleLabel != null) {
                pageTitleLabel.setText("List of Reservations");
            }

            if (contentArea != null) {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/patient/PatientReservation.fxml"));
                Parent view = loader.load();

                PatientReservationController controller = loader.getController();
                controller.chargerDonnees();

                contentArea.getChildren().clear();
                contentArea.getChildren().add(view);
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Navigation error: " + e.getMessage());
        }
    }
}
