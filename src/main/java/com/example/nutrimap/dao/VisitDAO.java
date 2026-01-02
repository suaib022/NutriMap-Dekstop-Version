package com.example.nutrimap.dao;

import com.example.nutrimap.model.VisitModel;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class VisitDAO {
    private final DatabaseManager dbManager;
    private final ObservableList<VisitModel> visitList = FXCollections.observableArrayList();
    private static final DateTimeFormatter DATETIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public VisitDAO() {
        this.dbManager = DatabaseManager.getInstance();
        loadVisits();
    }

    private void loadVisits() {
        visitList.setAll(getAll());
    }

    public List<VisitModel> getAll() {
        List<VisitModel> visits = new ArrayList<>();
        String sql = "SELECT v.*, c.full_name as child_name FROM visits v " +
                     "LEFT JOIN children c ON v.child_id = c.id " +
                     "WHERE v.deleted = 0 ORDER BY v.visit_id DESC";
        
        try (PreparedStatement pstmt = dbManager.getConnection().prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            
            while (rs.next()) {
                visits.add(mapResultSetToVisit(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return visits;
    }

    public ObservableList<VisitModel> getObservableVisits() {
        return visitList;
    }

    public VisitModel getById(int visitId) {
        String sql = "SELECT v.*, c.full_name as child_name FROM visits v " +
                     "LEFT JOIN children c ON v.child_id = c.id " +
                     "WHERE v.visit_id = ? AND v.deleted = 0";
        
        try (PreparedStatement pstmt = dbManager.getConnection().prepareStatement(sql)) {
            pstmt.setInt(1, visitId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToVisit(rs);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<VisitModel> getByChildId(int childId) {
        List<VisitModel> visits = new ArrayList<>();
        String sql = "SELECT v.*, c.full_name as child_name FROM visits v " +
                     "LEFT JOIN children c ON v.child_id = c.id " +
                     "WHERE v.child_id = ? AND v.deleted = 0 ORDER BY v.visit_date DESC";
        
        try (PreparedStatement pstmt = dbManager.getConnection().prepareStatement(sql)) {
            pstmt.setInt(1, childId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    visits.add(mapResultSetToVisit(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return visits;
    }

    public VisitModel getLatestVisitByChildId(int childId) {
        String sql = "SELECT v.*, c.full_name as child_name FROM visits v " +
                     "LEFT JOIN children c ON v.child_id = c.id " +
                     "WHERE v.child_id = ? AND v.deleted = 0 ORDER BY v.visit_date DESC LIMIT 1";
        
        try (PreparedStatement pstmt = dbManager.getConnection().prepareStatement(sql)) {
            pstmt.setInt(1, childId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToVisit(rs);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<VisitModel> search(String keyword) {
        if (keyword == null || keyword.isEmpty()) {
            return getAll();
        }
        
        List<VisitModel> visits = new ArrayList<>();
        String sql = "SELECT v.*, c.full_name as child_name FROM visits v " +
                     "LEFT JOIN children c ON v.child_id = c.id " +
                     "WHERE v.deleted = 0 AND (c.full_name LIKE ? OR v.visit_date LIKE ? OR v.notes LIKE ?) " +
                     "ORDER BY v.visit_id DESC";
        String pattern = "%" + keyword + "%";
        
        try (PreparedStatement pstmt = dbManager.getConnection().prepareStatement(sql)) {
            pstmt.setString(1, pattern);
            pstmt.setString(2, pattern);
            pstmt.setString(3, pattern);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    visits.add(mapResultSetToVisit(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return visits;
    }

    public void addVisit(VisitModel visit) {
        String now = LocalDateTime.now().format(DATETIME_FORMATTER);
        String sql = "INSERT INTO visits (child_id, visit_date, weight_kg, height_cm, muac_mm, risk_level, notes, created_at, updated_at, entered_by, deleted) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, 0)";
        
        try (PreparedStatement pstmt = dbManager.getConnection().prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setInt(1, visit.getChildId());
            pstmt.setString(2, visit.getVisitDate());
            pstmt.setDouble(3, visit.getWeightKg());
            pstmt.setDouble(4, visit.getHeightCm());
            pstmt.setInt(5, visit.getMuacMm());
            pstmt.setString(6, visit.getRiskLevel() != null ? visit.getRiskLevel() : "N/A");
            pstmt.setString(7, visit.getNotes());
            pstmt.setString(8, now);
            pstmt.setString(9, now);
            if (visit.getEnteredBy() != null) {
                pstmt.setInt(10, visit.getEnteredBy());
            } else {
                pstmt.setNull(10, java.sql.Types.INTEGER);
            }
            pstmt.executeUpdate();
            
            try (ResultSet keys = pstmt.getGeneratedKeys()) {
                if (keys.next()) {
                    visit.setVisitId(keys.getInt(1));
                }
            }
            
            updateChildLastVisit(visit.getChildId(), visit.getVisitDate());
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void updateVisit(VisitModel visit) {
        String now = LocalDateTime.now().format(DATETIME_FORMATTER);
        String sql = "UPDATE visits SET child_id = ?, visit_date = ?, weight_kg = ?, height_cm = ?, muac_mm = ?, risk_level = ?, notes = ?, updated_at = ? WHERE visit_id = ?";
        
        try (PreparedStatement pstmt = dbManager.getConnection().prepareStatement(sql)) {
            pstmt.setInt(1, visit.getChildId());
            pstmt.setString(2, visit.getVisitDate());
            pstmt.setDouble(3, visit.getWeightKg());
            pstmt.setDouble(4, visit.getHeightCm());
            pstmt.setInt(5, visit.getMuacMm());
            pstmt.setString(6, visit.getRiskLevel() != null ? visit.getRiskLevel() : "N/A");
            pstmt.setString(7, visit.getNotes());
            pstmt.setString(8, now);
            pstmt.setInt(9, visit.getVisitId());
            pstmt.executeUpdate();
            
            updateChildLastVisitFromAllVisits(visit.getChildId());
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void deleteVisit(int visitId) {
        String sql = "UPDATE visits SET deleted = 1 WHERE visit_id = ?";
        
        try (PreparedStatement pstmt = dbManager.getConnection().prepareStatement(sql)) {
            pstmt.setInt(1, visitId);
            pstmt.executeUpdate();
            
            VisitModel visit = getById(visitId);
            if (visit != null) {
                updateChildLastVisitFromAllVisits(visit.getChildId());
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void updateChildLastVisit(int childId, String visitDate) {
        String sql = "UPDATE children SET last_visit = ? WHERE id = ?";
        try (PreparedStatement pstmt = dbManager.getConnection().prepareStatement(sql)) {
            pstmt.setString(1, visitDate);
            pstmt.setInt(2, childId);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void updateChildLastVisitFromAllVisits(int childId) {
        String sql = "SELECT MAX(visit_date) as last_visit FROM visits WHERE child_id = ? AND deleted = 0";
        try (PreparedStatement pstmt = dbManager.getConnection().prepareStatement(sql)) {
            pstmt.setInt(1, childId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    String lastVisit = rs.getString("last_visit");
                    updateChildLastVisit(childId, lastVisit);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void refreshVisits() {
        loadVisits();
    }

    private VisitModel mapResultSetToVisit(ResultSet rs) throws SQLException {
        VisitModel visit = new VisitModel();
        visit.setVisitId(rs.getInt("visit_id"));
        visit.setChildId(rs.getInt("child_id"));
        visit.setChildName(rs.getString("child_name"));
        visit.setVisitDate(rs.getString("visit_date"));
        visit.setWeightKg(rs.getDouble("weight_kg"));
        visit.setHeightCm(rs.getDouble("height_cm"));
        visit.setMuacMm(rs.getInt("muac_mm"));
        visit.setRiskLevel(rs.getString("risk_level"));
        visit.setNotes(rs.getString("notes"));
        visit.setCreatedAt(rs.getString("created_at"));
        visit.setUpdatedAt(rs.getString("updated_at"));
        int enteredBy = rs.getInt("entered_by");
        if (!rs.wasNull()) {
            visit.setEnteredBy(enteredBy);
        }
        visit.setDeleted(rs.getInt("deleted") == 1);
        return visit;
    }
}
