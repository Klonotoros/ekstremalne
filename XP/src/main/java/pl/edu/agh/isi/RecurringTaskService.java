package pl.edu.agh.isi;

import java.time.LocalDateTime;
import java.util.Optional;

/**
 * Service for managing recurring tasks
 */
public class RecurringTaskService {
    private final TaskRepository taskRepository;
    
    public RecurringTaskService(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }
    
    /**
     * Create a recurring task
     * @param topic task topic
     * @param dueDate initial due date
     * @param description task description
     * @param priority task priority
     * @param recurrenceConfig the recurrence configuration
     * @return the created task
     */
    public Task createRecurringTask(String topic, LocalDateTime dueDate, String description, 
                                TaskPriority priority, RecurrenceConfig recurrenceConfig) {
        if (recurrenceConfig == null) {
            throw new IllegalArgumentException("Recurrence configuration cannot be null");
        }
        
        Task task = new Task(topic, dueDate, description, priority, recurrenceConfig);
        return taskRepository.save(task);
    }
    
    /**
     * Set a task as recurring
     * @param taskId the ID of the task to make recurring
     * @param recurrenceConfig the recurrence configuration
     * @return the updated task
     */
    public Task setTaskAsRecurring(int taskId, RecurrenceConfig recurrenceConfig) {
        if (recurrenceConfig == null) {
            throw new IllegalArgumentException("Recurrence configuration cannot be null");
        }
        
        Task task = taskRepository.findById(taskId)
            .orElseThrow(() -> new IllegalArgumentException("Task not found with ID: " + taskId));
        
        if (task.isCompleted()) {
            throw new IllegalArgumentException("Cannot set a completed task as recurring");
        }
        
        if (task.isRecurrenceInstance()) {
            throw new IllegalArgumentException("Cannot set a recurrence instance as recurring");
        }
        
        task.setRecurrenceConfig(recurrenceConfig);
        return taskRepository.update(task);
    }
    
    /**
     * Set a task as recurring
     * @param taskId the ID of the task as a string
     * @param recurrenceConfig the recurrence configuration
     * @return the updated task
     */
    public Task setTaskAsRecurring(String taskId, RecurrenceConfig recurrenceConfig) {
        try {
            return setTaskAsRecurring(Integer.parseInt(taskId), recurrenceConfig);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid task ID format: " + taskId);
        }
    }
    
    /**
     * Remove recurrence configuration from a task
     * @param taskId the ID of the task
     * @return the updated task
     */
    public Task removeRecurrence(int taskId) {
        Task task = taskRepository.findById(taskId)
            .orElseThrow(() -> new IllegalArgumentException("Task not found with ID: " + taskId));
        
        if (!task.isRecurring()) {
            throw new IllegalArgumentException("Task is not recurring");
        }
        
        task.setRecurrenceConfig(null);
        return taskRepository.update(task);
    }
    
    /**
     * Remove recurrence configuration from a task
     * @param taskId the ID of the task as a string
     * @return the updated task
     */
    public Task removeRecurrence(String taskId) {
        try {
            return removeRecurrence(Integer.parseInt(taskId));
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid task ID format: " + taskId);
        }
    }
    
    /**
     * Generate the next instance of a recurring task
     * @param taskId the ID of the completed recurring task
     * @return the new task instance, or empty if no new instance should be created
     */
    public Optional<Task> generateNextInstance(int taskId) {
        Task parentTask = taskRepository.findById(taskId)
            .orElseThrow(() -> new IllegalArgumentException("Task not found with ID: " + taskId));
        
        if (!parentTask.isRecurring()) {
            return Optional.empty();
        }
        
        RecurrenceConfig config = parentTask.getRecurrenceConfig();
        
        // Determine the occurrence number
        int occurrenceNumber = 1;
        
        // Check if we're at the limit of occurrences
        if (config.getOccurrences() != -1 && occurrenceNumber >= config.getOccurrences()) {
            return Optional.empty();
        }
        
        // Calculate the next due date
        LocalDateTime nextDueDate = config.calculateNextDueDate(parentTask.getDueDate());
        
        // Check if we're past the end date
        if (config.getEndDate() != null && (nextDueDate == null || nextDueDate.isAfter(config.getEndDate()))) {
            return Optional.empty();
        }
        
        // Create the new task instance
        Task newTask = new Task(
            parentTask.getTopic(),
            nextDueDate,
            parentTask.getDescription(),
            parentTask.getPriority(),
            parentTask.getId(),
            occurrenceNumber
        );
        
        // Copy assignee if present
        if (parentTask.getAssignedTo() != null && !parentTask.getAssignedTo().isEmpty()) {
            newTask.setAssignedTo(parentTask.getAssignedTo());
        }
        
        // Save the new task
        return Optional.of(taskRepository.save(newTask));
    }
    
    /**
     * Generate the next instance of a recurring task
     * @param taskId the ID of the completed recurring task as a string
     * @return the new task instance, or empty if no new instance should be created
     */
    public Optional<Task> generateNextInstance(String taskId) {
        try {
            return generateNextInstance(Integer.parseInt(taskId));
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid task ID format: " + taskId);
        }
    }
    
    /**
     * Get the parent task of a recurrence instance
     * @param taskId the ID of the recurrence instance
     * @return the parent task, or empty if not found or if task is not a recurrence instance
     */
    public Optional<Task> getParentTask(int taskId) {
        Task task = taskRepository.findById(taskId)
            .orElseThrow(() -> new IllegalArgumentException("Task not found with ID: " + taskId));
        
        if (!task.isRecurrenceInstance() || task.getParentTaskId() == null) {
            return Optional.empty();
        }
        
        return taskRepository.findById(task.getParentTaskId());
    }
    
    /**
     * Get the parent task of a recurrence instance
     * @param taskId the ID of the recurrence instance as a string
     * @return the parent task, or empty if not found or if task is not a recurrence instance
     */
    public Optional<Task> getParentTask(String taskId) {
        try {
            return getParentTask(Integer.parseInt(taskId));
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid task ID format: " + taskId);
        }
    }
} 