package pl.edu.agh.isi.cli;

import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

import java.io.File;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.concurrent.Callable;

import pl.edu.agh.isi.Task;
import pl.edu.agh.isi.TaskRepository;
import pl.edu.agh.isi.TaskService;

@Command(
    name = "list",
    description = "List tasks",
    mixinStandardHelpOptions = false
)
public class ListTasksCommand implements Callable<Integer> {
    
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
    
    @Option(names = {"-a", "--all"}, description = "Show all tasks including completed ones")
    protected boolean showAll = false;
    
    @Option(names = {"-c", "--completed"}, description = "Show only completed tasks")
    protected boolean showOnlyCompleted = false;
    
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
            
            TaskRepository repository = new TaskRepository(tasksFile);
            TaskService service = new TaskService(repository);
            
            List<Task> tasks;
            if (showAll) {
                tasks = service.getAllTasks();
            } else if (showOnlyCompleted) {
                tasks = service.getCompletedTasks();
            } else {
                tasks = service.getActiveTasks();
            }
            
            if (tasks.isEmpty()) {
                System.out.println("No tasks found");
                return 0;
            }
            
            String heading;
            if (showAll) {
                heading = "All Tasks";
            } else if (showOnlyCompleted) {
                heading = "Completed Tasks";
            } else {
                heading = "Active Tasks";
            }
            
            System.out.println(heading + ":");
            System.out.println("--------------------------------------------------");
            System.out.println("ID | Status | Topic");
            System.out.println("--------------------------------------------------");
            
            for (Task task : tasks) {
                String status = task.isCompleted() ? "✓" : " ";
                System.out.printf("%-2d | %-6s | %s%n", task.getId(), status, task.getTopic());
            }
            
            System.out.println("--------------------------------------------------");
            System.out.println("Total: " + tasks.size() + " task(s)");
            System.out.println();
            System.out.println("Use 'list -a' to show all tasks, 'list -c' to show only completed tasks");
            
            return 0;
        } catch (Exception e) {
            System.err.println("Unexpected error: " + e.getMessage());
            e.printStackTrace();
            return 2;
        }
    }
    
    private void showExamples() {
        System.out.println("Usage: list [-a | -c]");
        System.out.println();
        System.out.println("Examples:");
        System.out.println("  list          - List active (non-completed) tasks");
        System.out.println("  list -a       - List all tasks, including completed ones");
        System.out.println("  list -c       - List only completed tasks");
        System.out.println();
        System.out.println("Options:");
        System.out.println("  -a, --all                 Show all tasks including completed ones");
        System.out.println("  -c, --completed           Show only completed tasks");
        System.out.println("  -h, --help                Show this help message");
    }
} 