package com.example.nutrimap;
import com.example.nutrimap.model.BranchModel;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.io.File;
import java.io.FileReader;
import java.io.Reader;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
public class DebugBranches {
    public static void main(String[] args) {
        String filePath = "src/main/resources/data/branches.json";
        File file = new File(filePath);
        if (!file.exists()) {
            System.err.println("File not found: " + filePath);
            return;
        }
        try (Reader reader = new FileReader(file)) {
            Gson gson = new Gson();
            Type listType = new TypeToken<ArrayList<BranchModel>>(){}.getType();
            List<BranchModel> branches = gson.fromJson(reader, listType);
            System.out.println("Total loaded branches: " + branches.size());
            long nullCount = branches.stream().filter(b -> b == null).count();
            System.out.println("Null entries: " + nullCount);
            List<String> ids = branches.stream().filter(b -> b != null).map(BranchModel::getId).collect(Collectors.toList());
            System.out.println("Total IDs found: " + ids.size());
            if (branches.size() < 494) {
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
