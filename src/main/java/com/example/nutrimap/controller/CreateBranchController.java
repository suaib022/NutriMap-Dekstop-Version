package com.example.nutrimap.controller;

import com.example.nutrimap.dao.*;
import com.example.nutrimap.model.*;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.util.List;
import java.util.stream.Collectors;

public class CreateBranchController {
    
    public enum Mode {
        CREATE, EDIT
    }
    
    @FXML private TextField nameField;
    @FXML private TextField bnNameField;
    @FXML private ComboBox<DivisionModel> divisionCombo;
    @FXML private TextField bnDivisionField;
    @FXML private ComboBox<DistrictModel> districtCombo;
    @FXML private TextField bnDistrictField;
    @FXML private ComboBox<UpazilaModel> upazillaCombo;
    @FXML private TextField bnUpazillaField;
    @FXML private TextArea areaTextArea;
    @FXML private TextField urlField;
    @FXML private Label titleLabel;
    @FXML private Button submitButton;
    
    private BranchesController parentController;
    private BranchDAO branchDAO;
    private DivisionDAO divisionDAO;
    private DistrictDAO districtDAO;
    private UpazilaDAO upazilaDAO;
    private UnionDAO unionDAO;
    private Mode currentMode = Mode.CREATE;
    private BranchModel editingBranch = null;
    
    private String currentAreaEnglish = "";
    private String currentAreaBengali = "";
    
    @FXML
    public void initialize() {
        branchDAO = new BranchDAO();
        divisionDAO = new DivisionDAO();
        districtDAO = new DistrictDAO();
        upazilaDAO = new UpazilaDAO();
        unionDAO = new UnionDAO();
        
        loadDivisions();
        setupComboListeners();
    }
    
    private void loadDivisions() {
        List<DivisionModel> divisions = divisionDAO.getAll();
        divisionCombo.setItems(FXCollections.observableArrayList(divisions));
    }
    
