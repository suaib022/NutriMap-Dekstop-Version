package com.example.nutrimap.model;

import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;

public class ChildModel {
    private int id;
    private String fullName;
    private String fathersName;
    private String mothersName;
    private String contactNumber;
    private String division;
    private String district;
    private String upazilla;
    private String unionName;
    private String branchId;
    private String branchName;
    private String lastVisit;
    private String gender;
    private String dateOfBirth;

    public ChildModel() {}

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public String getFathersName() { return fathersName; }
    public void setFathersName(String fathersName) { this.fathersName = fathersName; }

    public String getMothersName() { return mothersName; }
    public void setMothersName(String mothersName) { this.mothersName = mothersName; }

    public String getContactNumber() { return contactNumber; }
    public void setContactNumber(String contactNumber) { this.contactNumber = contactNumber; }

    public String getDivision() { return division; }
    public void setDivision(String division) { this.division = division; }

    public String getDistrict() { return district; }
    public void setDistrict(String district) { this.district = district; }

    public String getUpazilla() { return upazilla; }
    public void setUpazilla(String upazilla) { this.upazilla = upazilla; }

    public String getUnionName() { return unionName; }
    public void setUnionName(String unionName) { this.unionName = unionName; }

    public String getBranchId() { return branchId; }
    public void setBranchId(String branchId) { this.branchId = branchId; }

    public String getBranchName() { return branchName; }
    public void setBranchName(String branchName) { this.branchName = branchName; }

    public String getLastVisit() { return lastVisit; }
    public void setLastVisit(String lastVisit) { this.lastVisit = lastVisit; }

    public String getGender() { return gender; }
    public void setGender(String gender) { this.gender = gender; }

    public String getDateOfBirth() { return dateOfBirth; }
    public void setDateOfBirth(String dateOfBirth) { this.dateOfBirth = dateOfBirth; }

    public String getAge() {
        if (dateOfBirth == null || dateOfBirth.isEmpty()) {
            return "N/A";
        }
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            LocalDate dob = LocalDate.parse(dateOfBirth, formatter);
            LocalDate now = LocalDate.now();
            Period period = Period.between(dob, now);
            
            if (period.getYears() > 0) {
                return period.getYears() + " years, " + period.getMonths() + " months";
            } else if (period.getMonths() > 0) {
                return period.getMonths() + " months, " + period.getDays() + " days";
            } else {
                return period.getDays() + " days";
            }
        } catch (Exception e) {
            return "N/A";
        }
    }
    
    public String getDisplayLastVisit() {
        return (lastVisit == null || lastVisit.isEmpty()) ? "N/A" : lastVisit;
    }
    
    public String getArea() {
        StringBuilder sb = new StringBuilder();
        if (unionName != null && !unionName.isEmpty()) {
            sb.append(unionName);
        }
        if (upazilla != null && !upazilla.isEmpty()) {
            if (sb.length() > 0) sb.append(", ");
            sb.append(upazilla);
        }
        if (district != null && !district.isEmpty()) {
            if (sb.length() > 0) sb.append(", ");
            sb.append(district);
        }
        if (division != null && !division.isEmpty()) {
            if (sb.length() > 0) sb.append(", ");
            sb.append(division);
        }
        return sb.length() > 0 ? sb.toString() : "N/A";
    }
}
