package com.example.nutrimap.util;

import java.util.HashMap;
import java.util.Map;

/**
 * WHO Child Growth Standards - Weight-for-Height LMS Reference Data
 * 
 * Contains L, M, S parameters for calculating weight-for-height z-scores (WHZ)
 * for children aged 6-59 months using the WHO LMS method.
 * 
 * Formula: z = [ (X/M)^L - 1 ] / (L * S)
 * Where: X = child's weight, L, M, S = reference values for given sex and height
 * 
 * Data source: WHO Child Growth Standards (simplified subset for common heights)
 * Height range: 45-120 cm (covering 6-59 months age range)
 */
public class WhoGrowthStandards {
    
    /**
     * LMS parameter holder
     */
    public static class LmsParams {
        public final double L;
        public final double M;
        public final double S;
        
        public LmsParams(double l, double m, double s) {
            this.L = l;
            this.M = m;
            this.S = s;
        }
    }
    
    // WHO Weight-for-Height reference data for BOYS (height in cm -> LMS)
    private static final Map<Double, LmsParams> BOYS_WFH = new HashMap<>();
    
    // WHO Weight-for-Height reference data for GIRLS (height in cm -> LMS)
    private static final Map<Double, LmsParams> GIRLS_WFH = new HashMap<>();
    
