package com.example.nutrimap.model;

public class VisitModel {
    private int visitId;
    private int childId;
    private String childName;
    private String visitDate;
    private double weightKg;
    private double heightCm;
    private int muacMm;
    private String riskLevel;
    private String notes;
    private String createdAt;
    private String updatedAt;
    private Integer enteredBy;
    private boolean deleted;

    public VisitModel() {
        this.riskLevel = "N/A";
        this.deleted = false;
    }

    public int getVisitId() { return visitId; }
    public void setVisitId(int visitId) { this.visitId = visitId; }

    public int getChildId() { return childId; }
    public void setChildId(int childId) { this.childId = childId; }

    public String getChildName() { return childName; }
    public void setChildName(String childName) { this.childName = childName; }

    public String getVisitDate() { return visitDate; }
    public void setVisitDate(String visitDate) { this.visitDate = visitDate; }

    public double getWeightKg() { return weightKg; }
    public void setWeightKg(double weightKg) { this.weightKg = weightKg; }

    public double getHeightCm() { return heightCm; }
    public void setHeightCm(double heightCm) { this.heightCm = heightCm; }

    public int getMuacMm() { return muacMm; }
    public void setMuacMm(int muacMm) { this.muacMm = muacMm; }

    public String getRiskLevel() { return riskLevel; }
    public void setRiskLevel(String riskLevel) { this.riskLevel = riskLevel; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }

    public String getCreatedAt() { return createdAt; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }

    public String getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(String updatedAt) { this.updatedAt = updatedAt; }

    public Integer getEnteredBy() { return enteredBy; }
    public void setEnteredBy(Integer enteredBy) { this.enteredBy = enteredBy; }

    public boolean isDeleted() { return deleted; }
    public void setDeleted(boolean deleted) { this.deleted = deleted; }
}
