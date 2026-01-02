package com.example.nutrimap.dao;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseManager {
    private static final String DB_NAME = "nutrimap.db";
    private static final String DB_PATH;
    private static DatabaseManager instance;
    private Connection connection;

    static {
        String userDir = System.getProperty("user.dir");
        DB_PATH = userDir + File.separator + DB_NAME;
    }

    private DatabaseManager() {
        initializeDatabase();
    }

    public static synchronized DatabaseManager getInstance() {
        if (instance == null) {
            instance = new DatabaseManager();
        }
        return instance;
    }

    private void initializeDatabase() {
        try {
            Class.forName("org.sqlite.JDBC");
            connection = DriverManager.getConnection("jdbc:sqlite:" + DB_PATH);
            createTables();
            System.out.println("Database initialized at: " + DB_PATH);
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
    }

    private void createTables() throws SQLException {
        try (Statement stmt = connection.createStatement()) {
            stmt.execute(
                "CREATE TABLE IF NOT EXISTS divisions (" +
                "    id TEXT PRIMARY KEY," +
                "    name TEXT NOT NULL," +
                "    bn_name TEXT," +
                "    url TEXT" +
                ")"
            );

            stmt.execute(
                "CREATE TABLE IF NOT EXISTS districts (" +
                "    id TEXT PRIMARY KEY," +
                "    division_id TEXT," +
                "    name TEXT NOT NULL," +
                "    bn_name TEXT," +
                "    lat TEXT," +
                "    lon TEXT," +
                "    url TEXT," +
                "    FOREIGN KEY (division_id) REFERENCES divisions(id)" +
                ")"
            );

            stmt.execute(
                "CREATE TABLE IF NOT EXISTS upazilas (" +
                "    id TEXT PRIMARY KEY," +
                "    district_id TEXT," +
                "    name TEXT NOT NULL," +
                "    bn_name TEXT," +
                "    url TEXT," +
                "    FOREIGN KEY (district_id) REFERENCES districts(id)" +
                ")"
            );

            stmt.execute(
                "CREATE TABLE IF NOT EXISTS unions (" +
                "    id TEXT PRIMARY KEY," +
                "    upazila_id TEXT," +
                "    name TEXT NOT NULL," +
                "    bn_name TEXT," +
                "    url TEXT," +
                "    FOREIGN KEY (upazila_id) REFERENCES upazilas(id)" +
                ")"
            );

            stmt.execute(
                "CREATE TABLE IF NOT EXISTS branches (" +
                "    id TEXT PRIMARY KEY," +
                "    name TEXT NOT NULL," +
                "    bn_name TEXT," +
                "    area TEXT," +
                "    bn_area TEXT," +
                "    upazilla TEXT," +
                "    bn_upazilla TEXT," +
                "    district TEXT," +
                "    bn_district TEXT," +
                "    division TEXT," +
                "    bn_division TEXT," +
                "    url TEXT" +
                ")"
            );

            stmt.execute(
                "CREATE TABLE IF NOT EXISTS users (" +
                "    id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "    name TEXT NOT NULL," +
                "    email TEXT UNIQUE NOT NULL," +
                "    password TEXT NOT NULL," +
                "    role TEXT DEFAULT 'USER'," +
                "    image_path TEXT" +
                ")"
            );

            stmt.execute(
                "CREATE TABLE IF NOT EXISTS children (" +
                "    id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "    full_name TEXT NOT NULL," +
                "    fathers_name TEXT," +
                "    mothers_name TEXT," +
                "    contact_number TEXT," +
                "    division TEXT," +
                "    district TEXT," +
                "    upazilla TEXT," +
                "    union_name TEXT," +
                "    branch_id TEXT," +
                "    branch_name TEXT," +
                "    last_visit TEXT," +
                "    gender TEXT," +
                "    date_of_birth TEXT," +
                "    FOREIGN KEY (branch_id) REFERENCES branches(id)" +
                ")"
            );

            stmt.execute(
                "CREATE TABLE IF NOT EXISTS visits (" +
                "    visit_id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "    child_id INTEGER NOT NULL," +
                "    visit_date TEXT NOT NULL," +
                "    weight_kg REAL," +
                "    height_cm REAL," +
                "    muac_mm INTEGER," +
                "    risk_level TEXT DEFAULT 'N/A'," +
                "    notes TEXT," +
                "    created_at TEXT," +
                "    updated_at TEXT," +
                "    entered_by INTEGER," +
                "    deleted INTEGER DEFAULT 0," +
                "    FOREIGN KEY (child_id) REFERENCES children(id)" +
                ")"
            );

            stmt.execute("CREATE INDEX IF NOT EXISTS idx_districts_division ON districts(division_id)");
            stmt.execute("CREATE INDEX IF NOT EXISTS idx_upazilas_district ON upazilas(district_id)");
            stmt.execute("CREATE INDEX IF NOT EXISTS idx_unions_upazila ON unions(upazila_id)");
            stmt.execute("CREATE INDEX IF NOT EXISTS idx_branches_division ON branches(division)");
            stmt.execute("CREATE INDEX IF NOT EXISTS idx_branches_district ON branches(district)");
            stmt.execute("CREATE INDEX IF NOT EXISTS idx_users_email ON users(email)");
            stmt.execute("CREATE INDEX IF NOT EXISTS idx_children_branch ON children(branch_id)");
            stmt.execute("CREATE INDEX IF NOT EXISTS idx_visits_child ON visits(child_id)");
        }
    }

    public Connection getConnection() {
        try {
            if (connection == null || connection.isClosed()) {
                connection = DriverManager.getConnection("jdbc:sqlite:" + DB_PATH);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return connection;
    }

    public boolean isTableEmpty(String tableName) {
        try (Statement stmt = getConnection().createStatement()) {
            var rs = stmt.executeQuery("SELECT COUNT(*) FROM " + tableName);
            if (rs.next()) {
                return rs.getInt(1) == 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return true;
    }

    public void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
