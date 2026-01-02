package com.example.nutrimap.controller;

import com.example.nutrimap.dao.*;
import com.example.nutrimap.model.*;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.util.StringConverter;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class CreateChildController {
    
    public enum Mode {
        CREATE, EDIT
    }
    
    @FXML private TextField fullNameField;
    @FXML private ComboBox<String> genderCombo;
    @FXML private DatePicker dobPicker;

    @FXML private TextField fathersNameField;
    @FXML private TextField mothersNameField;
    @FXML private TextField contactField;
    @FXML private ComboBox<DivisionModel> divisionCombo;
    @FXML private ComboBox<DistrictModel> districtCombo;
    @FXML private ComboBox<UpazilaModel> upazillaCombo;
    @FXML private ComboBox<UnionModel> unionCombo;
    @FXML private TextField branchField;
    @FXML private Label titleLabel;
    @FXML private Button submitButton;
    
    private ChildrenController parentController;
    private ChildDAO childDAO;
    private DivisionDAO divisionDAO;
    private DistrictDAO districtDAO;
    private UpazilaDAO upazilaDAO;
    private UnionDAO unionDAO;
    private BranchDAO branchDAO;
    private Mode currentMode = Mode.CREATE;
    private ChildModel editingChild = null;
    
    private BranchModel matchedBranch = null;
    
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    
    @FXML
    public void initialize() {
        childDAO = new ChildDAO();
        divisionDAO = new DivisionDAO();
        districtDAO = new DistrictDAO();
        upazilaDAO = new UpazilaDAO();
        unionDAO = new UnionDAO();
        branchDAO = new BranchDAO();
        
        genderCombo.setItems(FXCollections.observableArrayList("Male", "Female", "Other"));
        
        loadDivisions();
        setupComboListeners();
        setupDatePickers();
    }
    
    private void loadDivisions() {
        List<DivisionModel> divisions = divisionDAO.getAll();
        divisionCombo.setItems(FXCollections.observableArrayList(divisions));
    }
    
    private void setupComboListeners() {
        divisionCombo.valueProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                districtCombo.setDisable(false);
                List<DistrictModel> districts = districtDAO.getByDivisionId(newVal.getId());
                districtCombo.setItems(FXCollections.observableArrayList(districts));
                districtCombo.setValue(null);
                
                upazillaCombo.setValue(null);
                upazillaCombo.setItems(FXCollections.observableArrayList());
                upazillaCombo.setDisable(true);
                
                unionCombo.setValue(null);
                unionCombo.setItems(FXCollections.observableArrayList());
                unionCombo.setDisable(true);
                
                branchField.setText("");
                matchedBranch = null;
            } else {
                districtCombo.setDisable(true);
                districtCombo.setValue(null);
            }
        });
        
        districtCombo.valueProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                upazillaCombo.setDisable(false);
                List<UpazilaModel> upazilas = upazilaDAO.getByDistrictId(newVal.getId());
                upazillaCombo.setItems(FXCollections.observableArrayList(upazilas));
                upazillaCombo.setValue(null);
                
                unionCombo.setValue(null);
                unionCombo.setItems(FXCollections.observableArrayList());
                unionCombo.setDisable(true);
                
                branchField.setText("");
                matchedBranch = null;
            } else {
                upazillaCombo.setDisable(true);
                upazillaCombo.setValue(null);
            }
        });
        
        upazillaCombo.valueProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                unionCombo.setDisable(false);
                List<UnionModel> unions = unionDAO.getByUpazilaId(newVal.getId());
                unionCombo.setItems(FXCollections.observableArrayList(unions));
                unionCombo.setValue(null);
                
                fetchBranch();
            } else {
                unionCombo.setDisable(true);
                unionCombo.setValue(null);
                branchField.setText("");
                matchedBranch = null;
            }
        });
        
        unionCombo.valueProperty().addListener((obs, oldVal, newVal) -> {
            fetchBranch();
        });
    }
    
    private void fetchBranch() {
        DivisionModel division = divisionCombo.getValue();
        DistrictModel district = districtCombo.getValue();
        UpazilaModel upazilla = upazillaCombo.getValue();
        
        if (division != null && district != null && upazilla != null) {
            List<BranchModel> branches = branchDAO.getByUpazilla(upazilla.getName());
            if (!branches.isEmpty()) {
                matchedBranch = branches.get(0);
                branchField.setText(matchedBranch.getName());
            } else {
                branches = branchDAO.getByDistrict(district.getName());
                if (!branches.isEmpty()) {
                    matchedBranch = branches.get(0);
                    branchField.setText(matchedBranch.getName());
                } else {
                    matchedBranch = null;
                    branchField.setText("No branch found for this area");
                }
            }
        }
    }
    
    private void setupDatePickers() {
        dobPicker.setConverter(new StringConverter<LocalDate>() {
            @Override
            public String toString(LocalDate date) {
                return date != null ? DATE_FORMATTER.format(date) : "";
            }
            
            @Override
            public LocalDate fromString(String string) {
                return string != null && !string.isEmpty() ? LocalDate.parse(string, DATE_FORMATTER) : null;
            }
        });
        

    }
    
    public void setParentController(ChildrenController parentController) {
        this.parentController = parentController;
    }
    
    public void setMode(Mode mode, ChildModel child) {
        this.currentMode = mode;
        this.editingChild = child;
        
        if (mode == Mode.EDIT && child != null) {
            titleLabel.setText("Edit Child");
            submitButton.setText("Update Child");
            
            fullNameField.setText(child.getFullName() != null ? child.getFullName() : "");
            genderCombo.setValue(child.getGender());
            fathersNameField.setText(child.getFathersName() != null ? child.getFathersName() : "");
            mothersNameField.setText(child.getMothersName() != null ? child.getMothersName() : "");
            contactField.setText(child.getContactNumber() != null ? child.getContactNumber() : "");
            
            if (child.getDateOfBirth() != null && !child.getDateOfBirth().isEmpty()) {
                try {
                    dobPicker.setValue(LocalDate.parse(child.getDateOfBirth(), DATE_FORMATTER));
                } catch (Exception e) {
                    dobPicker.setValue(null);
                }
            }
            

            
            if (child.getDivision() != null) {
                for (DivisionModel div : divisionCombo.getItems()) {
                    if (div.getName().equals(child.getDivision())) {
                        divisionCombo.setValue(div);
                        break;
                    }
                }
            }
            
            if (child.getDistrict() != null && !districtCombo.getItems().isEmpty()) {
                for (DistrictModel dist : districtCombo.getItems()) {
                    if (dist.getName().equals(child.getDistrict())) {
                        districtCombo.setValue(dist);
                        break;
                    }
                }
            }
            
            if (child.getUpazilla() != null && !upazillaCombo.getItems().isEmpty()) {
                for (UpazilaModel upa : upazillaCombo.getItems()) {
                    if (upa.getName().equals(child.getUpazilla())) {
                        upazillaCombo.setValue(upa);
                        break;
                    }
                }
            }
            
            if (child.getUnionName() != null && !unionCombo.getItems().isEmpty()) {
                for (UnionModel uni : unionCombo.getItems()) {
                    if (uni.getName().equals(child.getUnionName())) {
                        unionCombo.setValue(uni);
                        break;
                    }
                }
            }
            
            branchField.setText(child.getBranchName() != null ? child.getBranchName() : "");
        }
    }
    
    @FXML
    private void handleCancel() {
        closeWindow();
    }
    
    @FXML
    private void handleCreate() {
        String fullName = fullNameField.getText() != null ? fullNameField.getText().trim() : "";
        String gender = genderCombo.getValue();
        LocalDate dob = dobPicker.getValue();

        String fathersName = fathersNameField.getText() != null ? fathersNameField.getText().trim() : "";
        String mothersName = mothersNameField.getText() != null ? mothersNameField.getText().trim() : "";
        String contact = contactField.getText() != null ? contactField.getText().trim() : "";
        
        DivisionModel selectedDivision = divisionCombo.getValue();
        DistrictModel selectedDistrict = districtCombo.getValue();
        UpazilaModel selectedUpazilla = upazillaCombo.getValue();
        UnionModel selectedUnion = unionCombo.getValue();
        
        if (fullName.isEmpty()) {
            showAlert("Error", "Please enter the child's full name.");
            return;
        }
        
        if (gender == null) {
            showAlert("Error", "Please select the gender.");
            return;
        }
        
        if (dob == null) {
            showAlert("Error", "Please select the date of birth.");
            return;
        }
        
        if (selectedDivision == null || selectedDistrict == null || selectedUpazilla == null || selectedUnion == null) {
            showAlert("Error", "Please select all location fields (Division, District, Upazilla, Union).");
            return;
        }
        
        if (currentMode == Mode.CREATE) {
            ChildModel newChild = new ChildModel();
            newChild.setFullName(fullName);
            newChild.setGender(gender);
            newChild.setDateOfBirth(DATE_FORMATTER.format(dob));
            newChild.setLastVisit("");
            newChild.setFathersName(fathersName);
            newChild.setMothersName(mothersName);
            newChild.setContactNumber(contact);
            newChild.setDivision(selectedDivision.getName());
            newChild.setDistrict(selectedDistrict.getName());
            newChild.setUpazilla(selectedUpazilla.getName());
            newChild.setUnionName(selectedUnion.getName());
            if (matchedBranch != null) {
                newChild.setBranchId(matchedBranch.getId());
                newChild.setBranchName(matchedBranch.getName());
            }
            
            childDAO.addChild(newChild);
            
            closeWindow();
            
            if (parentController != null) {
                parentController.refreshTable();
                parentController.showSuccessAlert("Success", "Child added successfully!");
            }
        } else if (currentMode == Mode.EDIT && editingChild != null) {
            editingChild.setFullName(fullName);
            editingChild.setGender(gender);
            editingChild.setDateOfBirth(DATE_FORMATTER.format(dob));

            editingChild.setFathersName(fathersName);
            editingChild.setMothersName(mothersName);
            editingChild.setContactNumber(contact);
            editingChild.setDivision(selectedDivision.getName());
            editingChild.setDistrict(selectedDistrict.getName());
            editingChild.setUpazilla(selectedUpazilla.getName());
            editingChild.setUnionName(selectedUnion.getName());
            if (matchedBranch != null) {
                editingChild.setBranchId(matchedBranch.getId());
                editingChild.setBranchName(matchedBranch.getName());
            }
            
            childDAO.updateChild(editingChild);
            
            closeWindow();
            
            if (parentController != null) {
                parentController.refreshTable();
                parentController.showSuccessAlert("Success", "Child updated successfully!");
            }
        }
    }
    
    private void closeWindow() {
        Stage stage = (Stage) fullNameField.getScene().getWindow();
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
