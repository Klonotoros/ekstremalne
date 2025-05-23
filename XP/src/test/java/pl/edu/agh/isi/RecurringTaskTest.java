package pl.edu.agh.isi;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Recurring Tasks")
public class RecurringTaskTest {
    
    @Mock
    private TaskRepository taskRepository;
    
    @Nested
    @DisplayName("RecurrenceInterval")
    class RecurrenceIntervalTest {
        
        @Test
        @DisplayName("should get interval from string")
        void shouldGetIntervalFromString() {
            assertEquals(RecurrenceInterval.DAILY, RecurrenceInterval.fromString("DAILY"));
            assertEquals(RecurrenceInterval.WEEKLY, RecurrenceInterval.fromString("WEEKLY"));
            assertEquals(RecurrenceInterval.MONTHLY, RecurrenceInterval.fromString("MONTHLY"));
            
            // Case insensitive
            assertEquals(RecurrenceInterval.DAILY, RecurrenceInterval.fromString("daily"));
            assertEquals(RecurrenceInterval.WEEKLY, RecurrenceInterval.fromString("Weekly"));
            assertEquals(RecurrenceInterval.MONTHLY, RecurrenceInterval.fromString("Monthly"));
        }
        
        @Test
        @DisplayName("should return null for invalid interval")
        void shouldReturnNullForInvalidInterval() {
            assertNull(RecurrenceInterval.fromString(""));
            assertNull(RecurrenceInterval.fromString(null));
            assertNull(RecurrenceInterval.fromString("invalid"));
            assertNull(RecurrenceInterval.fromString("yearly"));
        }
        
        @Test
        @DisplayName("should have correct display names")
        void shouldHaveCorrectDisplayNames() {
            assertEquals("Daily", RecurrenceInterval.DAILY.getDisplayName());
            assertEquals("Weekly", RecurrenceInterval.WEEKLY.getDisplayName());
            assertEquals("Monthly", RecurrenceInterval.MONTHLY.getDisplayName());
        }
    }
    
    @Nested
    @DisplayName("RecurrenceConfig")
    class RecurrenceConfigTest {
        
        @Test
        @DisplayName("should create config with unlimited occurrences")
        void shouldCreateConfigWithUnlimitedOccurrences() {
            RecurrenceConfig config = new RecurrenceConfig(RecurrenceInterval.DAILY);
            
            assertEquals(RecurrenceInterval.DAILY, config.getInterval());
            assertEquals(-1, config.getOccurrences());
            assertNull(config.getEndDate());
            assertTrue(config.isUnlimited());
        }
        
        @Test
        @DisplayName("should create config with limited occurrences")
        void shouldCreateConfigWithLimitedOccurrences() {
            RecurrenceConfig config = new RecurrenceConfig(RecurrenceInterval.WEEKLY, 10);
            
            assertEquals(RecurrenceInterval.WEEKLY, config.getInterval());
            assertEquals(10, config.getOccurrences());
            assertNull(config.getEndDate());
            assertFalse(config.isUnlimited());
        }
        
        @Test
        @DisplayName("should create config with end date")
        void shouldCreateConfigWithEndDate() {
            LocalDateTime endDate = LocalDateTime.now().plusMonths(3);
            RecurrenceConfig config = new RecurrenceConfig(RecurrenceInterval.MONTHLY, endDate);
            
            assertEquals(RecurrenceInterval.MONTHLY, config.getInterval());
            assertEquals(-1, config.getOccurrences());
            assertEquals(endDate, config.getEndDate());
            assertFalse(config.isUnlimited());
        }
        
