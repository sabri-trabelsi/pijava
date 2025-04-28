package tn.esprit.Controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import tn.esprit.models.User;
import tn.esprit.services.UserService;

import java.io.IOException;

public class LoginController {

    // FXML Components
    @FXML private TextField txtEmail;
    @FXML private PasswordField txtPassword;
    @FXML private Label lblMessage;

    // Services
    private final UserService userService = new UserService();

    // Login Handler
    @FXML
    private void handleLogin() {
        String email = txtEmail.getText().trim();
        String password = txtPassword.getText().trim();

        if (!validateInputs(email, password)) return;

        try {
            User authenticatedUser = userService.authenticate(email, password);
            if (authenticatedUser != null) {
                handleSuccessfulLogin(authenticatedUser);
            } else {
                showError("Invalid email or password");
            }
        } catch (Exception e) {
            showError("Login error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // Input Validation
    private boolean validateInputs(String email, String password) {
        if (email.isEmpty() || password.isEmpty()) {
            showError("Please fill in all fields");
            return false;
        }
        return true;
    }

    // Successful Login Handling
    private void handleSuccessfulLogin(User user) {
        if (!user.isEnabled()) {
            showError("Account is disabled");
            return;
        }
        if (user.isIs_banned()) {
            showError("Account is banned");
            return;
        }


        try {
            redirectToDashboard(user);
        } catch (IOException e) {
            showError("Error loading dashboard: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // Dashboard Redirection
    private void redirectToDashboard(User user) throws IOException {
        String dashboardPath = switch (user.getRole()) {
            case "Admin" -> "/MainLayout.fxml";
            case "Doctor" -> "/doctor/DoctorLayout.fxml";
            case "Patient" -> "/PatientLayout.fxml";
            default -> throw new IllegalArgumentException("Unknown role: " + user.getRole());
        };

        FXMLLoader loader = new FXMLLoader(getClass().getResource(dashboardPath));
        Parent root = loader.load();

        // Set user directly to controller
        if (loader.getController() instanceof MainLayoutController) {
            ((MainLayoutController) loader.getController()).setCurrentUser(user);
        } else if (loader.getController() instanceof PatientDashboardController) {
            ((PatientDashboardController) loader.getController()).setCurrentUser(user);
        } else if (loader.getController() instanceof DoctorDashboardController) {
            ((DoctorDashboardController) loader.getController()).setCurrentUser(user);
        }

        Stage stage = (Stage) txtEmail.getScene().getWindow();
        stage.setScene(new Scene(root));
        stage.setMaximized(true);
    }

    // Registration Navigation
    @FXML
    private void handleRegisterLink() {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/Register.fxml"));
            Stage stage = (Stage) txtEmail.getScene().getWindow();
            stage.setScene(new Scene(root));
        } catch (IOException e) {
            showError("Error loading registration form");
            e.printStackTrace();
        }
    }

    // Error Display
    private void showError(String message) {
        lblMessage.setText(message);
        lblMessage.setStyle("-fx-text-fill: red;");
    }
    @FXML
    private void handleForgotPassword() {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/ResetPassword.fxml"));
            Stage stage = (Stage) txtEmail.getScene().getWindow();
            stage.setScene(new Scene(root));
        } catch (IOException e) {
            showError("Error loading password reset form");
            e.printStackTrace();
        }
    }
}