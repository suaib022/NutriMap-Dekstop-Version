package com.example.nutrimap.dao;

import com.example.nutrimap.model.UnionModel;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class UnionDAO {
    private final DatabaseManager dbManager;

    public UnionDAO() {
        this.dbManager = DatabaseManager.getInstance();
    }

    public List<UnionModel> getAll() {
        List<UnionModel> unions = new ArrayList<>();
        String sql = "SELECT * FROM unions ORDER BY CAST(id AS INTEGER)";
        
        try (PreparedStatement pstmt = dbManager.getConnection().prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            
            while (rs.next()) {
                unions.add(mapResultSetToUnion(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return unions;
    }

    public ObservableList<UnionModel> getObservableUnions() {
        return FXCollections.observableArrayList(getAll());
    }

    public UnionModel getById(String id) {
        String sql = "SELECT * FROM unions WHERE id = ?";
        
        try (PreparedStatement pstmt = dbManager.getConnection().prepareStatement(sql)) {
            pstmt.setString(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToUnion(rs);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<UnionModel> getByUpazilaId(String upazilaId) {
        List<UnionModel> unions = new ArrayList<>();
        String sql = "SELECT * FROM unions WHERE upazila_id = ? ORDER BY CAST(id AS INTEGER)";
        
        try (PreparedStatement pstmt = dbManager.getConnection().prepareStatement(sql)) {
            pstmt.setString(1, upazilaId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    unions.add(mapResultSetToUnion(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return unions;
    }

    public List<UnionModel> search(String keyword) {
        if (keyword == null || keyword.isEmpty()) {
            return getAll();
        }
        
        List<UnionModel> unions = new ArrayList<>();
        String sql = "SELECT * FROM unions WHERE name LIKE ? OR bn_name LIKE ? ORDER BY CAST(id AS INTEGER)";
        String pattern = "%" + keyword + "%";
        
        try (PreparedStatement pstmt = dbManager.getConnection().prepareStatement(sql)) {
            pstmt.setString(1, pattern);
            pstmt.setString(2, pattern);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    unions.add(mapResultSetToUnion(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return unions;
    }

    private UnionModel mapResultSetToUnion(ResultSet rs) throws SQLException {
        UnionModel union = new UnionModel();
        union.setId(rs.getString("id"));
        union.setUpazilaId(rs.getString("upazila_id"));
        union.setName(rs.getString("name"));
        union.setBnName(rs.getString("bn_name"));
        union.setUrl(rs.getString("url"));
        return union;
    }
}
