package com.example.nutrimap.dao;

import com.example.nutrimap.model.DistrictModel;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class DistrictDAO {
    private final DatabaseManager dbManager;

    public DistrictDAO() {
        this.dbManager = DatabaseManager.getInstance();
    }

    public List<DistrictModel> getAll() {
        List<DistrictModel> districts = new ArrayList<>();
        String sql = "SELECT * FROM districts ORDER BY CAST(id AS INTEGER)";
        
        try (PreparedStatement pstmt = dbManager.getConnection().prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            
            while (rs.next()) {
                districts.add(mapResultSetToDistrict(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return districts;
    }

    public ObservableList<DistrictModel> getObservableDistricts() {
        return FXCollections.observableArrayList(getAll());
    }

    public DistrictModel getById(String id) {
        String sql = "SELECT * FROM districts WHERE id = ?";
        
        try (PreparedStatement pstmt = dbManager.getConnection().prepareStatement(sql)) {
            pstmt.setString(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToDistrict(rs);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<DistrictModel> getByDivisionId(String divisionId) {
        List<DistrictModel> districts = new ArrayList<>();
        String sql = "SELECT * FROM districts WHERE division_id = ? ORDER BY CAST(id AS INTEGER)";
        
        try (PreparedStatement pstmt = dbManager.getConnection().prepareStatement(sql)) {
            pstmt.setString(1, divisionId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    districts.add(mapResultSetToDistrict(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return districts;
    }

    public List<DistrictModel> search(String keyword) {
        if (keyword == null || keyword.isEmpty()) {
            return getAll();
        }
        
        List<DistrictModel> districts = new ArrayList<>();
        String sql = "SELECT * FROM districts WHERE name LIKE ? OR bn_name LIKE ? ORDER BY CAST(id AS INTEGER)";
        String pattern = "%" + keyword + "%";
        
        try (PreparedStatement pstmt = dbManager.getConnection().prepareStatement(sql)) {
            pstmt.setString(1, pattern);
            pstmt.setString(2, pattern);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    districts.add(mapResultSetToDistrict(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return districts;
    }

    private DistrictModel mapResultSetToDistrict(ResultSet rs) throws SQLException {
        DistrictModel district = new DistrictModel();
        district.setId(rs.getString("id"));
        district.setDivisionId(rs.getString("division_id"));
        district.setName(rs.getString("name"));
        district.setBnName(rs.getString("bn_name"));
        district.setLat(rs.getString("lat"));
        district.setLon(rs.getString("lon"));
        district.setUrl(rs.getString("url"));
        return district;
    }
}
