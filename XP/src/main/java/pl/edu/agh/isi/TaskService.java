package pl.edu.agh.isi;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class TaskService {
    private final TaskRepository taskRepository;

    public TaskService(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    public Task createTask(String topic, LocalDateTime dueDate, String description) {
        if (topic == null || topic.trim().isEmpty()) {
            throw new IllegalArgumentException("Topic cannot be empty");
        }
        Task task = new Task(topic, dueDate, description);
        return taskRepository.save(task);
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
    
    public Task markTaskAsCompleted(int id, String completionComment) {
        Task task = taskRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Task not found with id: " + id));
        
        task.setCompleted(true);
        
        if (completionComment != null && !completionComment.trim().isEmpty()) {
            Comment comment = new Comment(completionComment);
            task.addComment(comment);
        }
        
        return taskRepository.update(task);
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
} 