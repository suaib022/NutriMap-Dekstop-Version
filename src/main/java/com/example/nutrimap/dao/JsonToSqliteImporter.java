package com.example.nutrimap.dao;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class JsonToSqliteImporter {
    private final DatabaseManager dbManager;
    private final Gson gson;

    public JsonToSqliteImporter() {
        this.dbManager = DatabaseManager.getInstance();
        this.gson = new Gson();
    }

    public void importAllData() {
        System.out.println("Starting data import from JSON files...");
        
        if (dbManager.isTableEmpty("divisions")) {
            importDivisions();
        }
        if (dbManager.isTableEmpty("districts")) {
            importDistricts();
        }
        if (dbManager.isTableEmpty("upazilas")) {
            importUpazilas();
        }
        if (dbManager.isTableEmpty("unions")) {
            importUnions();
        }
        if (dbManager.isTableEmpty("branches")) {
            importBranches();
        }
        if (dbManager.isTableEmpty("users")) {
            importUsers();
        }
        
        System.out.println("Data import completed.");
    }

    private void importDivisions() {
        System.out.println("Importing divisions...");
        try (InputStream is = getClass().getResourceAsStream("/data/divisions.json")) {
            if (is == null) {
                System.err.println("divisions.json not found!");
                return;
            }
            
            Reader reader = new InputStreamReader(is);
            JsonArray rootArray = JsonParser.parseReader(reader).getAsJsonArray();
            
            JsonArray dataArray = null;
            for (JsonElement element : rootArray) {
                if (element.isJsonObject()) {
                    JsonObject obj = element.getAsJsonObject();
                    if (obj.has("type") && "table".equals(obj.get("type").getAsString())) {
                        dataArray = obj.getAsJsonArray("data");
                        break;
                    }
                }
            }

            if (dataArray == null) {
                System.err.println("No division data found!");
                return;
            }

            Connection conn = dbManager.getConnection();
            String sql = "INSERT OR REPLACE INTO divisions (id, name, bn_name, url) VALUES (?, ?, ?, ?)";
            
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                for (JsonElement element : dataArray) {
                    JsonObject obj = element.getAsJsonObject();
                    pstmt.setString(1, getStringOrNull(obj, "id"));
                    pstmt.setString(2, getStringOrNull(obj, "name"));
                    pstmt.setString(3, getStringOrNull(obj, "bn_name"));
                    pstmt.setString(4, getStringOrNull(obj, "url"));
                    pstmt.addBatch();
                }
                pstmt.executeBatch();
            }
            System.out.println("Imported " + dataArray.size() + " divisions.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void importDistricts() {
        System.out.println("Importing districts...");
        try (InputStream is = getClass().getResourceAsStream("/data/districts.json")) {
            if (is == null) {
                System.err.println("districts.json not found!");
                return;
            }
            
            Reader reader = new InputStreamReader(is);
            JsonArray rootArray = JsonParser.parseReader(reader).getAsJsonArray();
            
            JsonArray dataArray = null;
            for (JsonElement element : rootArray) {
                if (element.isJsonObject()) {
                    JsonObject obj = element.getAsJsonObject();
                    if (obj.has("type") && "table".equals(obj.get("type").getAsString())) {
                        dataArray = obj.getAsJsonArray("data");
                        break;
                    }
                }
            }

            if (dataArray == null) {
                System.err.println("No district data found!");
                return;
            }

            Connection conn = dbManager.getConnection();
            String sql = "INSERT OR REPLACE INTO districts (id, division_id, name, bn_name, lat, lon, url) VALUES (?, ?, ?, ?, ?, ?, ?)";
            
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                for (JsonElement element : dataArray) {
                    JsonObject obj = element.getAsJsonObject();
                    pstmt.setString(1, getStringOrNull(obj, "id"));
                    pstmt.setString(2, getStringOrNull(obj, "division_id"));
                    pstmt.setString(3, getStringOrNull(obj, "name"));
                    pstmt.setString(4, getStringOrNull(obj, "bn_name"));
                    pstmt.setString(5, getStringOrNull(obj, "lat"));
                    pstmt.setString(6, getStringOrNull(obj, "lon"));
                    pstmt.setString(7, getStringOrNull(obj, "url"));
                    pstmt.addBatch();
                }
                pstmt.executeBatch();
            }
            System.out.println("Imported " + dataArray.size() + " districts.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void importUpazilas() {
        System.out.println("Importing upazilas...");
        try (InputStream is = getClass().getResourceAsStream("/data/upazilas.json")) {
            if (is == null) {
                System.err.println("upazilas.json not found!");
                return;
            }
            
            Reader reader = new InputStreamReader(is);
            JsonArray rootArray = JsonParser.parseReader(reader).getAsJsonArray();
            
            JsonArray dataArray = null;
            for (JsonElement element : rootArray) {
                if (element.isJsonObject()) {
                    JsonObject obj = element.getAsJsonObject();
                    if (obj.has("type") && "table".equals(obj.get("type").getAsString())) {
                        dataArray = obj.getAsJsonArray("data");
                        break;
                    }
                }
            }

            if (dataArray == null) {
                System.err.println("No upazila data found!");
                return;
            }

            Connection conn = dbManager.getConnection();
            String sql = "INSERT OR REPLACE INTO upazilas (id, district_id, name, bn_name, url) VALUES (?, ?, ?, ?, ?)";
            
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                for (JsonElement element : dataArray) {
                    JsonObject obj = element.getAsJsonObject();
                    pstmt.setString(1, getStringOrNull(obj, "id"));
                    pstmt.setString(2, getStringOrNull(obj, "district_id"));
                    pstmt.setString(3, getStringOrNull(obj, "name"));
                    pstmt.setString(4, getStringOrNull(obj, "bn_name"));
                    pstmt.setString(5, getStringOrNull(obj, "url"));
                    pstmt.addBatch();
                }
                pstmt.executeBatch();
            }
            System.out.println("Imported " + dataArray.size() + " upazilas.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void importUnions() {
        System.out.println("Importing unions...");
        try (InputStream is = getClass().getResourceAsStream("/data/unions.json")) {
            if (is == null) {
                System.err.println("unions.json not found!");
                return;
            }
            
            Reader reader = new InputStreamReader(is);
            JsonArray rootArray = JsonParser.parseReader(reader).getAsJsonArray();
            
            JsonArray dataArray = null;
            for (JsonElement element : rootArray) {
                if (element.isJsonObject()) {
                    JsonObject obj = element.getAsJsonObject();
                    if (obj.has("type") && "table".equals(obj.get("type").getAsString())) {
                        dataArray = obj.getAsJsonArray("data");
                        break;
                    }
                }
            }

            if (dataArray == null) {
                System.err.println("No union data found!");
                return;
            }

            Connection conn = dbManager.getConnection();
            String sql = "INSERT OR REPLACE INTO unions (id, upazila_id, name, bn_name, url) VALUES (?, ?, ?, ?, ?)";
            
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                for (JsonElement element : dataArray) {
                    JsonObject obj = element.getAsJsonObject();
                    pstmt.setString(1, getStringOrNull(obj, "id"));
                    pstmt.setString(2, getStringOrNull(obj, "upazilla_id"));
                    pstmt.setString(3, getStringOrNull(obj, "name"));
                    pstmt.setString(4, getStringOrNull(obj, "bn_name"));
                    pstmt.setString(5, getStringOrNull(obj, "url"));
                    pstmt.addBatch();
                }
                pstmt.executeBatch();
            }
            System.out.println("Imported " + dataArray.size() + " unions.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void importBranches() {
        System.out.println("Importing branches...");
        try (InputStream is = getClass().getResourceAsStream("/data/branches.json")) {
            if (is == null) {
                System.err.println("branches.json not found!");
                return;
            }
            
            Reader reader = new InputStreamReader(is);
            JsonArray dataArray = JsonParser.parseReader(reader).getAsJsonArray();

            Connection conn = dbManager.getConnection();
            String sql = "INSERT OR REPLACE INTO branches (id, name, bn_name, area, bn_area, upazilla, bn_upazilla, district, bn_district, division, bn_division, url) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
            
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                for (JsonElement element : dataArray) {
                    JsonObject obj = element.getAsJsonObject();
                    pstmt.setString(1, getStringOrNull(obj, "id"));
                    pstmt.setString(2, getStringOrNull(obj, "name"));
                    pstmt.setString(3, getStringOrNull(obj, "bn_name"));
                    pstmt.setString(4, getStringOrNull(obj, "Area"));
                    pstmt.setString(5, getStringOrNull(obj, "bn_Area"));
                    pstmt.setString(6, getStringOrNull(obj, "Upazilla"));
                    pstmt.setString(7, getStringOrNull(obj, "bn_Upazilla"));
                    pstmt.setString(8, getStringOrNull(obj, "District"));
                    pstmt.setString(9, getStringOrNull(obj, "bn_District"));
                    pstmt.setString(10, getStringOrNull(obj, "Division"));
                    pstmt.setString(11, getStringOrNull(obj, "bn_Division"));
                    pstmt.setString(12, getStringOrNull(obj, "url"));
                    pstmt.addBatch();
                }
                pstmt.executeBatch();
            }
            System.out.println("Imported " + dataArray.size() + " branches.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void importUsers() {
        System.out.println("Importing users...");
        try (InputStream is = getClass().getResourceAsStream("/data/user-json.json")) {
            if (is == null) {
                System.err.println("user-json.json not found!");
                return;
            }
            
            Reader reader = new InputStreamReader(is);
            JsonArray dataArray = JsonParser.parseReader(reader).getAsJsonArray();

            Connection conn = dbManager.getConnection();
            String sql = "INSERT OR REPLACE INTO users (id, name, email, password, role, image_path) VALUES (?, ?, ?, ?, ?, ?)";
            
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                for (JsonElement element : dataArray) {
                    JsonObject obj = element.getAsJsonObject();
                    pstmt.setInt(1, obj.has("id") ? obj.get("id").getAsInt() : 0);
                    pstmt.setString(2, getStringOrNull(obj, "name"));
                    pstmt.setString(3, getStringOrNull(obj, "email"));
                    pstmt.setString(4, getStringOrNull(obj, "password"));
                    pstmt.setString(5, getStringOrNull(obj, "role"));
                    pstmt.setString(6, getStringOrNull(obj, "imagePath"));
                    pstmt.addBatch();
                }
                pstmt.executeBatch();
            }
            System.out.println("Imported " + dataArray.size() + " users.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String getStringOrNull(JsonObject obj, String key) {
        if (obj.has(key) && !obj.get(key).isJsonNull()) {
            return obj.get(key).getAsString();
        }
        return null;
    }
}
