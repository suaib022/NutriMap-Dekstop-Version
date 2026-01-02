package com.example.nutrimap.model;

public class DistrictModel {
    private String id;
    private String divisionId;
    private String name;
    private String bnName;
    private String lat;
    private String lon;
    private String url;

    public DistrictModel() {}

    public DistrictModel(String id, String divisionId, String name, String bnName, String lat, String lon, String url) {
        this.id = id;
        this.divisionId = divisionId;
        this.name = name;
        this.bnName = bnName;
        this.lat = lat;
        this.lon = lon;
        this.url = url;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getDivisionId() { return divisionId; }
    public void setDivisionId(String divisionId) { this.divisionId = divisionId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getBnName() { return bnName; }
    public void setBnName(String bnName) { this.bnName = bnName; }

    public String getLat() { return lat; }
    public void setLat(String lat) { this.lat = lat; }

    public String getLon() { return lon; }
    public void setLon(String lon) { this.lon = lon; }

    public String getUrl() { return url; }
    public void setUrl(String url) { this.url = url; }

    @Override
    public String toString() {
        return name;
    }
}
