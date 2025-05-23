package pl.edu.agh.isi;

import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import pl.edu.agh.isi.cli.CreateTaskCommand;
import pl.edu.agh.isi.cli.AddFamilyMemberCommand;
import pl.edu.agh.isi.cli.RemoveFamilyMemberCommand;
import pl.edu.agh.isi.cli.ListFamilyMembersCommand;
import pl.edu.agh.isi.cli.CompleteTaskCommand;
import pl.edu.agh.isi.cli.ReopenTaskCommand;
import pl.edu.agh.isi.cli.ListTasksCommand;
import pl.edu.agh.isi.cli.AssignTaskCommand;
import pl.edu.agh.isi.cli.UnassignTaskCommand;
import pl.edu.agh.isi.cli.SetPriorityCommand;
import pl.edu.agh.isi.cli.SetRecurringCommand;
import pl.edu.agh.isi.cli.ConfigCommand;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;
import java.io.File;

@Command(
    name = "household-tasks",
    description = "Household Task Management Application",
    subcommands = {
        CreateTaskCommand.class,
        CompleteTaskCommand.class,
        ReopenTaskCommand.class,
        ListTasksCommand.class,
        AddFamilyMemberCommand.class,
        RemoveFamilyMemberCommand.class,
        ListFamilyMembersCommand.class,
        AssignTaskCommand.class,
        UnassignTaskCommand.class,
        SetPriorityCommand.class,
        SetRecurringCommand.class,
        ConfigCommand.class
    },
    mixinStandardHelpOptions = false,
    versionProvider = Main.VersionProvider.class
)
public class Main implements Runnable {
    
    @Option(names = {"-i", "--interactive"}, description = "Run in interactive mode", hidden = true)
    private boolean interactive = false;
    
    @Option(names = {"-h", "--help"}, usageHelp = true, description = "Show help message")
    private boolean helpRequested = false;
    
    public static void main(String[] args) {
        // Initialize configuration
        AppConfig.getInstance();
        
        Main main = new Main();
        CommandLine cmd = new CommandLine(main);
        cmd.setHelpFactory(new CustomHelpFactory());
        
        if (args.length > 0 && (args[0].equals("-i") || args[0].equals("--interactive"))) {
            main.runInteractive(cmd);
        } else {
            int exitCode = cmd.execute(args);
            System.exit(exitCode);
        }
    }

    @Override
    public void run() {
        if (interactive) {
            runInteractive(new CommandLine(this));
        } else {
            CommandLine.usage(this, System.out);
        }
    }
    
