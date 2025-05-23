package pl.edu.agh.isi;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.nio.file.Path;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("AppConfig")
class AppConfigTest {
    
    private AppConfig config;
    private File configFile;
    
    @TempDir
    Path tempDir;
    
    @BeforeEach
    void setUp() {
        // Force a new instance for each test by reflection
        try {
            java.lang.reflect.Field instanceField = AppConfig.class.getDeclaredField("instance");
            instanceField.setAccessible(true);
            instanceField.set(null, null);
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        config = AppConfig.getInstance();
        configFile = tempDir.resolve("test_config.json").toFile();
    }
    
    @AfterEach
    void tearDown() {
        // Delete the config file if it exists
        if (configFile.exists()) {
            configFile.delete();
        }
    }
    
    @Test
    @DisplayName("should save and load configuration correctly")
    void shouldSaveAndLoadConfigurationCorrectly() {
        // Modify some settings
        config.setTasksFilePath("custom_tasks.json");
        config.setFamilyMembersFilePath("custom_members.json");
        config.setEnableDebugMode(true);
        config.setLogLevel("DEBUG");
        
        // Save to the temporary file
        assertTrue(config.saveConfig(configFile));
        assertTrue(configFile.exists());
        
        // Reset the instance to force a new load
        try {
            java.lang.reflect.Field instanceField = AppConfig.class.getDeclaredField("instance");
            instanceField.setAccessible(true);
            instanceField.set(null, null);
        } catch (Exception e) {
            e.printStackTrace();
            fail("Failed to reset AppConfig instance");
        }
        
        // Get a new instance and load from the file
        AppConfig newConfig = AppConfig.getInstance();
        newConfig.loadConfig(configFile);
        
        // Verify the settings were loaded correctly
        assertEquals("custom_tasks.json", newConfig.getTasksFilePath());
        assertEquals("custom_members.json", newConfig.getFamilyMembersFilePath());
        assertTrue(newConfig.isEnableDebugMode());
        assertEquals("DEBUG", newConfig.getLogLevel());
    }
    
    @Test
    @DisplayName("should provide default values when no config file exists")
    void shouldProvideDefaultValuesWhenNoConfigFileExists() {
        File nonExistentFile = tempDir.resolve("non_existent.json").toFile();
        
        // Create a new instance
        try {
            java.lang.reflect.Field instanceField = AppConfig.class.getDeclaredField("instance");
            instanceField.setAccessible(true);
            instanceField.set(null, null);
        } catch (Exception e) {
            e.printStackTrace();
            fail("Failed to reset AppConfig instance");
        }
        
        AppConfig newConfig = AppConfig.getInstance();
        newConfig.loadConfig(nonExistentFile);
        
        // Verify default values
        assertEquals("tasks.json", newConfig.getTasksFilePath());
        assertEquals("family_members.json", newConfig.getFamilyMembersFilePath());
        assertFalse(newConfig.isEnableDebugMode());
        assertEquals("INFO", newConfig.getLogLevel());
        assertEquals(30, newConfig.getSessionTimeoutMinutes());
    }
    
    @Test
    @DisplayName("should reset configuration to defaults")
    void shouldResetConfigurationToDefaults() {
        // Modify some settings
        config.setTasksFilePath("custom_tasks.json");
        config.setFamilyMembersFilePath("custom_members.json");
        config.setEnableDebugMode(true);
        config.setLogLevel("DEBUG");
        
        // Reset to defaults
        config.resetToDefaults();
        
        // Verify default values
        assertEquals("tasks.json", config.getTasksFilePath());
        assertEquals("family_members.json", config.getFamilyMembersFilePath());
        assertFalse(config.isEnableDebugMode());
        assertEquals("INFO", config.getLogLevel());
    }
    
    @Test
    @DisplayName("should return file objects for tasks and members")
    void shouldReturnFileObjectsForTasksAndMembers() {
        // Set custom paths
        config.setTasksFilePath("data/custom_tasks.json");
        config.setFamilyMembersFilePath("data/custom_members.json");
        
        // Get file objects
        File tasksFile = config.getTasksFile();
        File membersFile = config.getFamilyMembersFile();
        
        // Verify paths
        assertEquals("data/custom_tasks.json", tasksFile.getPath().replace('\\', '/'));
        assertEquals("data/custom_members.json", membersFile.getPath().replace('\\', '/'));
    }
} 