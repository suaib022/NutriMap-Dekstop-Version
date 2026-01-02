package com.example.nutrimap.controller;

import com.example.nutrimap.dao.ChildDAO;
import com.example.nutrimap.model.ChildModel;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;
import java.util.Optional;

public class ChildrenController {
    @FXML private TextField searchField;
    @FXML private TableView<ChildModel> childrenTable;
    @FXML private TableColumn<ChildModel, Integer> colId;
    @FXML private TableColumn<ChildModel, String> colFullName;
    @FXML private TableColumn<ChildModel, String> colGender;
    @FXML private TableColumn<ChildModel, String> colAge;
    @FXML private TableColumn<ChildModel, String> colFathersName;
    @FXML private TableColumn<ChildModel, String> colMothersName;
    @FXML private TableColumn<ChildModel, String> colContact;
    @FXML private TableColumn<ChildModel, String> colArea;
    @FXML private TableColumn<ChildModel, String> colBranch;
    @FXML private TableColumn<ChildModel, String> colLastVisit;
    @FXML private TableColumn<ChildModel, Void> colActions;
    @FXML private Pagination pagination;
    @FXML private Label resultsLabel;
    
    private ChildDAO childDAO;
    private ObservableList<ChildModel> masterData = FXCollections.observableArrayList();
    private FilteredList<ChildModel> filteredData;
    private static final int ROWS_PER_PAGE = 10;
    
    @FXML
    public void initialize() {
        childDAO = new ChildDAO();
        masterData.addAll(childDAO.getObservableChildren());
        filteredData = new FilteredList<>(masterData, p -> true);
        
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colFullName.setCellValueFactory(new PropertyValueFactory<>("fullName"));
        colGender.setCellValueFactory(new PropertyValueFactory<>("gender"));
        colAge.setCellValueFactory(new PropertyValueFactory<>("age"));
        colFathersName.setCellValueFactory(new PropertyValueFactory<>("fathersName"));
        colMothersName.setCellValueFactory(new PropertyValueFactory<>("mothersName"));
        colContact.setCellValueFactory(new PropertyValueFactory<>("contactNumber"));
        colLastVisit.setCellValueFactory(new PropertyValueFactory<>("displayLastVisit"));
        colBranch.setCellValueFactory(new PropertyValueFactory<>("branchName"));
        
        colArea.setCellValueFactory(new PropertyValueFactory<>("area"));
        colArea.setCellFactory(tc -> new TableCell<ChildModel, String>() {
            private final Text text = new Text();
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setGraphic(null);
                    setText(null);
                } else {
                    text.setText(item);
                    text.wrappingWidthProperty().bind(colArea.widthProperty().subtract(10));
                    setGraphic(text);
                }
            }
        });
        
        colActions.setCellFactory(col -> new TableCell<>() {
            private final Button editBtn = new Button("✏️");
            private final Button deleteBtn = new Button("🗑️");
            private final HBox container = new HBox(8, editBtn, deleteBtn);
            
            {
                container.setAlignment(Pos.CENTER);
                editBtn.setStyle("-fx-background-color: #3498db; -fx-text-fill: white; -fx-cursor: hand; -fx-padding: 5 10; -fx-background-radius: 5;");
                deleteBtn.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white; -fx-cursor: hand; -fx-padding: 5 10; -fx-background-radius: 5;");
                
                editBtn.setOnAction(event -> {
                    ChildModel child = getTableView().getItems().get(getIndex());
                    handleEditChild(child);
                });
                
                deleteBtn.setOnAction(event -> {
                    ChildModel child = getTableView().getItems().get(getIndex());
                    handleDeleteChild(child);
                });
            }
            
            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(container);
                }
            }
        });
        
        colArea.prefWidthProperty().bind(childrenTable.widthProperty()
            .subtract(colId.widthProperty())
            .subtract(colFullName.widthProperty())
            .subtract(colGender.widthProperty())
            .subtract(colAge.widthProperty())
            .subtract(colFathersName.widthProperty())
            .subtract(colMothersName.widthProperty())
            .subtract(colContact.widthProperty())
            .subtract(colBranch.widthProperty())
            .subtract(colLastVisit.widthProperty())
            .subtract(colActions.widthProperty())
            .subtract(20));
        
        pagination.setPageFactory(this::createPage);
        
        searchField.textProperty().addListener((obs, oldVal, newVal) -> updateFilter());
        updateFilter();
    }
    
    private void updateFilter() {
        String search = searchField.getText() == null ? "" : searchField.getText().toLowerCase();
        
        filteredData.setPredicate(child -> {
            if (search.isEmpty()) return true;
            
            return (child.getFullName() != null && child.getFullName().toLowerCase().contains(search)) ||
                   (child.getFathersName() != null && child.getFathersName().toLowerCase().contains(search)) ||
                   (child.getMothersName() != null && child.getMothersName().toLowerCase().contains(search)) ||
                   (child.getBranchName() != null && child.getBranchName().toLowerCase().contains(search)) ||
                   (child.getDivision() != null && child.getDivision().toLowerCase().contains(search)) ||
                   (child.getDistrict() != null && child.getDistrict().toLowerCase().contains(search)) ||
                   (child.getUpazilla() != null && child.getUpazilla().toLowerCase().contains(search)) ||
                   (child.getUnionName() != null && child.getUnionName().toLowerCase().contains(search));
        });
        
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
        ObservableList<ChildModel> pageItems = FXCollections.observableArrayList();
        for (int i = fromIndex; i < toIndex; i++) {
            pageItems.add(filteredData.get(i));
        }
        childrenTable.setItems(pageItems);
    }
    
    @FXML
    private void handleCreateChild() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/nutrimap/view/create-child-view.fxml"));
            Parent root = loader.load();
            CreateChildController controller = loader.getController();
            controller.setParentController(this);
            
            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.initStyle(StageStyle.TRANSPARENT);
            stage.initOwner(childrenTable.getScene().getWindow());
            Scene scene = new Scene(root);
            scene.setFill(javafx.scene.paint.Color.TRANSPARENT);
            stage.setScene(scene);
            stage.centerOnScreen();
            stage.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    private void handleEditChild(ChildModel child) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/nutrimap/view/create-child-view.fxml"));
            Parent root = loader.load();
            CreateChildController controller = loader.getController();
            controller.setParentController(this);
            controller.setMode(CreateChildController.Mode.EDIT, child);
            
            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.initStyle(StageStyle.TRANSPARENT);
            stage.initOwner(childrenTable.getScene().getWindow());
            Scene scene = new Scene(root);
            scene.setFill(javafx.scene.paint.Color.TRANSPARENT);
            stage.setScene(scene);
            stage.centerOnScreen();
            stage.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    private void handleDeleteChild(ChildModel child) {
        Alert confirmDialog = new Alert(Alert.AlertType.CONFIRMATION);
        confirmDialog.setTitle("Delete Child");
        confirmDialog.setHeaderText("Are you sure you want to delete this child record?");
        confirmDialog.setContentText("Child: " + child.getFullName() + "\n\nThis action cannot be undone.");
        
        confirmDialog.getButtonTypes().setAll(ButtonType.YES, ButtonType.NO);
        
        Optional<ButtonType> result = confirmDialog.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.YES) {
            childDAO.deleteChild(child.getId());
            refreshTable();
            showSuccessAlert("Success", "Child record deleted successfully!");
        }
    }
    
    public void refreshTable() {
        childDAO = new ChildDAO();
        masterData.clear();
        masterData.addAll(childDAO.getObservableChildren());
        updateFilter();
    }
    
    public void showSuccessAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
