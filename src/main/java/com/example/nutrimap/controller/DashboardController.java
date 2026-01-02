package com.example.nutrimap.controller;
import com.example.nutrimap.HelloApplication;
import com.example.nutrimap.model.UserModel;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Circle;
import java.io.IOException;
import java.util.Objects;
public class DashboardController {
    @FXML private StackPane contentArea;
    @FXML private Label userNameLabel;
    @FXML private Label userRoleLabel;
    @FXML private Circle avatarCircle;
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
        System.exit(0);
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
