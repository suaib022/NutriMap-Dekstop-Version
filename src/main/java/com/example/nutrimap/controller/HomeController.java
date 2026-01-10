package com.example.nutrimap.controller;

import com.example.nutrimap.dao.ChildDAO;
import com.example.nutrimap.dao.VisitDAO;
import com.example.nutrimap.model.ChildModel;
import com.example.nutrimap.model.VisitModel;
import com.example.nutrimap.util.NutritionRiskCalculator;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * Controller for the enhanced home/dashboard view.
 * Shows summary statistics, charts for risk distribution and visit trends,
 * and area-wise nutrition data.
 */
public class HomeController {
    
    // Summary cards
    @FXML private Label totalChildrenLabel;
    @FXML private Label totalVisitsLabel;
    @FXML private Label highRiskLabel;
    @FXML private Label mediumRiskLabel;
    @FXML private Label lowRiskLabel;
    
    // Charts
    @FXML private PieChart riskPieChart;
    @FXML private LineChart<String, Number> visitsLineChart;
    
    // Area-wise table
    @FXML private TableView<AreaRiskData> areaTable;
    @FXML private TableColumn<AreaRiskData, String> areaColumn;
    @FXML private TableColumn<AreaRiskData, Integer> childrenColumn;
    @FXML private TableColumn<AreaRiskData, Integer> highColumn;
    @FXML private TableColumn<AreaRiskData, Integer> mediumColumn;
    @FXML private TableColumn<AreaRiskData, Integer> lowColumn;
    
    private ChildDAO childDAO;
    private VisitDAO visitDAO;
    
    @FXML
    public void initialize() {
        childDAO = new ChildDAO();
        visitDAO = new VisitDAO();
        
        loadStatistics();
        loadRiskPieChart();
        loadVisitsLineChart();
        loadAreaTable();
    }
    
    private void loadStatistics() {
        List<ChildModel> children = childDAO.getAll();
        List<VisitModel> visits = visitDAO.getAll();
        
        int totalChildren = children.size();
        int totalVisits = visits.size();
        
        // Get the LATEST visit risk for each child
        Map<Integer, String> childLatestRisk = getLatestVisitRiskPerChild(visits, children);
        
        // Count by risk level (based on latest visit per child)
        int highRisk = 0;
        int mediumRisk = 0;
        int lowRisk = 0;
        
        for (String risk : childLatestRisk.values()) {
            if (risk != null) {
                switch (risk.toLowerCase()) {
                    case "high":
                        highRisk++;
                        break;
                    case "medium":
                        mediumRisk++;
                        break;
                    case "low":
                        lowRisk++;
                        break;
                }
            }
        }
        
        totalChildrenLabel.setText(String.valueOf(totalChildren));
        totalVisitsLabel.setText(String.valueOf(totalVisits));
        highRiskLabel.setText(String.valueOf(highRisk));
        mediumRiskLabel.setText(String.valueOf(mediumRisk));
        lowRiskLabel.setText(String.valueOf(lowRisk));
    }
    
