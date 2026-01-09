package com.example.nutrimap.controller;
import com.example.nutrimap.HelloApplication;
import com.example.nutrimap.model.UserModel;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;

import java.io.IOException;

public class DashboardController {
    @FXML private StackPane contentArea;
    @FXML private Label userNameLabel;
    @FXML private Label userRoleLabel;
    @FXML private Circle avatarCircle;
    
    // Sidebar buttons for role-based visibility
    @FXML private Button homeButton;
    @FXML private Button profileButton;
    @FXML private Button usersButton;
    @FXML private Button branchesButton;
    @FXML private Button childrenButton;
    @FXML private Button visitsButton;
    
    private UserModel loggedUser;
    
    @FXML
    public void initialize() {
        showHome();
    }
    
    public void setLoggedUser(UserModel user) {
        this.loggedUser = user;
        if (user != null) {
            userNameLabel.setText(user.getName());
            userRoleLabel.setText(user.getRole());
            applyRoleBasedAccess(user.getRole());
        }
    }
    
    /**
     * Apply role-based access control to sidebar buttons.
     * 
     * ADMIN: Full access to everything
     * SUPERVISOR: Same as Admin except Users management
     * FIELD_WORKER: Children, Visits, Profile only
     */
    private void applyRoleBasedAccess(String role) {
        if (role == null) return;
        
        String normalizedRole = role.toUpperCase().replace(" ", "_");
        
        switch (normalizedRole) {
            case "ADMIN":
                // Admin has full access - all buttons visible
                break;
                
            case "SUPERVISOR":
                // Supervisor: hide Users button only
                usersButton.setVisible(false);
                usersButton.setManaged(false);
                break;
                
            case "FIELD_WORKER":
            case "FIELDWORKER":
                // Field Worker: only Children, Visits, Profile visible
                // Hide: Home (dashboard), Users, Branches
                homeButton.setVisible(false);
                homeButton.setManaged(false);
                usersButton.setVisible(false);
                usersButton.setManaged(false);
                branchesButton.setVisible(false);
                branchesButton.setManaged(false);
                
                // For field worker, start with children view instead of home
                showChildren();
                break;
                
            default:
                // Unknown role - restrict to minimal access
                usersButton.setVisible(false);
                usersButton.setManaged(false);
                branchesButton.setVisible(false);
                branchesButton.setManaged(false);
                break;
        }
    }
    
    @FXML
    private void showHome() {
        loadView("/com/example/nutrimap/view/home-view.fxml", null);
    }
    
    @FXML
    private void showProfile() {
        loadView("/com/example/nutrimap/view/profile-view.fxml", controller -> {
            if (controller instanceof ProfileController) {
                ((ProfileController) controller).setUser(loggedUser);
            }
        });
    }
    
    @FXML
    private void showUsers() {
        loadView("/com/example/nutrimap/view/users-view.fxml", null);
    }
    
    @FXML
    private void showBranches() {
        loadView("/com/example/nutrimap/view/branches-view.fxml", null);
    }
    
    @FXML
    private void showChildren() {
        loadView("/com/example/nutrimap/view/children-view.fxml", null);
    }
    
    @FXML
    private void showVisits() {
        loadView("/com/example/nutrimap/view/visits-view.fxml", null);
    }
    
    @FXML
    private void handleLogout() {
        try {
            FXMLLoader loader = new FXMLLoader(HelloApplication.class.getResource("view/login-view.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) contentArea.getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setTitle("NutriMap - Login");
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(0);
        }
    }
    
    private interface ControllerInitializer {
        void init(Object controller);
    }
    
    private void loadView(String fxmlPath, ControllerInitializer initializer) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent view = loader.load();
            if (initializer != null) {
                initializer.init(loader.getController());
            }
            contentArea.getChildren().clear();
            contentArea.getChildren().add(view);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
