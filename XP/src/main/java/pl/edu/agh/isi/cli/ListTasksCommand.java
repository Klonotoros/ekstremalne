package pl.edu.agh.isi.cli;

import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

import java.io.File;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.Callable;

import pl.edu.agh.isi.FamilyMemberRepository;
import pl.edu.agh.isi.FamilyMemberService;
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
    
    @Option(names = {"-m", "--member-file"}, description = "Family members data file", defaultValue = "family_members.json", hidden = true)
    protected File familyMembersFile;
    
    @Option(names = {"-h", "--help"}, usageHelp = true, description = "Show help message")
    protected boolean helpRequested = false;
    
    @Option(names = {"-d", "--date-asc"}, description = "Sort tasks by due date (ascending)")
    protected boolean sortAscending = false;
    
    @Option(names = {"-r", "--date-desc"}, description = "Sort tasks by due date (descending)")
    protected boolean sortDescending = false;

    @Override
    public Integer call() throws Exception {
        try {
            if (helpRequested) {
                showExamples();
                return 0;
            }
            
            TaskService taskService = createTaskService(tasksFile);
            FamilyMemberService familyMemberService = createFamilyMemberService(familyMembersFile);
            
            // Set family member service in task service
            taskService.setFamilyMemberService(familyMemberService);
            
            List<Task> tasks;
            if (showAll) {
                tasks = taskService.getAllTasks();
            } else if (showOnlyCompleted) {
                tasks = taskService.getCompletedTasks();
            } else {
                tasks = taskService.getActiveTasks();
            }
            
            // Apply sorting if requested
            if (sortAscending && sortDescending) {
                System.err.println("Warning: Both ascending and descending sort options specified. Using ascending sort.");
                tasks = taskService.getTasksSortedByDueDateAscending(tasks);
            } else if (sortAscending) {
                tasks = taskService.getTasksSortedByDueDateAscending(tasks);
            } else if (sortDescending) {
                tasks = taskService.getTasksSortedByDueDateDescending(tasks);
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
            
            // Add sorting information to heading if applicable
            if (sortAscending) {
                heading += " (Sorted by due date, earliest first)";
            } else if (sortDescending) {
                heading += " (Sorted by due date, latest first)";
            }
            
            System.out.println(heading + ":");
            System.out.println("---------------------------------------------------------------------------------");
            System.out.println("ID | Status | Due Date           | Assigned To        | Topic");
            System.out.println("---------------------------------------------------------------------------------");
            
            for (Task task : tasks) {
                String status = task.isCompleted() ? "✓" : " ";
                String dueDateStr = task.getDueDate() != null ? 
                    task.getDueDate().format(DATE_FORMATTER) : "Not specified";
                
                // Get assigned family member's name
                String assignedTo = "Not assigned";
                if (task.getAssignedTo() != null && !task.getAssignedTo().isEmpty()) {
                    Optional<String> memberName = taskService.getAssignedFamilyMemberName(task);
                    if (memberName.isPresent()) {
                        assignedTo = memberName.get();
                    } else {
                        assignedTo = "Member ID: " + task.getAssignedTo();
                    }
                }
                
                System.out.printf("%-2d | %-6s | %-18s | %-18s | %s%n", 
                    task.getId(), status, dueDateStr, assignedTo, task.getTopic());
            }
            
            System.out.println("---------------------------------------------------------------------------------");
            System.out.println("Total: " + tasks.size() + " task(s)");
            System.out.println();
            
            // Show hint about sorting options
            if (!(sortAscending || sortDescending)) {
                System.out.println("Tip: Use -d to sort by due date (earliest first) or -r (latest first)");
            }
            
            return 0;
        } catch (Exception e) {
            System.err.println("Unexpected error: " + e.getMessage());
            e.printStackTrace();
            return 2;
        }
    }
    
    private void showExamples() {
        System.out.println("Usage: list [-a | -c] [-d | -r]");
        System.out.println();
        System.out.println("Examples:");
        System.out.println("  list          - List active (non-completed) tasks");
        System.out.println("  list -a       - List all tasks, including completed ones");
        System.out.println("  list -c       - List only completed tasks");
        System.out.println("  list -d       - List active tasks sorted by due date (ascending)");
        System.out.println("  list -r       - List active tasks sorted by due date (descending)");
        System.out.println("  list -a -d    - List all tasks sorted by due date (ascending)");
        System.out.println();
        System.out.println("Options:");
        System.out.println("  -a, --all                 Show all tasks including completed ones");
        System.out.println("  -c, --completed           Show only completed tasks");
        System.out.println("  -d, --date-asc            Sort tasks by due date (ascending)");
        System.out.println("  -r, --date-desc           Sort tasks by due date (descending)");
        System.out.println("  -h, --help                Show this help message");
    }

    // Protected method for better testability
    protected TaskService createTaskService(File file) {
        TaskRepository repository = new TaskRepository(file);
        return new TaskService(repository);
    }
    
    // Protected method for family member service creation
    protected FamilyMemberService createFamilyMemberService(File file) {
        FamilyMemberRepository repository = new FamilyMemberRepository(file);
        return new FamilyMemberService(repository);
    }
} 