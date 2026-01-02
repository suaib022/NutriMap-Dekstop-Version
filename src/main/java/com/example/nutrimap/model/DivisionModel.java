package com.example.nutrimap.model;

public class DivisionModel {
    private String id;
    private String name;
    private String bnName;
    private String url;

    public DivisionModel() {}

    public DivisionModel(String id, String name, String bnName, String url) {
        this.id = id;
        this.name = name;
        this.bnName = bnName;
        this.url = url;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getBnName() { return bnName; }
    public void setBnName(String bnName) { this.bnName = bnName; }

    public String getUrl() { return url; }
    public void setUrl(String url) { this.url = url; }

    @Override
    public String toString() {
        return name;
    }
}
