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
import pl.edu.agh.isi.RecurrenceConfig;
import pl.edu.agh.isi.RecurrenceInterval;
import pl.edu.agh.isi.RecurringTaskService;

@Command(
    name = "create",
    description = "Create a new task",
    mixinStandardHelpOptions = false
)
public class CreateTaskCommand implements Callable<Integer> {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
    
    @Parameters(index = "0", description = "Task topic (name)", arity = "1")
    protected String topic;
    
    @Option(names = {"-d", "--due-date"}, description = "Due date (format: yyyy-MM-dd HH:mm)")
    protected String dueDateStr;
    
    @Option(names = {"-i", "--description"}, description = "Task description")
    protected String description = "";
    
    @Option(names = {"-p", "--priority"}, description = "Task priority (1-low, 2-medium, 3-high)")
    protected String priorityStr;
    
    @Option(names = {"-r", "--recurring"}, description = "Recurrence interval (DAILY, WEEKLY, MONTHLY)")
    protected String recurringInterval;
    
    @Option(names = {"-n", "--occurrences"}, description = "Number of recurrences (for recurring tasks)")
    protected Integer occurrences;
    
    @Option(names = {"-e", "--end-date"}, description = "End date for recurrence (format: yyyy-MM-dd HH:mm)")
    protected String endDateStr;
    
    @Option(names = {"-f", "--file"}, description = "Tasks data file", defaultValue = "tasks.json", hidden = true)
    protected File tasksFile;
    
    @Option(names = {"-h", "--help"}, usageHelp = true, description = "Show help message")
    protected boolean helpRequested = false;

    @Override
    public Integer call() {
        try {
            if (helpRequested) {
                showExamples();
                return 0;
            }
            
            TaskRepository repository = new TaskRepository(tasksFile);
            TaskService service = createTaskService(tasksFile);
            
            // Set up RecurringTaskService if needed
            RecurringTaskService recurringTaskService = null;
            if (recurringInterval != null && !recurringInterval.isEmpty()) {
                recurringTaskService = new RecurringTaskService(repository);
                service.setRecurringTaskService(recurringTaskService);
            }
            
            // Parse due date
            LocalDateTime dueDate = null;
            if (dueDateStr != null && !dueDateStr.isEmpty()) {
                try {
                    dueDate = LocalDateTime.parse(dueDateStr, DATE_FORMATTER);
                } catch (DateTimeParseException e) {
                    System.err.println("Invalid date format for due date. Please use: yyyy-MM-dd HH:mm");
                    System.err.println("Example: 2024-12-31 14:30");
                    return 1;
                }
            }
            
            // Parse recurrence end date if specified
            LocalDateTime endDate = null;
            if (endDateStr != null && !endDateStr.isEmpty()) {
                try {
                    endDate = LocalDateTime.parse(endDateStr, DATE_FORMATTER);
                } catch (DateTimeParseException e) {
                    System.err.println("Invalid date format for end date. Please use: yyyy-MM-dd HH:mm");
                    System.err.println("Example: 2024-12-31 14:30");
                    return 1;
                }
            }
            
            // Parse priority
            TaskPriority priority = TaskPriority.MEDIUM; // Default
            if (priorityStr != null && !priorityStr.isEmpty()) {
                try {
                    int priorityLevel = Integer.parseInt(priorityStr);
                    if (priorityLevel < 1 || priorityLevel > 3) {
                        System.err.println("Invalid priority level. Please use: 1 (low), 2 (medium), or 3 (high)");
                        return 1;
                    }
                    priority = TaskPriority.fromLevel(priorityLevel);
                } catch (NumberFormatException e) {
                    System.err.println("Invalid priority format. Please use: 1 (low), 2 (medium), or 3 (high)");
                    return 1;
                }
            }
            
            // Create the task
            Task task;
            
            // Handle recurring task creation
            if (recurringInterval != null && !recurringInterval.isEmpty()) {
                RecurrenceInterval interval = RecurrenceInterval.fromString(recurringInterval);
                if (interval == null) {
                    System.err.println("Invalid recurrence interval. Please use: DAILY, WEEKLY, or MONTHLY");
                    return 1;
                }
                
                if (dueDate == null) {
                    System.err.println("Error: Due date is required for recurring tasks");
                    return 1;
                }
                
                RecurrenceConfig config;
                if (occurrences != null && endDate != null) {
                    config = new RecurrenceConfig(interval, occurrences, endDate);
                } else if (occurrences != null) {
                    config = new RecurrenceConfig(interval, occurrences);
                } else if (endDate != null) {
                    config = new RecurrenceConfig(interval, endDate);
                } else {
                    config = new RecurrenceConfig(interval);
                }
                
                task = service.createRecurringTask(topic, dueDate, description, priority, config);
                System.out.println("Recurring task created successfully with ID: " + task.getId());
                System.out.println("Priority: " + priority.getDisplayName());
                System.out.println("Recurrence: " + config.toString());
            } else {
                // Create a normal task
                task = service.createTask(topic, dueDate, description, priority);
                System.out.println("Task created successfully with ID: " + task.getId());
                System.out.println("Priority: " + priority.getDisplayName());
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
        System.out.println("Usage: create \"Task Topic\" [-d \"YYYY-MM-DD HH:MM\"] [-i \"Description\"] [-p PRIORITY] [-r INTERVAL] [-n OCCURRENCES] [-e \"END_DATE\"]");
        System.out.println();
        System.out.println("Examples:");
        System.out.println("  create \"Clean kitchen\"                       - Create a simple task");
        System.out.println("  create \"Buy groceries\" -d \"2024-12-30 18:00\" - Create a task with due date");
        System.out.println("  create \"Fix car\" -i \"Check engine light\"     - Create a task with description");
        System.out.println("  create \"Pay bills\" -p 3                      - Create a high priority task");
        System.out.println("  create \"Take out trash\" -d \"2024-08-01 18:00\" -r WEEKLY  - Create a weekly recurring task");
        System.out.println("  create \"Pay rent\" -d \"2024-08-01 10:00\" -r MONTHLY -n 12  - Create a monthly task for 1 year");
        System.out.println();
        System.out.println("Options:");
        System.out.println("  -d, --due-date \"YYYY-MM-DD HH:MM\"  Set the due date for the task");
        System.out.println("  -i, --description \"text\"          Add a description to the task");
        System.out.println("  -p, --priority NUMBER             Set the priority (1-low, 2-medium, 3-high)");
        System.out.println("  -r, --recurring INTERVAL          Set recurrence (DAILY, WEEKLY, MONTHLY)");
        System.out.println("  -n, --occurrences NUMBER          Limit the number of recurrences");
        System.out.println("  -e, --end-date \"YYYY-MM-DD HH:MM\" Set an end date for recurrences");
        System.out.println("  -h, --help                        Show this help message");
    }
    
    // Protected method for better testability
    protected TaskService createTaskService(File file) {
        TaskRepository repository = new TaskRepository(file);
        return new TaskService(repository);
    }
} 