package pl.edu.agh.isi.cli;

import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;

import java.io.File;
import java.util.Optional;
import java.util.concurrent.Callable;

import pl.edu.agh.isi.Task;
import pl.edu.agh.isi.TaskPriority;
import pl.edu.agh.isi.TaskRepository;
import pl.edu.agh.isi.TaskService;

@Command(
    name = "set-priority",
    description = "Set the priority of a task",
    mixinStandardHelpOptions = false
)
public class SetPriorityCommand implements Callable<Integer> {
    
    @Parameters(index = "0", description = "Task ID", arity = "1")
    protected String taskId;
    
    @Parameters(index = "1", description = "Priority level (1-low, 2-medium, 3-high)", arity = "1")
    protected String priorityLevel;
    
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
            
            TaskService service = createTaskService(tasksFile);
            
            // First check if the task exists
            Optional<Task> taskOpt = service.getTask(taskId);
            if (!taskOpt.isPresent()) {
                System.err.println("Error: Task with ID " + taskId + " not found");
                return 1;
            }
            
            // Validate the priority level
            int level;
            try {
                level = Integer.parseInt(priorityLevel);
                if (level < 1 || level > 3) {
                    System.err.println("Error: Priority level must be between 1 and 3");
                    System.err.println("1 - Low, 2 - Medium, 3 - High");
                    return 1;
                }
            } catch (NumberFormatException e) {
                System.err.println("Error: Priority must be a number between 1 and 3");
                return 1;
            }
            
            // Set the priority
            TaskPriority priority = TaskPriority.fromLevel(level);
            Task task = service.setPriority(taskId, priority);
            
            System.out.println("Task '" + task.getTopic() + "' (ID: " + task.getId() + ") priority set to: " + 
                priority.getDisplayName() + " " + priority.getSymbol());
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
        System.out.println("Usage: set-priority TASK_ID PRIORITY_LEVEL");
        System.out.println();
        System.out.println("Priority Levels:");
        System.out.println("  1 - Low");
        System.out.println("  2 - Medium");
        System.out.println("  3 - High");
        System.out.println();
        System.out.println("Examples:");
        System.out.println("  set-priority 1 3        - Set task with ID 1 to high priority");
        System.out.println("  set-priority 2 1        - Set task with ID 2 to low priority");
        System.out.println();
        System.out.println("Options:");
        System.out.println("  -h, --help              Show this help message");
    }
    
    // Protected method for better testability
    protected TaskService createTaskService(File file) {
        TaskRepository repository = new TaskRepository(file);
        return new TaskService(repository);
    }
} 