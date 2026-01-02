package com.example.nutrimap.dao;

import com.example.nutrimap.model.ChildModel;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class ChildDAO {
    private final DatabaseManager dbManager;
    private final ObservableList<ChildModel> childList = FXCollections.observableArrayList();

    public ChildDAO() {
        this.dbManager = DatabaseManager.getInstance();
        loadChildren();
    }

    private void loadChildren() {
        childList.setAll(getAll());
    }

    public List<ChildModel> getAll() {
        List<ChildModel> children = new ArrayList<>();
        String sql = "SELECT * FROM children ORDER BY id";
        
        try (PreparedStatement pstmt = dbManager.getConnection().prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            
            while (rs.next()) {
                children.add(mapResultSetToChild(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return children;
    }

    public ObservableList<ChildModel> getObservableChildren() {
        return childList;
    }

    public ChildModel getById(int id) {
        String sql = "SELECT * FROM children WHERE id = ?";
        
        try (PreparedStatement pstmt = dbManager.getConnection().prepareStatement(sql)) {
            pstmt.setInt(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToChild(rs);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<ChildModel> getByBranchId(String branchId) {
        List<ChildModel> children = new ArrayList<>();
        String sql = "SELECT * FROM children WHERE branch_id = ? ORDER BY id";
        
        try (PreparedStatement pstmt = dbManager.getConnection().prepareStatement(sql)) {
            pstmt.setString(1, branchId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    children.add(mapResultSetToChild(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return children;
    }

    public List<ChildModel> search(String keyword) {
        if (keyword == null || keyword.isEmpty()) {
            return getAll();
        }
        
        List<ChildModel> children = new ArrayList<>();
        String sql = "SELECT * FROM children WHERE full_name LIKE ? OR fathers_name LIKE ? OR mothers_name LIKE ? OR branch_name LIKE ? OR division LIKE ? OR district LIKE ? OR upazilla LIKE ? OR union_name LIKE ? ORDER BY id";
        String pattern = "%" + keyword + "%";
        
        try (PreparedStatement pstmt = dbManager.getConnection().prepareStatement(sql)) {
            pstmt.setString(1, pattern);
            pstmt.setString(2, pattern);
            pstmt.setString(3, pattern);
            pstmt.setString(4, pattern);
            pstmt.setString(5, pattern);
            pstmt.setString(6, pattern);
            pstmt.setString(7, pattern);
            pstmt.setString(8, pattern);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    children.add(mapResultSetToChild(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return children;
    }

    public void addChild(ChildModel child) {
        String sql = "INSERT INTO children (full_name, fathers_name, mothers_name, contact_number, division, district, upazilla, union_name, branch_id, branch_name, last_visit, gender, date_of_birth) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        
        try (PreparedStatement pstmt = dbManager.getConnection().prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, child.getFullName());
            pstmt.setString(2, child.getFathersName());
            pstmt.setString(3, child.getMothersName());
            pstmt.setString(4, child.getContactNumber());
            pstmt.setString(5, child.getDivision());
            pstmt.setString(6, child.getDistrict());
            pstmt.setString(7, child.getUpazilla());
            pstmt.setString(8, child.getUnionName());
            pstmt.setString(9, child.getBranchId());
            pstmt.setString(10, child.getBranchName());
            pstmt.setString(11, child.getLastVisit());
            pstmt.setString(12, child.getGender());
            pstmt.setString(13, child.getDateOfBirth());
            pstmt.executeUpdate();
            
            try (ResultSet keys = pstmt.getGeneratedKeys()) {
                if (keys.next()) {
                    child.setId(keys.getInt(1));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void updateChild(ChildModel child) {
        String sql = "UPDATE children SET full_name = ?, fathers_name = ?, mothers_name = ?, contact_number = ?, division = ?, district = ?, upazilla = ?, union_name = ?, branch_id = ?, branch_name = ?, last_visit = ?, gender = ?, date_of_birth = ? WHERE id = ?";
        
        try (PreparedStatement pstmt = dbManager.getConnection().prepareStatement(sql)) {
            pstmt.setString(1, child.getFullName());
            pstmt.setString(2, child.getFathersName());
            pstmt.setString(3, child.getMothersName());
            pstmt.setString(4, child.getContactNumber());
            pstmt.setString(5, child.getDivision());
            pstmt.setString(6, child.getDistrict());
            pstmt.setString(7, child.getUpazilla());
            pstmt.setString(8, child.getUnionName());
            pstmt.setString(9, child.getBranchId());
            pstmt.setString(10, child.getBranchName());
            pstmt.setString(11, child.getLastVisit());
            pstmt.setString(12, child.getGender());
            pstmt.setString(13, child.getDateOfBirth());
            pstmt.setInt(14, child.getId());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void deleteChild(int id) {
        String sql = "DELETE FROM children WHERE id = ?";
        
        try (PreparedStatement pstmt = dbManager.getConnection().prepareStatement(sql)) {
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void refreshChildren() {
        loadChildren();
    }

    private ChildModel mapResultSetToChild(ResultSet rs) throws SQLException {
        ChildModel child = new ChildModel();
        child.setId(rs.getInt("id"));
        child.setFullName(rs.getString("full_name"));
        child.setFathersName(rs.getString("fathers_name"));
        child.setMothersName(rs.getString("mothers_name"));
        child.setContactNumber(rs.getString("contact_number"));
        child.setDivision(rs.getString("division"));
        child.setDistrict(rs.getString("district"));
        child.setUpazilla(rs.getString("upazilla"));
        child.setUnionName(rs.getString("union_name"));
        child.setBranchId(rs.getString("branch_id"));
        child.setBranchName(rs.getString("branch_name"));
        child.setLastVisit(rs.getString("last_visit"));
        child.setGender(rs.getString("gender"));
        child.setDateOfBirth(rs.getString("date_of_birth"));
        return child;
    }
}
