package com.example.nutrimap.dao;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Database manager for SQLite.
 * Only manages users, children, and visits tables.
 * Location data (divisions, districts, upazilas, unions, branches) is fetched from GitHub.
 */
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
            // Users table
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

            // Children table
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
                "    date_of_birth TEXT" +
                ")"
            );

            // Visits table
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

            // Indexes
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
