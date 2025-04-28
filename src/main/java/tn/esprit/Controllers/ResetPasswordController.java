package tn.esprit.Controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import tn.esprit.models.User;
import tn.esprit.services.UserService;
import tn.esprit.utils.Mail;
import java.net.URL;
import java.io.IOException;
import java.util.Random;

public class ResetPasswordController {

    @FXML private TextField txtEmail;
    @FXML private TextField txtVerificationCode;
    @FXML private PasswordField txtNewPassword;
    @FXML private PasswordField txtConfirmPassword;
    @FXML private Label lblMessage;

    private UserService userService = new UserService();
    private String generatedCode;
    private String userEmail;

    @FXML
    private void handleSendCode() {
        String email = txtEmail.getText().trim();

        if (email.isEmpty()) {
            showError("Please enter your email");
            return;
        }

        if (!userService.emailExists(email)) {
            showError("No account found with this email");
            return;
        }

        try {
            // Generate a 6-digit verification code
            generatedCode = String.format("%06d", new Random().nextInt(999999));
            userEmail = email;

            // Send email with verification code
            Mail mail = new Mail();
            String subject = "Password Reset Verification Code";
            String content = "Your verification code is: <strong>" + generatedCode + "</strong>";

            mail.sendEmail(email, subject, content);

            showSuccess("Verification code sent to your email");
        } catch (Exception e) {
            showError("Failed to send verification code: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void handleResetPassword() {
        String code = txtVerificationCode.getText().trim();
        String newPassword = txtNewPassword.getText();
        String confirmPassword = txtConfirmPassword.getText();

        if (code.isEmpty() || newPassword.isEmpty() || confirmPassword.isEmpty()) {
            showError("Please fill all fields");
            return;
        }

        if (!code.equals(generatedCode)) {
            showError("Invalid verification code");
            return;
        }

        if (!newPassword.equals(confirmPassword)) {
            showError("Passwords do not match");
            return;
        }

        // Validate password strength (same as registration)
        String passwordRegex = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$";
        if (!newPassword.matches(passwordRegex)) {
            showError("Password must be at least 8 characters with uppercase, lowercase, number and special character");
            return;
        }

        try {
            User user = userService.rechercherByEmail(userEmail);
            if (user != null) {
                user.setPassword(newPassword);
                userService.modifier(user);
                showSuccess("Password reset successfully");

                // Return to login page
                returnToLogin();
            } else {
                showError("User not found");
            }
        } catch (Exception e) {
            showError("Error resetting password: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // 1. Dans ResetPasswordController.java, modifiez légèrement returnToLogin():
    @FXML
    private void returnToLogin() {
        try {
            System.out.println("Attempting to load Login.fxml..."); // Debug
            URL url = getClass().getResource("/Login.fxml");
            if (url == null) {
                showError("Login.fxml not found!");
                System.err.println("Login.fxml not found at: " + System.getProperty("user.dir"));
                return;
            }
            Parent root = FXMLLoader.load(url);
            Stage stage = (Stage) txtEmail.getScene().getWindow();
            stage.setScene(new Scene(root));
        } catch (IOException e) {
            showError("Error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void showError(String message) {
        lblMessage.setText(message);
        lblMessage.setStyle("-fx-text-fill: red;");
    }

    private void showSuccess(String message) {
        lblMessage.setText(message);
        lblMessage.setStyle("-fx-text-fill: green;");
    }
}