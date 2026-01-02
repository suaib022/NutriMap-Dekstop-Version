package com.example.nutrimap.dao;

import com.example.nutrimap.model.DivisionModel;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class DivisionDAO {
    private final DatabaseManager dbManager;

    public DivisionDAO() {
        this.dbManager = DatabaseManager.getInstance();
    }

    public List<DivisionModel> getAll() {
        List<DivisionModel> divisions = new ArrayList<>();
        String sql = "SELECT * FROM divisions ORDER BY CAST(id AS INTEGER)";
        
        try (PreparedStatement pstmt = dbManager.getConnection().prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            
            while (rs.next()) {
                divisions.add(mapResultSetToDivision(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return divisions;
    }

    public ObservableList<DivisionModel> getObservableDivisions() {
        return FXCollections.observableArrayList(getAll());
    }

    public DivisionModel getById(String id) {
        String sql = "SELECT * FROM divisions WHERE id = ?";
        
        try (PreparedStatement pstmt = dbManager.getConnection().prepareStatement(sql)) {
            pstmt.setString(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToDivision(rs);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<DivisionModel> search(String keyword) {
        if (keyword == null || keyword.isEmpty()) {
            return getAll();
        }
        
        List<DivisionModel> divisions = new ArrayList<>();
        String sql = "SELECT * FROM divisions WHERE name LIKE ? OR bn_name LIKE ? ORDER BY CAST(id AS INTEGER)";
        String pattern = "%" + keyword + "%";
        
        try (PreparedStatement pstmt = dbManager.getConnection().prepareStatement(sql)) {
            pstmt.setString(1, pattern);
            pstmt.setString(2, pattern);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    divisions.add(mapResultSetToDivision(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return divisions;
    }

    private DivisionModel mapResultSetToDivision(ResultSet rs) throws SQLException {
        DivisionModel division = new DivisionModel();
        division.setId(rs.getString("id"));
        division.setName(rs.getString("name"));
        division.setBnName(rs.getString("bn_name"));
        division.setUrl(rs.getString("url"));
        return division;
    }
}
