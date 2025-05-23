package pl.edu.agh.isi;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class TaskRepositoryTest {

    @TempDir
    Path tempDir;
    
    private File tasksFile;
    private ObjectMapper mapper;
    private TaskRepository repository;
    private LocalDateTime dueDate;

    @BeforeEach
    void setUp() {
        tasksFile = tempDir.resolve("tasks-test.json").toFile();
        mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        repository = new TaskRepository(tasksFile, mapper);
        dueDate = LocalDateTime.now().plusDays(1);
    }

    @Test
    void shouldSaveAndRetrieveTask() {
        // given
        Task task = new Task("Test task", dueDate, "Task details");

        // when
        Task savedTask = repository.save(task);
        Optional<Task> retrievedTask = repository.findById(savedTask.getId());

        // then
        assertTrue(retrievedTask.isPresent());
        assertEquals(savedTask.getId(), retrievedTask.get().getId());
        assertEquals("Test task", retrievedTask.get().getTopic());
        assertEquals(dueDate, retrievedTask.get().getDueDate());
        assertEquals("Task details", retrievedTask.get().getDescription());
    }

    @Test
    void shouldGenerateIdForNewTask() {
        // given
        Task task1 = new Task("Task 1", dueDate, "Details 1");
        Task task2 = new Task("Task 2", dueDate, "Details 2");

        // when
        Task savedTask1 = repository.save(task1);
        Task savedTask2 = repository.save(task2);

        // then
        assertNotEquals(0, savedTask1.getId());
        assertNotEquals(0, savedTask2.getId());
        assertNotEquals(savedTask1.getId(), savedTask2.getId());
    }

    @Test
    void shouldFindAllTasks() {
        // given
        repository.save(new Task("Task 1", dueDate, "Details 1"));
        repository.save(new Task("Task 2", dueDate, "Details 2"));
        repository.save(new Task("Task 3", dueDate, "Details 3"));

        // when
        List<Task> tasks = repository.findAll();

        // then
        assertEquals(3, tasks.size());
    }

    @Test
    void shouldDeleteTask() {
        // given
        Task task = repository.save(new Task("Task to delete", dueDate, "Details"));
        int taskId = task.getId();

        // when
        repository.delete(taskId);
        Optional<Task> deletedTask = repository.findById(taskId);

        // then
        assertFalse(deletedTask.isPresent());
    }

    @Test
    void shouldUpdateTask() {
        // given
        Task task = repository.save(new Task("Original task", dueDate, "Original details"));
        int taskId = task.getId();
        
        task.setTopic("Updated task");
        task.setDescription("Updated details");
        task.setDueDate(dueDate.plusDays(1));

        // when
        Task updatedTask = repository.update(task);
        Optional<Task> retrievedTask = repository.findById(taskId);

        // then
        assertTrue(retrievedTask.isPresent());
        assertEquals("Updated task", retrievedTask.get().getTopic());
        assertEquals("Updated details", retrievedTask.get().getDescription());
        assertEquals(dueDate.plusDays(1), retrievedTask.get().getDueDate());
    }

    @Test
    void shouldThrowExceptionWhenUpdatingNonExistentTask() {
        // given
        Task task = new Task("Non-existent task", dueDate, "Details");
        task.setId(999); // ID that doesn't exist

        // when & then
        assertThrows(IllegalArgumentException.class, () -> {
            repository.update(task);
        });
    }

    @Test
    void shouldLoadTasksFromFile() throws IOException {
        // given
        Task task1 = repository.save(new Task("Task 1", dueDate, "Details 1"));
        Task task2 = repository.save(new Task("Task 2", dueDate, "Details 2"));
        
        // when
        TaskRepository newRepository = new TaskRepository(tasksFile, mapper);
        
        // then
        assertEquals(2, newRepository.findAll().size());
        assertTrue(newRepository.findById(task1.getId()).isPresent());
        assertTrue(newRepository.findById(task2.getId()).isPresent());
    }
}


