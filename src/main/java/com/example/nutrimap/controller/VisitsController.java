package com.example.nutrimap.controller;

import com.example.nutrimap.dao.VisitDAO;
import com.example.nutrimap.model.VisitModel;
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
import javafx.stage.FileChooser;

import com.example.nutrimap.service.ExportService;

import java.io.File;
import java.io.IOException;
import java.util.Optional;

public class VisitsController {
    @FXML private TextField searchField;
    @FXML private TableView<VisitModel> visitsTable;
    @FXML private TableColumn<VisitModel, Integer> colId;
    @FXML private TableColumn<VisitModel, String> colChildName;
    @FXML private TableColumn<VisitModel, String> colVisitDate;
    @FXML private TableColumn<VisitModel, Double> colWeight;
    @FXML private TableColumn<VisitModel, Double> colHeight;
    @FXML private TableColumn<VisitModel, Integer> colMuac;
    @FXML private TableColumn<VisitModel, String> colRiskLevel;
    @FXML private TableColumn<VisitModel, String> colNotes;
    @FXML private TableColumn<VisitModel, Void> colActions;
    @FXML private Pagination pagination;
    @FXML private Label resultsLabel;
    
    private VisitDAO visitDAO;
    private ObservableList<VisitModel> masterData = FXCollections.observableArrayList();
    private FilteredList<VisitModel> filteredData;
    private static final int ROWS_PER_PAGE = 10;
    
