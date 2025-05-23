package pl.edu.agh.isi.cli;

import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;

import java.io.File;
import java.util.concurrent.Callable;

import pl.edu.agh.isi.AppConfig;

@Command(
    name = "config",
    description = "Display or modify application configuration",
    mixinStandardHelpOptions = false
)
public class ConfigCommand implements Callable<Integer> {
    
    @Option(names = {"-s", "--save"}, description = "Save the current configuration to file")
    private boolean saveConfig = false;
    
    @Option(names = {"-r", "--reset"}, description = "Reset configuration to defaults")
    private boolean resetConfig = false;
    
    @Option(names = {"-f", "--file"}, description = "Configuration file path", defaultValue = "app_config.json")
    private File configFile;
    
    @Option(names = {"-t", "--tasks-file"}, description = "Set the tasks file path")
    private String tasksFilePath;
    
    @Option(names = {"-m", "--members-file"}, description = "Set the family members file path")
    private String familyMembersFilePath;
    
    @Option(names = {"-d", "--debug"}, description = "Enable debug mode")
    private Boolean debugMode;
    
    @Option(names = {"-h", "--help"}, usageHelp = true, description = "Show help message")
    private boolean helpRequested = false;
    
    @Override
    public Integer call() throws Exception {
        try {
            if (helpRequested) {
                showExamples();
                return 0;
            }
            
            AppConfig config = AppConfig.getInstance();
            
            // Handle reset first, if requested
            if (resetConfig) {
                config.resetToDefaults();
                System.out.println("Configuration reset to defaults.");
            }
            
            // Apply any specified changes
            if (tasksFilePath != null) {
                config.setTasksFilePath(tasksFilePath);
                System.out.println("Tasks file path set to: " + tasksFilePath);
            }
            
            if (familyMembersFilePath != null) {
                config.setFamilyMembersFilePath(familyMembersFilePath);
                System.out.println("Family members file path set to: " + familyMembersFilePath);
            }
            
            if (debugMode != null) {
                config.setEnableDebugMode(debugMode);
                System.out.println("Debug mode " + (debugMode ? "enabled" : "disabled"));
            }
            
            // Save if requested
            if (saveConfig) {
                boolean saved = config.saveConfig(configFile);
                if (saved) {
                    System.out.println("Configuration saved to " + configFile.getPath());
                } else {
                    System.err.println("Failed to save configuration to " + configFile.getPath());
                    return 1;
                }
            }
            
            // If no specific action was requested, display the current config
            if (!saveConfig && !resetConfig && tasksFilePath == null && 
                familyMembersFilePath == null && debugMode == null) {
                displayConfig(config);
            }
            
            return 0;
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
            e.printStackTrace();
            return 1;
        }
    }
    
    private void displayConfig(AppConfig config) {
        System.out.println("=== Current Application Configuration ===");
        System.out.println("Tasks file path: " + config.getTasksFilePath());
        System.out.println("Family members file path: " + config.getFamilyMembersFilePath());
        System.out.println("Database URL: " + config.getDbUrl());
        System.out.println("Debug mode: " + (config.isEnableDebugMode() ? "Enabled" : "Disabled"));
        System.out.println("Log level: " + config.getLogLevel());
        System.out.println("Session timeout (minutes): " + config.getSessionTimeoutMinutes());
        System.out.println("Admin username: " + config.getAdminUsername());
        System.out.println("Admin password: " + (config.getAdminPassword().isEmpty() ? "Not set" : "********"));
    }
    
    private void showExamples() {
        System.out.println("Usage: config [-s] [-r] [-t PATH] [-m PATH] [-d true|false]");
        System.out.println();
        System.out.println("Examples:");
        System.out.println("  config                       - Display current configuration");
        System.out.println("  config -s                    - Save current configuration to file");
        System.out.println("  config -r                    - Reset configuration to defaults");
        System.out.println("  config -t custom_tasks.json  - Set tasks file path");
        System.out.println("  config -d true               - Enable debug mode");
        System.out.println("  config -r -s                 - Reset to defaults and save");
        System.out.println();
        System.out.println("Options:");
        System.out.println("  -s, --save                   Save configuration to file");
        System.out.println("  -r, --reset                  Reset configuration to defaults");
        System.out.println("  -f, --file PATH              Specify configuration file path");
        System.out.println("  -t, --tasks-file PATH        Set the tasks file path");
        System.out.println("  -m, --members-file PATH      Set the family members file path");
        System.out.println("  -d, --debug true|false       Enable or disable debug mode");
        System.out.println("  -h, --help                   Show this help message");
    }
} 