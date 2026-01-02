package com.example.nutrimap.dao;

import com.example.nutrimap.model.UpazilaModel;
import com.example.nutrimap.service.GitHubJsonDataService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.List;
import java.util.stream.Collectors;

public class UpazilaDAO {
    private final GitHubJsonDataService dataService;

    public UpazilaDAO() {
        this.dataService = GitHubJsonDataService.getInstance();
    }

    public List<UpazilaModel> getAll() {
        return dataService.getUpazilas();
    }

    public ObservableList<UpazilaModel> getObservableUpazilas() {
        return FXCollections.observableArrayList(getAll());
    }

    public UpazilaModel getById(String id) {
        return dataService.getUpazilas().stream()
                .filter(u -> u.getId() != null && u.getId().equals(id))
                .findFirst()
                .orElse(null);
    }

    public UpazilaModel getByName(String name) {
        return dataService.getUpazilas().stream()
                .filter(u -> u.getName() != null && u.getName().equalsIgnoreCase(name))
                .findFirst()
                .orElse(null);
    }

    public List<UpazilaModel> getByDistrictId(String districtId) {
        return dataService.getUpazilas().stream()
                .filter(u -> u.getDistrictId() != null && u.getDistrictId().equals(districtId))
                .collect(Collectors.toList());
    }

    public List<UpazilaModel> search(String keyword) {
        if (keyword == null || keyword.isEmpty()) {
            return getAll();
        }
        
        String lower = keyword.toLowerCase();
        return dataService.getUpazilas().stream()
                .filter(u -> (u.getName() != null && u.getName().toLowerCase().contains(lower)) ||
                             (u.getBnName() != null && u.getBnName().contains(keyword)))
                .collect(Collectors.toList());
    }
}
