package com.example.nutrimap.dao;

import com.example.nutrimap.model.UnionModel;
import com.example.nutrimap.service.GitHubJsonDataService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.List;
import java.util.stream.Collectors;

public class UnionDAO {
    private final GitHubJsonDataService dataService;

    public UnionDAO() {
        this.dataService = GitHubJsonDataService.getInstance();
    }

    public List<UnionModel> getAll() {
        return dataService.getUnions();
    }

    public ObservableList<UnionModel> getObservableUnions() {
        return FXCollections.observableArrayList(getAll());
    }

    public UnionModel getById(String id) {
        return dataService.getUnions().stream()
                .filter(u -> u.getId() != null && u.getId().equals(id))
                .findFirst()
                .orElse(null);
    }

    public UnionModel getByName(String name) {
        return dataService.getUnions().stream()
                .filter(u -> u.getName() != null && u.getName().equalsIgnoreCase(name))
                .findFirst()
                .orElse(null);
    }

    public List<UnionModel> getByUpazilaId(String upazilaId) {
        return dataService.getUnions().stream()
                .filter(u -> u.getUpazilaId() != null && u.getUpazilaId().equals(upazilaId))
                .collect(Collectors.toList());
    }

    public List<UnionModel> search(String keyword) {
        if (keyword == null || keyword.isEmpty()) {
            return getAll();
        }
        
        String lower = keyword.toLowerCase();
        return dataService.getUnions().stream()
                .filter(u -> (u.getName() != null && u.getName().toLowerCase().contains(lower)) ||
                             (u.getBnName() != null && u.getBnName().contains(keyword)))
                .collect(Collectors.toList());
    }
}
