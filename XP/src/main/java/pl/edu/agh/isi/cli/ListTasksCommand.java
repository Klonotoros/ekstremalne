package pl.edu.agh.isi.cli;

import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

import java.io.File;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.Callable;
import java.util.stream.Collectors;

import pl.edu.agh.isi.FamilyMemberRepository;
import pl.edu.agh.isi.FamilyMemberService;
import pl.edu.agh.isi.Task;
import pl.edu.agh.isi.TaskPriority;
import pl.edu.agh.isi.TaskRepository;
import pl.edu.agh.isi.TaskService;
import pl.edu.agh.isi.RecurringTaskService;

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
    
    @Option(names = {"-P", "--priority-sort"}, description = "Sort tasks by priority (highest first)")
    protected boolean sortByPriority = false;
    
    @Option(names = {"-p", "--priority"}, description = "Filter tasks by priority level (1-low, 2-medium, 3-high)")
    protected String priorityFilter;
    
    @Option(names = {"-R", "--recurring"}, description = "Show only recurring tasks")
    protected boolean onlyRecurring = false;

    @Override
    public Integer call() throws Exception {
        try {
            if (helpRequested) {
                showExamples();
                return 0;
            }
            
            TaskRepository taskRepository = new TaskRepository(tasksFile);
            TaskService taskService = createTaskService(tasksFile);
            FamilyMemberService familyMemberService = createFamilyMemberService(familyMembersFile);
            RecurringTaskService recurringTaskService = new RecurringTaskService(taskRepository);
            
            // Set family member service in task service
            taskService.setFamilyMemberService(familyMemberService);
            taskService.setRecurringTaskService(recurringTaskService);
            
            List<Task> tasks;
            if (showAll) {
                tasks = taskService.getAllTasks();
            } else if (showOnlyCompleted) {
                tasks = taskService.getCompletedTasks();
            } else {
                tasks = taskService.getActiveTasks();
            }
            
            // Filter by recurring if specified
            if (onlyRecurring) {
                tasks = tasks.stream()
                        .filter(Task::isRecurring)
                        .collect(Collectors.toList());
            }
            
            // Filter by priority if specified
            if (priorityFilter != null && !priorityFilter.isEmpty()) {
                try {
                    int priorityLevel = Integer.parseInt(priorityFilter);
                    if (priorityLevel < 1 || priorityLevel > 3) {
                        System.err.println("Warning: Priority level must be between 1 and 3. Showing all priorities.");
                    } else {
                        TaskPriority priority = TaskPriority.fromLevel(priorityLevel);
                        tasks = tasks.stream()
                                .filter(task -> task.getPriority() == priority)
                                .collect(Collectors.toList());
                    }
                } catch (NumberFormatException e) {
                    System.err.println("Warning: Invalid priority format. Showing all priorities.");
                }
            }
            
            // Apply sorting if requested
            if (sortByPriority) {
                tasks = taskService.getTasksSortedByPriorityDescending(tasks);
            } else if (sortAscending && sortDescending) {
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
            
            // Add recurring filter info if applicable
            if (onlyRecurring) {
                heading += " (Recurring only)";
            }
            
            // Add filter information to heading if applicable
            if (priorityFilter != null && !priorityFilter.isEmpty()) {
                try {
                    int priorityLevel = Integer.parseInt(priorityFilter);
                    if (priorityLevel >= 1 && priorityLevel <= 3) {
                        TaskPriority priority = TaskPriority.fromLevel(priorityLevel);
                        heading += " (Priority: " + priority.getDisplayName() + ")";
                    }
                } catch (NumberFormatException ignored) {
                    // Ignore invalid priority format as warning is already shown
                }
            }
            
            // Add sorting information to heading if applicable
            if (sortByPriority) {
                heading += " (Sorted by priority)";
            } else if (sortAscending) {
                heading += " (Sorted by due date, earliest first)";
            } else if (sortDescending) {
                heading += " (Sorted by due date, latest first)";
            }
            
            System.out.println(heading + ":");
            System.out.println("-----------------------------------------------------------------------------------------------------------");
            System.out.println("ID | Status | Priority | Due Date           | Assigned To        | Recurring | Topic");
            System.out.println("-----------------------------------------------------------------------------------------------------------");
            
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
                
                // Get priority display
                TaskPriority priority = task.getPriority();
                String priorityDisplay = String.format("%s %s", 
                    priority.getSymbol(), 
                    priority.getDisplayName().substring(0, 1));
                
                // Get recurrence info
                String recurringDisplay = task.isRecurring() ? "Yes" : "No";
                
                // Show recurrence info for recurring tasks
                if (task.isRecurring() && task.getRecurrenceConfig() != null) {
                    recurringDisplay = task.getRecurrenceConfig().getInterval().getDisplayName().substring(0, 1);
                } else if (task.isRecurrenceInstance()) {
                    recurringDisplay = "Instance";
                }
                
                System.out.printf("%-2d | %-6s | %-8s | %-18s | %-18s | %-9s | %s%n", 
                    task.getId(), status, priorityDisplay, dueDateStr, assignedTo, recurringDisplay, task.getTopic());
            }
            
            System.out.println("-----------------------------------------------------------------------------------------------------------");
            System.out.println("Total: " + tasks.size() + " task(s)");
            System.out.println("Priorities: ! Low, !! Medium, !!! High");
            System.out.println("Recurring: D - Daily, W - Weekly, M - Monthly");
            System.out.println();
            
            // Show hints
            if (!(sortAscending || sortDescending || sortByPriority)) {
                System.out.println("Tip: Use -d to sort by due date (earliest first) or -r (latest first) or -P (by priority)");
            }
            
            return 0;
        } catch (Exception e) {
            System.err.println("Unexpected error: " + e.getMessage());
            e.printStackTrace();
            return 2;
        }
    }
    
    private void showExamples() {
        System.out.println("Usage: list [-a | -c] [-d | -r | -P] [-p PRIORITY] [-R]");
        System.out.println();
        System.out.println("Examples:");
        System.out.println("  list                          - List active (non-completed) tasks");
        System.out.println("  list -a                       - List all tasks, including completed ones");
        System.out.println("  list -c                       - List only completed tasks");
        System.out.println("  list -d                       - List active tasks sorted by due date (ascending)");
        System.out.println("  list -r                       - List active tasks sorted by due date (descending)");
        System.out.println("  list -P                       - List active tasks sorted by priority (highest first)");
        System.out.println("  list -p 3                     - List only high priority tasks");
        System.out.println("  list -R                       - List only recurring tasks");
        System.out.println("  list -a -d                    - List all tasks sorted by due date (ascending)");
        System.out.println();
        System.out.println("Options:");
        System.out.println("  -a, --all                     Show all tasks including completed ones");
        System.out.println("  -c, --completed               Show only completed tasks");
        System.out.println("  -d, --date-asc                Sort tasks by due date (ascending)");
        System.out.println("  -r, --date-desc               Sort tasks by due date (descending)");
        System.out.println("  -P, --priority-sort           Sort tasks by priority (highest first)");
        System.out.println("  -p, --priority LEVEL          Filter tasks by priority level (1-low, 2-medium, 3-high)");
        System.out.println("  -R, --recurring               Show only recurring tasks");
        System.out.println("  -h, --help                    Show this help message");
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