package pl.edu.agh.isi;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Application configuration class that handles loading and saving 
 * configuration parameters to/from JSON files.
 */
public class AppConfig {
    private static final Logger LOGGER = Logger.getLogger(AppConfig.class.getName());
    private static final String DEFAULT_CONFIG_FILE = "app_config.json";
    private static AppConfig instance;
    
    // Database configuration
    private String dbUrl = "jdbc:sqlite:household_tasks.db";
    private String dbUsername = "";
    private String dbPassword = "";
    
    // File paths
    private String tasksFilePath = "tasks.json";
    private String familyMembersFilePath = "family_members.json";
    
    // Application settings
    private boolean enableDebugMode = false;
    private String logLevel = "INFO";
    private int sessionTimeoutMinutes = 30;
    
    // Default admin credentials
    private String adminUsername = "admin";
    private String adminPassword = "password"; // In a real app, this should be securely stored
    
    // Jackson ObjectMapper for JSON serialization/deserialization
    private transient ObjectMapper mapper;
    
    /**
     * Private constructor for singleton pattern
     */
    private AppConfig() {
        initMapper();
    }
    
    /**
     * Initialize the ObjectMapper
     */
    private void initMapper() {
        mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        mapper.enable(SerializationFeature.INDENT_OUTPUT);
    }
    
    /**
     * Get the singleton instance of AppConfig
     * @return the AppConfig instance
     */
    public static synchronized AppConfig getInstance() {
        if (instance == null) {
            instance = new AppConfig();
            instance.loadConfig();
        }
        return instance;
    }
    
    /**
     * Load configuration from the default config file
     */
    public void loadConfig() {
        loadConfig(new File(DEFAULT_CONFIG_FILE));
    }
    
    /**
     * Load configuration from a specified file
     * @param configFile the configuration file to load from
     */
    public void loadConfig(File configFile) {
        if (!configFile.exists()) {
            LOGGER.info("Configuration file " + configFile.getPath() + 
                " not found. Using default configuration.");
            return;
        }
        
        try {
            if (mapper == null) {
                initMapper();
            }
            
            // Read as a map to avoid overwriting transient fields
            java.util.Map<String, Object> map = mapper.readValue(configFile, java.util.Map.class);
            
            // Update fields from the map
            if (map.containsKey("dbUrl")) this.dbUrl = (String) map.get("dbUrl");
            if (map.containsKey("dbUsername")) this.dbUsername = (String) map.get("dbUsername");
            if (map.containsKey("dbPassword")) this.dbPassword = (String) map.get("dbPassword");
            if (map.containsKey("tasksFilePath")) this.tasksFilePath = (String) map.get("tasksFilePath");
            if (map.containsKey("familyMembersFilePath")) this.familyMembersFilePath = (String) map.get("familyMembersFilePath");
            if (map.containsKey("enableDebugMode")) this.enableDebugMode = (Boolean) map.get("enableDebugMode");
            if (map.containsKey("logLevel")) this.logLevel = (String) map.get("logLevel");
            if (map.containsKey("sessionTimeoutMinutes")) this.sessionTimeoutMinutes = ((Number) map.get("sessionTimeoutMinutes")).intValue();
            if (map.containsKey("adminUsername")) this.adminUsername = (String) map.get("adminUsername");
            if (map.containsKey("adminPassword")) this.adminPassword = (String) map.get("adminPassword");
            
            LOGGER.info("Configuration loaded from " + configFile.getPath());
        } catch (IOException e) {
            LOGGER.log(Level.WARNING, "Error loading configuration: " + e.getMessage(), e);
            LOGGER.info("Using default configuration instead.");
        }
    }
    
    /**
     * Save configuration to the default config file
     * @return true if save was successful, false otherwise
     */
    public boolean saveConfig() {
        return saveConfig(new File(DEFAULT_CONFIG_FILE));
    }
    
    /**
     * Save configuration to a specified file
     * @param configFile the file to save to
     * @return true if save was successful, false otherwise
     */
    public boolean saveConfig(File configFile) {
        try {
            if (mapper == null) {
                initMapper();
            }
            mapper.writeValue(configFile, this);
            LOGGER.info("Configuration saved to " + configFile.getPath());
            return true;
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Error saving configuration: " + e.getMessage(), e);
            return false;
        }
    }
    
    /**
     * Reset configuration to default values
     */
    public void resetToDefaults() {
        dbUrl = "jdbc:sqlite:household_tasks.db";
        dbUsername = "";
        dbPassword = "";
        tasksFilePath = "tasks.json";
        familyMembersFilePath = "family_members.json";
        enableDebugMode = false;
        logLevel = "INFO";
        sessionTimeoutMinutes = 30;
        adminUsername = "admin";
        adminPassword = "password";
    }
    
    /**
     * Get the file object for tasks data based on configuration
     * @return the tasks file
     */
    public File getTasksFile() {
        return Paths.get(tasksFilePath).toFile();
    }
    
    /**
     * Get the file object for family members data based on configuration
     * @return the family members file
     */
    public File getFamilyMembersFile() {
        return Paths.get(familyMembersFilePath).toFile();
    }
    
    // Getters and setters
    
    public String getDbUrl() {
        return dbUrl;
    }
    
    public void setDbUrl(String dbUrl) {
        this.dbUrl = dbUrl;
    }
    
    public String getDbUsername() {
        return dbUsername;
    }
    
    public void setDbUsername(String dbUsername) {
        this.dbUsername = dbUsername;
    }
    
    public String getDbPassword() {
        return dbPassword;
    }
    
    public void setDbPassword(String dbPassword) {
        this.dbPassword = dbPassword;
    }
    
    public String getTasksFilePath() {
        return tasksFilePath;
    }
    
    public void setTasksFilePath(String tasksFilePath) {
        this.tasksFilePath = tasksFilePath;
    }
    
    public String getFamilyMembersFilePath() {
        return familyMembersFilePath;
    }
    
    public void setFamilyMembersFilePath(String familyMembersFilePath) {
        this.familyMembersFilePath = familyMembersFilePath;
    }
    
    public boolean isEnableDebugMode() {
        return enableDebugMode;
    }
    
    public void setEnableDebugMode(boolean enableDebugMode) {
        this.enableDebugMode = enableDebugMode;
    }
    
    public String getLogLevel() {
        return logLevel;
    }
    
    public void setLogLevel(String logLevel) {
        this.logLevel = logLevel;
    }
    
    public int getSessionTimeoutMinutes() {
        return sessionTimeoutMinutes;
    }
    
    public void setSessionTimeoutMinutes(int sessionTimeoutMinutes) {
        this.sessionTimeoutMinutes = sessionTimeoutMinutes;
    }
    
    public String getAdminUsername() {
        return adminUsername;
    }
    
    public void setAdminUsername(String adminUsername) {
        this.adminUsername = adminUsername;
    }
    
    public String getAdminPassword() {
        return adminPassword;
    }
    
    public void setAdminPassword(String adminPassword) {
        this.adminPassword = adminPassword;
    }
} 