    static {
        // Initialize WHO Weight-for-Height LMS data for BOYS
        // Format: height (cm) -> L, M (median weight kg), S
        // Data from WHO Child Growth Standards (selected heights 45-120 cm)
        
        // Boys - Height 45-60 cm (typical for infants 0-6 months)
        BOYS_WFH.put(45.0, new LmsParams(-0.3521, 2.441, 0.09182));
        BOYS_WFH.put(46.0, new LmsParams(-0.3521, 2.528, 0.09153));
        BOYS_WFH.put(47.0, new LmsParams(-0.3521, 2.618, 0.09124));
        BOYS_WFH.put(48.0, new LmsParams(-0.3521, 2.711, 0.09094));
        BOYS_WFH.put(49.0, new LmsParams(-0.3521, 2.807, 0.09065));
        BOYS_WFH.put(50.0, new LmsParams(-0.3521, 2.906, 0.09036));
        BOYS_WFH.put(51.0, new LmsParams(-0.3521, 3.010, 0.09007));
        BOYS_WFH.put(52.0, new LmsParams(-0.3521, 3.117, 0.08977));
        BOYS_WFH.put(53.0, new LmsParams(-0.3521, 3.227, 0.08948));
        BOYS_WFH.put(54.0, new LmsParams(-0.3521, 3.341, 0.08919));
        BOYS_WFH.put(55.0, new LmsParams(-0.3521, 3.459, 0.08889));
        BOYS_WFH.put(56.0, new LmsParams(-0.3521, 3.581, 0.08860));
        BOYS_WFH.put(57.0, new LmsParams(-0.3521, 3.708, 0.08831));
        BOYS_WFH.put(58.0, new LmsParams(-0.3521, 3.840, 0.08802));
        BOYS_WFH.put(59.0, new LmsParams(-0.3521, 3.976, 0.08773));
        BOYS_WFH.put(60.0, new LmsParams(-0.3521, 4.117, 0.08744));
        
        // Boys - Height 61-75 cm (typical for 6-12 months)
        BOYS_WFH.put(61.0, new LmsParams(-0.3521, 4.263, 0.08716));
        BOYS_WFH.put(62.0, new LmsParams(-0.3521, 4.413, 0.08687));
        BOYS_WFH.put(63.0, new LmsParams(-0.3521, 4.565, 0.08659));
        BOYS_WFH.put(64.0, new LmsParams(-0.3521, 4.720, 0.08631));
        BOYS_WFH.put(65.0, new LmsParams(-0.3521, 4.877, 0.08603));
        BOYS_WFH.put(66.0, new LmsParams(-0.3521, 5.037, 0.08576));
        BOYS_WFH.put(67.0, new LmsParams(-0.3521, 5.199, 0.08549));
        BOYS_WFH.put(68.0, new LmsParams(-0.3521, 5.364, 0.08522));
        BOYS_WFH.put(69.0, new LmsParams(-0.3521, 5.532, 0.08495));
        BOYS_WFH.put(70.0, new LmsParams(-0.3521, 5.703, 0.08469));
        BOYS_WFH.put(71.0, new LmsParams(-0.3521, 5.877, 0.08443));
        BOYS_WFH.put(72.0, new LmsParams(-0.3521, 6.053, 0.08418));
        BOYS_WFH.put(73.0, new LmsParams(-0.3521, 6.231, 0.08393));
        BOYS_WFH.put(74.0, new LmsParams(-0.3521, 6.411, 0.08369));
        BOYS_WFH.put(75.0, new LmsParams(-0.3521, 6.593, 0.08345));
        
        // Boys - Height 76-90 cm (typical for 12-24 months)
        BOYS_WFH.put(76.0, new LmsParams(-0.3521, 6.777, 0.08321));
        BOYS_WFH.put(77.0, new LmsParams(-0.3521, 6.963, 0.08298));
        BOYS_WFH.put(78.0, new LmsParams(-0.3521, 7.149, 0.08276));
        BOYS_WFH.put(79.0, new LmsParams(-0.3521, 7.337, 0.08254));
        BOYS_WFH.put(80.0, new LmsParams(-0.3521, 7.527, 0.08232));
        BOYS_WFH.put(81.0, new LmsParams(-0.3521, 7.719, 0.08211));
        BOYS_WFH.put(82.0, new LmsParams(-0.3521, 7.913, 0.08190));
        BOYS_WFH.put(83.0, new LmsParams(-0.3521, 8.109, 0.08170));
        BOYS_WFH.put(84.0, new LmsParams(-0.3521, 8.308, 0.08150));
        BOYS_WFH.put(85.0, new LmsParams(-0.3521, 8.509, 0.08131));
        BOYS_WFH.put(86.0, new LmsParams(-0.3521, 8.714, 0.08112));
        BOYS_WFH.put(87.0, new LmsParams(-0.3521, 8.922, 0.08094));
        BOYS_WFH.put(88.0, new LmsParams(-0.3521, 9.134, 0.08076));
        BOYS_WFH.put(89.0, new LmsParams(-0.3521, 9.350, 0.08059));
        BOYS_WFH.put(90.0, new LmsParams(-0.3521, 9.570, 0.08042));
        
        // Boys - Height 91-105 cm (typical for 24-48 months)
        BOYS_WFH.put(91.0, new LmsParams(-0.3521, 9.795, 0.08025));
        BOYS_WFH.put(92.0, new LmsParams(-0.3521, 10.024, 0.08009));
        BOYS_WFH.put(93.0, new LmsParams(-0.3521, 10.258, 0.07993));
        BOYS_WFH.put(94.0, new LmsParams(-0.3521, 10.496, 0.07978));
        BOYS_WFH.put(95.0, new LmsParams(-0.3521, 10.739, 0.07963));
        BOYS_WFH.put(96.0, new LmsParams(-0.3521, 10.987, 0.07948));
        BOYS_WFH.put(97.0, new LmsParams(-0.3521, 11.240, 0.07934));
        BOYS_WFH.put(98.0, new LmsParams(-0.3521, 11.498, 0.07920));
        BOYS_WFH.put(99.0, new LmsParams(-0.3521, 11.761, 0.07907));
        BOYS_WFH.put(100.0, new LmsParams(-0.3521, 12.029, 0.07894));
        BOYS_WFH.put(101.0, new LmsParams(-0.3521, 12.302, 0.07881));
        BOYS_WFH.put(102.0, new LmsParams(-0.3521, 12.580, 0.07869));
        BOYS_WFH.put(103.0, new LmsParams(-0.3521, 12.864, 0.07857));
        BOYS_WFH.put(104.0, new LmsParams(-0.3521, 13.153, 0.07845));
        BOYS_WFH.put(105.0, new LmsParams(-0.3521, 13.448, 0.07834));
        
        // Boys - Height 106-120 cm (typical for 48-59 months)
        BOYS_WFH.put(106.0, new LmsParams(-0.3521, 13.749, 0.07823));
        BOYS_WFH.put(107.0, new LmsParams(-0.3521, 14.056, 0.07813));
        BOYS_WFH.put(108.0, new LmsParams(-0.3521, 14.369, 0.07803));
        BOYS_WFH.put(109.0, new LmsParams(-0.3521, 14.688, 0.07793));
        BOYS_WFH.put(110.0, new LmsParams(-0.3521, 15.014, 0.07783));
        BOYS_WFH.put(111.0, new LmsParams(-0.3521, 15.346, 0.07774));
        BOYS_WFH.put(112.0, new LmsParams(-0.3521, 15.685, 0.07765));
        BOYS_WFH.put(113.0, new LmsParams(-0.3521, 16.030, 0.07757));
        BOYS_WFH.put(114.0, new LmsParams(-0.3521, 16.382, 0.07749));
        BOYS_WFH.put(115.0, new LmsParams(-0.3521, 16.741, 0.07741));
        BOYS_WFH.put(116.0, new LmsParams(-0.3521, 17.107, 0.07733));
        BOYS_WFH.put(117.0, new LmsParams(-0.3521, 17.480, 0.07726));
        BOYS_WFH.put(118.0, new LmsParams(-0.3521, 17.860, 0.07719));
        BOYS_WFH.put(119.0, new LmsParams(-0.3521, 18.247, 0.07713));
        BOYS_WFH.put(120.0, new LmsParams(-0.3521, 18.641, 0.07707));
        
        // Initialize WHO Weight-for-Height LMS data for GIRLS
        // Girls - Height 45-60 cm
        GIRLS_WFH.put(45.0, new LmsParams(-0.3833, 2.343, 0.09029));
        GIRLS_WFH.put(46.0, new LmsParams(-0.3833, 2.421, 0.09003));
        GIRLS_WFH.put(47.0, new LmsParams(-0.3833, 2.503, 0.08977));
        GIRLS_WFH.put(48.0, new LmsParams(-0.3833, 2.588, 0.08951));
        GIRLS_WFH.put(49.0, new LmsParams(-0.3833, 2.676, 0.08925));
        GIRLS_WFH.put(50.0, new LmsParams(-0.3833, 2.768, 0.08899));
        GIRLS_WFH.put(51.0, new LmsParams(-0.3833, 2.863, 0.08873));
        GIRLS_WFH.put(52.0, new LmsParams(-0.3833, 2.962, 0.08847));
        GIRLS_WFH.put(53.0, new LmsParams(-0.3833, 3.064, 0.08821));
        GIRLS_WFH.put(54.0, new LmsParams(-0.3833, 3.170, 0.08795));
        GIRLS_WFH.put(55.0, new LmsParams(-0.3833, 3.281, 0.08769));
        GIRLS_WFH.put(56.0, new LmsParams(-0.3833, 3.396, 0.08743));
        GIRLS_WFH.put(57.0, new LmsParams(-0.3833, 3.515, 0.08717));
        GIRLS_WFH.put(58.0, new LmsParams(-0.3833, 3.638, 0.08691));
        GIRLS_WFH.put(59.0, new LmsParams(-0.3833, 3.766, 0.08665));
        GIRLS_WFH.put(60.0, new LmsParams(-0.3833, 3.899, 0.08639));
        
        // Girls - Height 61-75 cm
        GIRLS_WFH.put(61.0, new LmsParams(-0.3833, 4.036, 0.08614));
        GIRLS_WFH.put(62.0, new LmsParams(-0.3833, 4.177, 0.08589));
        GIRLS_WFH.put(63.0, new LmsParams(-0.3833, 4.321, 0.08564));
        GIRLS_WFH.put(64.0, new LmsParams(-0.3833, 4.469, 0.08539));
        GIRLS_WFH.put(65.0, new LmsParams(-0.3833, 4.620, 0.08515));
        GIRLS_WFH.put(66.0, new LmsParams(-0.3833, 4.773, 0.08491));
        GIRLS_WFH.put(67.0, new LmsParams(-0.3833, 4.929, 0.08468));
        GIRLS_WFH.put(68.0, new LmsParams(-0.3833, 5.088, 0.08445));
        GIRLS_WFH.put(69.0, new LmsParams(-0.3833, 5.251, 0.08422));
        GIRLS_WFH.put(70.0, new LmsParams(-0.3833, 5.418, 0.08400));
        GIRLS_WFH.put(71.0, new LmsParams(-0.3833, 5.588, 0.08379));
        GIRLS_WFH.put(72.0, new LmsParams(-0.3833, 5.762, 0.08358));
        GIRLS_WFH.put(73.0, new LmsParams(-0.3833, 5.939, 0.08338));
        GIRLS_WFH.put(74.0, new LmsParams(-0.3833, 6.120, 0.08318));
        GIRLS_WFH.put(75.0, new LmsParams(-0.3833, 6.303, 0.08299));
        
        // Girls - Height 76-90 cm
        GIRLS_WFH.put(76.0, new LmsParams(-0.3833, 6.490, 0.08280));
        GIRLS_WFH.put(77.0, new LmsParams(-0.3833, 6.679, 0.08262));
        GIRLS_WFH.put(78.0, new LmsParams(-0.3833, 6.871, 0.08245));
        GIRLS_WFH.put(79.0, new LmsParams(-0.3833, 7.066, 0.08228));
        GIRLS_WFH.put(80.0, new LmsParams(-0.3833, 7.264, 0.08211));
        GIRLS_WFH.put(81.0, new LmsParams(-0.3833, 7.464, 0.08195));
        GIRLS_WFH.put(82.0, new LmsParams(-0.3833, 7.667, 0.08180));
        GIRLS_WFH.put(83.0, new LmsParams(-0.3833, 7.873, 0.08165));
        GIRLS_WFH.put(84.0, new LmsParams(-0.3833, 8.082, 0.08151));
        GIRLS_WFH.put(85.0, new LmsParams(-0.3833, 8.293, 0.08137));
        GIRLS_WFH.put(86.0, new LmsParams(-0.3833, 8.508, 0.08124));
        GIRLS_WFH.put(87.0, new LmsParams(-0.3833, 8.725, 0.08111));
        GIRLS_WFH.put(88.0, new LmsParams(-0.3833, 8.946, 0.08099));
        GIRLS_WFH.put(89.0, new LmsParams(-0.3833, 9.170, 0.08088));
        GIRLS_WFH.put(90.0, new LmsParams(-0.3833, 9.397, 0.08076));
        
        // Girls - Height 91-105 cm
        GIRLS_WFH.put(91.0, new LmsParams(-0.3833, 9.628, 0.08066));
        GIRLS_WFH.put(92.0, new LmsParams(-0.3833, 9.862, 0.08055));
        GIRLS_WFH.put(93.0, new LmsParams(-0.3833, 10.099, 0.08046));
        GIRLS_WFH.put(94.0, new LmsParams(-0.3833, 10.340, 0.08036));
        GIRLS_WFH.put(95.0, new LmsParams(-0.3833, 10.584, 0.08027));
        GIRLS_WFH.put(96.0, new LmsParams(-0.3833, 10.832, 0.08019));
        GIRLS_WFH.put(97.0, new LmsParams(-0.3833, 11.083, 0.08011));
        GIRLS_WFH.put(98.0, new LmsParams(-0.3833, 11.338, 0.08003));
        GIRLS_WFH.put(99.0, new LmsParams(-0.3833, 11.597, 0.07996));
        GIRLS_WFH.put(100.0, new LmsParams(-0.3833, 11.859, 0.07989));
        GIRLS_WFH.put(101.0, new LmsParams(-0.3833, 12.125, 0.07983));
        GIRLS_WFH.put(102.0, new LmsParams(-0.3833, 12.394, 0.07977));
        GIRLS_WFH.put(103.0, new LmsParams(-0.3833, 12.668, 0.07971));
        GIRLS_WFH.put(104.0, new LmsParams(-0.3833, 12.946, 0.07966));
        GIRLS_WFH.put(105.0, new LmsParams(-0.3833, 13.228, 0.07961));
        
        // Girls - Height 106-120 cm
        GIRLS_WFH.put(106.0, new LmsParams(-0.3833, 13.515, 0.07957));
        GIRLS_WFH.put(107.0, new LmsParams(-0.3833, 13.806, 0.07953));
        GIRLS_WFH.put(108.0, new LmsParams(-0.3833, 14.102, 0.07949));
        GIRLS_WFH.put(109.0, new LmsParams(-0.3833, 14.402, 0.07946));
        GIRLS_WFH.put(110.0, new LmsParams(-0.3833, 14.707, 0.07943));
        GIRLS_WFH.put(111.0, new LmsParams(-0.3833, 15.018, 0.07941));
        GIRLS_WFH.put(112.0, new LmsParams(-0.3833, 15.334, 0.07939));
        GIRLS_WFH.put(113.0, new LmsParams(-0.3833, 15.656, 0.07937));
        GIRLS_WFH.put(114.0, new LmsParams(-0.3833, 15.983, 0.07936));
        GIRLS_WFH.put(115.0, new LmsParams(-0.3833, 16.316, 0.07935));
        GIRLS_WFH.put(116.0, new LmsParams(-0.3833, 16.655, 0.07934));
        GIRLS_WFH.put(117.0, new LmsParams(-0.3833, 17.000, 0.07934));
        GIRLS_WFH.put(118.0, new LmsParams(-0.3833, 17.352, 0.07934));
        GIRLS_WFH.put(119.0, new LmsParams(-0.3833, 17.710, 0.07935));
        GIRLS_WFH.put(120.0, new LmsParams(-0.3833, 18.075, 0.07936));
    }
    
