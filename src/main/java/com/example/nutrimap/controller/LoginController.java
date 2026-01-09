package com.example.nutrimap.controller;

import com.example.nutrimap.HelloApplication;
import com.example.nutrimap.dao.UserDAO;
import com.example.nutrimap.model.UserModel;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;

public class LoginController {
    
    @FXML private TextField emailField;
    @FXML private PasswordField passwordField;
    @FXML private Label errorLabel;
    
    private UserDAO userDAO;
    
    @FXML
    public void initialize() {
        userDAO = new UserDAO();
        errorLabel.setText("");
    }
    
    @FXML
    private void handleLogin() {
        String email = emailField.getText() != null ? emailField.getText().trim() : "";
        String password = passwordField.getText() != null ? passwordField.getText().trim() : "";
        
        // Validate inputs
        if (email.isEmpty()) {
            showError("Please enter your email.");
            return;
        }
        
        if (password.isEmpty()) {
            showError("Please enter your password.");
            return;
        }
        
        // Authenticate user
        UserModel user = userDAO.authenticate(email, password);
        
        if (user != null) {
            // Login successful - navigate to dashboard
            navigateToDashboard(user);
        } else {
            showError("Invalid email or password.");
        }
    }
    
    private void showError(String message) {
        errorLabel.setText(message);
    }
    
    private void navigateToDashboard(UserModel user) {
        try {
            FXMLLoader loader = new FXMLLoader(HelloApplication.class.getResource("view/dashboard-view.fxml"));
            Parent root = loader.load();
            
            DashboardController dashboardController = loader.getController();
            dashboardController.setLoggedUser(user);
            
            Stage stage = (Stage) emailField.getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setTitle("NutriMap Desktop");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showError("Error loading dashboard.");
        }
    }
}
