package com.example.nutrimap.controller;

import com.example.nutrimap.dao.BranchDAO;
import com.example.nutrimap.model.BranchModel;
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
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;
import java.util.Optional;
import java.util.stream.Collectors;

public class BranchesController {
    @FXML private TextField searchField;
    @FXML private ComboBox<String> divisionFilter;
    @FXML private ComboBox<String> districtFilter;
    @FXML private ComboBox<String> upazilaFilter;
    @FXML private TableView<BranchModel> branchesTable;
    @FXML private TableColumn<BranchModel, String> colId;
    @FXML private TableColumn<BranchModel, String> colName;
    @FXML private TableColumn<BranchModel, String> colDivision;
    @FXML private TableColumn<BranchModel, String> colDistrict;
    @FXML private TableColumn<BranchModel, String> colUpazilla;
    @FXML private TableColumn<BranchModel, String> colArea;
    @FXML private TableColumn<BranchModel, String> colUrl;
    @FXML private TableColumn<BranchModel, Void> colActions;
    @FXML private Pagination pagination;
    @FXML private Label resultsLabel;
    
    private BranchDAO branchDAO;
    private ObservableList<BranchModel> masterData = FXCollections.observableArrayList();
    private FilteredList<BranchModel> filteredData;
    private static final int ROWS_PER_PAGE = 10;
    
