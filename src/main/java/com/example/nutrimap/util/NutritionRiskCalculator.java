package com.example.nutrimap.util;

import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;

/**
 * Utility class for calculating nutrition levels and risk levels.
 * 
 * Nutrition Level (A1 + A2 Combined):
 * - Severe malnutrition: MUAC < 11.5 cm OR z-score < -3
 * - Moderate malnutrition: MUAC 11.5-12.5 cm OR z-score -3 to -2
 * - Normal: Otherwise
 * 
 * Risk Level (B2 Scoring System):
 * - Based on nutrition level, age, borderline MUAC, and trend factors
 * - Points >= 4: High, Points 2-3: Medium, Points <= 1: Low
 */
public class NutritionRiskCalculator {
    
    // MUAC thresholds in centimeters
    public static final double MUAC_SEVERE_THRESHOLD_CM = 11.5;
    public static final double MUAC_MODERATE_THRESHOLD_CM = 12.5;
    
    // Z-score thresholds
    public static final double ZSCORE_SEVERE_THRESHOLD = -3.0;
    public static final double ZSCORE_MODERATE_THRESHOLD = -2.0;
    
    // Nutrition level strings (exact format as specified)
    public static final String NUTRITION_SEVERE = "severe malnutrition";
    public static final String NUTRITION_MODERATE = "moderate malnutrition";
    public static final String NUTRITION_NORMAL = "normal";
    
    // Risk level strings
    public static final String RISK_HIGH = "high";
    public static final String RISK_MEDIUM = "medium";
    public static final String RISK_LOW = "low";
    public static final String RISK_NA = "N/A";
    
    /**
     * Classify nutrition level based on MUAC (cm) and WHZ/BAZ z-score.
     * Uses the LATEST visit data only.
     * 
     * Rules:
     * - Severe malnutrition if: muacCm < 11.5 OR zScore < -3
     * - Moderate malnutrition if: (11.5 <= muacCm < 12.5) OR (-3 <= zScore < -2)
     * - Normal otherwise
     * 
     * @param muacCm MUAC measurement in centimeters
     * @param zScore WHZ or BAZ z-score (can be null if not available)
     * @return Nutrition level: "severe malnutrition", "moderate malnutrition", or "normal"
     */
    public static String classifyNutritionLevel(double muacCm, Double zScore) {
        // Check for invalid MUAC
        if (muacCm <= 0) {
            // If MUAC is invalid, rely solely on z-score if available
            if (zScore != null) {
                if (zScore < ZSCORE_SEVERE_THRESHOLD) {
                    return NUTRITION_SEVERE;
                } else if (zScore < ZSCORE_MODERATE_THRESHOLD) {
                    return NUTRITION_MODERATE;
                } else {
                    return NUTRITION_NORMAL;
                }
            }
            return NUTRITION_NORMAL; // Default if no valid data
        }
        
        // Check severe malnutrition conditions
        if (muacCm < MUAC_SEVERE_THRESHOLD_CM) {
            return NUTRITION_SEVERE;
        }
        if (zScore != null && zScore < ZSCORE_SEVERE_THRESHOLD) {
            return NUTRITION_SEVERE;
        }
        
        // Check moderate malnutrition conditions
        if (muacCm < MUAC_MODERATE_THRESHOLD_CM) {
            return NUTRITION_MODERATE;
        }
        if (zScore != null && zScore < ZSCORE_MODERATE_THRESHOLD) {
            return NUTRITION_MODERATE;
        }
        
        // Normal
        return NUTRITION_NORMAL;
    }
    
    /**
     * Overloaded method for when z-score is not available (common case).
     * 
     * @param muacCm MUAC measurement in centimeters
     * @return Nutrition level string
     */
    public static String classifyNutritionLevel(double muacCm) {
        return classifyNutritionLevel(muacCm, null);
    }
    