    private void runInteractive(CommandLine cmd) {
        System.out.println("Welcome to Household Task Management Application!");
        System.out.println("Available commands:");
        System.out.println("  create \"Task Topic\" [-d \"YYYY-MM-DD HH:MM\"] [-i \"Description\"] [-p PRIORITY] [-r INTERVAL] - Create a new task");
        System.out.println("  complete TASK_ID [-c \"Comment\"]                                 - Mark a task as completed");
        System.out.println("  reopen TASK_ID [-c \"Comment\"]                                   - Reopen a completed task");
        System.out.println("  list [-a | -c] [-d | -r | -P] [-p PRIORITY] [-R]               - List tasks");
        System.out.println("  set-priority TASK_ID PRIORITY                                   - Set task priority (1-low, 2-medium, 3-high)");
        System.out.println("  set-recurring TASK_ID INTERVAL [-n OCCURRENCES] [-e \"END_DATE\"] - Set task as recurring");
        System.out.println("  assign TASK_ID MEMBER_ID                                        - Assign task to a family member");
        System.out.println("  unassign TASK_ID                                                - Unassign task from family member");
        System.out.println("  add-member \"Name\"                                               - Add a new family member");
        System.out.println("  remove-member ID                                                - Remove a family member");
        System.out.println("  list-members                                                    - List all family members");
        System.out.println("  config [-s] [-r] [-t PATH] [-m PATH]                            - View or modify configuration");
        System.out.println("  help                                                            - Show available commands");
        System.out.println("  exit                                                            - Quit the application");
        System.out.println();
        
        // Get configuration
        AppConfig config = AppConfig.getInstance();
        
        // Get task and family member files from configuration
        File tasksFile = config.getTasksFile();
        File familyMembersFile = config.getFamilyMembersFile();
        
        if (!tasksFile.exists()) {
            try {
                tasksFile.createNewFile();
                System.out.println("Created new tasks file: " + tasksFile.getPath());
            } catch (IOException e) {
                System.err.println("Warning: Could not create tasks file: " + e.getMessage());
            }
        }
        
        if (!familyMembersFile.exists()) {
            try {
                familyMembersFile.createNewFile();
                System.out.println("Created new family members file: " + familyMembersFile.getPath());
            } catch (IOException e) {
                System.err.println("Warning: Could not create family members file: " + e.getMessage());
            }
        }
        
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        boolean running = true;
        
        while (running) {
            try {
                System.out.print("> ");
                System.out.flush();
                String input = reader.readLine();
                
                if (input == null) {
                    running = false;
                    continue;
                }
                
                input = input.trim();
                
                if (input.equalsIgnoreCase("exit") || input.equalsIgnoreCase("quit")) {
                    running = false;
                    System.out.println("Goodbye!");
                } else if (input.equalsIgnoreCase("help")) {
                    System.out.println("Available commands:");
                    System.out.println("  create \"Task Topic\" [-d \"YYYY-MM-DD HH:MM\"] [-i \"Description\"] [-p PRIORITY] [-r INTERVAL] - Create a new task");
                    System.out.println("  complete TASK_ID [-c \"Comment\"]                                 - Mark a task as completed");
                    System.out.println("  reopen TASK_ID [-c \"Comment\"]                                   - Reopen a completed task");
                    System.out.println("  list [-a | -c] [-d | -r | -P] [-p PRIORITY] [-R]               - List tasks");
                    System.out.println("  set-priority TASK_ID PRIORITY                                   - Set task priority (1-low, 2-medium, 3-high)");
                    System.out.println("  set-recurring TASK_ID INTERVAL [-n OCCURRENCES] [-e \"END_DATE\"] - Set task as recurring");
                    System.out.println("  assign TASK_ID MEMBER_ID                                        - Assign task to a family member");
                    System.out.println("  unassign TASK_ID                                                - Unassign task from family member");
                    System.out.println("  add-member \"Name\"                                               - Add a new family member");
                    System.out.println("  remove-member ID                                                - Remove a family member");
                    System.out.println("  list-members                                                    - List all family members");
                    System.out.println("  config [-s] [-r] [-t PATH] [-m PATH]                            - View or modify configuration");
                    System.out.println("  help                                                            - Show this help message");
                    System.out.println("  exit                                                            - Quit the application");
                    System.out.println();
                    System.out.println("Examples:");
                    System.out.println("  create \"Clean kitchen\"                       - Create a simple task");
                    System.out.println("  create \"Pay bills\" -p 3                      - Create a high priority task");
                    System.out.println("  create \"Take out trash\" -r WEEKLY            - Create a weekly recurring task");
                    System.out.println("  complete 1 -c \"Cleaned thoroughly\"            - Mark task as completed with comment");
                    System.out.println("  list -p 3                                    - List high priority tasks");
                    System.out.println("  list -P                                      - List tasks sorted by priority");
                    System.out.println("  list -R                                      - List only recurring tasks");
                    System.out.println("  set-priority 1 3                             - Set task 1 to high priority");
                    System.out.println("  set-recurring 1 WEEKLY                       - Set task 1 as weekly recurring");
                    System.out.println("  assign 1 2                                    - Assign task 1 to family member 2");
                    System.out.println("  unassign 1                                    - Unassign task 1");
                    System.out.println("  add-member \"John Smith\"                       - Add John as a family member");
                    System.out.println("  remove-member 1                               - Remove family member with ID 1");
                    System.out.println("  list-members                                  - List all family members");
                    System.out.println("  config -t data/tasks.json -m data/members.json -s - Set paths and save config");
                } else if (!input.isEmpty()) {
                    String[] cmdArgs = parseCommandLine(input);
                    cmd.execute(cmdArgs);
                }
            } catch (IOException e) {
                System.err.println("Error reading input: " + e.getMessage());
                running = false;
            }
        }
    }
    
    private String[] parseCommandLine(String input) {
        java.util.List<String> args = new java.util.ArrayList<>();
        StringBuilder currentArg = new StringBuilder();
        boolean inQuotes = false;
        
        for (char c : input.toCharArray()) {
            if (c == '"' || c == '\'') {
                inQuotes = !inQuotes;
            } else if (c == ' ' && !inQuotes) {
                if (currentArg.length() > 0) {
                    args.add(currentArg.toString());
                    currentArg.setLength(0);
                }
            } else {
                currentArg.append(c);
            }
        }
        
        if (currentArg.length() > 0) {
            args.add(currentArg.toString());
        }
        
        return args.toArray(new String[0]);
    }
    
    public static class VersionProvider implements CommandLine.IVersionProvider {
        @Override
        public String[] getVersion() {
            return new String[] { "1.0" };
        }
    }
    
    static class CustomHelpFactory implements CommandLine.IHelpFactory {
        @Override
        public CommandLine.Help create(CommandLine.Model.CommandSpec commandSpec, CommandLine.Help.ColorScheme colorScheme) {
            return new CommandLine.Help(commandSpec, colorScheme);
        }
    }
}