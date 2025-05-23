package pl.edu.agh.isi.cli;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.io.TempDir;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.mockito.Mockito.*;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.Optional;

import pl.edu.agh.isi.Task;
import pl.edu.agh.isi.TaskService;
import pl.edu.agh.isi.RecurrenceConfig;
import pl.edu.agh.isi.RecurrenceInterval;
import pl.edu.agh.isi.RecurringTaskService;
import pl.edu.agh.isi.TaskPriority;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Recurring Task Commands")
public class RecurringTaskCommandsTest {
    
    @Mock
    private TaskService mockTaskService;
    
    @Mock
    private RecurringTaskService mockRecurringTaskService;
    
    private final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
    private final PrintStream originalOut = System.out;
    private final PrintStream originalErr = System.err;
    
    @TempDir
    Path tempDir;
    
    @BeforeEach
    void setUp() {
        System.setOut(new PrintStream(outputStream));
        System.setErr(new PrintStream(outputStream));
    }
    
    @Test
    @DisplayName("SetRecurringCommand should set a task as recurring successfully")
    void setRecurringCommandShouldSetTaskAsRecurringSuccessfully() throws Exception {
        // Given
        SetRecurringCommand command = spy(new SetRecurringCommand());
        
        String taskId = "1";
        String intervalStr = "WEEKLY";
        
        Task existingTask = new Task("Clean kitchen", LocalDateTime.now(), "");
        existingTask.setId(1);
        
        Task updatedTask = new Task("Clean kitchen", LocalDateTime.now(), "");
        updatedTask.setId(1);
        RecurrenceConfig config = new RecurrenceConfig(RecurrenceInterval.WEEKLY);
        updatedTask.setRecurrenceConfig(config);
        
        // Setup mocks
        doReturn(mockTaskService).when(command).createTaskService(any());
        when(mockTaskService.getTask(taskId)).thenReturn(Optional.of(existingTask));
        when(mockTaskService.setTaskAsRecurring(eq(taskId), any(RecurrenceConfig.class))).thenReturn(updatedTask);
        
        // When
        command.taskId = taskId;
        command.intervalStr = intervalStr;
        command.tasksFile = tempDir.resolve("test_tasks.json").toFile();
        int result = command.call();
        
        // Then
        assertEquals(0, result);
        verify(mockTaskService).getTask(taskId);
        verify(mockTaskService).setTaskAsRecurring(eq(taskId), any(RecurrenceConfig.class));
        
        String output = outputStream.toString();
        assertTrue(output.contains("Task 'Clean kitchen'"));
        assertTrue(output.contains("set as recurring"));
        assertTrue(output.contains("Weekly"));
    }
    
    @Test
    @DisplayName("SetRecurringCommand should handle invalid task ID")
    void setRecurringCommandShouldHandleInvalidTaskId() throws Exception {
        // Given
        SetRecurringCommand command = spy(new SetRecurringCommand());
        
        String taskId = "999";
        String intervalStr = "WEEKLY";
        
        // Setup mocks
        doReturn(mockTaskService).when(command).createTaskService(any());
        when(mockTaskService.getTask(taskId)).thenReturn(Optional.empty());
        
        // When
        command.taskId = taskId;
        command.intervalStr = intervalStr;
        command.tasksFile = tempDir.resolve("test_tasks.json").toFile();
        int result = command.call();
        
        // Then
        assertEquals(1, result);
        verify(mockTaskService).getTask(taskId);
        verify(mockTaskService, never()).setTaskAsRecurring(anyString(), any(RecurrenceConfig.class));
        
        String output = outputStream.toString();
        assertTrue(output.contains("Error: Task with ID 999 not found"));
    }
    
    @Test
    @DisplayName("CreateTaskCommand should create recurring task successfully")
    void createTaskCommandShouldCreateRecurringTaskSuccessfully() throws Exception {
        // Given
        CreateTaskCommand command = spy(new CreateTaskCommand());
        
        String topic = "Clean kitchen";
        String dueDateStr = "2024-08-15 10:00";
        String recurringInterval = "WEEKLY";
        
        Task createdTask = new Task(topic, LocalDateTime.parse("2024-08-15T10:00"), "");
        createdTask.setId(1);
        RecurrenceConfig config = new RecurrenceConfig(RecurrenceInterval.WEEKLY);
        createdTask.setRecurrenceConfig(config);
        
        // Setup mocks
        doReturn(mockTaskService).when(command).createTaskService(any());
        when(mockTaskService.createRecurringTask(
            eq(topic), 
            any(LocalDateTime.class), 
            anyString(), 
            any(TaskPriority.class), 
            any(RecurrenceConfig.class)
        )).thenReturn(createdTask);
        
        // When
        command.topic = topic;
        command.dueDateStr = dueDateStr;
        command.recurringInterval = recurringInterval;
        command.tasksFile = tempDir.resolve("test_tasks.json").toFile();
        int result = command.call();
        
        // Then
        assertEquals(0, result);
        verify(mockTaskService).createRecurringTask(
            eq(topic), 
            any(LocalDateTime.class), 
            anyString(), 
            any(TaskPriority.class), 
            any(RecurrenceConfig.class)
        );
        
        String output = outputStream.toString();
        assertTrue(output.contains("Recurring task created successfully"));
        assertTrue(output.contains("ID: 1"));
    }
    
    @Test
    @DisplayName("CreateTaskCommand should require due date for recurring tasks")
    void createTaskCommandShouldRequireDueDateForRecurringTasks() throws Exception {
        // Given
        CreateTaskCommand command = spy(new CreateTaskCommand());
        
        String topic = "Clean kitchen";
        String recurringInterval = "WEEKLY";
        
        // Setup mocks
        doReturn(mockTaskService).when(command).createTaskService(any());
        
        // When
        command.topic = topic;
        command.dueDateStr = null; // No due date provided
        command.recurringInterval = recurringInterval;
        command.tasksFile = tempDir.resolve("test_tasks.json").toFile();
        int result = command.call();
        
        // Then
        assertEquals(1, result);
        verify(mockTaskService, never()).createRecurringTask(
            anyString(), 
            any(LocalDateTime.class), 
            anyString(), 
            any(TaskPriority.class), 
            any(RecurrenceConfig.class)
        );
        
        String output = outputStream.toString();
        assertTrue(output.contains("Error: Due date is required for recurring tasks"));
    }
    
    @Test
    @DisplayName("ListTasksCommand should filter recurring tasks")
    void listTasksCommandShouldFilterRecurringTasks() throws Exception {
        // Test to be implemented
        // Complex to mock due to the numerous dependencies
        // This is more suited for integration testing
    }
} 