package com.example.nutrimap.dao;

import com.example.nutrimap.model.UpazilaModel;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class UpazilaDAO {
    private final DatabaseManager dbManager;

    public UpazilaDAO() {
        this.dbManager = DatabaseManager.getInstance();
    }

    public List<UpazilaModel> getAll() {
        List<UpazilaModel> upazilas = new ArrayList<>();
        String sql = "SELECT * FROM upazilas ORDER BY CAST(id AS INTEGER)";
        
        try (PreparedStatement pstmt = dbManager.getConnection().prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            
            while (rs.next()) {
                upazilas.add(mapResultSetToUpazila(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return upazilas;
    }

    public ObservableList<UpazilaModel> getObservableUpazilas() {
        return FXCollections.observableArrayList(getAll());
    }

    public UpazilaModel getById(String id) {
        String sql = "SELECT * FROM upazilas WHERE id = ?";
        
        try (PreparedStatement pstmt = dbManager.getConnection().prepareStatement(sql)) {
            pstmt.setString(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToUpazila(rs);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<UpazilaModel> getByDistrictId(String districtId) {
        List<UpazilaModel> upazilas = new ArrayList<>();
        String sql = "SELECT * FROM upazilas WHERE district_id = ? ORDER BY CAST(id AS INTEGER)";
        
        try (PreparedStatement pstmt = dbManager.getConnection().prepareStatement(sql)) {
            pstmt.setString(1, districtId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    upazilas.add(mapResultSetToUpazila(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return upazilas;
    }

    public List<UpazilaModel> search(String keyword) {
        if (keyword == null || keyword.isEmpty()) {
            return getAll();
        }
        
        List<UpazilaModel> upazilas = new ArrayList<>();
        String sql = "SELECT * FROM upazilas WHERE name LIKE ? OR bn_name LIKE ? ORDER BY CAST(id AS INTEGER)";
        String pattern = "%" + keyword + "%";
        
        try (PreparedStatement pstmt = dbManager.getConnection().prepareStatement(sql)) {
            pstmt.setString(1, pattern);
            pstmt.setString(2, pattern);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    upazilas.add(mapResultSetToUpazila(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return upazilas;
    }

    private UpazilaModel mapResultSetToUpazila(ResultSet rs) throws SQLException {
        UpazilaModel upazila = new UpazilaModel();
        upazila.setId(rs.getString("id"));
        upazila.setDistrictId(rs.getString("district_id"));
        upazila.setName(rs.getString("name"));
        upazila.setBnName(rs.getString("bn_name"));
        upazila.setUrl(rs.getString("url"));
        return upazila;
    }
}
