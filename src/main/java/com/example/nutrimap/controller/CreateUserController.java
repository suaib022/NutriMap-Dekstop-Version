package com.example.nutrimap.controller;
import com.example.nutrimap.dao.UserDAO;
import com.example.nutrimap.model.UserModel;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

public class CreateUserController {
    
    public enum Mode {
        CREATE, EDIT
    }
    
    @FXML private TextField nameField;
    @FXML private TextField emailField;
    @FXML private PasswordField passwordField;
    @FXML private ComboBox<String> roleCombo;
    @FXML private Label titleLabel;
    @FXML private Button submitButton;
    
    private UsersController parentController;
    private UserDAO userDAO;
    private Mode currentMode = Mode.CREATE;
    private UserModel editingUser = null;
    
    @FXML
    public void initialize() {
        userDAO = new UserDAO();
        roleCombo.getItems().addAll("ADMIN", "USER");
        roleCombo.getSelectionModel().select("USER");
    }
    
    public void setParentController(UsersController parentController) {
        this.parentController = parentController;
    }
    
    public void setMode(Mode mode, UserModel user) {
        this.currentMode = mode;
        this.editingUser = user;
        
        if (mode == Mode.EDIT && user != null) {
            titleLabel.setText("Edit User");
            submitButton.setText("Update User");
            nameField.setText(user.getName() != null ? user.getName() : "");
            emailField.setText(user.getEmail() != null ? user.getEmail() : "");
            passwordField.setText(user.getPassword() != null ? user.getPassword() : "");
            if (user.getRole() != null) {
                roleCombo.setValue(user.getRole());
            }
        }
    }
    
    @FXML
    private void handleCancel() {
        closeWindow();
    }
    
    @FXML
    private void handleCreate() {
        String name = nameField.getText() != null ? nameField.getText().trim() : "";
        String email = emailField.getText() != null ? emailField.getText().trim() : "";
        String password = passwordField.getText() != null ? passwordField.getText() : "";
        String role = roleCombo.getValue();
        
        if (name.isEmpty() || email.isEmpty() || password.isEmpty() || role == null) {
            showAlert("Error", "Please fill in all fields.");
            return;
        }
        
        if (currentMode == Mode.CREATE) {
            if (userDAO.getByEmail(email) != null) {
                showAlert("Error", "User with this email already exists.");
                return;
            }
            
            UserModel newUser = new UserModel(0, name, email, password, role, "");
            userDAO.addUser(newUser);
            
            closeWindow();
            
            if (parentController != null) {
                parentController.refreshTable();
                parentController.showSuccessAlert("Success", "User created successfully!");
            }
        } else if (currentMode == Mode.EDIT && editingUser != null) {
            UserModel existingUser = userDAO.getByEmail(email);
            if (existingUser != null && existingUser.getId() != editingUser.getId()) {
                showAlert("Error", "Another user with this email already exists.");
                return;
            }
            
            editingUser.setName(name);
            editingUser.setEmail(email);
            editingUser.setPassword(password);
            editingUser.setRole(role);
            userDAO.updateUser(editingUser);
            
            closeWindow();
            
            if (parentController != null) {
                parentController.refreshTable();
                parentController.showSuccessAlert("Success", "User updated successfully!");
            }
        }
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