    /**
     * Get the latest visit's RECALCULATED risk level for each child.
     * This ensures each child is counted only once based on their most recent visit,
     * and the risk is calculated using the same logic as the child profile view.
     */
    private Map<Integer, String> getLatestVisitRiskPerChild(List<VisitModel> visits, List<ChildModel> children) {
        // Build child lookup map
        Map<Integer, ChildModel> childMap = new HashMap<>();
        for (ChildModel child : children) {
            childMap.put(child.getId(), child);
        }
        
        // Find latest visit per child
        Map<Integer, VisitModel> latestVisitPerChild = new HashMap<>();
        Map<Integer, VisitModel> previousVisitPerChild = new HashMap<>();
        
        for (VisitModel visit : visits) {
            int childId = visit.getChildId();
            VisitModel existing = latestVisitPerChild.get(childId);
            
            if (existing == null) {
                latestVisitPerChild.put(childId, visit);
            } else {
                // Compare visit dates to find the latest
                // Use visit ID as tiebreaker when dates are equal (higher ID = more recent)
                String existingDate = existing.getVisitDate();
                String currentDate = visit.getVisitDate();
                
                boolean currentIsNewer = false;
                if (currentDate != null && existingDate == null) {
                    currentIsNewer = true;
                } else if (currentDate != null && existingDate != null) {
                    int dateCompare = currentDate.compareTo(existingDate);
                    if (dateCompare > 0) {
                        currentIsNewer = true;
                    } else if (dateCompare == 0 && visit.getVisitId() > existing.getVisitId()) {
                        // Same date, use ID as tiebreaker
                        currentIsNewer = true;
                    }
                }
                
                if (currentIsNewer) {
                    // Current becomes latest, existing becomes previous
                    previousVisitPerChild.put(childId, existing);
                    latestVisitPerChild.put(childId, visit);
                } else {
                    // Check if current should be previous (second most recent)
                    VisitModel currentPrev = previousVisitPerChild.get(childId);
                    if (currentPrev == null) {
                        previousVisitPerChild.put(childId, visit);
                    } else {
                        // Compare with current previous
                        String prevDate = currentPrev.getVisitDate();
                        if (currentDate != null && (prevDate == null || currentDate.compareTo(prevDate) > 0 ||
                            (currentDate.equals(prevDate) && visit.getVisitId() > currentPrev.getVisitId()))) {
                            previousVisitPerChild.put(childId, visit);
                        }
                    }
                }
            }
        }
        
        // RECALCULATE risk levels using the same logic as profile view
        Map<Integer, String> result = new HashMap<>();
        for (Map.Entry<Integer, VisitModel> entry : latestVisitPerChild.entrySet()) {
            int childId = entry.getKey();
            VisitModel latestVisit = entry.getValue();
            ChildModel child = childMap.get(childId);
            
            if (child == null) {
                result.put(childId, latestVisit.getRiskLevel()); // fallback to stored
                continue;
            }
            
            // Get previous visit for trend analysis
            VisitModel previousVisit = previousVisitPerChild.get(childId);
            Double muacPrevMm = null;
            Double weightPrevKg = null;
            if (previousVisit != null) {
                if (previousVisit.getMuacMm() > 0) {
                    muacPrevMm = (double) previousVisit.getMuacMm();
                }
                if (previousVisit.getWeightKg() > 0) {
                    weightPrevKg = previousVisit.getWeightKg();
                }
            }
            
            // Recalculate using the same method as profile view
            NutritionRiskCalculator.NutritionRiskResult evalResult = NutritionRiskCalculator.evaluateFromVisitData(
                child.getDateOfBirth(),
                latestVisit.getVisitDate(),
                child.getGender(),
                latestVisit.getHeightCm(),
                latestVisit.getWeightKg(),
                latestVisit.getMuacMm(),
                muacPrevMm,
                weightPrevKg
            );
            
            result.put(childId, evalResult.getRiskLevel());
        }
        return result;
    }
    
    private void loadRiskPieChart() {
        List<VisitModel> visits = visitDAO.getAll();
        
        List<ChildModel> children = childDAO.getAll();
        
        // Get latest visit risk per child
        Map<Integer, String> childLatestRisk = getLatestVisitRiskPerChild(visits, children);
        
        int high = 0, medium = 0, low = 0, na = 0;
        
        for (String risk : childLatestRisk.values()) {
            if (risk != null) {
                switch (risk.toLowerCase()) {
                    case "high": high++; break;
                    case "medium": medium++; break;
                    case "low": low++; break;
                    default: na++; break;
                }
            } else {
                na++;
            }
        }
        
        riskPieChart.getData().clear();
        if (high > 0) riskPieChart.getData().add(new PieChart.Data("High Risk (" + high + ")", high));
        if (medium > 0) riskPieChart.getData().add(new PieChart.Data("Medium Risk (" + medium + ")", medium));
        if (low > 0) riskPieChart.getData().add(new PieChart.Data("Low Risk (" + low + ")", low));
        if (na > 0) riskPieChart.getData().add(new PieChart.Data("N/A (" + na + ")", na));
    }
    
