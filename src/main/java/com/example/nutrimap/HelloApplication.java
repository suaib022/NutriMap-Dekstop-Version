package com.example.nutrimap;
import com.example.nutrimap.controller.DashboardController;
import com.example.nutrimap.dao.DatabaseManager;
import com.example.nutrimap.dao.JsonToSqliteImporter;
import com.example.nutrimap.dao.UserDAO;
import com.example.nutrimap.model.UserModel;
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
        
        DatabaseManager.getInstance();
        JsonToSqliteImporter importer = new JsonToSqliteImporter();
        importer.importAllData();
        
        UserDAO userDAO = new UserDAO();
        UserModel adminUser = userDAO.getById(1);
        if (adminUser == null) {
            adminUser = new UserModel(1, "Fallback Admin", "admin@gmail.com", "123", "ADMIN", "");
        }
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("view/dashboard-view.fxml"));
        Parent root = fxmlLoader.load();
        com.example.nutrimap.controller.DashboardController dashboardController = fxmlLoader.getController();
        dashboardController.setLoggedUser(adminUser);
        Scene scene = new Scene(root);
        stage.setTitle("NutriMap Desktop");
        stage.setScene(scene);
        stage.show();
    }
    public static void main(String[] args) {
        launch();
    }
}
