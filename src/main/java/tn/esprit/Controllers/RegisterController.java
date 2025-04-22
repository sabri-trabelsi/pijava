package tn.esprit.Controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import tn.esprit.models.User;
import tn.esprit.services.UserService;

import java.io.IOException;

public class RegisterController {

    @FXML private TextField txtName, txtLastName, txtEmail, txtNumTel, txtAge, txtAdresse;
    @FXML private PasswordField txtPassword;
    @FXML private ComboBox<String> comboRole, comboSpecialty;
    @FXML private Label lblMessage;

    private final UserService userService = new UserService();

    @FXML
    public void initialize() {
        comboRole.getItems().addAll("Patient", "Doctor");
        comboRole.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            comboSpecialty.setVisible("Doctor".equals(newVal));
            if (!"Doctor".equals(newVal)) comboSpecialty.getSelectionModel().clearSelection();
        });
    }

    @FXML
    private void handleRegister() {
        if (!validateInputs()) return;

        User newUser = new User();
        newUser.setName(txtName.getText());
        newUser.setLastName(txtLastName.getText());
        newUser.setEmail(txtEmail.getText());
        newUser.setPassword(txtPassword.getText());
        newUser.setNumTel(txtNumTel.getText());
        newUser.setRole(comboRole.getValue());
        newUser.setAge(Integer.parseInt(txtAge.getText()));
        newUser.setAdresse(txtAdresse.getText());
        newUser.setSpecialty("Doctor".equals(comboRole.getValue()) ? comboSpecialty.getValue() : null);
        newUser.setIs_banned(false);
        newUser.setEnabled(true);

        userService.ajouter(newUser);
        lblMessage.setText("Registration successful! Please login.");
        navigateTo("/Login.fxml");
    }

    @FXML
    private void handleLoginLink() {
        navigateTo("/Login.fxml");
    }

    private boolean validateInputs() {
        StringBuilder errors = new StringBuilder();
        String nameRegex = "^[a-zA-Z]{5,10}$"; // Name: only characters, min length 5, max length 10
        String emailRegex = "^[A-Za-z0-9+_.-]+@(.+)$"; // Simple email validation regex
        String numTelRegex = "^[0-9]+$"; // Only numbers
        String passwordRegex = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$"; // At least one lowercase, one uppercase, one symbol, one number, min 8 chars

        // Validate Name
        if (txtName.getText().isEmpty() || !txtName.getText().matches(nameRegex))
            errors.append("- Name is required, must be 5-10 characters and letters only\n");

        // Validate Last Name
        if (txtLastName.getText().isEmpty() || !txtLastName.getText().matches(nameRegex))
            errors.append("- Last Name is required, must be 5-10 characters and letters only\n");

        // Validate Email
        if (txtEmail.getText().isEmpty() || !txtEmail.getText().matches(emailRegex))
            errors.append("- Email is required and must be a valid email format\n");

        // Validate Adresse
        if (txtAdresse.getText().isEmpty())
            errors.append("- Adresse is required\n");

        // Validate Num Tel
        if (txtNumTel.getText().isEmpty() || !txtNumTel.getText().matches(numTelRegex))
            errors.append("- Num Tel is required and must be numbers only\n");

        // Validate Age
        if (txtAge.getText().isEmpty() || !txtAge.getText().matches(numTelRegex))
            errors.append("- Age is required and must be numbers only\n");

        // Validate Password
        if (txtPassword.getText().isEmpty() || !txtPassword.getText().matches(passwordRegex))
            errors.append("- Password is required, must be secure (at least one uppercase, one lowercase, one digit, one special character, and minimum 8 characters)\n");

        // Validate Role
        if (comboRole.getValue() == null)
            errors.append("- Role is required\n");

        // Validate Specialty (if role is Doctor)
        if (comboRole.getValue() != null && comboRole.getValue().equals("Doctor") && comboSpecialty.getValue() == null)
            errors.append("- Specialty is required for role Doctor\n");



        // Show errors
        if (errors.length() > 0) {
            afficherMessage("Please fix the following errors:\n" + errors.toString(), Alert.AlertType.ERROR);
            return false;
        }

        return true;
    }

    private void afficherMessage(String message, Alert.AlertType alertType) {
        Alert alert = new Alert(alertType);
        alert.setTitle(alertType == Alert.AlertType.ERROR ? "Error" : "Information");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void navigateTo(String fxmlFile) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource(fxmlFile));
            Stage stage = (Stage) txtName.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}