        @Test
        @DisplayName("should calculate next due date correctly")
        void shouldCalculateNextDueDateCorrectly() {
            LocalDateTime baseDate = LocalDateTime.of(2024, 8, 15, 10, 0);
            
            RecurrenceConfig dailyConfig = new RecurrenceConfig(RecurrenceInterval.DAILY);
            assertEquals(baseDate.plusDays(1), dailyConfig.calculateNextDueDate(baseDate));
            
            RecurrenceConfig weeklyConfig = new RecurrenceConfig(RecurrenceInterval.WEEKLY);
            assertEquals(baseDate.plusWeeks(1), weeklyConfig.calculateNextDueDate(baseDate));
            
            RecurrenceConfig monthlyConfig = new RecurrenceConfig(RecurrenceInterval.MONTHLY);
            assertEquals(baseDate.plusMonths(1), monthlyConfig.calculateNextDueDate(baseDate));
        }
        
        @Test
        @DisplayName("should validate recurrence based on occurrences")
        void shouldValidateRecurrenceBasedOnOccurrences() {
            RecurrenceConfig config = new RecurrenceConfig(RecurrenceInterval.WEEKLY, 5);
            
            LocalDateTime currentDate = LocalDateTime.now();
            
            assertTrue(config.isStillValid(currentDate, 1));
            assertTrue(config.isStillValid(currentDate, 4));
            assertFalse(config.isStillValid(currentDate, 5));
            assertFalse(config.isStillValid(currentDate, 10));
        }
        
        @Test
        @DisplayName("should validate recurrence based on end date")
        void shouldValidateRecurrenceBasedOnEndDate() {
            LocalDateTime endDate = LocalDateTime.of(2024, 12, 31, 23, 59);
            RecurrenceConfig config = new RecurrenceConfig(RecurrenceInterval.MONTHLY, endDate);
            
            LocalDateTime beforeEnd = LocalDateTime.of(2024, 12, 15, 10, 0);
            LocalDateTime afterEnd = LocalDateTime.of(2025, 1, 1, 0, 0);
            
            assertTrue(config.isStillValid(beforeEnd, 1));
            assertFalse(config.isStillValid(afterEnd, 1));
        }
    }
    
    @Nested
    @DisplayName("Task with Recurrence")
    class TaskWithRecurrenceTest {
        
        @Test
        @DisplayName("should create task with recurrence config")
        void shouldCreateTaskWithRecurrenceConfig() {
            RecurrenceConfig config = new RecurrenceConfig(RecurrenceInterval.WEEKLY);
            Task task = new Task("Clean kitchen", LocalDateTime.now(), "", config);
            
            assertTrue(task.isRecurring());
            assertFalse(task.isRecurrenceInstance());
            assertEquals(config, task.getRecurrenceConfig());
        }
        
        @Test
        @DisplayName("should create recurrence instance")
        void shouldCreateRecurrenceInstance() {
            Task task = new Task("Clean kitchen", LocalDateTime.now(), "", TaskPriority.MEDIUM, 1, 2);
            
            assertFalse(task.isRecurring());
            assertTrue(task.isRecurrenceInstance());
            assertEquals(Integer.valueOf(1), task.getParentTaskId());
            assertEquals(Integer.valueOf(2), task.getRecurrenceNumber());
        }
    }
    
    @Nested
    @DisplayName("RecurringTaskService")
    class RecurringTaskServiceTest {
        
        @Test
        @DisplayName("should create recurring task")
        void shouldCreateRecurringTask() {
            RecurringTaskService service = new RecurringTaskService(taskRepository);
            RecurrenceConfig config = new RecurrenceConfig(RecurrenceInterval.DAILY);
            
            when(taskRepository.save(any(Task.class))).thenAnswer(invocation -> {
                Task task = invocation.getArgument(0);
                task.setId(1);
                return task;
            });
            
            Task task = service.createRecurringTask("Clean kitchen", LocalDateTime.now(), 
                                               "", TaskPriority.HIGH, config);
            
            assertEquals(1, task.getId());
            assertEquals("Clean kitchen", task.getTopic());
            assertEquals(TaskPriority.HIGH, task.getPriority());
            assertTrue(task.isRecurring());
            assertEquals(config, task.getRecurrenceConfig());
            
            verify(taskRepository).save(any(Task.class));
        }
        
