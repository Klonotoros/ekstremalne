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
    name = "assign",
    description = "Assign a task to a family member",
    mixinStandardHelpOptions = false
)
public class AssignTaskCommand implements Callable<Integer> {
    
    @Parameters(index = "0", description = "Task ID", arity = "1")
    protected String taskId;
    
    @Parameters(index = "1", description = "Family member ID", arity = "1")
    protected String familyMemberId;
    
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
            
            // Check if the family member exists
            Optional<FamilyMember> memberOpt = familyMemberService.getFamilyMember(familyMemberId);
            if (!memberOpt.isPresent()) {
                System.err.println("Error: Family member with ID " + familyMemberId + " not found");
                return 1;
            }
            
            Task task = taskOpt.get();
            FamilyMember member = memberOpt.get();
            
            // Check if the task is already assigned
            if (task.getAssignedTo() != null && !task.getAssignedTo().isEmpty()) {
                System.err.println("Error: Task is already assigned to a family member");
                return 1;
            }
            
            // Assign the task
            task = taskService.assignTask(taskId, familyMemberId);
            
            System.out.println("Task '" + task.getTopic() + "' (ID: " + task.getId() + ") assigned to " + member.getName());
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
        System.out.println("Usage: assign TASK_ID FAMILY_MEMBER_ID");
        System.out.println();
        System.out.println("Examples:");
        System.out.println("  assign 1 2        - Assign task with ID 1 to family member with ID 2");
        System.out.println();
        System.out.println("Options:");
        System.out.println("  -h, --help        Show this help message");
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