    /**
     * Classify risk level using the B2 scoring system.
     * Uses the LATEST visit data for current values, and previous visit for trend analysis.
     * 
     * Risk points logic:
     * - Base on nutritionLevel: severe=+3, moderate=+2, normal=+1
     * - Age factor: if ageMonths < 24 -> +1
     * - Borderline MUAC: 11.5-11.9 cm -> +1, 12.5-12.9 cm -> +1
     * - Trend: MUAC drop >= 0.5 cm -> +1
     * - Trend: Weight loss >= 5% -> +1
     * 
     * Final mapping: points >= 4 -> high, 2-3 -> medium, <= 1 -> low
     * 
     * @param nutritionLevel The nutrition level from classifyNutritionLevel()
     * @param ageMonths Child's age in months
     * @param muacCm Current MUAC in centimeters
     * @param muacPrevCm Previous MUAC in centimeters (nullable)
     * @param weightKg Current weight in kilograms
     * @param weightPrevKg Previous weight in kilograms (nullable)
     * @return Risk level: "high", "medium", or "low"
     */
    public static String classifyRiskLevel(
            String nutritionLevel,
            int ageMonths,
            double muacCm,
            Double muacPrevCm,
            double weightKg,
            Double weightPrevKg) {
        
        int riskPoints = 0;
        
        // Base points from nutrition level
        if (NUTRITION_SEVERE.equals(nutritionLevel)) {
            riskPoints += 3;
        } else if (NUTRITION_MODERATE.equals(nutritionLevel)) {
            riskPoints += 2;
        } else {
            riskPoints += 1;
        }
        
        // Age factor: children under 24 months are at higher risk
        if (ageMonths < 24) {
            riskPoints += 1;
        }
        
        // Borderline MUAC checks
        // Borderline severe: 11.5 <= muacCm < 11.9
        if (muacCm >= 11.5 && muacCm < 11.9) {
            riskPoints += 1;
        }
        // Borderline moderate: 12.5 <= muacCm < 12.9
        if (muacCm >= 12.5 && muacCm < 12.9) {
            riskPoints += 1;
        }
        
        // Trend factor: MUAC decline >= 0.5 cm (only if previous data exists)
        if (muacPrevCm != null && muacPrevCm > 0) {
            double muacDrop = muacPrevCm - muacCm;
            if (muacDrop >= 0.5) {
                riskPoints += 1;
            }
        }
        
        // Trend factor: Weight loss >= 5% (only if previous data exists)
        if (weightPrevKg != null && weightPrevKg > 0) {
            double weightLossPercent = (weightPrevKg - weightKg) / weightPrevKg;
            if (weightLossPercent >= 0.05) {
                riskPoints += 1;
            }
        }
        
        // Final risk level mapping
        if (riskPoints >= 4) {
            return RISK_HIGH;
        } else if (riskPoints >= 2) {
            return RISK_MEDIUM;
        } else {
            return RISK_LOW;
        }
    }
    
