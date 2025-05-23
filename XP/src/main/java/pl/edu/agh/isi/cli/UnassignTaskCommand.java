package pl.edu.agh.isi.cli;

import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;

import java.io.File;
import java.util.Optional;
import java.util.concurrent.Callable;

import pl.edu.agh.isi.FamilyMember;
import pl.edu.agh.isi.FamilyMemberRepository;
import pl.edu.agh.isi.FamilyMemberService;
import pl.edu.agh.isi.Task;
import pl.edu.agh.isi.TaskRepository;
import pl.edu.agh.isi.TaskService;

@Command(
    name = "unassign",
    description = "Unassign a task from a family member",
    mixinStandardHelpOptions = false
)
public class UnassignTaskCommand implements Callable<Integer> {
    
    @Parameters(index = "0", description = "Task ID", arity = "1")
    protected String taskId;
    
    @Option(names = {"-f", "--task-file"}, description = "Tasks data file", defaultValue = "tasks.json", hidden = true)
    protected File tasksFile;
    
    @Option(names = {"-m", "--member-file"}, description = "Family members data file", defaultValue = "family_members.json", hidden = true)
    protected File familyMembersFile;
    
    @Option(names = {"-h", "--help"}, usageHelp = true, description = "Show help message")
    protected boolean helpRequested = false;
    
    @Override
    public Integer call() throws Exception {
        try {
            if (helpRequested) {
                showExamples();
                return 0;
            }
            
            TaskService taskService = createTaskService(tasksFile);
            FamilyMemberService familyMemberService = createFamilyMemberService(familyMembersFile);
            
            // Set the familyMemberService in the taskService
            taskService.setFamilyMemberService(familyMemberService);
            
            // First check if the task exists
            Optional<Task> taskOpt = taskService.getTask(taskId);
            if (!taskOpt.isPresent()) {
                System.err.println("Error: Task with ID " + taskId + " not found");
                return 1;
            }
            
            Task task = taskOpt.get();
            
            // Check if the task is assigned
            if (task.getAssignedTo() == null || task.getAssignedTo().isEmpty()) {
                System.err.println("Error: Task is not assigned to any family member");
                return 1;
            }
            
            // Get the family member's name if possible
            String memberName = "a family member";
            try {
                int memberId = Integer.parseInt(task.getAssignedTo());
                Optional<FamilyMember> memberOpt = familyMemberService.getFamilyMember(memberId);
                if (memberOpt.isPresent()) {
                    memberName = memberOpt.get().getName();
                }
            } catch (Exception ignored) {
                // Ignore any errors in getting member name
            }
            
            // Unassign the task
            task = taskService.unassignTask(taskId);
            
            System.out.println("Task '" + task.getTopic() + "' (ID: " + task.getId() + ") unassigned from " + memberName);
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
        System.out.println("Usage: unassign TASK_ID");
        System.out.println();
        System.out.println("Examples:");
        System.out.println("  unassign 1      - Unassign task with ID 1 from its assigned family member");
        System.out.println();
        System.out.println("Options:");
        System.out.println("  -h, --help      Show this help message");
    }
    
    // Protected methods for better testability
    protected TaskService createTaskService(File file) {
        TaskRepository repository = new TaskRepository(file);
        return new TaskService(repository);
    }
    
    protected FamilyMemberService createFamilyMemberService(File file) {
        FamilyMemberRepository repository = new FamilyMemberRepository(file);
        return new FamilyMemberService(repository);
    }
} 