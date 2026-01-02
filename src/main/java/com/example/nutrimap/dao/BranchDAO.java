package com.example.nutrimap.dao;

import com.example.nutrimap.model.BranchModel;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class BranchDAO {
    private final DatabaseManager dbManager;
    private final ObservableList<BranchModel> branchList = FXCollections.observableArrayList();

    public BranchDAO() {
        this.dbManager = DatabaseManager.getInstance();
        loadBranches();
    }

    private void loadBranches() {
        branchList.setAll(getAll());
    }

    public List<BranchModel> getAll() {
        List<BranchModel> branches = new ArrayList<>();
        String sql = "SELECT * FROM branches ORDER BY CAST(id AS INTEGER)";
        
        try (PreparedStatement pstmt = dbManager.getConnection().prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            
            while (rs.next()) {
                branches.add(mapResultSetToBranch(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return branches;
    }

    public ObservableList<BranchModel> getObservableBranches() {
        return branchList;
    }

    public BranchModel getById(String id) {
        String sql = "SELECT * FROM branches WHERE id = ?";
        
        try (PreparedStatement pstmt = dbManager.getConnection().prepareStatement(sql)) {
            pstmt.setString(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToBranch(rs);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<BranchModel> getByDivision(String division) {
        List<BranchModel> branches = new ArrayList<>();
        String sql = "SELECT * FROM branches WHERE division = ? ORDER BY CAST(id AS INTEGER)";
        
        try (PreparedStatement pstmt = dbManager.getConnection().prepareStatement(sql)) {
            pstmt.setString(1, division);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    branches.add(mapResultSetToBranch(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return branches;
    }

    public List<BranchModel> getByDistrict(String district) {
        List<BranchModel> branches = new ArrayList<>();
        String sql = "SELECT * FROM branches WHERE district = ? ORDER BY CAST(id AS INTEGER)";
        
        try (PreparedStatement pstmt = dbManager.getConnection().prepareStatement(sql)) {
            pstmt.setString(1, district);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    branches.add(mapResultSetToBranch(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return branches;
    }

    public List<BranchModel> getByUpazilla(String upazilla) {
        List<BranchModel> branches = new ArrayList<>();
        String sql = "SELECT * FROM branches WHERE upazilla = ? ORDER BY CAST(id AS INTEGER)";
        
        try (PreparedStatement pstmt = dbManager.getConnection().prepareStatement(sql)) {
            pstmt.setString(1, upazilla);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    branches.add(mapResultSetToBranch(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return branches;
    }

    public List<BranchModel> search(String keyword) {
        if (keyword == null || keyword.isEmpty()) {
            return getAll();
        }
        
        List<BranchModel> branches = new ArrayList<>();
        String sql = "SELECT * FROM branches WHERE name LIKE ? OR bn_name LIKE ? OR district LIKE ? OR division LIKE ? OR upazilla LIKE ? ORDER BY CAST(id AS INTEGER)";
        String pattern = "%" + keyword + "%";
        
        try (PreparedStatement pstmt = dbManager.getConnection().prepareStatement(sql)) {
            pstmt.setString(1, pattern);
            pstmt.setString(2, pattern);
            pstmt.setString(3, pattern);
            pstmt.setString(4, pattern);
            pstmt.setString(5, pattern);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    branches.add(mapResultSetToBranch(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return branches;
    }

    public void refreshBranches() {
        loadBranches();
    }

    private BranchModel mapResultSetToBranch(ResultSet rs) throws SQLException {
        BranchModel branch = new BranchModel();
        branch.setId(rs.getString("id"));
        branch.setName(rs.getString("name"));
        branch.setBn_name(rs.getString("bn_name"));
        branch.setArea(rs.getString("area"));
        branch.setBn_area(rs.getString("bn_area"));
        branch.setUpazilla(rs.getString("upazilla"));
        branch.setBn_upazilla(rs.getString("bn_upazilla"));
        branch.setDistrict(rs.getString("district"));
        branch.setBn_district(rs.getString("bn_district"));
        branch.setDivision(rs.getString("division"));
        branch.setBn_division(rs.getString("bn_division"));
        branch.setUrl(rs.getString("url"));
        return branch;
    }

    public int getNextId() {
        String sql = "SELECT MAX(CAST(id AS INTEGER)) as max_id FROM branches";
        try (PreparedStatement pstmt = dbManager.getConnection().prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            if (rs.next()) {
                return rs.getInt("max_id") + 1;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 1;
    }

    public void addBranch(BranchModel branch) {
        String sql = "INSERT INTO branches (id, name, bn_name, area, bn_area, upazilla, bn_upazilla, district, bn_district, division, bn_division, url) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        
        try (PreparedStatement pstmt = dbManager.getConnection().prepareStatement(sql)) {
            if (branch.getId() == null || branch.getId().isEmpty()) {
                branch.setId(String.valueOf(getNextId()));
            }
            pstmt.setString(1, branch.getId());
            pstmt.setString(2, branch.getName());
            pstmt.setString(3, branch.getBn_name());
            pstmt.setString(4, branch.getArea());
            pstmt.setString(5, branch.getBn_area());
            pstmt.setString(6, branch.getUpazilla());
            pstmt.setString(7, branch.getBn_upazilla());
            pstmt.setString(8, branch.getDistrict());
            pstmt.setString(9, branch.getBn_district());
            pstmt.setString(10, branch.getDivision());
            pstmt.setString(11, branch.getBn_division());
            pstmt.setString(12, branch.getUrl());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void updateBranch(BranchModel branch) {
        String sql = "UPDATE branches SET name = ?, bn_name = ?, area = ?, bn_area = ?, upazilla = ?, bn_upazilla = ?, district = ?, bn_district = ?, division = ?, bn_division = ?, url = ? WHERE id = ?";
        
        try (PreparedStatement pstmt = dbManager.getConnection().prepareStatement(sql)) {
            pstmt.setString(1, branch.getName());
            pstmt.setString(2, branch.getBn_name());
            pstmt.setString(3, branch.getArea());
            pstmt.setString(4, branch.getBn_area());
            pstmt.setString(5, branch.getUpazilla());
            pstmt.setString(6, branch.getBn_upazilla());
            pstmt.setString(7, branch.getDistrict());
            pstmt.setString(8, branch.getBn_district());
            pstmt.setString(9, branch.getDivision());
            pstmt.setString(10, branch.getBn_division());
            pstmt.setString(11, branch.getUrl());
            pstmt.setString(12, branch.getId());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void deleteBranch(String id) {
        String sql = "DELETE FROM branches WHERE id = ?";
        
        try (PreparedStatement pstmt = dbManager.getConnection().prepareStatement(sql)) {
            pstmt.setString(1, id);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
