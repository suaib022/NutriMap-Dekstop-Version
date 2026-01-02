package com.example.nutrimap.service;

import com.example.nutrimap.model.*;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;

import java.io.InputStreamReader;
import java.io.Reader;
import java.lang.reflect.Type;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Service for fetching JSON data from GitHub raw URLs.
 * Caches data in memory to avoid repeated HTTP calls.
 */
public class GitHubJsonDataService {
    private static final String BASE_URL = "https://raw.githubusercontent.com/suaib022/NutriMap-Dekstop-Version/main/src/main/resources/data/";
    private static GitHubJsonDataService instance;
    
    private final HttpClient httpClient;
    private final Gson gson;
    
    // In-memory cache
    private List<DivisionModel> divisionsCache;
    private List<DistrictModel> districtsCache;
    private List<UpazilaModel> upazilasCache;
    private List<UnionModel> unionsCache;
    private List<BranchModel> branchesCache;
    
    private GitHubJsonDataService() {
        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(10))
                .build();
        this.gson = new Gson();
    }
    
    public static synchronized GitHubJsonDataService getInstance() {
        if (instance == null) {
            instance = new GitHubJsonDataService();
        }
        return instance;
    }
    
    private String fetchJson(String filename) {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(BASE_URL + filename))
                    .timeout(Duration.ofSeconds(30))
                    .GET()
                    .build();
            
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            
            if (response.statusCode() == 200) {
                return response.body();
            } else {
                System.err.println("Failed to fetch " + filename + ": HTTP " + response.statusCode());
                return null;
            }
        } catch (Exception e) {
            System.err.println("Error fetching " + filename + ": " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }
    
    // ==================== DIVISIONS ====================
    public List<DivisionModel> getDivisions() {
        if (divisionsCache != null) {
            return divisionsCache;
        }
        
        String json = fetchJson("divisions.json");
        if (json == null) return new ArrayList<>();
        
        try {
            divisionsCache = new ArrayList<>();
            JsonArray rootArray = JsonParser.parseString(json).getAsJsonArray();
            
            // Find the data array (format: [{type: "table", data: [...]}])
            JsonArray dataArray = null;
            for (JsonElement element : rootArray) {
                if (element.isJsonObject()) {
                    JsonObject obj = element.getAsJsonObject();
                    if (obj.has("type") && "table".equals(obj.get("type").getAsString())) {
                        dataArray = obj.getAsJsonArray("data");
                        break;
                    }
                }
            }
            
            if (dataArray != null) {
                for (JsonElement element : dataArray) {
                    JsonObject obj = element.getAsJsonObject();
                    DivisionModel division = new DivisionModel();
                    division.setId(getStringOrNull(obj, "id"));
                    division.setName(getStringOrNull(obj, "name"));
                    division.setBnName(getStringOrNull(obj, "bn_name"));
                    division.setUrl(getStringOrNull(obj, "url"));
                    divisionsCache.add(division);
                }
            }
            System.out.println("Loaded " + divisionsCache.size() + " divisions from GitHub");
        } catch (Exception e) {
            e.printStackTrace();
            divisionsCache = new ArrayList<>();
        }
        return divisionsCache;
    }
    
    // ==================== DISTRICTS ====================
    public List<DistrictModel> getDistricts() {
        if (districtsCache != null) {
            return districtsCache;
        }
        
        String json = fetchJson("districts.json");
        if (json == null) return new ArrayList<>();
        
        try {
            districtsCache = new ArrayList<>();
            JsonArray rootArray = JsonParser.parseString(json).getAsJsonArray();
            
            JsonArray dataArray = null;
            for (JsonElement element : rootArray) {
                if (element.isJsonObject()) {
                    JsonObject obj = element.getAsJsonObject();
                    if (obj.has("type") && "table".equals(obj.get("type").getAsString())) {
                        dataArray = obj.getAsJsonArray("data");
                        break;
                    }
                }
            }
            
            if (dataArray != null) {
                for (JsonElement element : dataArray) {
                    JsonObject obj = element.getAsJsonObject();
                    DistrictModel district = new DistrictModel();
                    district.setId(getStringOrNull(obj, "id"));
                    district.setDivisionId(getStringOrNull(obj, "division_id"));
                    district.setName(getStringOrNull(obj, "name"));
                    district.setBnName(getStringOrNull(obj, "bn_name"));
                    district.setLat(getStringOrNull(obj, "lat"));
                    district.setLon(getStringOrNull(obj, "lon"));
                    district.setUrl(getStringOrNull(obj, "url"));
                    districtsCache.add(district);
                }
            }
            System.out.println("Loaded " + districtsCache.size() + " districts from GitHub");
        } catch (Exception e) {
            e.printStackTrace();
            districtsCache = new ArrayList<>();
        }
        return districtsCache;
    }
    
    // ==================== UPAZILAS ====================
    public List<UpazilaModel> getUpazilas() {
        if (upazilasCache != null) {
            return upazilasCache;
        }
        
        String json = fetchJson("upazilas.json");
        if (json == null) return new ArrayList<>();
        
        try {
            upazilasCache = new ArrayList<>();
            JsonArray rootArray = JsonParser.parseString(json).getAsJsonArray();
            
            JsonArray dataArray = null;
            for (JsonElement element : rootArray) {
                if (element.isJsonObject()) {
                    JsonObject obj = element.getAsJsonObject();
                    if (obj.has("type") && "table".equals(obj.get("type").getAsString())) {
                        dataArray = obj.getAsJsonArray("data");
                        break;
                    }
                }
            }
            
            if (dataArray != null) {
                for (JsonElement element : dataArray) {
                    JsonObject obj = element.getAsJsonObject();
                    UpazilaModel upazila = new UpazilaModel();
                    upazila.setId(getStringOrNull(obj, "id"));
                    upazila.setDistrictId(getStringOrNull(obj, "district_id"));
                    upazila.setName(getStringOrNull(obj, "name"));
                    upazila.setBnName(getStringOrNull(obj, "bn_name"));
                    upazila.setUrl(getStringOrNull(obj, "url"));
                    upazilasCache.add(upazila);
                }
            }
            System.out.println("Loaded " + upazilasCache.size() + " upazilas from GitHub");
        } catch (Exception e) {
            e.printStackTrace();
            upazilasCache = new ArrayList<>();
        }
        return upazilasCache;
    }
    
    // ==================== UNIONS ====================
    public List<UnionModel> getUnions() {
        if (unionsCache != null) {
            return unionsCache;
        }
        
        String json = fetchJson("unions.json");
        if (json == null) return new ArrayList<>();
        
        try {
            unionsCache = new ArrayList<>();
            JsonArray rootArray = JsonParser.parseString(json).getAsJsonArray();
            
            JsonArray dataArray = null;
            for (JsonElement element : rootArray) {
                if (element.isJsonObject()) {
                    JsonObject obj = element.getAsJsonObject();
                    if (obj.has("type") && "table".equals(obj.get("type").getAsString())) {
                        dataArray = obj.getAsJsonArray("data");
                        break;
                    }
                }
            }
            
            if (dataArray != null) {
                for (JsonElement element : dataArray) {
                    JsonObject obj = element.getAsJsonObject();
                    UnionModel union = new UnionModel();
                    union.setId(getStringOrNull(obj, "id"));
                    union.setUpazilaId(getStringOrNull(obj, "upazilla_id"));
                    union.setName(getStringOrNull(obj, "name"));
                    union.setBnName(getStringOrNull(obj, "bn_name"));
                    union.setUrl(getStringOrNull(obj, "url"));
                    unionsCache.add(union);
                }
            }
            System.out.println("Loaded " + unionsCache.size() + " unions from GitHub");
        } catch (Exception e) {
            e.printStackTrace();
            unionsCache = new ArrayList<>();
        }
        return unionsCache;
    }
    
    // ==================== BRANCHES ====================
    public List<BranchModel> getBranches() {
        if (branchesCache != null) {
            return branchesCache;
        }
        
        String json = fetchJson("branches.json");
        if (json == null) return new ArrayList<>();
        
        try {
            branchesCache = new ArrayList<>();
            JsonArray dataArray = JsonParser.parseString(json).getAsJsonArray();
            
            for (JsonElement element : dataArray) {
                JsonObject obj = element.getAsJsonObject();
                BranchModel branch = new BranchModel();
                branch.setId(getStringOrNull(obj, "id"));
                branch.setName(getStringOrNull(obj, "name"));
                branch.setBn_name(getStringOrNull(obj, "bn_name"));
                branch.setArea(getStringOrNull(obj, "Area"));
                branch.setBn_area(getStringOrNull(obj, "bn_Area"));
                branch.setUpazilla(getStringOrNull(obj, "Upazilla"));
                branch.setBn_upazilla(getStringOrNull(obj, "bn_Upazilla"));
                branch.setDistrict(getStringOrNull(obj, "District"));
                branch.setBn_district(getStringOrNull(obj, "bn_District"));
                branch.setDivision(getStringOrNull(obj, "Division"));
                branch.setBn_division(getStringOrNull(obj, "bn_Division"));
                branch.setUrl(getStringOrNull(obj, "url"));
                branchesCache.add(branch);
            }
            System.out.println("Loaded " + branchesCache.size() + " branches from GitHub");
        } catch (Exception e) {
            e.printStackTrace();
            branchesCache = new ArrayList<>();
        }
        return branchesCache;
    }
    
    // ==================== HELPER METHODS ====================
    private String getStringOrNull(JsonObject obj, String key) {
        if (obj.has(key) && !obj.get(key).isJsonNull()) {
            return obj.get(key).getAsString();
        }
        return null;
    }
    
    /**
     * Clears all cached data, forcing a fresh fetch on next access.
     */
    public void clearCache() {
        divisionsCache = null;
        districtsCache = null;
        upazilasCache = null;
        unionsCache = null;
        branchesCache = null;
    }
    
    /**
     * Preloads all data in background threads.
     */
    public void preloadData() {
        CompletableFuture.runAsync(() -> {
            System.out.println("Preloading data from GitHub...");
            getDivisions();
            getDistricts();
            getUpazilas();
            getUnions();
            getBranches();
            System.out.println("Data preloading complete.");
        });
    }
}
