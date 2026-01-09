package com.example.nutrimap.controller;

import com.example.nutrimap.dao.ChildDAO;
import com.example.nutrimap.dao.VisitDAO;
import com.example.nutrimap.model.ChildModel;
import com.example.nutrimap.model.VisitModel;
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
        
        // Count by risk level
        int highRisk = 0;
        int mediumRisk = 0;
        int lowRisk = 0;
        
        for (VisitModel visit : visits) {
            String risk = visit.getRiskLevel();
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
    
    private void loadRiskPieChart() {
        List<VisitModel> visits = visitDAO.getAll();
        
        int high = 0, medium = 0, low = 0, na = 0;
        
        for (VisitModel visit : visits) {
            String risk = visit.getRiskLevel();
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
        
        // Map child ID to latest risk
        Map<Integer, String> childLatestRisk = new HashMap<>();
        for (VisitModel visit : visits) {
            if (!childLatestRisk.containsKey(visit.getChildId())) {
                childLatestRisk.put(visit.getChildId(), visit.getRiskLevel());
            }
        }
        
        // Group by district
        Map<String, AreaRiskData> areaData = new HashMap<>();
        
        for (ChildModel child : children) {
            String district = child.getDistrict();
            if (district == null || district.isEmpty()) {
                district = "Unknown";
            }
            
            AreaRiskData data = areaData.getOrDefault(district, new AreaRiskData(district));
            data.childrenCount++;
            
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
