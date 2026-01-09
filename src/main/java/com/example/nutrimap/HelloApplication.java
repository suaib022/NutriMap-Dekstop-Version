package com.example.nutrimap;
import com.example.nutrimap.dao.DatabaseManager;
import com.example.nutrimap.service.GitHubJsonDataService;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.IOException;
import javafx.application.HostServices;

public class HelloApplication extends Application {
    private static HelloApplication instance;
    
    public static HelloApplication getInstance() {
        return instance;
    }
    
    @Override
    public void start(Stage stage) throws IOException {
        instance = this;
        
        // Initialize database (for users, children, visits)
        DatabaseManager.getInstance();
        
        // Preload location data from GitHub in background
        GitHubJsonDataService.getInstance().preloadData();
        
        // Start with login view
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("view/login-view.fxml"));
        Parent root = fxmlLoader.load();
        Scene scene = new Scene(root);
        stage.setTitle("NutriMap - Login");
        stage.setScene(scene);
        stage.show();
    }
    
    public static void main(String[] args) {
        launch();
    }
}
