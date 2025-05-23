package pl.edu.agh.isi;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Task {
    private int id;
    private String topic;
    private String description;
    private LocalDateTime createdAt;
    private LocalDateTime dueDate;
    private boolean isCompleted;
    private String assignedTo;
    private List<Comment> comments;
    private TaskPriority priority;
    
    // Recurrence-related fields
    private RecurrenceConfig recurrenceConfig;
    private Integer parentTaskId;
    private Integer recurrenceNumber;

    // Default constructor for Jackson
    public Task() {
        this.comments = new ArrayList<>();
        this.isCompleted = false;
        this.createdAt = LocalDateTime.now();
        this.priority = TaskPriority.MEDIUM; // Default priority
        this.recurrenceNumber = null;
    }

    public Task(String topic, LocalDateTime dueDate, String description) {
        this();
        if (topic == null || topic.trim().isEmpty()) {
            throw new IllegalArgumentException("Topic cannot be empty");
        }
        this.topic = topic;
        this.dueDate = dueDate;
        this.description = description;
    }

    public Task(int id, String topic, LocalDateTime dueDate, String description) {
        this(topic, dueDate, description);
        this.id = id;
    }
    
    public Task(String topic, LocalDateTime dueDate, String description, TaskPriority priority) {
        this(topic, dueDate, description);
        this.priority = priority;
    }
    
    public Task(int id, String topic, LocalDateTime dueDate, String description, TaskPriority priority) {
        this(topic, dueDate, description, priority);
        this.id = id;
    }
    
    /**
     * Create a task with recurrence configuration
     */
    public Task(String topic, LocalDateTime dueDate, String description, RecurrenceConfig recurrenceConfig) {
        this(topic, dueDate, description);
        this.recurrenceConfig = recurrenceConfig;
    }
    
    /**
     * Create a task with recurrence configuration and priority
     */
    public Task(String topic, LocalDateTime dueDate, String description, 
            TaskPriority priority, RecurrenceConfig recurrenceConfig) {
        this(topic, dueDate, description, priority);
        this.recurrenceConfig = recurrenceConfig;
    }
    
    /**
     * Create a recurring task instance (child task from a parent recurring task)
     */
    public Task(String topic, LocalDateTime dueDate, String description, TaskPriority priority, 
            Integer parentTaskId, Integer recurrenceNumber) {
        this(topic, dueDate, description, priority);
        this.parentTaskId = parentTaskId;
        this.recurrenceNumber = recurrenceNumber;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        if (topic == null || topic.trim().isEmpty()) {
            throw new IllegalArgumentException("Topic cannot be empty");
        }
        this.topic = topic;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getDueDate() {
        return dueDate;
    }

    public void setDueDate(LocalDateTime dueDate) {
        this.dueDate = dueDate;
    }

    public boolean isCompleted() {
        return isCompleted;
    }

    public void setCompleted(boolean completed) {
        isCompleted = completed;
    }

    public String getAssignedTo() {
        return assignedTo;
    }

    public void setAssignedTo(String assignedTo) {
        this.assignedTo = assignedTo;
    }

    public List<Comment> getComments() {
        return comments;
    }

    public void setComments(List<Comment> comments) {
        this.comments = comments;
    }

    public void addComment(Comment comment) {
        if (this.comments == null) {
            this.comments = new ArrayList<>();
        }
        this.comments.add(comment);
    }
    
    public TaskPriority getPriority() {
        return priority;
    }
    
    public void setPriority(TaskPriority priority) {
        this.priority = priority;
    }
    
    public RecurrenceConfig getRecurrenceConfig() {
        return recurrenceConfig;
    }
    
    public void setRecurrenceConfig(RecurrenceConfig recurrenceConfig) {
        this.recurrenceConfig = recurrenceConfig;
    }
    
    public Integer getParentTaskId() {
        return parentTaskId;
    }
    
    public void setParentTaskId(Integer parentTaskId) {
        this.parentTaskId = parentTaskId;
    }
    
    public Integer getRecurrenceNumber() {
        return recurrenceNumber;
    }
    
    public void setRecurrenceNumber(Integer recurrenceNumber) {
        this.recurrenceNumber = recurrenceNumber;
    }
    
    /**
     * Check if this task is a recurring task (has recurrence configuration)
     * @return true if this is a recurring task, false otherwise
     */
    public boolean isRecurring() {
        return recurrenceConfig != null;
    }
    
    /**
     * Check if this task is a recurrence instance (child of a recurring task)
     * @return true if this is a recurrence instance, false otherwise
     */
    public boolean isRecurrenceInstance() {
        return parentTaskId != null && recurrenceNumber != null;
    }
}



