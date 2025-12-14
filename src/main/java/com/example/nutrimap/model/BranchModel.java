package com.example.nutrimap.model;
public class BranchModel {
    @com.google.gson.annotations.SerializedName("id")
    private String id;
    @com.google.gson.annotations.SerializedName("name")
    private String name;
    @com.google.gson.annotations.SerializedName("bn_name")
    private String bn_name;
    @com.google.gson.annotations.SerializedName("Area")
    private String area;
    @com.google.gson.annotations.SerializedName("bn_Area")
    private String bn_area;
    @com.google.gson.annotations.SerializedName("Upazilla")
    private String upazilla;
    @com.google.gson.annotations.SerializedName("bn_Upazilla")
    private String bn_upazilla;
    @com.google.gson.annotations.SerializedName("District")
    private String district;
    @com.google.gson.annotations.SerializedName("bn_District")
    private String bn_district;
    @com.google.gson.annotations.SerializedName("Division")
    private String division;
    @com.google.gson.annotations.SerializedName("bn_Division")
    private String bn_division;
    @com.google.gson.annotations.SerializedName("url")
    private String url;
    public BranchModel() {}
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getBn_name() { return bn_name; }
    public void setBn_name(String bn_name) { this.bn_name = bn_name; }
    public String getArea() { return area; }
    public void setArea(String area) { this.area = area; }
    public String getBn_area() { return bn_area; }
    public void setBn_area(String bn_area) { this.bn_area = bn_area; }
    public String getUpazilla() { return upazilla; }
    public void setUpazilla(String upazilla) { this.upazilla = upazilla; }
    public String getBn_upazilla() { return bn_upazilla; }
    public void setBn_upazilla(String bn_upazilla) { this.bn_upazilla = bn_upazilla; }
    public String getDistrict() { return district; }
    public void setDistrict(String district) { this.district = district; }
    public String getBn_district() { return bn_district; }
    public void setBn_district(String bn_district) { this.bn_district = bn_district; }
    public String getDivision() { return division; }
    public void setDivision(String division) { this.division = division; }
    public String getBn_division() { return bn_division; }
    public void setBn_division(String bn_division) { this.bn_division = bn_division; }
    public String getUrl() { return url; }
    public void setUrl(String url) { this.url = url; }
}
