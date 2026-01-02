package com.example.nutrimap.dao;

import com.example.nutrimap.model.BranchModel;
import com.example.nutrimap.service.GitHubJsonDataService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Data Access Object for branches.
 * Fetches data from GitHub JSON (read-only).
 */
public class BranchDAO {
    private final GitHubJsonDataService dataService;
    private ObservableList<BranchModel> branchList = FXCollections.observableArrayList();

    public BranchDAO() {
        this.dataService = GitHubJsonDataService.getInstance();
        loadBranches();
    }

    private void loadBranches() {
        branchList.setAll(getAll());
    }

    public List<BranchModel> getAll() {
        return dataService.getBranches();
    }

    public ObservableList<BranchModel> getObservableBranches() {
        return branchList;
    }

    public BranchModel getById(String id) {
        return dataService.getBranches().stream()
                .filter(b -> b.getId() != null && b.getId().equals(id))
                .findFirst()
                .orElse(null);
    }

    public BranchModel getByName(String name) {
        return dataService.getBranches().stream()
                .filter(b -> b.getName() != null && b.getName().equalsIgnoreCase(name))
                .findFirst()
                .orElse(null);
    }

    public List<BranchModel> getByDivision(String division) {
        return dataService.getBranches().stream()
                .filter(b -> b.getDivision() != null && b.getDivision().equalsIgnoreCase(division))
                .collect(Collectors.toList());
    }

    public List<BranchModel> getByDistrict(String district) {
        return dataService.getBranches().stream()
                .filter(b -> b.getDistrict() != null && b.getDistrict().equalsIgnoreCase(district))
                .collect(Collectors.toList());
    }

    public List<BranchModel> getByUpazilla(String upazilla) {
        return dataService.getBranches().stream()
                .filter(b -> b.getUpazilla() != null && b.getUpazilla().equalsIgnoreCase(upazilla))
                .collect(Collectors.toList());
    }

    public List<BranchModel> search(String keyword) {
        if (keyword == null || keyword.isEmpty()) {
            return getAll();
        }
        
        String lower = keyword.toLowerCase();
        return dataService.getBranches().stream()
                .filter(b -> (b.getName() != null && b.getName().toLowerCase().contains(lower)) ||
                             (b.getBn_name() != null && b.getBn_name().contains(keyword)) ||
                             (b.getDistrict() != null && b.getDistrict().toLowerCase().contains(lower)) ||
                             (b.getDivision() != null && b.getDivision().toLowerCase().contains(lower)) ||
                             (b.getUpazilla() != null && b.getUpazilla().toLowerCase().contains(lower)))
                .collect(Collectors.toList());
    }

    public void refreshBranches() {
        dataService.clearCache();
        loadBranches();
    }
}
