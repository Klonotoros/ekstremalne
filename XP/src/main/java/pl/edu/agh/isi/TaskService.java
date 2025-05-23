package pl.edu.agh.isi;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

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
} 