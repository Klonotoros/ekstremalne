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

    // Default constructor for Jackson
    public Task() {
        this.comments = new ArrayList<>();
        this.isCompleted = false;
        this.createdAt = LocalDateTime.now();
        this.priority = TaskPriority.MEDIUM; // Default priority
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
}



