package com.example.nutrimap.controller;
import com.example.nutrimap.model.UserModel;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.shape.Circle;
public class ProfileController {
    @FXML private Circle avatarCircle;
    @FXML private Label nameLabel;
    @FXML private Label emailLabel;
    @FXML private Label roleLabel;
    public void setUser(UserModel user) {
        if (user != null) {
            nameLabel.setText(user.getName());
            emailLabel.setText(user.getEmail());
            roleLabel.setText(user.getRole());
        }
    }
}