    private void loadVisitsLineChart() {
        List<VisitModel> visits = visitDAO.getAll();
        
        // Count visits by month (last 6 months)
        Map<String, Integer> monthlyVisits = new LinkedHashMap<>();
        DateTimeFormatter monthFormatter = DateTimeFormatter.ofPattern("yyyy-MM");
        DateTimeFormatter displayFormatter = DateTimeFormatter.ofPattern("MMM yy");
        
        // Initialize last 6 months
        LocalDate now = LocalDate.now();
        for (int i = 5; i >= 0; i--) {
            LocalDate month = now.minusMonths(i);
            String key = month.format(monthFormatter);
            String display = month.format(displayFormatter);
            monthlyVisits.put(display, 0);
        }
        
        // Count visits
        for (VisitModel visit : visits) {
            if (visit.getVisitDate() != null && !visit.getVisitDate().isEmpty()) {
                try {
                    LocalDate visitDate = LocalDate.parse(visit.getVisitDate().substring(0, 10));
                    String monthKey = visitDate.format(monthFormatter);
                    String display = visitDate.format(displayFormatter);
                    if (monthlyVisits.containsKey(display)) {
                        monthlyVisits.put(display, monthlyVisits.get(display) + 1);
                    }
                } catch (Exception e) {
                    // Skip invalid dates
                }
            }
        }
        
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Visits");
        
        for (Map.Entry<String, Integer> entry : monthlyVisits.entrySet()) {
            series.getData().add(new XYChart.Data<>(entry.getKey(), entry.getValue()));
        }
        
        visitsLineChart.getData().clear();
        visitsLineChart.getData().add(series);
    }
    
    private void loadAreaTable() {
        List<ChildModel> children = childDAO.getAll();
        List<VisitModel> visits = visitDAO.getAll();
        
        // Get the latest visit risk for each child
        Map<Integer, String> childLatestRisk = getLatestVisitRiskPerChild(visits, children);
        
        // Group by district
        Map<String, AreaRiskData> areaData = new HashMap<>();
        
        for (ChildModel child : children) {
            String district = child.getDistrict();
            if (district == null || district.isEmpty()) {
                district = "Unknown";
            }
            
            AreaRiskData data = areaData.getOrDefault(district, new AreaRiskData(district));
            data.childrenCount++;
            
            // Get latest risk for this child
            String risk = childLatestRisk.get(child.getId());
            if (risk != null) {
                switch (risk.toLowerCase()) {
                    case "high": data.highCount++; break;
                    case "medium": data.mediumCount++; break;
                    case "low": data.lowCount++; break;
                }
            }
            
            areaData.put(district, data);
        }
        
        // Setup table columns
        areaColumn.setCellValueFactory(new PropertyValueFactory<>("area"));
        childrenColumn.setCellValueFactory(new PropertyValueFactory<>("childrenCount"));
        highColumn.setCellValueFactory(new PropertyValueFactory<>("highCount"));
        mediumColumn.setCellValueFactory(new PropertyValueFactory<>("mediumCount"));
        lowColumn.setCellValueFactory(new PropertyValueFactory<>("lowCount"));
        
        areaTable.setItems(FXCollections.observableArrayList(areaData.values()));
    }
    
    /**
     * Data class for area-wise risk summary
     */
    public static class AreaRiskData {
        private String area;
        private int childrenCount;
        private int highCount;
        private int mediumCount;
        private int lowCount;
        
        public AreaRiskData(String area) {
            this.area = area;
            this.childrenCount = 0;
            this.highCount = 0;
            this.mediumCount = 0;
            this.lowCount = 0;
        }
        
        public String getArea() { return area; }
        public int getChildrenCount() { return childrenCount; }
        public int getHighCount() { return highCount; }
        public int getMediumCount() { return mediumCount; }
        public int getLowCount() { return lowCount; }
    }
}
