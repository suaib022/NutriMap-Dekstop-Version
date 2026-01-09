package com.example.nutrimap.util;

/**
 * Utility class for calculating nutrition risk levels using WHO MUAC standards.
 * 
 * MUAC (Mid-Upper Arm Circumference) is used to assess malnutrition in children 6-59 months.
 * 
 * WHO Classification:
 * - Severe Acute Malnutrition (High Risk): MUAC < 115mm
 * - Moderate Acute Malnutrition (Medium Risk): MUAC 115-124mm
 * - Normal (Low Risk): MUAC >= 125mm
 */
public class NutritionRiskCalculator {
    
    // MUAC thresholds in millimeters (WHO standards)
    public static final int MUAC_SEVERE_THRESHOLD = 115;  // < 115mm = Severe
    public static final int MUAC_MODERATE_THRESHOLD = 125; // 115-124mm = Moderate
    
    public static final String RISK_HIGH = "High";
    public static final String RISK_MEDIUM = "Medium";
    public static final String RISK_LOW = "Low";
    public static final String RISK_NA = "N/A";
    
    /**
     * Calculate nutrition risk level based on MUAC measurement.
     * 
     * @param muacMm MUAC measurement in millimeters
     * @return Risk level: "High", "Medium", or "Low"
     */
    public static String calculateRiskFromMuac(int muacMm) {
        if (muacMm <= 0) {
            return RISK_NA;
        }
        
        if (muacMm < MUAC_SEVERE_THRESHOLD) {
            return RISK_HIGH;
        } else if (muacMm < MUAC_MODERATE_THRESHOLD) {
            return RISK_MEDIUM;
        } else {
            return RISK_LOW;
        }
    }
    
    /**
     * Get a descriptive label for the risk level.
     * 
     * @param riskLevel The risk level string
     * @return A descriptive label
     */
    public static String getRiskDescription(String riskLevel) {
        if (riskLevel == null) {
            return "Not Available";
        }
        
        switch (riskLevel) {
            case RISK_HIGH:
                return "Severe Acute Malnutrition - Urgent attention needed";
            case RISK_MEDIUM:
                return "Moderate Acute Malnutrition - Monitoring required";
            case RISK_LOW:
                return "Normal nutrition status";
            default:
                return "Not Available";
        }
    }
    
    /**
     * Get CSS class for risk level styling.
     * 
     * @param riskLevel The risk level string
     * @return CSS class name for styling
     */
    public static String getRiskStyleClass(String riskLevel) {
        if (riskLevel == null) {
            return "status-na";
        }
        
        switch (riskLevel.toLowerCase()) {
            case "high":
                return "status-high";
            case "medium":
                return "status-medium";
            case "low":
                return "status-low";
            default:
                return "status-na";
        }
    }
}