    @FXML
    public void initialize() {
        visitDAO = new VisitDAO();
        masterData.addAll(visitDAO.getObservableVisits());
        filteredData = new FilteredList<>(masterData, p -> true);
        
        colId.setCellValueFactory(new PropertyValueFactory<>("visitId"));
        colChildName.setCellValueFactory(new PropertyValueFactory<>("childName"));
        colVisitDate.setCellValueFactory(new PropertyValueFactory<>("visitDate"));
        colWeight.setCellValueFactory(new PropertyValueFactory<>("weightKg"));
        colHeight.setCellValueFactory(new PropertyValueFactory<>("heightCm"));
        colMuac.setCellValueFactory(new PropertyValueFactory<>("muacMm"));
        colRiskLevel.setCellValueFactory(new PropertyValueFactory<>("riskLevel"));
        colNotes.setCellValueFactory(new PropertyValueFactory<>("notes"));
        
        colActions.setCellFactory(col -> new TableCell<>() {
            private final Button editBtn = new Button("✏️");
            private final Button deleteBtn = new Button("🗑️");
            private final HBox container = new HBox(8, editBtn, deleteBtn);
            
            {
                container.setAlignment(Pos.CENTER);
                editBtn.setStyle("-fx-background-color: #3498db; -fx-text-fill: white; -fx-cursor: hand; -fx-padding: 5 10; -fx-background-radius: 5;");
                deleteBtn.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white; -fx-cursor: hand; -fx-padding: 5 10; -fx-background-radius: 5;");
                
                editBtn.setOnAction(event -> {
                    VisitModel visit = getTableView().getItems().get(getIndex());
                    handleEditVisit(visit);
                });
                
                deleteBtn.setOnAction(event -> {
                    VisitModel visit = getTableView().getItems().get(getIndex());
                    handleDeleteVisit(visit);
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
        
        colNotes.prefWidthProperty().bind(visitsTable.widthProperty()
            .subtract(colId.widthProperty())
            .subtract(colChildName.widthProperty())
            .subtract(colVisitDate.widthProperty())
            .subtract(colWeight.widthProperty())
            .subtract(colHeight.widthProperty())
            .subtract(colMuac.widthProperty())
            .subtract(colRiskLevel.widthProperty())
            .subtract(colActions.widthProperty())
            .subtract(20));
        
        pagination.setPageFactory(this::createPage);
        
        searchField.textProperty().addListener((obs, oldVal, newVal) -> updateFilter());
        updateFilter();
    }
    
    private void updateFilter() {
        String search = searchField.getText() == null ? "" : searchField.getText().toLowerCase();
        
        filteredData.setPredicate(visit -> {
            if (search.isEmpty()) return true;
            
            return (visit.getChildName() != null && visit.getChildName().toLowerCase().contains(search)) ||
                   (visit.getVisitDate() != null && visit.getVisitDate().toLowerCase().contains(search)) ||
                   (visit.getNotes() != null && visit.getNotes().toLowerCase().contains(search));
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
        ObservableList<VisitModel> pageItems = FXCollections.observableArrayList();
        for (int i = fromIndex; i < toIndex; i++) {
            pageItems.add(filteredData.get(i));
        }
        visitsTable.setItems(pageItems);
    }
    
    @FXML
    private void handleCreateVisit() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/nutrimap/view/create-visit-view.fxml"));
            Parent root = loader.load();
            CreateVisitController controller = loader.getController();
            controller.setParentController(this);
            
            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.initStyle(StageStyle.TRANSPARENT);
            stage.initOwner(visitsTable.getScene().getWindow());
            Scene scene = new Scene(root);
            scene.setFill(javafx.scene.paint.Color.TRANSPARENT);
            stage.setScene(scene);
            stage.centerOnScreen();
            stage.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    private void handleEditVisit(VisitModel visit) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/nutrimap/view/create-visit-view.fxml"));
            Parent root = loader.load();
            CreateVisitController controller = loader.getController();
            controller.setParentController(this);
            controller.setMode(CreateVisitController.Mode.EDIT, visit);
            
            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.initStyle(StageStyle.TRANSPARENT);
            stage.initOwner(visitsTable.getScene().getWindow());
            Scene scene = new Scene(root);
            scene.setFill(javafx.scene.paint.Color.TRANSPARENT);
            stage.setScene(scene);
            stage.centerOnScreen();
            stage.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    private void handleDeleteVisit(VisitModel visit) {
        Alert confirmDialog = new Alert(Alert.AlertType.CONFIRMATION);
        confirmDialog.setTitle("Delete Visit");
        confirmDialog.setHeaderText("Are you sure you want to delete this visit record?");
        confirmDialog.setContentText("Visit ID: " + visit.getVisitId() + " for " + visit.getChildName() + "\n\nThis action cannot be undone.");
        
        confirmDialog.getButtonTypes().setAll(ButtonType.YES, ButtonType.NO);
        
        Optional<ButtonType> result = confirmDialog.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.YES) {
            visitDAO.deleteVisit(visit.getVisitId());
            refreshTable();
            showSuccessAlert("Success", "Visit record deleted successfully!");
        }
    }
    
    public void refreshTable() {
        visitDAO = new VisitDAO();
        masterData.clear();
        masterData.addAll(visitDAO.getObservableVisits());
        updateFilter();
    }
    
    public void showSuccessAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    
    @FXML
    private void handleExportCsv() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Export Visits to CSV");
        fileChooser.setInitialFileName("visits_export.csv");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("CSV Files", "*.csv"));
        
        File file = fileChooser.showSaveDialog(visitsTable.getScene().getWindow());
        if (file != null) {
            try {
                ExportService.exportVisitsToCsv(visitDAO.getAll(), file);
                showSuccessAlert("Export Successful", "Visits data exported to CSV successfully!");
            } catch (IOException e) {
                e.printStackTrace();
                showErrorAlert("Export Failed", "Failed to export data: " + e.getMessage());
            }
        }
    }
    
    @FXML
    private void handleExportPdf() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Export Visits to PDF");
        fileChooser.setInitialFileName("visits_report.pdf");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("PDF Files", "*.pdf"));
        
        File file = fileChooser.showSaveDialog(visitsTable.getScene().getWindow());
        if (file != null) {
            try {
                ExportService.exportVisitsToPdf(visitDAO.getAll(), file);
                showSuccessAlert("Export Successful", "Visits report exported to PDF successfully!");
            } catch (Exception e) {
                e.printStackTrace();
                showErrorAlert("Export Failed", "Failed to export PDF: " + e.getMessage());
            }
        }
    }
    
    private void showErrorAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
