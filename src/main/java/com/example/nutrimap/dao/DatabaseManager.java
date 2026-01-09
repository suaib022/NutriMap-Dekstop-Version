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
            seedDefaultUsers();
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
    
    private void seedDefaultUsers() {
        if (!isTableEmpty("users")) {
            return; // Users already exist
        }
        
        try (Statement stmt = connection.createStatement()) {
            // Admin user
            stmt.execute(
                "INSERT INTO users (name, email, password, role) VALUES " +
                "('Admin User', 'admin@gmail.com', 'a1234', 'ADMIN')"
            );
            
            // Supervisor user
            stmt.execute(
                "INSERT INTO users (name, email, password, role) VALUES " +
                "('Supervisor User', 'super@gmail.com', 's1234', 'SUPERVISOR')"
            );
            
            // Field Worker user
            stmt.execute(
                "INSERT INTO users (name, email, password, role) VALUES " +
                "('Field Worker', 'worker@gmail.com', 'w1234', 'FIELD_WORKER')"
            );
            
            System.out.println("Default users created successfully.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        // Seed sample data after creating users
        seedSampleData();
    }
    
    private void seedSampleData() {
        if (!isTableEmpty("children")) {
            return; // Sample data already exists
        }
        
        try (Statement stmt = connection.createStatement()) {
            // Sample children
            stmt.execute(
                "INSERT INTO children (full_name, fathers_name, mothers_name, contact_number, division, district, upazilla, union_name, branch_name, gender, date_of_birth, last_visit) VALUES " +
                "('Rahim Ahmed', 'Karim Ahmed', 'Fatima Ahmed', '01712345678', 'Dhaka', 'Dhaka', 'Mirpur', 'Pallabi', 'Dhaka North Branch', 'Male', '2021-03-15', '2025-12-01')"
            );
            stmt.execute(
                "INSERT INTO children (full_name, fathers_name, mothers_name, contact_number, division, district, upazilla, union_name, branch_name, gender, date_of_birth, last_visit) VALUES " +
                "('Aisha Begum', 'Hasan Ali', 'Mina Ali', '01798765432', 'Chittagong', 'Chittagong', 'Pahartali', 'Raujan', 'Chittagong Main Branch', 'Female', '2020-07-22', '2025-11-28')"
            );
            stmt.execute(
                "INSERT INTO children (full_name, fathers_name, mothers_name, contact_number, division, district, upazilla, union_name, branch_name, gender, date_of_birth, last_visit) VALUES " +
                "('Tanvir Hossain', 'Rafiq Hossain', 'Saleha Hossain', '01654321098', 'Rajshahi', 'Rajshahi', 'Boalia', 'Shiroil', 'Rajshahi Central', 'Male', '2022-01-10', '2025-12-05')"
            );
            stmt.execute(
                "INSERT INTO children (full_name, fathers_name, mothers_name, contact_number, division, district, upazilla, union_name, branch_name, gender, date_of_birth, last_visit) VALUES " +
                "('Nusrat Jahan', 'Jahangir Alam', 'Rahima Alam', '01876543210', 'Khulna', 'Khulna', 'Sonadanga', 'Khalishpur', 'Khulna South Branch', 'Female', '2019-11-05', '2025-11-15')"
            );
            stmt.execute(
                "INSERT INTO children (full_name, fathers_name, mothers_name, contact_number, division, district, upazilla, union_name, branch_name, gender, date_of_birth, last_visit) VALUES " +
                "('Imran Khan', 'Salim Khan', 'Nasreen Khan', '01543216789', 'Sylhet', 'Sylhet', 'Kotwali', 'Jalalabad', 'Sylhet East Branch', 'Male', '2021-08-20', '2025-12-10')"
            );
            
            // Sample visits
            stmt.execute(
                "INSERT INTO visits (child_id, visit_date, weight_kg, height_cm, muac_mm, risk_level, notes, created_at) VALUES " +
                "(1, '2025-12-01', 12.5, 85.0, 135, 'LOW', 'Healthy growth observed', '2025-12-01 10:00:00')"
            );
            stmt.execute(
                "INSERT INTO visits (child_id, visit_date, weight_kg, height_cm, muac_mm, risk_level, notes, created_at) VALUES " +
                "(2, '2025-11-28', 10.2, 78.5, 118, 'MEDIUM', 'Slightly underweight, follow-up needed', '2025-11-28 11:30:00')"
            );
            stmt.execute(
                "INSERT INTO visits (child_id, visit_date, weight_kg, height_cm, muac_mm, risk_level, notes, created_at) VALUES " +
                "(3, '2025-12-05', 14.0, 92.0, 140, 'LOW', 'Good nutrition status', '2025-12-05 09:15:00')"
            );
            stmt.execute(
                "INSERT INTO visits (child_id, visit_date, weight_kg, height_cm, muac_mm, risk_level, notes, created_at) VALUES " +
                "(4, '2025-11-15', 8.5, 72.0, 105, 'HIGH', 'Severe acute malnutrition detected', '2025-11-15 14:00:00')"
            );
            stmt.execute(
                "INSERT INTO visits (child_id, visit_date, weight_kg, height_cm, muac_mm, risk_level, notes, created_at) VALUES " +
                "(5, '2025-12-10', 11.8, 82.5, 128, 'LOW', 'Normal development', '2025-12-10 15:45:00')"
            );
            stmt.execute(
                "INSERT INTO visits (child_id, visit_date, weight_kg, height_cm, muac_mm, risk_level, notes, created_at) VALUES " +
                "(1, '2025-11-01', 11.8, 83.0, 132, 'LOW', 'Previous checkup - normal', '2025-11-01 10:00:00')"
            );
            stmt.execute(
                "INSERT INTO visits (child_id, visit_date, weight_kg, height_cm, muac_mm, risk_level, notes, created_at) VALUES " +
                "(2, '2025-10-28', 9.8, 76.0, 115, 'MEDIUM', 'Moderate underweight', '2025-10-28 11:00:00')"
            );
            
            System.out.println("Sample children and visits data created successfully.");
        } catch (SQLException e) {
            e.printStackTrace();
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
