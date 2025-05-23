package pl.edu.agh.isi.cli;

import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;

import java.io.File;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.concurrent.Callable;

import pl.edu.agh.isi.Task;
import pl.edu.agh.isi.TaskPriority;
import pl.edu.agh.isi.TaskRepository;
import pl.edu.agh.isi.TaskService;

@Command(
    name = "create",
    description = "Create a new task",
    mixinStandardHelpOptions = false
)
public class CreateTaskCommand implements Callable<Integer> {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
    
    @Parameters(index = "0", description = "Task topic (name)", arity = "1")
    private String topic;
    
    @Option(names = {"-d", "--due-date"}, description = "Due date (format: yyyy-MM-dd HH:mm)")
    private String dueDateStr;
    
    @Option(names = {"-i", "--description"}, description = "Task description")
    private String description = "";
    
    @Option(names = {"-p", "--priority"}, description = "Task priority (1-low, 2-medium, 3-high)")
    private String priorityStr;
    
    @Option(names = {"-f", "--file"}, description = "Tasks data file", defaultValue = "tasks.json", hidden = true)
    private File tasksFile;
    
    @Option(names = {"-h", "--help"}, usageHelp = true, description = "Show help message")
    private boolean helpRequested = false;

    @Override
    public Integer call() {
        try {
            if (helpRequested) {
                showExamples();
                return 0;
            }
            
            TaskRepository repository = new TaskRepository(tasksFile);
            TaskService service = new TaskService(repository);
            
            LocalDateTime dueDate = null;
            if (dueDateStr != null && !dueDateStr.isEmpty()) {
                try {
                    dueDate = LocalDateTime.parse(dueDateStr, DATE_FORMATTER);
                } catch (DateTimeParseException e) {
                    System.err.println("Invalid date format. Please use: yyyy-MM-dd HH:mm");
                    System.err.println("Example: 2024-12-31 14:30");
                    return 1;
                }
            }
            
            Task task;
            if (priorityStr != null && !priorityStr.isEmpty()) {
                try {
                    int priorityLevel = Integer.parseInt(priorityStr);
                    if (priorityLevel < 1 || priorityLevel > 3) {
                        System.err.println("Invalid priority level. Please use: 1 (low), 2 (medium), or 3 (high)");
                        return 1;
                    }
                    TaskPriority priority = TaskPriority.fromLevel(priorityLevel);
                    task = service.createTask(topic, dueDate, description, priority);
                    System.out.println("Task created successfully with ID: " + task.getId() + " and priority: " + priority.getDisplayName());
                } catch (NumberFormatException e) {
                    System.err.println("Invalid priority format. Please use: 1 (low), 2 (medium), or 3 (high)");
                    return 1;
                }
            } else {
                task = service.createTask(topic, dueDate, description);
                System.out.println("Task created successfully with ID: " + task.getId() + " and default priority: " + TaskPriority.MEDIUM.getDisplayName());
            }
            
            return 0;
        } catch (IllegalArgumentException e) {
            System.err.println("Error: " + e.getMessage());
            return 1;
        } catch (Exception e) {
            System.err.println("Unexpected error: " + e.getMessage());
            e.printStackTrace();
            return 2;
        }
    }
    
    private void showExamples() {
        System.out.println("Usage: create \"Task Topic\" [-d \"YYYY-MM-DD HH:MM\"] [-i \"Description\"] [-p PRIORITY]");
        System.out.println();
        System.out.println("Examples:");
        System.out.println("  create \"Clean kitchen\"                       - Create a simple task");
        System.out.println("  create \"Buy groceries\" -d \"2024-12-30 18:00\" - Create a task with due date");
        System.out.println("  create \"Fix car\" -i \"Check engine light\"     - Create a task with description");
        System.out.println("  create \"Pay bills\" -p 3                      - Create a high priority task");
        System.out.println();
        System.out.println("Options:");
        System.out.println("  -d, --due-date \"YYYY-MM-DD HH:MM\"  Set the due date for the task");
        System.out.println("  -i, --description \"text\"          Add a description to the task");
        System.out.println("  -p, --priority NUMBER             Set the priority (1-low, 2-medium, 3-high)");
        System.out.println("  -h, --help                         Show this help message");
    }
} 