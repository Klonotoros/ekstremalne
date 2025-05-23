package pl.edu.agh.isi;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class TaskService {
    private final TaskRepository taskRepository;
    private FamilyMemberService familyMemberService;
    private RecurringTaskService recurringTaskService;

    public TaskService(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }
    
    public TaskService(TaskRepository taskRepository, FamilyMemberService familyMemberService) {
        this.taskRepository = taskRepository;
        this.familyMemberService = familyMemberService;
    }
    
    public TaskService(TaskRepository taskRepository, FamilyMemberService familyMemberService, 
            RecurringTaskService recurringTaskService) {
        this.taskRepository = taskRepository;
        this.familyMemberService = familyMemberService;
        this.recurringTaskService = recurringTaskService;
    }
    
    public void setFamilyMemberService(FamilyMemberService familyMemberService) {
        this.familyMemberService = familyMemberService;
    }
    
    public void setRecurringTaskService(RecurringTaskService recurringTaskService) {
        this.recurringTaskService = recurringTaskService;
    }

    public Task createTask(String topic, LocalDateTime dueDate, String description) {
        if (topic == null || topic.trim().isEmpty()) {
            throw new IllegalArgumentException("Topic cannot be empty");
        }
        Task task = new Task(topic, dueDate, description);
        return taskRepository.save(task);
    }
    
    public Task createTask(String topic, LocalDateTime dueDate, String description, TaskPriority priority) {
        if (topic == null || topic.trim().isEmpty()) {
            throw new IllegalArgumentException("Topic cannot be empty");
        }
        Task task = new Task(topic, dueDate, description, priority);
        return taskRepository.save(task);
    }
    
    public Task createTask(String topic, LocalDateTime dueDate, String description, int priorityLevel) {
        return createTask(topic, dueDate, description, TaskPriority.fromLevel(priorityLevel));
    }
    
    /**
     * Create a recurring task
     */
    public Task createRecurringTask(String topic, LocalDateTime dueDate, String description, 
            TaskPriority priority, RecurrenceConfig recurrenceConfig) {
        if (recurringTaskService == null) {
            throw new IllegalStateException("RecurringTaskService is not set");
        }
        
        return recurringTaskService.createRecurringTask(topic, dueDate, description, priority, recurrenceConfig);
    }
    
    /**
     * Set a task as recurring
     */
    public Task setTaskAsRecurring(int taskId, RecurrenceConfig recurrenceConfig) {
        if (recurringTaskService == null) {
            throw new IllegalStateException("RecurringTaskService is not set");
        }
        
        return recurringTaskService.setTaskAsRecurring(taskId, recurrenceConfig);
    }
    
    /**
     * Set a task as recurring using string ID
     */
    public Task setTaskAsRecurring(String taskId, RecurrenceConfig recurrenceConfig) {
        if (recurringTaskService == null) {
            throw new IllegalStateException("RecurringTaskService is not set");
        }
        
        return recurringTaskService.setTaskAsRecurring(taskId, recurrenceConfig);
    }

    public Optional<Task> getTask(int id) {
        return taskRepository.findById(id);
    }

    public Optional<Task> getTask(String id) {
        return taskRepository.findById(id);
    }

    public List<Task> getAllTasks() {
        return taskRepository.findAll();
    }
    
    public List<Task> getActiveTasks() {
        return taskRepository.findAll().stream()
                .filter(task -> !task.isCompleted())
                .collect(Collectors.toList());
    }
    
    public List<Task> getCompletedTasks() {
        return taskRepository.findAll().stream()
                .filter(Task::isCompleted)
                .collect(Collectors.toList());
    }
    
    /**
     * Returns tasks sorted by due date in descending order (latest first)
     */
    public List<Task> getTasksSortedByDueDateDescending(List<Task> tasks) {
        return tasks.stream()
                .sorted((task1, task2) -> {
                    // Handle null values explicitly
                    if (task1.getDueDate() == null && task2.getDueDate() == null) {
                        return 0;
                    }
                    if (task1.getDueDate() == null) {
                        return 1; // Null values should be last
                    }
                    if (task2.getDueDate() == null) {
                        return -1; // Null values should be last
                    }
                    // For non-null values, compare in descending order
                    return task2.getDueDate().compareTo(task1.getDueDate());
                })
                .collect(Collectors.toList());
    }
    
    /**
     * Returns tasks sorted by due date in ascending order (earliest first)
     */
    public List<Task> getTasksSortedByDueDateAscending(List<Task> tasks) {
        return tasks.stream()
                .sorted(Comparator.comparing(Task::getDueDate, 
                         Comparator.nullsLast(Comparator.naturalOrder())))
                .collect(Collectors.toList());
    }
    
    /**
     * Returns tasks sorted by priority (highest first)
     */
    public List<Task> getTasksSortedByPriorityDescending(List<Task> tasks) {
        return tasks.stream()
                .sorted(Comparator.comparing(
                    task -> task.getPriority() != null ? task.getPriority() : TaskPriority.MEDIUM,
                    Comparator.comparing(TaskPriority::getLevel).reversed()))
                .collect(Collectors.toList());
    }
    
    /**
     * Returns tasks with a specific priority
     */
    public List<Task> getTasksByPriority(TaskPriority priority) {
        return taskRepository.findAll().stream()
                .filter(task -> task.getPriority() == priority)
                .collect(Collectors.toList());
    }
    
    /**
     * Returns tasks with a specific priority level (1-3)
     */
    public List<Task> getTasksByPriorityLevel(int priorityLevel) {
        return getTasksByPriority(TaskPriority.fromLevel(priorityLevel));
    }
    
    /**
     * Returns recurring tasks
     */
    public List<Task> getRecurringTasks() {
        return taskRepository.findAll().stream()
                .filter(Task::isRecurring)
                .collect(Collectors.toList());
    }
    
    /**
     * Returns recurrence instances (tasks that are part of a recurring sequence)
     */
    public List<Task> getRecurrenceInstances() {
        return taskRepository.findAll().stream()
                .filter(Task::isRecurrenceInstance)
                .collect(Collectors.toList());
    }
    
    /**
     * Returns recurrence instances for a specific parent task
     */
    public List<Task> getRecurrenceInstancesForParent(int parentTaskId) {
        return taskRepository.findAll().stream()
                .filter(task -> task.isRecurrenceInstance() && 
                       task.getParentTaskId() != null && 
                       task.getParentTaskId() == parentTaskId)
                .collect(Collectors.toList());
    }

    public void deleteTask(int id) {
        taskRepository.delete(id);
    }

    public void deleteTask(String id) {
        taskRepository.delete(id);
    }

    public Task updateTask(int id, String topic, LocalDateTime dueDate, String description) {
        Task task = taskRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Task not found with id: " + id));
        
        if (topic != null) {
            task.setTopic(topic);
        }
        if (dueDate != null) {
            task.setDueDate(dueDate);
        }
        task.setDescription(description);
        
        return taskRepository.update(task);
    }

    public Task updateTask(String id, String topic, LocalDateTime dueDate, String description) {
        try {
            return updateTask(Integer.parseInt(id), topic, dueDate, description);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid task ID format: " + id);
        }
    }
    
    public Task setPriority(int id, TaskPriority priority) {
        Task task = taskRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Task not found with id: " + id));
        
        task.setPriority(priority);
        
        return taskRepository.update(task);
    }
    
    public Task setPriority(String id, TaskPriority priority) {
        try {
            return setPriority(Integer.parseInt(id), priority);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid task ID format: " + id);
        }
    }
    
    public Task setPriorityByLevel(int id, int priorityLevel) {
        return setPriority(id, TaskPriority.fromLevel(priorityLevel));
    }
    
    public Task setPriorityByLevel(String id, int priorityLevel) {
        return setPriority(id, TaskPriority.fromLevel(priorityLevel));
    }
    
    public Task markTaskAsCompleted(int id, String completionComment) {
        Task task = taskRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Task not found with id: " + id));
        
        task.setCompleted(true);
        
        if (completionComment != null && !completionComment.trim().isEmpty()) {
            Comment comment = new Comment(completionComment);
            task.addComment(comment);
        }
        
        Task updatedTask = taskRepository.update(task);
        
        // If this is a recurring task and recurringTaskService is set,
        // generate the next instance
        if (updatedTask.isRecurring() && recurringTaskService != null) {
            Optional<Task> nextInstance = recurringTaskService.generateNextInstance(updatedTask.getId());
            if (nextInstance.isPresent()) {
                Task newTask = nextInstance.get();
                Comment comment = new Comment("Generated as part of recurring task #" + updatedTask.getId());
                newTask.addComment(comment);
                taskRepository.update(newTask);
            }
        }
        
        return updatedTask;
    }
    
    public Task markTaskAsCompleted(String id, String completionComment) {
        try {
            return markTaskAsCompleted(Integer.parseInt(id), completionComment);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid task ID format: " + id);
        }
    }
    
    public Task reopenTask(int id, String reopenComment) {
        Task task = taskRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Task not found with id: " + id));
        
        task.setCompleted(false);
        
        if (reopenComment != null && !reopenComment.trim().isEmpty()) {
            Comment comment = new Comment(reopenComment);
            task.addComment(comment);
        }
        
        return taskRepository.update(task);
    }
    
    public Task reopenTask(String id, String reopenComment) {
        try {
            return reopenTask(Integer.parseInt(id), reopenComment);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid task ID format: " + id);
        }
    }
    
    /**
     * Assigns a task to a family member
     * 
     * @param taskId The ID of the task to assign
     * @param familyMemberId The ID of the family member to assign the task to
     * @return The updated task
     * @throws IllegalArgumentException if the task or family member is not found,
     *                                  or if the task is already assigned
     */
    public Task assignTask(int taskId, int familyMemberId) {
        if (familyMemberService == null) {
            throw new IllegalStateException("FamilyMemberService is not set");
        }
        
        // Verify task exists
        Task task = taskRepository.findById(taskId)
            .orElseThrow(() -> new IllegalArgumentException("Task not found with ID: " + taskId));
        
        // Check if task is already assigned
        if (task.getAssignedTo() != null && !task.getAssignedTo().isEmpty()) {
            throw new IllegalArgumentException("Task is already assigned to someone");
        }
        
        // Verify family member exists
        FamilyMember member = familyMemberService.getFamilyMember(familyMemberId)
            .orElseThrow(() -> new IllegalArgumentException("Family member not found with ID: " + familyMemberId));
        
        // Assign the task
        task.setAssignedTo(String.valueOf(member.getId()));
        
        // Add a comment for the assignment
        Comment comment = new Comment("Task assigned to " + member.getName());
        task.addComment(comment);
        
        return taskRepository.update(task);
    }
    
    /**
     * Assigns a task to a family member using string IDs
     */
    public Task assignTask(String taskId, String familyMemberId) {
        try {
            return assignTask(Integer.parseInt(taskId), Integer.parseInt(familyMemberId));
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid ID format: " + e.getMessage());
        }
    }
    
    /**
     * Unassigns a task from a family member
     */
    public Task unassignTask(int taskId) {
        // Verify task exists
        Task task = taskRepository.findById(taskId)
            .orElseThrow(() -> new IllegalArgumentException("Task not found with ID: " + taskId));
        
        // Check if task is assigned
        if (task.getAssignedTo() == null || task.getAssignedTo().isEmpty()) {
            throw new IllegalArgumentException("Task is not assigned to anyone");
        }
        
        // Get the member's name for the comment if possible
        String memberName = "someone";
        if (familyMemberService != null) {
            try {
                int memberId = Integer.parseInt(task.getAssignedTo());
                Optional<FamilyMember> member = familyMemberService.getFamilyMember(memberId);
                if (member.isPresent()) {
                    memberName = member.get().getName();
                }
            } catch (Exception ignored) {
                // Ignore any errors in getting member name
            }
        }
        
        // Unassign the task
        task.setAssignedTo(null);
        
        // Add a comment for the unassignment
        Comment comment = new Comment("Task unassigned from " + memberName);
        task.addComment(comment);
        
        return taskRepository.update(task);
    }
    
    /**
     * Unassigns a task using string ID
     */
    public Task unassignTask(String taskId) {
        try {
            return unassignTask(Integer.parseInt(taskId));
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid task ID format: " + taskId);
        }
    }
    
    /**
     * Gets the name of the family member assigned to a task
     */
    public Optional<String> getAssignedFamilyMemberName(Task task) {
        if (task.getAssignedTo() == null || task.getAssignedTo().isEmpty() || familyMemberService == null) {
            return Optional.empty();
        }
        
        try {
            int memberId = Integer.parseInt(task.getAssignedTo());
            return familyMemberService.getFamilyMember(memberId)
                .map(FamilyMember::getName);
        } catch (Exception e) {
            return Optional.empty();
        }
    }
} 