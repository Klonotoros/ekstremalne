package pl.edu.agh.isi;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

class TaskServiceTest {
    
    @Mock
    private TaskRepository taskRepository;

    private TaskService taskService;
    private LocalDateTime dueDate;
    
    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        taskService = new TaskService(taskRepository);
        dueDate = LocalDateTime.now().plusDays(1);
    }
    
    @Test
    @DisplayName("Should create task successfully when data is provided")
    void shouldCreateTaskSuccessfully() {
        // given
        String topic = "Clean the kitchen";
        String description = "Wash dishes and clean countertops";
        
        Task task = new Task(topic, dueDate, description);
        task.setId(1);
        when(taskRepository.save(any(Task.class))).thenReturn(task);
        
        // when
        Task createdTask = taskService.createTask(topic, dueDate, description);
        
        // then
        assertNotNull(createdTask);
        assertEquals(1, createdTask.getId());
        assertEquals(topic, createdTask.getTopic());
        assertEquals(dueDate, createdTask.getDueDate());
        assertEquals(description, createdTask.getDescription());
        verify(taskRepository).save(any(Task.class));
    }
    
    @Test
    @DisplayName("Should create task without details when details are not provided")
    void shouldCreateTaskWithoutDetails() {
        // given
        String topic = "Clean the kitchen";
        LocalDateTime dueDate = LocalDateTime.now().plusDays(1);
        
        Task task = new Task(topic, dueDate, null);
        when(taskRepository.save(any(Task.class))).thenReturn(task);
        
        // when
        Task createdTask = taskService.createTask(topic, dueDate, null);
        
        // then
        assertNotNull(createdTask);
        assertEquals(topic, createdTask.getTopic());
        assertEquals(dueDate, createdTask.getDueDate());
        assertNull(createdTask.getDescription());
        verify(taskRepository).save(any(Task.class));
    }

    @Test
    @DisplayName("Should get task by id")
    void shouldGetTaskById() {
        // given
        int taskId = 1;
        Task task = new Task("Clean kitchen", dueDate, "Details");
        task.setId(taskId);
        when(taskRepository.findById(taskId)).thenReturn(Optional.of(task));

        // when
        Optional<Task> foundTask = taskService.getTask(taskId);

        // then
        assertTrue(foundTask.isPresent());
        assertEquals(taskId, foundTask.get().getId());
        assertEquals("Clean kitchen", foundTask.get().getTopic());
        verify(taskRepository).findById(taskId);
    }

    @Test
    @DisplayName("Should get all tasks")
    void shouldGetAllTasks() {
        // given
        Task task1 = new Task("Task 1", dueDate, "Details 1");
        task1.setId(1);
        Task task2 = new Task("Task 2", dueDate, "Details 2");
        task2.setId(2);
        List<Task> tasks = Arrays.asList(task1, task2);
        when(taskRepository.findAll()).thenReturn(tasks);

        // when
        List<Task> foundTasks = taskService.getAllTasks();

        // then
        assertEquals(2, foundTasks.size());
        assertEquals("Task 1", foundTasks.get(0).getTopic());
        assertEquals("Task 2", foundTasks.get(1).getTopic());
        verify(taskRepository).findAll();
    }

    @Test
    @DisplayName("Should delete task")
    void shouldDeleteTask() {
        // given
        int taskId = 1;

        // when
        taskService.deleteTask(taskId);

        // then
        verify(taskRepository).delete(taskId);
    }

    @Test
    @DisplayName("Should update task")
    void shouldUpdateTask() {
        // given
        int taskId = 1;
        String newTopic = "Updated task";
        String newDescription = "Updated details";
        LocalDateTime newDueDate = dueDate.plusDays(1);
        
        Task existingTask = new Task("Original task", dueDate, "Original details");
        existingTask.setId(taskId);
        
        Task updatedTask = new Task(newTopic, newDueDate, newDescription);
        updatedTask.setId(taskId);
        
        when(taskRepository.findById(taskId)).thenReturn(Optional.of(existingTask));
        when(taskRepository.update(any(Task.class))).thenReturn(updatedTask);

        // when
        Task result = taskService.updateTask(taskId, newTopic, newDueDate, newDescription);

        // then
        assertNotNull(result);
        assertEquals(taskId, result.getId());
        assertEquals(newTopic, result.getTopic());
        assertEquals(newDueDate, result.getDueDate());
        assertEquals(newDescription, result.getDescription());
        verify(taskRepository).findById(taskId);
        verify(taskRepository).update(any(Task.class));
    }

    @Test
    @DisplayName("Should throw exception when updating non-existent task")
    void shouldThrowExceptionWhenUpdatingNonExistentTask() {
        // given
        int taskId = 999;
        when(taskRepository.findById(taskId)).thenReturn(Optional.empty());

        // when & then
        assertThrows(IllegalArgumentException.class, () -> {
            taskService.updateTask(taskId, "New topic", dueDate.plusDays(1), "New details");
        });
        
        verify(taskRepository).findById(taskId);
        verify(taskRepository, never()).update(any());
    }

    @Test
    void shouldThrowExceptionWhenTopicIsEmpty() {
        // given
        String emptyTopic = "";
        String description = "Task description";

        // when & then
        assertThrows(IllegalArgumentException.class, () -> {
            taskService.createTask(emptyTopic, dueDate, description);
        });
        verify(taskRepository, never()).save(any(Task.class));
    }
} 