package com.example.nutrimap.controller;
import com.example.nutrimap.dao.UserDAO;
import com.example.nutrimap.model.UserModel;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
public class CreateUserController {
    @FXML private TextField nameField;
    @FXML private TextField emailField;
    @FXML private PasswordField passwordField;
    @FXML private ComboBox<String> roleCombo;
    private UsersController parentController;
    private UserDAO userDAO;
    @FXML
    public void initialize() {
        userDAO = new UserDAO();
        roleCombo.getItems().addAll("ADMIN", "USER");
        roleCombo.getSelectionModel().select("USER");
    }
    public void setParentController(UsersController parentController) {
        this.parentController = parentController;
    }
    @FXML
    private void handleCancel() {
        closeWindow();
    }
    @FXML
    private void handleCreate() {
        String name = nameField.getText();
        String email = emailField.getText();
        String password = passwordField.getText();
        String role = roleCombo.getValue();
        if (name.isEmpty() || email.isEmpty() || password.isEmpty() || role == null) {
            showAlert("Error", "Please fill in all fields.");
            return;
        }
        if (userDAO.getByEmail(email) != null) {
            showAlert("Error", "User with this email already exists.");
            return;
        }
        UserModel newUser = new UserModel(0, name, email, password, role, "");  
        userDAO.addUser(newUser);
        if (parentController != null) {
            parentController.refreshTable();
        }
        closeWindow();
    }
    private void closeWindow() {
        Stage stage = (Stage) nameField.getScene().getWindow();
        stage.close();
    }
    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
