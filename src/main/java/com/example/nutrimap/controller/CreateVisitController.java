package com.example.nutrimap.controller;

import com.example.nutrimap.dao.ChildDAO;
import com.example.nutrimap.dao.VisitDAO;
import com.example.nutrimap.model.ChildModel;
import com.example.nutrimap.model.VisitModel;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.util.StringConverter;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

import com.example.nutrimap.util.NutritionRiskCalculator;

public class CreateVisitController {
    
    public enum Mode {
        CREATE, EDIT
    }
    
    @FXML private ComboBox<ChildModel> childCombo;
    @FXML private DatePicker visitDatePicker;
    @FXML private TextField weightField;
    @FXML private TextField heightField;
    @FXML private TextField muacField;
    @FXML private TextArea notesArea;
    @FXML private Label titleLabel;
    @FXML private Button submitButton;
    
    private VisitsController parentController;
    private VisitDAO visitDAO;
    private ChildDAO childDAO;
    private Mode currentMode = Mode.CREATE;
    private VisitModel editingVisit = null;
    
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    
    @FXML
    public void initialize() {
        visitDAO = new VisitDAO();
        childDAO = new ChildDAO();
        
        loadChildren();
        setupDatePicker();
    }
    
    private void loadChildren() {
        List<ChildModel> children = childDAO.getAll();
        childCombo.setItems(FXCollections.observableArrayList(children));
        
        childCombo.setConverter(new StringConverter<ChildModel>() {
            @Override
            public String toString(ChildModel child) {
                if (child == null) return "";
                return child.getFullName() + " (ID: " + child.getId() + ")";
            }
            
            @Override
            public ChildModel fromString(String string) {
                return null;
            }
        });
    }
    
    private void setupDatePicker() {
        visitDatePicker.setConverter(new StringConverter<LocalDate>() {
            @Override
            public String toString(LocalDate date) {
                return date != null ? DATE_FORMATTER.format(date) : "";
            }
            
            @Override
            public LocalDate fromString(String string) {
                return string != null && !string.isEmpty() ? LocalDate.parse(string, DATE_FORMATTER) : null;
            }
        });
        
        visitDatePicker.setValue(LocalDate.now());
    }
    
    public void setParentController(VisitsController parentController) {
        this.parentController = parentController;
    }
    
    public void setMode(Mode mode, VisitModel visit) {
        this.currentMode = mode;
        this.editingVisit = visit;
        
        if (mode == Mode.EDIT && visit != null) {
            titleLabel.setText("Edit Visit");
            submitButton.setText("Update Visit");
            
            for (ChildModel child : childCombo.getItems()) {
                if (child.getId() == visit.getChildId()) {
                    childCombo.setValue(child);
                    break;
                }
            }
            
            if (visit.getVisitDate() != null && !visit.getVisitDate().isEmpty()) {
                try {
                    visitDatePicker.setValue(LocalDate.parse(visit.getVisitDate(), DATE_FORMATTER));
                } catch (Exception e) {
                    visitDatePicker.setValue(null);
                }
            }
            
            weightField.setText(String.valueOf(visit.getWeightKg()));
            heightField.setText(String.valueOf(visit.getHeightCm()));
            muacField.setText(String.valueOf(visit.getMuacMm()));
            notesArea.setText(visit.getNotes() != null ? visit.getNotes() : "");
        }
    }
    
    @FXML
    private void handleCancel() {
        closeWindow();
    }
    
    @FXML
    private void handleCreate() {
        ChildModel selectedChild = childCombo.getValue();
        LocalDate visitDate = visitDatePicker.getValue();
        String weightStr = weightField.getText() != null ? weightField.getText().trim() : "";
        String heightStr = heightField.getText() != null ? heightField.getText().trim() : "";
        String muacStr = muacField.getText() != null ? muacField.getText().trim() : "";
        String notes = notesArea.getText() != null ? notesArea.getText().trim() : "";
        
        if (selectedChild == null) {
            showAlert("Error", "Please select a child.");
            return;
        }
        
        if (visitDate == null) {
            showAlert("Error", "Please select a visit date.");
            return;
        }
        
        double weight;
        double height;
        int muac;
        
        try {
            weight = Double.parseDouble(weightStr);
        } catch (NumberFormatException e) {
            showAlert("Error", "Please enter a valid weight (e.g., 12.5).");
            return;
        }
        
        try {
            height = Double.parseDouble(heightStr);
        } catch (NumberFormatException e) {
            showAlert("Error", "Please enter a valid height (e.g., 85.0).");
            return;
        }
        
        try {
            muac = Integer.parseInt(muacStr);
        } catch (NumberFormatException e) {
            showAlert("Error", "Please enter a valid MUAC (e.g., 125).");
            return;
        }
        
        if (currentMode == Mode.CREATE) {
            VisitModel newVisit = new VisitModel();
            newVisit.setChildId(selectedChild.getId());
            newVisit.setVisitDate(DATE_FORMATTER.format(visitDate));
            newVisit.setWeightKg(weight);
            newVisit.setHeightCm(height);
            newVisit.setMuacMm(muac);
            newVisit.setNotes(notes);
            
            // Auto-calculate risk level from MUAC
            String riskLevel = NutritionRiskCalculator.calculateRiskFromMuac(muac);
            newVisit.setRiskLevel(riskLevel);
            
            visitDAO.addVisit(newVisit);
            
            closeWindow();
            
            if (parentController != null) {
                parentController.refreshTable();
                parentController.showSuccessAlert("Success", "Visit added successfully!");
            }
        } else if (currentMode == Mode.EDIT && editingVisit != null) {
            editingVisit.setChildId(selectedChild.getId());
            editingVisit.setVisitDate(DATE_FORMATTER.format(visitDate));
            editingVisit.setWeightKg(weight);
            editingVisit.setHeightCm(height);
            editingVisit.setMuacMm(muac);
            editingVisit.setNotes(notes);
            
            // Recalculate risk level on edit
            String riskLevel = NutritionRiskCalculator.calculateRiskFromMuac(muac);
            editingVisit.setRiskLevel(riskLevel);
            
            visitDAO.updateVisit(editingVisit);
            
            closeWindow();
            
            if (parentController != null) {
                parentController.refreshTable();
                parentController.showSuccessAlert("Success", "Visit updated successfully!");
            }
        }
    }
    
    private void closeWindow() {
        Stage stage = (Stage) childCombo.getScene().getWindow();
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
