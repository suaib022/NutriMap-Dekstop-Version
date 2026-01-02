package com.example.nutrimap.dao;

import com.example.nutrimap.model.DistrictModel;
import com.example.nutrimap.service.GitHubJsonDataService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.List;
import java.util.stream.Collectors;

public class DistrictDAO {
    private final GitHubJsonDataService dataService;

    public DistrictDAO() {
        this.dataService = GitHubJsonDataService.getInstance();
    }

    public List<DistrictModel> getAll() {
        return dataService.getDistricts();
    }

    public ObservableList<DistrictModel> getObservableDistricts() {
        return FXCollections.observableArrayList(getAll());
    }

    public DistrictModel getById(String id) {
        return dataService.getDistricts().stream()
                .filter(d -> d.getId() != null && d.getId().equals(id))
                .findFirst()
                .orElse(null);
    }

    public DistrictModel getByName(String name) {
        return dataService.getDistricts().stream()
                .filter(d -> d.getName() != null && d.getName().equalsIgnoreCase(name))
                .findFirst()
                .orElse(null);
    }

    public List<DistrictModel> getByDivisionId(String divisionId) {
        return dataService.getDistricts().stream()
                .filter(d -> d.getDivisionId() != null && d.getDivisionId().equals(divisionId))
                .collect(Collectors.toList());
    }

    public List<DistrictModel> search(String keyword) {
        if (keyword == null || keyword.isEmpty()) {
            return getAll();
        }
        
        String lower = keyword.toLowerCase();
        return dataService.getDistricts().stream()
                .filter(d -> (d.getName() != null && d.getName().toLowerCase().contains(lower)) ||
                             (d.getBnName() != null && d.getBnName().contains(keyword)))
                .collect(Collectors.toList());
    }
}
