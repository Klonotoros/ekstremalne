package pl.edu.agh.isi.cli;

import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;

import java.io.File;
import java.util.concurrent.Callable;

import pl.edu.agh.isi.Task;
import pl.edu.agh.isi.TaskRepository;
import pl.edu.agh.isi.TaskService;

@Command(
    name = "complete",
    description = "Mark a task as completed",
    mixinStandardHelpOptions = false
)
public class CompleteTaskCommand implements Callable<Integer> {
    
    @Parameters(index = "0", description = "Task ID", arity = "1")
    protected String id;
    
    @Option(names = {"-c", "--comment"}, description = "Completion comment")
    protected String comment;
    
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
            
            if (service.getTask(id).isPresent()) {
                Task task = service.markTaskAsCompleted(id, comment);
                System.out.println("Task '" + task.getTopic() + "' (ID: " + task.getId() + ") marked as completed");
                if (comment != null && !comment.trim().isEmpty()) {
                    System.out.println("Added comment: " + comment);
                }
                return 0;
            } else {
                System.err.println("Error: Task with ID " + id + " not found");
                return 1;
            }
        } catch (IllegalArgumentException e) {
            System.err.println("Error: " + e.getMessage());
            return 1;
        } catch (Exception e) {
            System.err.println("Unexpected error: " + e.getMessage());
            e.printStackTrace();
            return 2;
        }
    }
    
    // Protected method for better testability
    protected TaskService createTaskService(File file) {
        TaskRepository repository = new TaskRepository(file);
        return new TaskService(repository);
    }
    
    private void showExamples() {
        System.out.println("Usage: complete TASK_ID [-c \"Comment\"]");
        System.out.println();
        System.out.println("Examples:");
        System.out.println("  complete 1                               - Mark task 1 as completed");
        System.out.println("  complete 2 -c \"Fixed the issue\"          - Mark task 2 as completed with a comment");
        System.out.println();
        System.out.println("Options:");
        System.out.println("  -c, --comment \"text\"                    Add a completion comment");
        System.out.println("  -h, --help                               Show this help message");
    }
} 