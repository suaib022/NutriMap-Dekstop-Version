package com.example.nutrimap.dao;
import com.example.nutrimap.model.BranchModel;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import java.io.*;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
public class BranchDAO {
    private static final String DATA_FILE = "/data/branches.json";
    private static final String FILE_PATH = "src/main/resources/data/branches.json";
    private final ObservableList<BranchModel> branchList = FXCollections.observableArrayList();
    private final Gson gson = new Gson();
    public BranchDAO() {
        loadBranches();
    }
    private void loadBranches() {
        try {
            InputStream inputStream = getClass().getResourceAsStream(DATA_FILE);
            Reader reader;
            if (inputStream != null) {
                reader = new InputStreamReader(inputStream);
            } else {
                File file = new File(FILE_PATH);
                if (file.exists()) {
                    reader = new FileReader(file);
                } else {
                    System.err.println("Branches data file not found!");
                    return;
                }
            }
            try (Reader r = reader) {
                Type listType = new TypeToken<ArrayList<BranchModel>>(){}.getType();
                List<BranchModel> branches = gson.fromJson(r, listType);
                if (branches != null) {
                    branchList.setAll(branches);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public ObservableList<BranchModel> getObservableBranches() {
        return branchList;
    }
}
