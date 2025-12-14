package com.example.nutrimap.controller;
import com.example.nutrimap.dao.UserDAO;
import com.example.nutrimap.model.UserModel;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import java.io.IOException;
public class UsersController {
    @FXML private TextField searchField;
    @FXML private TableView<UserModel> usersTable;
    @FXML private TableColumn<UserModel, Integer> colId;
    @FXML private TableColumn<UserModel, String> colName;
    @FXML private TableColumn<UserModel, String> colEmail;
    @FXML private TableColumn<UserModel, String> colRole;
    @FXML private TableColumn<UserModel, String> colStatus;
    @FXML private TableColumn<UserModel, String> colImage;
    @FXML private TableColumn<UserModel, Boolean> colCheck;
    @FXML private Label resultsLabel;
    @FXML private Pagination pagination;
    private UserDAO userDAO;
    private FilteredList<UserModel> filteredData;
    private static final int ROWS_PER_PAGE = 10;
    @FXML
    public void initialize() {
        userDAO = new UserDAO();
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colName.setCellValueFactory(new PropertyValueFactory<>("name"));
        colEmail.setCellValueFactory(new PropertyValueFactory<>("email"));
        colRole.setCellValueFactory(new PropertyValueFactory<>("role"));
        colStatus.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) setText(null);
                else setText("Active");
            }
        });
        colImage.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) setText(null);
                else setText("👤");
            }
        });
        colCheck.setCellFactory(col -> new TableCell<>() {
            private final CheckBox checkBox = new CheckBox();
            @Override
            protected void updateItem(Boolean item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) setGraphic(null);
                else setGraphic(checkBox);
            }
        });
        colEmail.prefWidthProperty().bind(usersTable.widthProperty()
            .subtract(colId.widthProperty())
            .subtract(colImage.widthProperty())
            .subtract(colName.widthProperty())
            .subtract(colRole.widthProperty())
            .subtract(colStatus.widthProperty())
            .subtract(colCheck.widthProperty())
            .subtract(20));  
        filteredData = new FilteredList<>(userDAO.getObservableUsers(), p -> true);
        pagination.setPageFactory(this::createPage);
        updatePagination();
        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            filteredData.setPredicate(user -> {
                if (newValue == null || newValue.isEmpty()) {
                    return true;
                }
                String lower = newValue.toLowerCase();
                return user.getName().toLowerCase().contains(lower) || 
                       user.getEmail().toLowerCase().contains(lower) ||
                       user.getRole().toLowerCase().contains(lower);
            });
            updatePagination();
        });
    }
    private void updatePagination() {
        int totalItems = filteredData.size();
        int pageCount = (totalItems / ROWS_PER_PAGE) + (totalItems % ROWS_PER_PAGE > 0 ? 1 : 0);
        pagination.setPageCount(Math.max(1, pageCount));
        pagination.setCurrentPageIndex(0);
        updateTable(0);
        resultsLabel.setText("Showing " + totalItems + " results");
    }
    private javafx.scene.Node createPage(int pageIndex) {
        updateTable(pageIndex);
        return new javafx.scene.layout.VBox();  
    }
    private void updateTable(int pageIndex) {
        int fromIndex = pageIndex * ROWS_PER_PAGE;
        int toIndex = Math.min(fromIndex + ROWS_PER_PAGE, filteredData.size());
        if (fromIndex > toIndex) fromIndex = toIndex;
        javafx.collections.ObservableList<UserModel> pageItems = javafx.collections.FXCollections.observableArrayList();
        for(int i = fromIndex; i < toIndex; i++) {
            pageItems.add(filteredData.get(i));
        }
        usersTable.setItems(pageItems);
    }
    private void loadData() {
         filteredData = new FilteredList<>(userDAO.getObservableUsers(), p -> true);
         updatePagination();
    }
    @FXML
    private void handleCreateUser() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/nutrimap/view/create-user-view.fxml"));
            Parent root = loader.load();
            CreateUserController controller = loader.getController();
            controller.setParentController(this);
            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.initStyle(StageStyle.TRANSPARENT);
            stage.initOwner(usersTable.getScene().getWindow());
            Scene scene = new Scene(root);
            scene.setFill(javafx.scene.paint.Color.TRANSPARENT);
            stage.setScene(scene);
            stage.centerOnScreen();
            stage.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void refreshTable() {
         userDAO = new UserDAO();  
         loadData();
    }
}
