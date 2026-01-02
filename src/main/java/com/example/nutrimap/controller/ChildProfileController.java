package com.example.nutrimap.controller;

import com.example.nutrimap.dao.VisitDAO;
import com.example.nutrimap.model.ChildModel;
import com.example.nutrimap.model.VisitModel;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Circle;

import java.util.List;

public class ChildProfileController {
    @FXML private Label childNameLabel;
    @FXML private Circle photoCircle;
    @FXML private Label fatherNameLabel;
    @FXML private Label motherNameLabel;
    @FXML private Label contactLabel;
    @FXML private Label genderLabel;
    @FXML private Label dobLabel;
    @FXML private Label ageLabel;
    @FXML private Label areaLabel;
    @FXML private Label branchLabel;
    @FXML private Label nutritionLevelLabel;
    @FXML private Label riskLevelLabel;
    @FXML private Label noVisitsLabel;
    @FXML private TableView<VisitModel> visitsTable;
    @FXML private TableColumn<VisitModel, Integer> colId;
    @FXML private TableColumn<VisitModel, String> colVisitDate;
    @FXML private TableColumn<VisitModel, Double> colWeight;
    @FXML private TableColumn<VisitModel, Double> colHeight;
    @FXML private TableColumn<VisitModel, Integer> colMuac;
    @FXML private TableColumn<VisitModel, String> colRiskLevel;
    @FXML private TableColumn<VisitModel, String> colNotes;
    @FXML private TableColumn<VisitModel, Void> colActions;
    @FXML private Pagination pagination;
    @FXML private Label resultsLabel;

    private ChildModel child;
    private Pane parentContainer;
    private javafx.scene.Node previousView;
    private ChildrenController childrenController;
    private VisitDAO visitDAO;
    private ObservableList<VisitModel> visitsList = FXCollections.observableArrayList();
    private static final int ROWS_PER_PAGE = 10;

    @FXML
    public void initialize() {
        visitDAO = new VisitDAO();
        setupTableColumns();
    }

    private void setupTableColumns() {
        colId.setCellValueFactory(new PropertyValueFactory<>("visitId"));
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
            .subtract(colVisitDate.widthProperty())
            .subtract(colWeight.widthProperty())
            .subtract(colHeight.widthProperty())
            .subtract(colMuac.widthProperty())
            .subtract(colRiskLevel.widthProperty())
            .subtract(colActions.widthProperty())
            .subtract(20));

