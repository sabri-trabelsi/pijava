package tn.esprit.Controllers;


import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.StackPane;
import javafx.util.StringConverter;
import tn.esprit.models.User;
import tn.esprit.services.UserService;

import java.net.URL;
import java.util.ResourceBundle;

public class UserFormController implements Initializable {

    @FXML
    private TextField txtName;

    @FXML
    private TextField txtLastName;

    @FXML
    private TextField txtEmail;

    @FXML
    private TextField txtAdresse;

    @FXML
    private TextField txtNumTel;

    @FXML
    private TextField txtAge;

    @FXML
    private PasswordField txtPassword;

    @FXML
    private ComboBox<String> comboRole;

    @FXML
    private ComboBox<String> comboSpecialty;

    @FXML
    private ComboBox<String> comboBanned;

    @FXML
    private ComboBox<String> comboEnable;

    @FXML
    private Button btnSauvegarder;

    @FXML
    private Button btnAnnuler;

    private UserService userService;
    private User currentUser;  // Track user for edit mode
    private boolean isEditMode = false;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        userService = new UserService();

        // Initialize role ComboBox
        comboRole.getItems().addAll("Admin", "Doctor", "Patient");

        // Initialize specialty ComboBox
        comboSpecialty.getItems().addAll("Generaliste", "Psycotherapeute");
        comboSpecialty.setVisible(false); // Hide specialty by default

        // Initialize banned and enable ComboBoxes
        comboBanned.getItems().addAll("Yes", "No");
        comboEnable.getItems().addAll("Enable", "Disable");

        // Add listener to show specialty when role "Doctor" is selected
        comboRole.setOnAction(event -> {
            String selectedRole = comboRole.getValue();
            if ("Doctor".equals(selectedRole)) {
                comboSpecialty.setVisible(true);
            } else {
                comboSpecialty.setVisible(false);
                comboSpecialty.setValue(null); // Clear specialty selection if not Doctor
            }
        });

        System.out.println("UserFormController initialized");
    }

    @FXML
    private void handleSauvegarder() {
        if (validerChamps()) {
            try {
                // Convert ComboBox values to boolean
                boolean isBanned = "Yes".equals(comboBanned.getValue());
                boolean isEnabled = "Enable".equals(comboEnable.getValue());

                // Create/update User object
                User user = isEditMode ? currentUser : new User();
                user.setName(txtName.getText());
                user.setLastName(txtLastName.getText());
                user.setEmail(txtEmail.getText());
                user.setAdresse(txtAdresse.getText());
                user.setNumTel(txtNumTel.getText());
                user.setAge(Integer.parseInt(txtAge.getText()));
                user.setPassword(txtPassword.getText());
                user.setRole(comboRole.getValue());
                user.setSpecialty(comboSpecialty.isVisible() ? comboSpecialty.getValue() : null);
                user.setIs_banned(isBanned);
                user.setEnabled(isEnabled);
                // Handle password only if provided
                String passwordInput = txtPassword.getText();
                if (isEditMode) {
                    // Edit mode: Only update password if field is not empty
                    if (!passwordInput.isEmpty()) {
                        user.setPassword(passwordInput); // New password
                    }
                    userService.modifier(user);
                } else {
                    // Add mode: Password is required
                    user.setPassword(passwordInput);
                    userService.ajouter(user);
                }
                // Save to database
                if (isEditMode) {
                    userService.modifier(user);
                } else {
                    userService.ajouter(user);
                }

                afficherMessage("User saved successfully", Alert.AlertType.INFORMATION);
                navigateToList();
            } catch (Exception e) {
                afficherMessage("Error saving user: " + e.getMessage(), Alert.AlertType.ERROR);
                e.printStackTrace();
            }
        }
    }

    private void navigateToList() {
        try {
            StackPane contentArea = (StackPane) btnSauvegarder.getScene().lookup("#contentArea");
            if (contentArea != null) {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/UsersView.fxml"));
                Parent view = loader.load();
                contentArea.getChildren().clear();
                contentArea.getChildren().add(view);
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Error navigating to user list: " + e.getMessage());
        }
    }

    private void afficherMessage(String message, Alert.AlertType alertType) {
        Alert alert = new Alert(alertType);
        alert.setTitle(alertType == Alert.AlertType.ERROR ? "Error" : "Information");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private boolean validerChamps() {
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

        // Validate Banned Status
        if (comboBanned.getValue() == null)
            errors.append("- Banned status is required\n");

        // Validate Enable Status
        if (comboEnable.getValue() == null)
            errors.append("- Enable status is required\n");

        // Show errors
        if (errors.length() > 0) {
            afficherMessage("Please fix the following errors:\n" + errors.toString(), Alert.AlertType.ERROR);
            return false;
        }

        return true;
    }


    public void setUser(User user) {
        if (user != null) {
            this.currentUser = user;
            this.isEditMode = true;
            txtName.setText(user.getName());
            txtLastName.setText(user.getLastName());
            txtEmail.setText(user.getEmail());
            txtAdresse.setText(user.getAdresse());
            txtNumTel.setText(user.getNumTel());
            txtAge.setText(String.valueOf(user.getAge()));
            txtPassword.setText(user.getPassword()); // Set password
            comboRole.setValue(user.getRole());
            comboSpecialty.setValue(user.getSpecialty());
            comboSpecialty.setVisible("Doctor".equals(user.getRole()));
            comboBanned.setValue(user.isIs_banned() ? "Yes" : "No");
            comboEnable.setValue(user.isEnabled() ? "Enable" : "Disable");
            txtPassword.setText(""); // Leave blank intentionally
        }
    }

    @FXML
    private void handleAnnuler() {
        // Confirm cancellation
        Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmAlert.setTitle("Confirmation");
        confirmAlert.setHeaderText(null);
        confirmAlert.setContentText("Are you sure you want to cancel and go back to the user list?");
        ButtonType result = confirmAlert.showAndWait().orElse(ButtonType.CANCEL);

        if (result == ButtonType.OK) {
            // Navigate back to user list
            navigateToList();
        }
    }
}