    /**
     * Calculate age in months from date of birth string (calculates age as of today).
     * 
     * @param dateOfBirth Date of birth in "yyyy-MM-dd" format
     * @return Age in months, or -1 if invalid
     */
    public static int calculateAgeInMonths(String dateOfBirth) {
        if (dateOfBirth == null || dateOfBirth.isEmpty()) {
            return -1;
        }
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            LocalDate dob = LocalDate.parse(dateOfBirth, formatter);
            LocalDate now = LocalDate.now();
            Period period = Period.between(dob, now);
            return period.getYears() * 12 + period.getMonths();
        } catch (Exception e) {
            return -1;
        }
    }
    
    /**
     * Calculate age in months at a specific date (visit date).
     * This is the preferred method for calculating age at time of visit.
     * 
     * @param birthDate Child's date of birth
     * @param visitDate Date of the visit
     * @return Age in months at the visit date, or -1 if invalid
     */
    public static int calculateAgeInMonths(LocalDate birthDate, LocalDate visitDate) {
        if (birthDate == null || visitDate == null) {
            return -1;
        }
        if (visitDate.isBefore(birthDate)) {
            return -1;
        }
        Period period = Period.between(birthDate, visitDate);
        return period.getYears() * 12 + period.getMonths();
    }
    
    /**
     * Calculate age in months from date strings (birth date and visit date).
     * 
     * @param birthDateStr Child's date of birth in "yyyy-MM-dd" format
     * @param visitDateStr Date of the visit in "yyyy-MM-dd" format
     * @return Age in months at the visit date, or -1 if invalid
     */
    public static int calculateAgeInMonths(String birthDateStr, String visitDateStr) {
        if (birthDateStr == null || birthDateStr.isEmpty() || 
            visitDateStr == null || visitDateStr.isEmpty()) {
            return -1;
        }
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            LocalDate birthDate = LocalDate.parse(birthDateStr, formatter);
            LocalDate visitDate = LocalDate.parse(visitDateStr, formatter);
            return calculateAgeInMonths(birthDate, visitDate);
        } catch (Exception e) {
            return -1;
        }
    }
    
    /**
     * Convert MUAC from millimeters to centimeters.
     * 
     * @param muacMm MUAC in millimeters (int)
     * @return MUAC in centimeters
     */
    public static double muacMmToCm(int muacMm) {
        return muacMm / 10.0;
    }
    
    /**
     * Convert MUAC from millimeters to centimeters.
     * 
     * @param muacMm MUAC in millimeters (double)
     * @return MUAC in centimeters
     */
    public static double muacMmToCm(double muacMm) {
        return muacMm / 10.0;
    }
    
    /**
     * Get display-friendly nutrition level (capitalized).
     * 
     * @param nutritionLevel The nutrition level string
     * @return Capitalized version for display
     */
    public static String getNutritionLevelDisplay(String nutritionLevel) {
        if (nutritionLevel == null) {
            return "N/A";
        }
        switch (nutritionLevel) {
            case NUTRITION_SEVERE:
                return "Severe Malnutrition";
            case NUTRITION_MODERATE:
                return "Moderate Malnutrition";
            case NUTRITION_NORMAL:
                return "Normal";
            default:
                return nutritionLevel;
        }
    }
    
    /**
     * Get display-friendly risk level (uppercase).
     * 
     * @param riskLevel The risk level string
     * @return Uppercase version for display
     */
    public static String getRiskLevelDisplay(String riskLevel) {
        if (riskLevel == null) {
            return "N/A";
        }
        return riskLevel.toUpperCase();
    }
    
    /**
     * Get CSS style class for nutrition level.
     * 
     * @param nutritionLevel The nutrition level string
     * @return CSS class name for styling
     */
    public static String getNutritionStyleClass(String nutritionLevel) {
        if (nutritionLevel == null) {
            return "status-na";
        }
        switch (nutritionLevel) {
            case NUTRITION_SEVERE:
                return "status-severe";
            case NUTRITION_MODERATE:
                return "status-moderate";
            case NUTRITION_NORMAL:
                return "status-normal";
            default:
                return "status-na";
        }
    }
    
    /**
     * Get CSS style class for risk level.
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
    
    /**
     * Get descriptive label for risk level.
     * 
     * @param riskLevel The risk level string
     * @return A descriptive label
     */
    public static String getRiskDescription(String riskLevel) {
        if (riskLevel == null) {
            return "Not Available";
        }
        switch (riskLevel.toLowerCase()) {
            case "high":
                return "High Risk - Urgent intervention needed";
            case "medium":
                return "Medium Risk - Close monitoring required";
            case "low":
                return "Low Risk - Continue routine monitoring";
            default:
                return "Not Available";
        }
    }
    
    // ========== WHO Z-SCORE CALCULATION METHODS ==========
    
    /**
     * Compute Weight-for-Height Z-score (WHZ) using WHO Growth Standards.
     * This is a convenience wrapper around WhoGrowthStandards.computeWhzZScore().
     * 
     * @param ageMonths child's age in months (6-59 months typical range)
     * @param sex "M" for male, "F" for female (or "Male"/"Female")
     * @param heightCm height in centimeters
     * @param weightKg weight in kilograms
     * @return WHZ z-score, or Double.NaN if calculation not possible
     */
    public static double computeWhzZScore(int ageMonths, String sex, double heightCm, double weightKg) {
        // Convert gender string to sex code
        String sexCode = WhoGrowthStandards.genderToSex(sex);
        if (sexCode == null) {
            return Double.NaN;
        }
        return WhoGrowthStandards.computeWhzZScore(ageMonths, sexCode, heightCm, weightKg);
    }
    
    /**
     * Classify nutrition level from raw measurement data.
     * Automatically computes z-score using WHO Growth Standards.
     * 
     * Uses LATEST visit data only.
     * 
     * @param ageMonths child's age in months
     * @param sex "M" or "F" (or "Male"/"Female")
     * @param heightCm height in centimeters
     * @param weightKg weight in kilograms
     * @param muacCm MUAC in centimeters
     * @return Nutrition level: "severe malnutrition", "moderate malnutrition", or "normal"
     */
    public static String classifyNutritionLevelFromRawData(
            int ageMonths,
            String sex,
            double heightCm,
            double weightKg,
            double muacCm) {
        
        // Compute z-score using WHO Growth Standards
        double zScore = computeWhzZScore(ageMonths, sex, heightCm, weightKg);
        
        // If z-score is valid, use it with MUAC for classification
        if (!Double.isNaN(zScore)) {
            return classifyNutritionLevel(muacCm, zScore);
        }
        
        // Fallback to MUAC-only classification if z-score couldn't be computed
        if (muacCm > 0) {
            if (muacCm < MUAC_SEVERE_THRESHOLD_CM) {
                return NUTRITION_SEVERE;
            } else if (muacCm < MUAC_MODERATE_THRESHOLD_CM) {
                return NUTRITION_MODERATE;
            } else {
                return NUTRITION_NORMAL;
            }
        }
        
        // No valid data - default to normal
        return NUTRITION_NORMAL;
    }
    
    /**
     * Classify nutrition level from visit data.
     * Calculates age from birthDate and visitDate, converts MUAC from mm to cm.
     * 
     * This is the preferred method when working with Visit and Child entities.
     * 
     * @param birthDateStr Child's date of birth in "yyyy-MM-dd" format
     * @param visitDateStr Visit date in "yyyy-MM-dd" format
     * @param sex "M" or "F" (or "Male"/"Female")
     * @param heightCm Height in centimeters
     * @param weightKg Weight in kilograms
     * @param muacMm MUAC in MILLIMETERS (as stored in database)
     * @return Nutrition level: "severe malnutrition", "moderate malnutrition", or "normal"
     */
    public static String classifyNutritionLevelFromVisitData(
            String birthDateStr,
            String visitDateStr,
            String sex,
            double heightCm,
            double weightKg,
            double muacMm) {
        
        // Calculate age at time of visit
        int ageMonths = calculateAgeInMonths(birthDateStr, visitDateStr);
        if (ageMonths < 0) {
            ageMonths = 36; // Default if dates invalid
        }
        
        // Convert MUAC from mm to cm
        double muacCm = muacMmToCm(muacMm);
        
        // Use the existing raw data method
        return classifyNutritionLevelFromRawData(ageMonths, sex, heightCm, weightKg, muacCm);
    }
    
    /**
     * Complete evaluation from visit data.
     * Calculates nutrition level and risk level from Child + Visit entities.
     * 
     * @param birthDateStr Child's date of birth in "yyyy-MM-dd" format
     * @param visitDateStr Visit date in "yyyy-MM-dd" format
     * @param sex "M" or "F" (or "Male"/"Female")
     * @param heightCm Height in centimeters
     * @param weightKg Weight in kilograms
     * @param muacMm Current MUAC in MILLIMETERS
     * @param muacPrevMm Previous MUAC in MILLIMETERS (nullable)
     * @param weightPrevKg Previous weight in kilograms (nullable)
     * @return NutritionRiskResult containing nutritionLevel, riskLevel, and zScore
     */
    public static NutritionRiskResult evaluateFromVisitData(
            String birthDateStr,
            String visitDateStr,
            String sex,
            double heightCm,
            double weightKg,
            double muacMm,
            Double muacPrevMm,
            Double weightPrevKg) {
        
        // Calculate age at time of visit
        int ageMonths = calculateAgeInMonths(birthDateStr, visitDateStr);
        if (ageMonths < 0) {
            ageMonths = 36; // Default if dates invalid
        }
        
        // Convert MUAC from mm to cm
        double muacCm = muacMmToCm(muacMm);
        Double muacPrevCm = muacPrevMm != null ? muacMmToCm(muacPrevMm) : null;
        
        // Classify nutrition level
        String nutritionLevel = classifyNutritionLevelFromRawData(ageMonths, sex, heightCm, weightKg, muacCm);
        
        // Classify risk level
        String riskLevel = classifyRiskLevel(
            nutritionLevel,
            ageMonths,
            muacCm > 0 ? muacCm : 13.0,
            muacPrevCm,
            weightKg,
            weightPrevKg
        );
        
        // Compute z-score for informational purposes
        double zScore = computeWhzZScore(ageMonths, sex, heightCm, weightKg);
        
        return new NutritionRiskResult(nutritionLevel, riskLevel, zScore);
    }
    
    /**
     * Evaluate risk level from latest visit data.
     * Computes both nutrition level and risk level from raw measurements.
     * 
     * This is the main entry point for evaluating a child's risk from visit data.
     * Uses LATEST visit data for current values, and previous visit for trend analysis.
     * 
     * @param ageMonths child's age in months
     * @param sex "M" or "F" (or "Male"/"Female")
     * @param heightCm current height in centimeters
     * @param weightKg current weight in kilograms
     * @param muacCm current MUAC in centimeters
     * @param muacPrevCm previous MUAC in centimeters (nullable, for trend)
     * @param weightPrevKg previous weight in kilograms (nullable, for trend)
     * @return Risk level: "high", "medium", or "low"
     */
    public static String evaluateRiskFromLatestVisit(
            int ageMonths,
            String sex,
            double heightCm,
            double weightKg,
            double muacCm,
            Double muacPrevCm,
            Double weightPrevKg) {
        
        // Step 1: Classify nutrition level from raw data
        String nutritionLevel = classifyNutritionLevelFromRawData(
            ageMonths, sex, heightCm, weightKg, muacCm
        );
        
        // Step 2: Classify risk level using the B2 scoring system
        return classifyRiskLevel(
            nutritionLevel,
            ageMonths,
            muacCm > 0 ? muacCm : 13.0, // Default if MUAC not available
            muacPrevCm,
            weightKg,
            weightPrevKg
        );
    }
    
    /**
     * Convenience method to get both nutrition level and risk level.
     * Returns a result object containing both values.
     * 
     * @param ageMonths child's age in months
     * @param sex "M" or "F" (or "Male"/"Female")
     * @param heightCm current height in centimeters
     * @param weightKg current weight in kilograms
     * @param muacCm current MUAC in centimeters
     * @param muacPrevCm previous MUAC in centimeters (nullable)
     * @param weightPrevKg previous weight in kilograms (nullable)
     * @return NutritionRiskResult containing nutritionLevel and riskLevel
     */
    public static NutritionRiskResult evaluateFullAssessment(
            int ageMonths,
            String sex,
            double heightCm,
            double weightKg,
            double muacCm,
            Double muacPrevCm,
            Double weightPrevKg) {
        
        String nutritionLevel = classifyNutritionLevelFromRawData(
            ageMonths, sex, heightCm, weightKg, muacCm
        );
        
        String riskLevel = classifyRiskLevel(
            nutritionLevel,
            ageMonths,
            muacCm > 0 ? muacCm : 13.0,
            muacPrevCm,
            weightKg,
            weightPrevKg
        );
        
        // Compute z-score for informational purposes
        double zScore = computeWhzZScore(ageMonths, sex, heightCm, weightKg);
        
        return new NutritionRiskResult(nutritionLevel, riskLevel, zScore);
    }
    
    /**
     * Result class containing nutrition level, risk level, and computed z-score.
     */
    public static class NutritionRiskResult {
        private final String nutritionLevel;
        private final String riskLevel;
        private final double zScore;
        
        public NutritionRiskResult(String nutritionLevel, String riskLevel, double zScore) {
            this.nutritionLevel = nutritionLevel;
            this.riskLevel = riskLevel;
            this.zScore = zScore;
        }
        
        public String getNutritionLevel() { return nutritionLevel; }
        public String getRiskLevel() { return riskLevel; }
        public double getZScore() { return zScore; }
        public boolean hasValidZScore() { return !Double.isNaN(zScore); }
        
        public String getNutritionLevelDisplay() {
            return NutritionRiskCalculator.getNutritionLevelDisplay(nutritionLevel);
        }
        
        public String getRiskLevelDisplay() {
            return NutritionRiskCalculator.getRiskLevelDisplay(riskLevel);
        }
        
        public String getZScoreDisplay() {
            if (Double.isNaN(zScore)) {
                return "N/A";
            }
            return String.format("%.2f", zScore);
        }
    }
    
    // ========== LEGACY METHODS FOR BACKWARD COMPATIBILITY ==========
    
    /**
     * @deprecated Use evaluateRiskFromLatestVisit() or classifyRiskLevel() instead
     */
    @Deprecated
    public static String calculateRiskFromMuac(int muacMm) {
        double muacCm = muacMmToCm(muacMm);
        String nutritionLevel = classifyNutritionLevel(muacCm);
        // Use default values for simplified calculation
        return classifyRiskLevel(nutritionLevel, 36, muacCm, null, 0, null);
    }
}
