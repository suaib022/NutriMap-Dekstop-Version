package com.example.nutrimap.dao;

import com.example.nutrimap.model.DivisionModel;
import com.example.nutrimap.service.GitHubJsonDataService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.List;
import java.util.stream.Collectors;

public class DivisionDAO {
    private final GitHubJsonDataService dataService;

    public DivisionDAO() {
        this.dataService = GitHubJsonDataService.getInstance();
    }

    public List<DivisionModel> getAll() {
        return dataService.getDivisions();
    }

    public ObservableList<DivisionModel> getObservableDivisions() {
        return FXCollections.observableArrayList(getAll());
    }

    public DivisionModel getById(String id) {
        return dataService.getDivisions().stream()
                .filter(d -> d.getId() != null && d.getId().equals(id))
                .findFirst()
                .orElse(null);
    }

    public DivisionModel getByName(String name) {
        return dataService.getDivisions().stream()
                .filter(d -> d.getName() != null && d.getName().equalsIgnoreCase(name))
                .findFirst()
                .orElse(null);
    }

    public List<DivisionModel> search(String keyword) {
        if (keyword == null || keyword.isEmpty()) {
            return getAll();
        }
        
        String lower = keyword.toLowerCase();
        return dataService.getDivisions().stream()
                .filter(d -> (d.getName() != null && d.getName().toLowerCase().contains(lower)) ||
                             (d.getBnName() != null && d.getBnName().contains(keyword)))
                .collect(Collectors.toList());
    }
}