    private void setupComboListeners() {
        divisionCombo.valueProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                bnDivisionField.setText(newVal.getBnName() != null ? newVal.getBnName() : "");
                
                districtCombo.setDisable(false);
                List<DistrictModel> districts = districtDAO.getByDivisionId(newVal.getId());
                districtCombo.setItems(FXCollections.observableArrayList(districts));
                districtCombo.setValue(null);
                
                upazillaCombo.setValue(null);
                upazillaCombo.setItems(FXCollections.observableArrayList());
                upazillaCombo.setDisable(true);
                bnUpazillaField.setText("");
                
                areaTextArea.setText("");
                currentAreaEnglish = "";
                currentAreaBengali = "";
                
                bnDistrictField.setText("");
            } else {
                districtCombo.setDisable(true);
                districtCombo.setValue(null);
                bnDivisionField.setText("");
            }
        });
        
        districtCombo.valueProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                bnDistrictField.setText(newVal.getBnName() != null ? newVal.getBnName() : "");
                
                upazillaCombo.setDisable(false);
                List<UpazilaModel> upazilas = upazilaDAO.getByDistrictId(newVal.getId());
                upazillaCombo.setItems(FXCollections.observableArrayList(upazilas));
                upazillaCombo.setValue(null);
                
                areaTextArea.setText("");
                currentAreaEnglish = "";
                currentAreaBengali = "";
                
                bnUpazillaField.setText("");
            } else {
                upazillaCombo.setDisable(true);
                upazillaCombo.setValue(null);
                bnDistrictField.setText("");
            }
        });
        
        upazillaCombo.valueProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                bnUpazillaField.setText(newVal.getBnName() != null ? newVal.getBnName() : "");
                
                List<UnionModel> unions = unionDAO.getByUpazilaId(newVal.getId());
                
                currentAreaEnglish = unions.stream()
                        .map(UnionModel::getName)
                        .filter(name -> name != null && !name.isEmpty())
                        .collect(Collectors.joining(", "));
                
                currentAreaBengali = unions.stream()
                        .map(UnionModel::getBnName)
                        .filter(name -> name != null && !name.isEmpty())
                        .collect(Collectors.joining(", "));
                
                areaTextArea.setText(currentAreaEnglish);
            } else {
                bnUpazillaField.setText("");
                areaTextArea.setText("");
                currentAreaEnglish = "";
                currentAreaBengali = "";
            }
        });
    }
    
    public void setParentController(BranchesController parentController) {
        this.parentController = parentController;
    }
    
    public void setMode(Mode mode, BranchModel branch) {
        this.currentMode = mode;
        this.editingBranch = branch;
        
        if (mode == Mode.EDIT && branch != null) {
            titleLabel.setText("Edit Branch");
            submitButton.setText("Update Branch");
            nameField.setText(branch.getName() != null ? branch.getName() : "");
            bnNameField.setText(branch.getBn_name() != null ? branch.getBn_name() : "");
            urlField.setText(branch.getUrl() != null ? branch.getUrl() : "");
            
            if (branch.getDivision() != null) {
                for (DivisionModel div : divisionCombo.getItems()) {
                    if (div.getName().equals(branch.getDivision())) {
                        divisionCombo.setValue(div);
                        break;
                    }
                }
            }
            
            if (branch.getDistrict() != null && !districtCombo.getItems().isEmpty()) {
                for (DistrictModel dist : districtCombo.getItems()) {
                    if (dist.getName().equals(branch.getDistrict())) {
                        districtCombo.setValue(dist);
                        break;
                    }
                }
            }
            
            if (branch.getUpazilla() != null && !upazillaCombo.getItems().isEmpty()) {
                for (UpazilaModel upa : upazillaCombo.getItems()) {
                    if (upa.getName().equals(branch.getUpazilla())) {
                        upazillaCombo.setValue(upa);
                        break;
                    }
                }
            }
            
            bnDivisionField.setText(branch.getBn_division() != null ? branch.getBn_division() : "");
            bnDistrictField.setText(branch.getBn_district() != null ? branch.getBn_district() : "");
            bnUpazillaField.setText(branch.getBn_upazilla() != null ? branch.getBn_upazilla() : "");
        }
    }
    
    @FXML
    private void handleCancel() {
        closeWindow();
    }
    
    @FXML
    private void handleCreate() {
        String name = nameField.getText() != null ? nameField.getText().trim() : "";
        String bnName = bnNameField.getText() != null ? bnNameField.getText().trim() : "";
        
        DivisionModel selectedDivision = divisionCombo.getValue();
        DistrictModel selectedDistrict = districtCombo.getValue();
        UpazilaModel selectedUpazilla = upazillaCombo.getValue();
        
        String division = selectedDivision != null ? selectedDivision.getName() : "";
        String bnDivision = bnDivisionField.getText() != null ? bnDivisionField.getText().trim() : "";
        String district = selectedDistrict != null ? selectedDistrict.getName() : "";
        String bnDistrict = bnDistrictField.getText() != null ? bnDistrictField.getText().trim() : "";
        String upazilla = selectedUpazilla != null ? selectedUpazilla.getName() : "";
        String bnUpazilla = bnUpazillaField.getText() != null ? bnUpazillaField.getText().trim() : "";
        String area = currentAreaEnglish;
        String bnArea = currentAreaBengali;
        String url = urlField.getText() != null ? urlField.getText().trim() : "";
        
        if (name.isEmpty() || division.isEmpty() || district.isEmpty() || upazilla.isEmpty()) {
            showAlert("Error", "Please fill in required fields (Name, Division, District, Upazilla).");
            return;
        }
        
        if (currentMode == Mode.CREATE) {
            BranchModel newBranch = new BranchModel();
            newBranch.setName(name);
            newBranch.setBn_name(bnName);
            newBranch.setDivision(division);
            newBranch.setBn_division(bnDivision);
            newBranch.setDistrict(district);
            newBranch.setBn_district(bnDistrict);
            newBranch.setUpazilla(upazilla);
            newBranch.setBn_upazilla(bnUpazilla);
            newBranch.setArea(area);
            newBranch.setBn_area(bnArea);
            newBranch.setUrl(url);
            
            branchDAO.addBranch(newBranch);
            
            closeWindow();
            
            if (parentController != null) {
                parentController.refreshTable();
                parentController.showSuccessAlert("Success", "Branch created successfully!");
            }
        } else if (currentMode == Mode.EDIT && editingBranch != null) {
            editingBranch.setName(name);
            editingBranch.setBn_name(bnName);
            editingBranch.setDivision(division);
            editingBranch.setBn_division(bnDivision);
            editingBranch.setDistrict(district);
            editingBranch.setBn_district(bnDistrict);
            editingBranch.setUpazilla(upazilla);
            editingBranch.setBn_upazilla(bnUpazilla);
            editingBranch.setArea(area);
            editingBranch.setBn_area(bnArea);
            editingBranch.setUrl(url);
            
            branchDAO.updateBranch(editingBranch);
            
            closeWindow();
            
            if (parentController != null) {
                parentController.refreshTable();
                parentController.showSuccessAlert("Success", "Branch updated successfully!");
            }
        }
    }
    
    private void closeWindow() {
        Stage stage = (Stage) nameField.getScene().getWindow();
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
