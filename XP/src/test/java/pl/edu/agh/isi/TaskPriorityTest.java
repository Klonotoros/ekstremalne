package pl.edu.agh.isi;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
@DisplayName("Task Priority")
public class TaskPriorityTest {
    
    @Mock
    private TaskRepository taskRepository;
    
    @Nested
    @DisplayName("TaskPriority Enum")
    class TaskPriorityEnumTest {
        
        @Test
        @DisplayName("should get priority from level")
        void shouldGetPriorityFromLevel() {
            assertEquals(TaskPriority.LOW, TaskPriority.fromLevel(1));
            assertEquals(TaskPriority.MEDIUM, TaskPriority.fromLevel(2));
            assertEquals(TaskPriority.HIGH, TaskPriority.fromLevel(3));
        }
        
        @Test
        @DisplayName("should return MEDIUM for invalid level")
        void shouldReturnMediumForInvalidLevel() {
            assertEquals(TaskPriority.MEDIUM, TaskPriority.fromLevel(0));
            assertEquals(TaskPriority.MEDIUM, TaskPriority.fromLevel(4));
            assertEquals(TaskPriority.MEDIUM, TaskPriority.fromLevel(-1));
        }
        
        @Test
        @DisplayName("should get priority from level string")
        void shouldGetPriorityFromLevelString() {
            assertEquals(TaskPriority.LOW, TaskPriority.fromLevelString("1"));
            assertEquals(TaskPriority.MEDIUM, TaskPriority.fromLevelString("2"));
            assertEquals(TaskPriority.HIGH, TaskPriority.fromLevelString("3"));
        }
        
        @Test
        @DisplayName("should return MEDIUM for invalid level string")
        void shouldReturnMediumForInvalidLevelString() {
            assertEquals(TaskPriority.MEDIUM, TaskPriority.fromLevelString("0"));
            assertEquals(TaskPriority.MEDIUM, TaskPriority.fromLevelString("4"));
            assertEquals(TaskPriority.MEDIUM, TaskPriority.fromLevelString("invalid"));
        }
        
        @Test
        @DisplayName("should have correct display names")
        void shouldHaveCorrectDisplayNames() {
            assertEquals("Low", TaskPriority.LOW.getDisplayName());
            assertEquals("Medium", TaskPriority.MEDIUM.getDisplayName());
            assertEquals("High", TaskPriority.HIGH.getDisplayName());
        }
        
        @Test
        @DisplayName("should have correct symbols")
        void shouldHaveCorrectSymbols() {
            assertEquals("!", TaskPriority.LOW.getSymbol());
            assertEquals("!!", TaskPriority.MEDIUM.getSymbol());
            assertEquals("!!!", TaskPriority.HIGH.getSymbol());
        }
    }
    
    @Nested
    @DisplayName("Task with Priority")
    class TaskWithPriorityTest {
        
        @Test
        @DisplayName("should create task with default priority (MEDIUM)")
        void shouldCreateTaskWithDefaultPriority() {
            Task task = new Task("Clean kitchen", null, "");
            assertEquals(TaskPriority.MEDIUM, task.getPriority());
        }
        
        @Test
        @DisplayName("should create task with specified priority")
        void shouldCreateTaskWithSpecifiedPriority() {
            Task task = new Task("Clean kitchen", null, "", TaskPriority.HIGH);
            assertEquals(TaskPriority.HIGH, task.getPriority());
        }
        
        @Test
        @DisplayName("should update task priority")
        void shouldUpdateTaskPriority() {
            Task task = new Task("Clean kitchen", null, "");
            assertEquals(TaskPriority.MEDIUM, task.getPriority());
            
            task.setPriority(TaskPriority.HIGH);
            assertEquals(TaskPriority.HIGH, task.getPriority());
        }
    }
    
    @Nested
    @DisplayName("TaskService with Priority")
    class TaskServiceWithPriorityTest {
        
        @Test
        @DisplayName("should create task with priority")
        void shouldCreateTaskWithPriority() {
            TaskService service = new TaskService(taskRepository);
            
            when(taskRepository.save(any(Task.class))).thenAnswer(invocation -> {
                Task task = invocation.getArgument(0);
                task.setId(1);
                return task;
            });
            
            Task task = service.createTask("Clean kitchen", null, "", TaskPriority.HIGH);
            
            assertEquals(TaskPriority.HIGH, task.getPriority());
            verify(taskRepository).save(any(Task.class));
        }
        
