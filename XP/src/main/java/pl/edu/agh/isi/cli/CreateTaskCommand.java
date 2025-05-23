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
import pl.edu.agh.isi.TaskRepository;
import pl.edu.agh.isi.TaskService;

@Command(
    name = "create",
    description = "Create a new task",
    mixinStandardHelpOptions = true
)
public class CreateTaskCommand implements Callable<Integer> {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
    
    @Parameters(index = "0", description = "Task topic (name)")
    private String topic;
    
    @Option(names = {"-d", "--due-date"}, description = "Due date (format: yyyy-MM-dd HH:mm)")
    private String dueDateStr;
    
    @Option(names = {"-i", "--description"}, description = "Task description")
    private String description = "";
    
    @Option(names = {"-f", "--file"}, description = "Tasks data file", defaultValue = "tasks.json")
    private File tasksFile;

    @Override
    public Integer call() {
        try {
            TaskRepository repository = new TaskRepository(tasksFile);
            TaskService service = new TaskService(repository);
            
            LocalDateTime dueDate = null;
            if (dueDateStr != null && !dueDateStr.isEmpty()) {
                try {
                    dueDate = LocalDateTime.parse(dueDateStr, DATE_FORMATTER);
                } catch (DateTimeParseException e) {
                    System.err.println("Invalid date format. Please use: yyyy-MM-dd HH:mm");
                    return 1;
                }
            }
            
            Task task = service.createTask(topic, dueDate, description);
            System.out.println("Task created successfully with ID: " + task.getId());
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
} 