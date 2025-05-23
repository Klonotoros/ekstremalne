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
    name = "reopen",
    description = "Reopen a completed task",
    mixinStandardHelpOptions = false
)
public class ReopenTaskCommand implements Callable<Integer> {
    
    @Parameters(index = "0", description = "Task ID", arity = "1")
    protected String id;
    
    @Option(names = {"-c", "--comment"}, description = "Reopening comment")
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
            
            TaskRepository repository = new TaskRepository(tasksFile);
            TaskService service = new TaskService(repository);
            
            if (service.getTask(id).isPresent()) {
                Task task = service.getTask(id).get();
                
                if (!task.isCompleted()) {
                    System.err.println("Error: Task with ID " + id + " is already active");
                    return 1;
                }
                
                task = service.reopenTask(id, comment);
                System.out.println("Task '" + task.getTopic() + "' (ID: " + task.getId() + ") reopened");
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
    
    private void showExamples() {
        System.out.println("Usage: reopen TASK_ID [-c \"Comment\"]");
        System.out.println();
        System.out.println("Examples:");
        System.out.println("  reopen 1                               - Reopen task 1");
        System.out.println("  reopen 2 -c \"Need to fix again\"        - Reopen task 2 with a comment");
        System.out.println();
        System.out.println("Options:");
        System.out.println("  -c, --comment \"text\"                    Add a reopening comment");
        System.out.println("  -h, --help                               Show this help message");
    }
} 