        pagination.setPageFactory(this::createPage);
    }

    public void setChild(ChildModel child) {
        this.child = child;
        if (child == null) {
            showAlert("Error", "Child not found");
            handleBack();
            return;
        }
        populateChildInfo();
        loadVisitsInBackground();
    }

    public void setParentContainer(Pane container) {
        this.parentContainer = container;
    }

    public void setPreviousView(javafx.scene.Node view) {
        this.previousView = view;
    }

    public void setChildrenController(ChildrenController controller) {
        this.childrenController = controller;
    }

    private void populateChildInfo() {
        childNameLabel.setText(child.getFullName() != null ? child.getFullName() : "N/A");
        fatherNameLabel.setText(child.getFathersName() != null ? child.getFathersName() : "N/A");
        motherNameLabel.setText(child.getMothersName() != null ? child.getMothersName() : "N/A");
        contactLabel.setText(child.getContactNumber() != null ? child.getContactNumber() : "N/A");
        genderLabel.setText(child.getGender() != null ? child.getGender() : "N/A");
        dobLabel.setText(child.getDateOfBirth() != null ? child.getDateOfBirth() : "N/A");
        ageLabel.setText(child.getAge() != null ? child.getAge() : "N/A");
        areaLabel.setText(child.getArea() != null ? child.getArea() : "N/A");
        branchLabel.setText(child.getBranchName() != null ? child.getBranchName() : "N/A");
    }

    private void loadVisitsInBackground() {
        Task<List<VisitModel>> loadTask = new Task<>() {
            @Override
            protected List<VisitModel> call() {
                return visitDAO.getByChildId(child.getId());
            }
        };

        loadTask.setOnSucceeded(event -> {
            List<VisitModel> visits = loadTask.getValue();
            visitsList.clear();
            visitsList.addAll(visits);

            if (visits.isEmpty()) {
                noVisitsLabel.setVisible(true);
                noVisitsLabel.setManaged(true);
                setStatusBadge(nutritionLevelLabel, "N/A", "status-na");
                setStatusBadge(riskLevelLabel, "N/A", "status-na");
            } else {
                noVisitsLabel.setVisible(false);
                noVisitsLabel.setManaged(false);
                VisitModel latestVisit = visits.get(0);
                computeAndDisplayNutritionLevel(latestVisit);
                displayRiskLevel(latestVisit);
            }

            updatePagination();
        });

        loadTask.setOnFailed(event -> {
            noVisitsLabel.setVisible(true);
            noVisitsLabel.setManaged(true);
            noVisitsLabel.setText("Failed to load visits");
            setStatusBadge(nutritionLevelLabel, "N/A", "status-na");
            setStatusBadge(riskLevelLabel, "N/A", "status-na");
        });

        new Thread(loadTask).start();
    }

    private void computeAndDisplayNutritionLevel(VisitModel visit) {
        if (visit == null) {
            setStatusBadge(nutritionLevelLabel, "N/A", "status-na");
            return;
        }

        int muacMm = visit.getMuacMm();
        double muacCm = muacMm / 10.0;

        if (muacMm > 0) {
            if (muacCm < 11.5) {
                setStatusBadge(nutritionLevelLabel, "Severe Malnutrition", "status-severe");
            } else if (muacCm < 12.5) {
                setStatusBadge(nutritionLevelLabel, "Moderate Malnutrition", "status-moderate");
            } else {
                setStatusBadge(nutritionLevelLabel, "Normal", "status-normal");
            }
        } else {
            double weight = visit.getWeightKg();
            double height = visit.getHeightCm();
            if (weight > 0 && height > 0) {
                double heightM = height / 100.0;
                double bmi = weight / (heightM * heightM);
                if (bmi < 16) {
                    setStatusBadge(nutritionLevelLabel, "Severe Malnutrition", "status-severe");
                } else if (bmi < 17) {
                    setStatusBadge(nutritionLevelLabel, "Moderate Malnutrition", "status-moderate");
                } else {
                    setStatusBadge(nutritionLevelLabel, "Normal", "status-normal");
                }
            } else {
                setStatusBadge(nutritionLevelLabel, "Unknown", "status-na");
            }
        }
    }

    private void displayRiskLevel(VisitModel visit) {
        if (visit == null || visit.getRiskLevel() == null || visit.getRiskLevel().isEmpty() || "N/A".equalsIgnoreCase(visit.getRiskLevel())) {
            setStatusBadge(riskLevelLabel, "N/A", "status-na");
            return;
        }

        String risk = visit.getRiskLevel().toUpperCase();
        switch (risk) {
            case "LOW":
                setStatusBadge(riskLevelLabel, "LOW", "status-low");
                break;
            case "MEDIUM":
                setStatusBadge(riskLevelLabel, "MEDIUM", "status-medium");
                break;
            case "HIGH":
                setStatusBadge(riskLevelLabel, "HIGH", "status-high");
                break;
            default:
                setStatusBadge(riskLevelLabel, visit.getRiskLevel(), "status-na");
        }
    }

    private void setStatusBadge(Label label, String text, String styleClass) {
        label.setText(text);
        label.getStyleClass().removeAll("status-badge", "status-low", "status-medium", "status-high", "status-normal", "status-moderate", "status-severe", "status-na");
        label.getStyleClass().addAll("status-badge", styleClass);
    }

    private void updatePagination() {
        int totalItems = visitsList.size();
        int pageCount = (totalItems / ROWS_PER_PAGE) + (totalItems % ROWS_PER_PAGE > 0 ? 1 : 0);
        pagination.setPageCount(Math.max(1, pageCount));
        pagination.setCurrentPageIndex(0);
        updateTable(0);
        resultsLabel.setText("Showing " + totalItems + " visit records");
    }

    private javafx.scene.Node createPage(int pageIndex) {
        updateTable(pageIndex);
        return new javafx.scene.layout.VBox();
    }

    private void updateTable(int pageIndex) {
        int fromIndex = pageIndex * ROWS_PER_PAGE;
        int toIndex = Math.min(fromIndex + ROWS_PER_PAGE, visitsList.size());
        if (fromIndex > toIndex) fromIndex = toIndex;
        ObservableList<VisitModel> pageItems = FXCollections.observableArrayList();
        for (int i = fromIndex; i < toIndex; i++) {
            pageItems.add(visitsList.get(i));
        }
        visitsTable.setItems(pageItems);
    }

    @FXML
    private void handleBack() {
        if (parentContainer != null && previousView != null) {
            if (parentContainer instanceof StackPane) {
                ((StackPane) parentContainer).getChildren().clear();
                ((StackPane) parentContainer).getChildren().add(previousView);
            } else {
                parentContainer.getChildren().clear();
                parentContainer.getChildren().add(previousView);
            }
            if (childrenController != null) {
                childrenController.refreshTable();
            }
        }
    }

    private void handleEditVisit(VisitModel visit) {
        try {
            javafx.fxml.FXMLLoader loader = new javafx.fxml.FXMLLoader(getClass().getResource("/com/example/nutrimap/view/create-visit-view.fxml"));
            javafx.scene.Parent root = loader.load();
            CreateVisitController controller = loader.getController();
            controller.setMode(CreateVisitController.Mode.EDIT, visit);

            javafx.stage.Stage stage = new javafx.stage.Stage();
            stage.initModality(javafx.stage.Modality.APPLICATION_MODAL);
            stage.initStyle(javafx.stage.StageStyle.TRANSPARENT);
            stage.initOwner(visitsTable.getScene().getWindow());
            javafx.scene.Scene scene = new javafx.scene.Scene(root);
            scene.setFill(javafx.scene.paint.Color.TRANSPARENT);
            stage.setScene(scene);
            stage.centerOnScreen();
            stage.showAndWait();
            loadVisitsInBackground();
        } catch (java.io.IOException e) {
            e.printStackTrace();
        }
    }

    private void handleDeleteVisit(VisitModel visit) {
        Alert confirmDialog = new Alert(Alert.AlertType.CONFIRMATION);
        confirmDialog.setTitle("Delete Visit");
        confirmDialog.setHeaderText("Are you sure you want to delete this visit record?");
        confirmDialog.setContentText("Visit ID: " + visit.getVisitId() + "\n\nThis action cannot be undone.");

        confirmDialog.getButtonTypes().setAll(ButtonType.YES, ButtonType.NO);

        java.util.Optional<ButtonType> result = confirmDialog.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.YES) {
            visitDAO.deleteVisit(visit.getVisitId());
            loadVisitsInBackground();
            showSuccessAlert("Success", "Visit record deleted successfully!");
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showSuccessAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
