package com.example.nutrimap.dao;
import com.example.nutrimap.model.UserModel;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import java.io.*;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
public class UserDAO {
    private static final String FILE_PATH = "src/main/resources/data/user-json.json";
    private List<UserModel> users;
    private final Gson gson;
    public UserDAO() {
        this.gson = new GsonBuilder().setPrettyPrinting().create();
        this.users = new ArrayList<>();
        loadUsers();
    }
    private void loadUsers() {
        File file = new File(FILE_PATH);
        if (!file.exists()) {
            return;
        }
        try (Reader reader = new FileReader(file)) {
            Type listType = new TypeToken<ArrayList<UserModel>>(){}.getType();
            List<UserModel> loaded = gson.fromJson(reader, listType);
            if (loaded != null) {
                users = loaded;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private void saveUsers() {
        try (Writer writer = new FileWriter(FILE_PATH)) {
            gson.toJson(users, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public List<UserModel> getAllUsers() {
        return new ArrayList<>(users);
    }
    public ObservableList<UserModel> getObservableUsers() {
        return FXCollections.observableArrayList(users);
    }
    public UserModel getById(int id) {
        return users.stream().filter(u -> u.getId() == id).findFirst().orElse(null);
    }
    public UserModel getByEmail(String email) {
        return users.stream().filter(u -> u.getEmail().equalsIgnoreCase(email)).findFirst().orElse(null);
    }
    public void addUser(UserModel user) {
        int maxId = users.stream().mapToInt(UserModel::getId).max().orElse(0);
        user.setId(maxId + 1);
        users.add(user);
        saveUsers();
    }
    public void updateUser(UserModel user) {
        for (int i = 0; i < users.size(); i++) {
            if (users.get(i).getId() == user.getId()) {
                users.set(i, user);
                break;
            }
        }
        saveUsers();
    }
    public void deleteUser(int id) {
        users.removeIf(u -> u.getId() == id);
        saveUsers();
    }
    public List<UserModel> search(String keyword) {
        if (keyword == null || keyword.isEmpty()) {
            return getAllUsers();
        }
        String lower = keyword.toLowerCase();
        return users.stream()
                .filter(u -> u.getName().toLowerCase().contains(lower) ||
                             u.getEmail().toLowerCase().contains(lower) ||
                             u.getRole().toLowerCase().contains(lower))
                .collect(Collectors.toList());
    }
}