    /**
     * Get LMS parameters for weight-for-height based on sex and height.
     * Uses nearest-neighbor interpolation if exact height not found.
     * 
     * @param sex "M" for male, "F" for female
     * @param heightCm height in centimeters
     * @return LmsParams or null if out of range
     */
    public static LmsParams getWeightForHeightLms(String sex, double heightCm) {
        Map<Double, LmsParams> refData = "M".equalsIgnoreCase(sex) ? BOYS_WFH : GIRLS_WFH;
        
        // Round to nearest whole cm for lookup
        double roundedHeight = Math.round(heightCm);
        
        // Clamp to valid range
        if (roundedHeight < 45.0 || roundedHeight > 120.0) {
            return null;
        }
        
        // Direct lookup
        LmsParams params = refData.get(roundedHeight);
        if (params != null) {
            return params;
        }
        
        // If not found, try interpolation between two nearest heights
        double lowerHeight = Math.floor(heightCm);
        double upperHeight = Math.ceil(heightCm);
        
        LmsParams lowerParams = refData.get(lowerHeight);
        LmsParams upperParams = refData.get(upperHeight);
        
        if (lowerParams != null && upperParams != null) {
            // Linear interpolation
            double t = heightCm - lowerHeight;
            double L = lowerParams.L + t * (upperParams.L - lowerParams.L);
            double M = lowerParams.M + t * (upperParams.M - lowerParams.M);
            double S = lowerParams.S + t * (upperParams.S - lowerParams.S);
            return new LmsParams(L, M, S);
        }
        
        return lowerParams != null ? lowerParams : upperParams;
    }
    
