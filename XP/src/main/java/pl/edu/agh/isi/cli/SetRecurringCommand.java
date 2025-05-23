package pl.edu.agh.isi.cli;

import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;

import java.io.File;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Optional;
import java.util.concurrent.Callable;

import pl.edu.agh.isi.Task;
import pl.edu.agh.isi.TaskRepository;
import pl.edu.agh.isi.TaskService;
import pl.edu.agh.isi.RecurrenceConfig;
import pl.edu.agh.isi.RecurrenceInterval;
import pl.edu.agh.isi.RecurringTaskService;

@Command(
    name = "set-recurring",
    description = "Set a task as recurring",
    mixinStandardHelpOptions = false
)
public class SetRecurringCommand implements Callable<Integer> {
    
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
    
    @Parameters(index = "0", description = "Task ID", arity = "1")
    protected String taskId;
    
    @Parameters(index = "1", description = "Recurrence interval (DAILY, WEEKLY, MONTHLY)", arity = "1")
    protected String intervalStr;
    
    @Option(names = {"-n", "--occurrences"}, description = "Number of occurrences")
    protected Integer occurrences;
    
    @Option(names = {"-e", "--end-date"}, description = "End date (format: yyyy-MM-dd HH:mm)")
    protected String endDateStr;
    
    @Option(names = {"-f", "--file"}, description = "Tasks data file", defaultValue = "tasks.json", hidden = true)
    protected File tasksFile;
    
    @Option(names = {"-h", "--help"}, usageHelp = true, description = "Show help message")
    protected boolean helpRequested = false;
    
    @Override
    public Integer call() throws Exception {
        try {
            if (helpRequested) {
                showExamples();
                return 0;
            }
            
            // Create services
            TaskRepository taskRepository = new TaskRepository(tasksFile);
            RecurringTaskService recurringTaskService = new RecurringTaskService(taskRepository);
            TaskService taskService = createTaskService(tasksFile);
            taskService.setRecurringTaskService(recurringTaskService);
            
            // First check if the task exists
            Optional<Task> taskOpt = taskService.getTask(taskId);
            if (!taskOpt.isPresent()) {
                System.err.println("Error: Task with ID " + taskId + " not found");
                return 1;
            }
            
            Task task = taskOpt.get();
            
            // Parse the interval
            RecurrenceInterval interval = RecurrenceInterval.fromString(intervalStr);
            if (interval == null) {
                System.err.println("Error: Invalid interval. Please use: DAILY, WEEKLY, or MONTHLY");
                return 1;
            }
            
            // Parse the end date if provided
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
            
            // Create recurrence configuration
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
            
            // Set the task as recurring
            try {
                task = taskService.setTaskAsRecurring(taskId, config);
                
                System.out.println("Task '" + task.getTopic() + "' (ID: " + task.getId() + 
                        ") set as recurring: " + config.toString());
                
                if (task.getDueDate() == null) {
                    System.out.println("Warning: Task has no due date set. The recurrence will not be fully effective without a due date.");
                }
                
                return 0;
            } catch (IllegalArgumentException e) {
                System.err.println("Error: " + e.getMessage());
                return 1;
            }
            
        } catch (Exception e) {
            System.err.println("Unexpected error: " + e.getMessage());
            e.printStackTrace();
            return 2;
        }
    }
    
    private void showExamples() {
        System.out.println("Usage: set-recurring TASK_ID INTERVAL [-n OCCURRENCES] [-e \"END_DATE\"]");
        System.out.println();
        System.out.println("Intervals:");
        System.out.println("  DAILY   - Task repeats every day");
        System.out.println("  WEEKLY  - Task repeats every week");
        System.out.println("  MONTHLY - Task repeats every month");
        System.out.println();
        System.out.println("Examples:");
        System.out.println("  set-recurring 1 DAILY                       - Task repeats daily indefinitely");
        System.out.println("  set-recurring 2 WEEKLY -n 10                - Task repeats weekly for 10 occurrences");
        System.out.println("  set-recurring 3 MONTHLY -e \"2024-12-31 23:59\" - Task repeats monthly until end of 2024");
        System.out.println();
        System.out.println("Options:");
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