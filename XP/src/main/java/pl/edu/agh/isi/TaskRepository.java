package pl.edu.agh.isi;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

public class TaskRepository {
    private final File file;
    private final ObjectMapper mapper;
    private Map<Integer, Task> tasks;
    private AtomicInteger nextId;

    public TaskRepository(File file) {
        this.file = file;
        this.mapper = new ObjectMapper();
        this.mapper.registerModule(new JavaTimeModule());
        // Configure mapper to be more lenient when reading JSON
        this.mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        this.tasks = new HashMap<>();
        this.nextId = new AtomicInteger(1);
        loadTasks();
    }

    // Constructor for testing with a provided ObjectMapper
    TaskRepository(File file, ObjectMapper mapper) {
        this.file = file;
        this.mapper = mapper;
        // Configure mapper to be more lenient when reading JSON
        this.mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        this.tasks = new HashMap<>();
        this.nextId = new AtomicInteger(1);
        loadTasks();
    }

    private void loadTasks() {
        if (!file.exists()) {
            tasks = new HashMap<>();
            return;
        }
        try {
            Task[] loaded = mapper.readValue(file, Task[].class);
            tasks = new HashMap<>();
            for (Task t : loaded) {
                // Handle migration from old format - if topic is empty but description exists
                if ((t.getTopic() == null || t.getTopic().isEmpty()) && t.getDescription() != null) {
                    t.setTopic(t.getDescription());
                }
                
                // Skip tasks with invalid data
                if (t.getId() <= 0 || t.getTopic() == null || t.getTopic().isEmpty()) {
                    continue;
                }
                
                tasks.put(t.getId(), t);
                if (t.getId() >= nextId.get()) {
                    nextId.set(t.getId() + 1);
                }
            }
        } catch (IOException e) {
            System.err.println("Error loading tasks: " + e.getMessage());
            tasks = new HashMap<>();
        }
    }

    private void saveTasks() {
        try {
            if (!file.exists()) {
                // Create parent directories if needed
                File parentDir = file.getParentFile();
                if (parentDir != null) {
                    parentDir.mkdirs();
                }
                file.createNewFile();
            }
            mapper.writeValue(file, tasks.values());
        } catch (IOException e) {
            System.err.println("Error saving tasks: " + e.getMessage());
        }
    }

    public Task save(Task task) {
        if (task.getId() == 0) {
            task.setId(nextId.getAndIncrement());
        }
        tasks.put(task.getId(), task);
        saveTasks();
        return task;
    }

    public Optional<Task> findById(int id) {
        return Optional.ofNullable(tasks.get(id));
    }

    public Optional<Task> findById(String id) {
        try {
            return findById(Integer.parseInt(id));
        } catch (NumberFormatException e) {
            return Optional.empty();
        }
    }

    public List<Task> findAll() {
        return new ArrayList<>(tasks.values());
    }

    public void delete(int id) {
        tasks.remove(id);
        saveTasks();
    }

    public void delete(String id) {
        try {
            delete(Integer.parseInt(id));
        } catch (NumberFormatException e) {
            // Ignore invalid ID
        }
    }

    public Task update(Task task) {
        if (!tasks.containsKey(task.getId())) {
            throw new IllegalArgumentException("Task not found with id: " + task.getId());
        }
        tasks.put(task.getId(), task);
        saveTasks();
        return task;
    }
}