    /**
     * Calculate z-score using the LMS method.
     * Formula: z = [ (X/M)^L - 1 ] / (L * S)
     * 
     * @param measurement the child's measurement (weight in kg)
     * @param params LMS parameters
     * @return z-score, or Double.NaN if invalid
     */
    public static double calculateZScore(double measurement, LmsParams params) {
        if (params == null || measurement <= 0) {
            return Double.NaN;
        }
        
        double L = params.L;
        double M = params.M;
        double S = params.S;
        
        if (L == 0) {
            // Special case when L = 0: z = ln(X/M) / S
            return Math.log(measurement / M) / S;
        }
        
        // Standard LMS formula: z = [ (X/M)^L - 1 ] / (L * S)
        return (Math.pow(measurement / M, L) - 1) / (L * S);
    }
    
    /**
     * Compute Weight-for-Height Z-score (WHZ) using WHO standards.
     * 
     * @param ageMonths child's age in months (for validation, 6-59 months)
     * @param sex "M" for male, "F" for female
     * @param heightCm height in centimeters
     * @param weightKg weight in kilograms
     * @return WHZ z-score, or Double.NaN if calculation not possible
     */
    public static double computeWhzZScore(int ageMonths, String sex, double heightCm, double weightKg) {
        // Validate inputs
        if (sex == null || (!sex.equalsIgnoreCase("M") && !sex.equalsIgnoreCase("F"))) {
            return Double.NaN;
        }
        
        if (heightCm <= 0 || weightKg <= 0) {
            return Double.NaN;
        }
        
        // Height out of reference range
        if (heightCm < 45.0 || heightCm > 120.0) {
            return Double.NaN;
        }
        
        // Get LMS parameters for the given sex and height
        LmsParams params = getWeightForHeightLms(sex, heightCm);
        if (params == null) {
            return Double.NaN;
        }
        
        // Calculate z-score
        return calculateZScore(weightKg, params);
    }
    
    /**
     * Convert gender string to sex code.
     * 
     * @param gender "Male", "Female", "M", "F", etc.
     * @return "M" or "F", or null if invalid
     */
    public static String genderToSex(String gender) {
        if (gender == null) {
            return null;
        }
        String g = gender.trim().toUpperCase();
        if (g.startsWith("M")) {
            return "M";
        } else if (g.startsWith("F")) {
            return "F";
        }
        return null;
    }
}