    @FXML
    public void initialize() {
        branchDAO = new BranchDAO();
        masterData.addAll(branchDAO.getObservableBranches());
        filteredData = new FilteredList<>(masterData, p -> true);
        
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colName.setCellValueFactory(new PropertyValueFactory<>("name"));
        colDivision.setCellValueFactory(new PropertyValueFactory<>("division"));
        colDistrict.setCellValueFactory(new PropertyValueFactory<>("district"));
        colUpazilla.setCellValueFactory(new PropertyValueFactory<>("upazilla"));
        colArea.setCellValueFactory(new PropertyValueFactory<>("area"));
        
        colArea.setCellFactory(tc -> new TableCell<BranchModel, String>() {
            private final javafx.scene.text.Text text = new javafx.scene.text.Text();
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
        
        colUrl.setCellValueFactory(new PropertyValueFactory<>("url"));
        colUrl.setCellFactory(tc -> new TableCell<BranchModel, String>() {
            private final Hyperlink link = new Hyperlink();
            {
                link.setOnAction(e -> {
                    String url = getItem();
                    if (url != null && !url.isEmpty()) {
                        if (!url.startsWith("http")) {
                            url = "http://" + url;
                        }
                        com.example.nutrimap.HelloApplication.getInstance().getHostServices().showDocument(url);
                    }
                });
            }
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setGraphic(null);
                } else {
                    link.setText(item);
                    setGraphic(link);
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
                    BranchModel branch = getTableView().getItems().get(getIndex());
                    handleEditBranch(branch);
                });
                
                deleteBtn.setOnAction(event -> {
                    BranchModel branch = getTableView().getItems().get(getIndex());
                    handleDeleteBranch(branch);
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
        
        setupFilters();
        
        colArea.prefWidthProperty().bind(branchesTable.widthProperty()
            .subtract(colId.widthProperty())
            .subtract(colName.widthProperty())
            .subtract(colDivision.widthProperty())
            .subtract(colDistrict.widthProperty())
            .subtract(colUpazilla.widthProperty())
            .subtract(colUrl.widthProperty())
            .subtract(colActions.widthProperty())
            .subtract(20));
        
        pagination.setPageFactory(this::createPage);
        searchField.textProperty().addListener((obs, oldVal, newVal) -> updateFilter());
        divisionFilter.valueProperty().addListener((obs, oldVal, newVal) -> {
            updateDistrictFilter(newVal);
            updateFilter();
        });
        districtFilter.valueProperty().addListener((obs, oldVal, newVal) -> {
            updateUpazilaFilter(newVal);
            updateFilter();
        });
        upazilaFilter.valueProperty().addListener((obs, oldVal, newVal) -> updateFilter());
        updateFilter();
    }
    
    private void setupFilters() {
        ObservableList<String> divisions = FXCollections.observableArrayList(
                masterData.stream().map(BranchModel::getDivision).distinct().sorted().collect(Collectors.toList())
        );
        divisionFilter.setItems(divisions);
    }
    
    private void updateDistrictFilter(String selectedDivision) {
        String currentDistrict = districtFilter.getValue();
        if (selectedDivision == null || selectedDivision.isEmpty()) {
             ObservableList<String> districts = FXCollections.observableArrayList(
                masterData.stream()
                        .map(BranchModel::getDistrict).distinct().sorted().collect(Collectors.toList())
            );
            districtFilter.setItems(districts);
        } else {
             ObservableList<String> districts = FXCollections.observableArrayList(
                masterData.stream()
                        .filter(b -> b.getDivision().equals(selectedDivision))
                        .map(BranchModel::getDistrict).distinct().sorted().collect(Collectors.toList())
            );
            districtFilter.setItems(districts);
        }
        if (currentDistrict != null && districtFilter.getItems().contains(currentDistrict)) {
            districtFilter.setValue(currentDistrict);
        } else {
            districtFilter.setValue(null);
        }
    }
    
    private void updateUpazilaFilter(String selectedDistrict) {
         String currentUpazila = upazilaFilter.getValue();
         if (selectedDistrict == null || selectedDistrict.isEmpty()) {
             ObservableList<String> upazilas = FXCollections.observableArrayList(
                masterData.stream().map(BranchModel::getUpazilla).distinct().sorted().collect(Collectors.toList())
             );
             upazilaFilter.setItems(upazilas);
         } else {
              ObservableList<String> upazilas = FXCollections.observableArrayList(
                masterData.stream()
                        .filter(b -> b.getDistrict().equals(selectedDistrict))
                        .map(BranchModel::getUpazilla).distinct().sorted().collect(Collectors.toList())
             );
             upazilaFilter.setItems(upazilas);
         }
         if (currentUpazila != null && upazilaFilter.getItems().contains(currentUpazila)) {
             upazilaFilter.setValue(currentUpazila);
         } else {
             upazilaFilter.setValue(null);
         }
    }
    
    private void updateFilter() {
        String search = searchField.getText() == null ? "" : searchField.getText().toLowerCase();
        String div = divisionFilter.getValue();
        String dist = districtFilter.getValue();
        String upa = upazilaFilter.getValue();
        
        filteredData.setPredicate(branch -> {
            boolean matchSearch = search.isEmpty() || 
                                  branch.getName().toLowerCase().contains(search) ||
                                  branch.getDivision().toLowerCase().contains(search) ||
                                  branch.getDistrict().toLowerCase().contains(search) ||
                                  branch.getUpazilla().toLowerCase().contains(search) || 
                                  (branch.getArea() != null && branch.getArea().toLowerCase().contains(search));
            if (!matchSearch) return false;
            if (div != null && !div.isEmpty() && !branch.getDivision().equals(div)) return false;
            if (dist != null && !dist.isEmpty() && !branch.getDistrict().equals(dist)) return false;
            if (upa != null && !upa.isEmpty() && !branch.getUpazilla().equals(upa)) return false;
            return true;
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
        ObservableList<BranchModel> pageItems = FXCollections.observableArrayList();
        for (int i = fromIndex; i < toIndex; i++) {
            pageItems.add(filteredData.get(i));
        }
        branchesTable.setItems(pageItems);
    }
    
    @FXML
    private void handleResetFilters() {
        divisionFilter.setValue(null);
        districtFilter.setValue(null);
        upazilaFilter.setValue(null);
        searchField.clear();
    }
    
    @FXML
    private void handleCreateBranch() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/nutrimap/view/create-branch-view.fxml"));
            Parent root = loader.load();
            CreateBranchController controller = loader.getController();
            controller.setParentController(this);
            
            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.initStyle(StageStyle.TRANSPARENT);
            stage.initOwner(branchesTable.getScene().getWindow());
            Scene scene = new Scene(root);
            scene.setFill(javafx.scene.paint.Color.TRANSPARENT);
            stage.setScene(scene);
            stage.centerOnScreen();
            stage.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    private void handleEditBranch(BranchModel branch) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/nutrimap/view/create-branch-view.fxml"));
            Parent root = loader.load();
            CreateBranchController controller = loader.getController();
            controller.setParentController(this);
            controller.setMode(CreateBranchController.Mode.EDIT, branch);
            
            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.initStyle(StageStyle.TRANSPARENT);
            stage.initOwner(branchesTable.getScene().getWindow());
            Scene scene = new Scene(root);
            scene.setFill(javafx.scene.paint.Color.TRANSPARENT);
            stage.setScene(scene);
            stage.centerOnScreen();
            stage.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    private void handleDeleteBranch(BranchModel branch) {
        Alert confirmDialog = new Alert(Alert.AlertType.CONFIRMATION);
        confirmDialog.setTitle("Delete Branch");
        confirmDialog.setHeaderText("Are you sure you want to delete this branch?");
        confirmDialog.setContentText("Branch: " + branch.getName() + " (" + branch.getDistrict() + ")\n\nThis action cannot be undone.");
        
        confirmDialog.getButtonTypes().setAll(ButtonType.YES, ButtonType.NO);
        
        Optional<ButtonType> result = confirmDialog.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.YES) {
            branchDAO.deleteBranch(branch.getId());
            refreshTable();
            showSuccessAlert("Success", "Branch deleted successfully!");
        }
    }
    
    public void refreshTable() {
        branchDAO = new BranchDAO();
        masterData.clear();
        masterData.addAll(branchDAO.getObservableBranches());
        setupFilters();
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