        @Test
        @DisplayName("should set task as recurring")
        void shouldSetTaskAsRecurring() {
            RecurringTaskService service = new RecurringTaskService(taskRepository);
            RecurrenceConfig config = new RecurrenceConfig(RecurrenceInterval.WEEKLY);
            
            Task existingTask = new Task("Clean kitchen", LocalDateTime.now(), "");
            existingTask.setId(1);
            
            when(taskRepository.findById(1)).thenReturn(Optional.of(existingTask));
            when(taskRepository.update(any(Task.class))).thenAnswer(invocation -> invocation.getArgument(0));
            
            Task updatedTask = service.setTaskAsRecurring(1, config);
            
            assertTrue(updatedTask.isRecurring());
            assertEquals(config, updatedTask.getRecurrenceConfig());
            
            verify(taskRepository).findById(1);
            verify(taskRepository).update(any(Task.class));
        }
        
        @Test
        @DisplayName("should generate next instance of recurring task")
        void shouldGenerateNextInstanceOfRecurringTask() {
            RecurringTaskService service = new RecurringTaskService(taskRepository);
            
            LocalDateTime dueDate = LocalDateTime.of(2024, 8, 15, 10, 0);
            RecurrenceConfig config = new RecurrenceConfig(RecurrenceInterval.WEEKLY);
            
            Task parentTask = new Task("Clean kitchen", dueDate, "");
            parentTask.setId(1);
            parentTask.setRecurrenceConfig(config);
            
            when(taskRepository.findById(1)).thenReturn(Optional.of(parentTask));
            when(taskRepository.save(any(Task.class))).thenAnswer(invocation -> {
                Task task = invocation.getArgument(0);
                task.setId(2);
                return task;
            });
            
            Optional<Task> nextInstanceOpt = service.generateNextInstance(1);
            
            assertTrue(nextInstanceOpt.isPresent());
            Task nextInstance = nextInstanceOpt.get();
            
            assertEquals(2, nextInstance.getId());
            assertEquals("Clean kitchen", nextInstance.getTopic());
            assertEquals(dueDate.plusWeeks(1), nextInstance.getDueDate());
            assertTrue(nextInstance.isRecurrenceInstance());
            assertEquals(Integer.valueOf(1), nextInstance.getParentTaskId());
            assertEquals(Integer.valueOf(1), nextInstance.getRecurrenceNumber());
            
            verify(taskRepository).findById(1);
            verify(taskRepository).save(any(Task.class));
        }
    }
    
    @Nested
    @DisplayName("TaskService with Recurrence")
    class TaskServiceWithRecurrenceTest {
        
        @Test
        @DisplayName("should generate new instance when completing recurring task")
        void shouldGenerateNewInstanceWhenCompletingRecurringTask() {
            TaskService taskService = new TaskService(taskRepository);
            RecurringTaskService recurringTaskService = spy(new RecurringTaskService(taskRepository));
            taskService.setRecurringTaskService(recurringTaskService);
            
            LocalDateTime dueDate = LocalDateTime.of(2024, 8, 15, 10, 0);
            RecurrenceConfig config = new RecurrenceConfig(RecurrenceInterval.WEEKLY);
            
            Task parentTask = new Task("Clean kitchen", dueDate, "");
            parentTask.setId(1);
            parentTask.setRecurrenceConfig(config);
            
            Task newInstance = new Task("Clean kitchen", dueDate.plusWeeks(1), "",
                                       TaskPriority.MEDIUM, 1, 1);
            newInstance.setId(2);
            
            // Set up mocks
            when(taskRepository.findById(1)).thenReturn(Optional.of(parentTask));
            when(taskRepository.update(any(Task.class))).thenReturn(parentTask);
            
            // Mock the recurringTaskService to avoid the second findById call
            doReturn(Optional.of(newInstance)).when(recurringTaskService).generateNextInstance(1);
            
            // Execute
            taskService.markTaskAsCompleted(1, "Completed");
            
            // Verify
            verify(taskRepository).findById(1); // Only called once now
            verify(taskRepository, times(2)).update(any(Task.class));
            verify(recurringTaskService).generateNextInstance(1);
        }
    }
} 