        @Test
        @DisplayName("should create task with priority level")
        void shouldCreateTaskWithPriorityLevel() {
            TaskService service = new TaskService(taskRepository);
            
            when(taskRepository.save(any(Task.class))).thenAnswer(invocation -> {
                Task task = invocation.getArgument(0);
                task.setId(1);
                return task;
            });
            
            Task task = service.createTask("Clean kitchen", null, "", 3);
            
            assertEquals(TaskPriority.HIGH, task.getPriority());
            verify(taskRepository).save(any(Task.class));
        }
        
        @Test
        @DisplayName("should set task priority")
        void shouldSetTaskPriority() {
            TaskService service = new TaskService(taskRepository);
            
            Task existingTask = new Task("Clean kitchen", null, "");
            existingTask.setId(1);
            
            when(taskRepository.findById(1)).thenReturn(Optional.of(existingTask));
            when(taskRepository.update(any(Task.class))).thenAnswer(invocation -> invocation.getArgument(0));
            
            Task updatedTask = service.setPriority(1, TaskPriority.HIGH);
            
            assertEquals(TaskPriority.HIGH, updatedTask.getPriority());
            verify(taskRepository).findById(1);
            verify(taskRepository).update(any(Task.class));
        }
        
        @Test
        @DisplayName("should set task priority by level")
        void shouldSetTaskPriorityByLevel() {
            TaskService service = new TaskService(taskRepository);
            
            Task existingTask = new Task("Clean kitchen", null, "");
            existingTask.setId(1);
            
            when(taskRepository.findById(1)).thenReturn(Optional.of(existingTask));
            when(taskRepository.update(any(Task.class))).thenAnswer(invocation -> invocation.getArgument(0));
            
            Task updatedTask = service.setPriorityByLevel(1, 3);
            
            assertEquals(TaskPriority.HIGH, updatedTask.getPriority());
            verify(taskRepository).findById(1);
            verify(taskRepository).update(any(Task.class));
        }
        
        @Test
        @DisplayName("should filter tasks by priority")
        void shouldFilterTasksByPriority() {
            TaskService service = new TaskService(taskRepository);
            
            Task task1 = new Task("Task 1", null, "");
            task1.setId(1);
            task1.setPriority(TaskPriority.LOW);
            
            Task task2 = new Task("Task 2", null, "");
            task2.setId(2);
            task2.setPriority(TaskPriority.MEDIUM);
            
            Task task3 = new Task("Task 3", null, "");
            task3.setId(3);
            task3.setPriority(TaskPriority.HIGH);
            
            when(taskRepository.findAll()).thenReturn(Arrays.asList(task1, task2, task3));
            
            List<Task> highPriorityTasks = service.getTasksByPriority(TaskPriority.HIGH);
            
            assertEquals(1, highPriorityTasks.size());
            assertEquals(3, highPriorityTasks.get(0).getId());
            assertEquals(TaskPriority.HIGH, highPriorityTasks.get(0).getPriority());
            
            verify(taskRepository).findAll();
        }
        
        @Test
        @DisplayName("should sort tasks by priority (highest first)")
        void shouldSortTasksByPriorityHighestFirst() {
            TaskService service = new TaskService(taskRepository);
            
            Task task1 = new Task("Task 1", null, "");
            task1.setId(1);
            task1.setPriority(TaskPriority.LOW);
            
            Task task2 = new Task("Task 2", null, "");
            task2.setId(2);
            task2.setPriority(TaskPriority.MEDIUM);
            
            Task task3 = new Task("Task 3", null, "");
            task3.setId(3);
            task3.setPriority(TaskPriority.HIGH);
            
            List<Task> unsortedTasks = Arrays.asList(task1, task2, task3);
            
            List<Task> sortedTasks = service.getTasksSortedByPriorityDescending(unsortedTasks);
            
            assertEquals(3, sortedTasks.size());
            assertEquals(3, sortedTasks.get(0).getId()); // HIGH
            assertEquals(2, sortedTasks.get(1).getId()); // MEDIUM
            assertEquals(1, sortedTasks.get(2).getId()); // LOW
        }
    }
}