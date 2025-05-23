package pl.edu.agh.isi;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.ArgumentCaptor;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.ArrayList;

@ExtendWith(MockitoExtension.class)
@DisplayName("Task Service")
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

    @Nested
    @DisplayName("Task Completion")
    class TaskCompletion {

        @Test
        @DisplayName("should mark task as completed")
        void shouldMarkTaskAsCompleted() {
            // Given
            int taskId = 1;
            Task task = new Task(taskId, "Clean basement", null, "");
            task.setCompleted(false);
            
            Task updatedTask = new Task(taskId, "Clean basement", null, "");
            updatedTask.setCompleted(true);
            
            when(taskRepository.findById(taskId)).thenReturn(Optional.of(task));
            when(taskRepository.update(any(Task.class))).thenReturn(updatedTask);

            // When
            Task result = taskService.markTaskAsCompleted(taskId, null);

            // Then
            assertTrue(result.isCompleted());
            verify(taskRepository).findById(taskId);
            verify(taskRepository).update(any(Task.class));
        }

        @Test
        @DisplayName("should mark task as completed with comment")
        void shouldMarkTaskAsCompletedWithComment() {
            // Given
            int taskId = 1;
            String commentText = "Finished cleaning";
            Task task = new Task(taskId, "Clean basement", null, "");
            task.setCompleted(false);
            
            ArgumentCaptor<Task> taskCaptor = ArgumentCaptor.forClass(Task.class);
            
            when(taskRepository.findById(taskId)).thenReturn(Optional.of(task));
            when(taskRepository.update(taskCaptor.capture())).thenAnswer(invocation -> {
                Task capturedTask = taskCaptor.getValue();
                return capturedTask;
            });

            // When
            Task result = taskService.markTaskAsCompleted(taskId, commentText);

            // Then
            assertTrue(result.isCompleted());
            assertEquals(1, result.getComments().size());
            assertEquals(commentText, result.getComments().get(0).getContent());
            verify(taskRepository).findById(taskId);
            verify(taskRepository).update(any(Task.class));
        }

        @Test
        @DisplayName("should throw exception when task not found")
        void shouldThrowExceptionWhenTaskNotFound() {
            // Given
            int taskId = 999;
            when(taskRepository.findById(taskId)).thenReturn(Optional.empty());

            // When/Then
            assertThrows(IllegalArgumentException.class, () -> taskService.markTaskAsCompleted(taskId, null));
            verify(taskRepository).findById(taskId);
            verify(taskRepository, never()).update(any(Task.class));
        }

        @Test
        @DisplayName("should handle string ID")
        void shouldHandleStringId() {
            // Given
            String stringId = "1";
            int taskId = 1;
            Task task = new Task(taskId, "Clean basement", null, "");
            task.setCompleted(false);
            
            Task updatedTask = new Task(taskId, "Clean basement", null, "");
            updatedTask.setCompleted(true);
            
            when(taskRepository.findById(taskId)).thenReturn(Optional.of(task));
            when(taskRepository.update(any(Task.class))).thenReturn(updatedTask);

            // When
            Task result = taskService.markTaskAsCompleted(stringId, null);

            // Then
            assertTrue(result.isCompleted());
        }

        @Test
        @DisplayName("should throw exception for invalid string ID")
        void shouldThrowExceptionForInvalidStringId() {
            // Given
            String invalidId = "abc";

            // When/Then
            assertThrows(IllegalArgumentException.class, () -> taskService.markTaskAsCompleted(invalidId, null));
            verifyNoInteractions(taskRepository);
        }
    }

    @Nested
    @DisplayName("Task Reopening")
    class TaskReopening {

        @Test
        @DisplayName("should reopen completed task")
        void shouldReopenCompletedTask() {
            // Given
            int taskId = 1;
            Task task = new Task(taskId, "Clean basement", null, "");
            task.setCompleted(true);
            
            Task updatedTask = new Task(taskId, "Clean basement", null, "");
            updatedTask.setCompleted(false);
            
            when(taskRepository.findById(taskId)).thenReturn(Optional.of(task));
            when(taskRepository.update(any(Task.class))).thenReturn(updatedTask);

            // When
            Task result = taskService.reopenTask(taskId, null);

            // Then
            assertFalse(result.isCompleted());
            verify(taskRepository).findById(taskId);
            verify(taskRepository).update(any(Task.class));
        }

        @Test
        @DisplayName("should reopen task with comment")
        void shouldReopenTaskWithComment() {
            // Given
            int taskId = 1;
            String commentText = "Need to do again";
            Task task = new Task(taskId, "Clean basement", null, "");
            task.setCompleted(true);
            
            ArgumentCaptor<Task> taskCaptor = ArgumentCaptor.forClass(Task.class);
            
            when(taskRepository.findById(taskId)).thenReturn(Optional.of(task));
            when(taskRepository.update(taskCaptor.capture())).thenAnswer(invocation -> {
                Task capturedTask = taskCaptor.getValue();
                return capturedTask;
            });

            // When
            Task result = taskService.reopenTask(taskId, commentText);

            // Then
            assertFalse(result.isCompleted());
            assertEquals(1, result.getComments().size());
            assertEquals(commentText, result.getComments().get(0).getContent());
            verify(taskRepository).findById(taskId);
            verify(taskRepository).update(any(Task.class));
        }
    }
    
    @Nested
    @DisplayName("Task Filtering")
    class TaskFiltering {

        @Test
        @DisplayName("should return all active tasks")
        void shouldReturnAllActiveTasks() {
            // Given
            Task task1 = new Task(1, "Clean basement", null, "");
            task1.setCompleted(false);
            
            Task task2 = new Task(2, "Clean kitchen", null, "");
            task2.setCompleted(true);
            
            Task task3 = new Task(3, "Wash dishes", null, "");
            task3.setCompleted(false);
            
            List<Task> allTasks = Arrays.asList(task1, task2, task3);
            when(taskRepository.findAll()).thenReturn(allTasks);

            // When
            List<Task> activeTasks = taskService.getActiveTasks();

            // Then
            assertEquals(2, activeTasks.size());
            assertTrue(activeTasks.contains(task1));
            assertTrue(activeTasks.contains(task3));
            assertFalse(activeTasks.contains(task2));
        }

        @Test
        @DisplayName("should return all completed tasks")
        void shouldReturnAllCompletedTasks() {
            // Given
            Task task1 = new Task(1, "Clean basement", null, "");
            task1.setCompleted(false);
            
            Task task2 = new Task(2, "Clean kitchen", null, "");
            task2.setCompleted(true);
            
            Task task3 = new Task(3, "Wash dishes", null, "");
            task3.setCompleted(false);
            
            List<Task> allTasks = Arrays.asList(task1, task2, task3);
            when(taskRepository.findAll()).thenReturn(allTasks);

            // When
            List<Task> completedTasks = taskService.getCompletedTasks();

            // Then
            assertEquals(1, completedTasks.size());
            assertTrue(completedTasks.contains(task2));
            assertFalse(completedTasks.contains(task1));
            assertFalse(completedTasks.contains(task3));
        }

        @Test
        @DisplayName("should return empty list when no active tasks exist")
        void shouldReturnEmptyListWhenNoActiveTasksExist() {
            // Given
            Task task1 = new Task(1, "Clean basement", null, "");
            task1.setCompleted(true);
            
            Task task2 = new Task(2, "Clean kitchen", null, "");
            task2.setCompleted(true);
            
            List<Task> allTasks = Arrays.asList(task1, task2);
            when(taskRepository.findAll()).thenReturn(allTasks);

            // When
            List<Task> activeTasks = taskService.getActiveTasks();

            // Then
            assertTrue(activeTasks.isEmpty());
        }

        @Test
        @DisplayName("should return empty list when no completed tasks exist")
        void shouldReturnEmptyListWhenNoCompletedTasksExist() {
            // Given
            Task task1 = new Task(1, "Clean basement", null, "");
            task1.setCompleted(false);
            
            Task task2 = new Task(2, "Clean kitchen", null, "");
            task2.setCompleted(false);
            
            List<Task> allTasks = Arrays.asList(task1, task2);
            when(taskRepository.findAll()).thenReturn(allTasks);

            // When
            List<Task> completedTasks = taskService.getCompletedTasks();

            // Then
            assertTrue(completedTasks.isEmpty());
        }
    }

    @Nested
    @DisplayName("Task Sorting")
    class TaskSorting {

        @Test
        @DisplayName("should sort tasks by due date in ascending order")
        void shouldSortTasksByDueDateAscending() {
            // Given
            LocalDateTime now = LocalDateTime.now();
            Task task1 = new Task(1, "Task with earliest date", now.minusDays(1), "");
            Task task2 = new Task(2, "Task with middle date", now, "");
            Task task3 = new Task(3, "Task with latest date", now.plusDays(1), "");
            Task task4 = new Task(4, "Task with no due date", null, "");
            
            List<Task> unsortedTasks = Arrays.asList(task1, task2, task3, task4);
            
            // When
            List<Task> sortedTasks = taskService.getTasksSortedByDueDateAscending(unsortedTasks);
            
            // Then
            assertEquals(4, sortedTasks.size());
            assertEquals(task1, sortedTasks.get(0)); // Earliest should be first
            assertEquals(task2, sortedTasks.get(1));
            assertEquals(task3, sortedTasks.get(2));
            assertEquals(task4, sortedTasks.get(3)); // Null date should be last
        }
        
        @Test
        @DisplayName("should sort tasks by due date in descending order")
        void shouldSortTasksByDueDateDescending() {
            // Given
            LocalDateTime now = LocalDateTime.now();
            Task task1 = new Task(1, "Task with earliest date", now.minusDays(1), "");
            Task task2 = new Task(2, "Task with middle date", now, "");
            Task task3 = new Task(3, "Task with latest date", now.plusDays(1), "");
            Task task4 = new Task(4, "Task with no due date", null, "");
            
            List<Task> unsortedTasks = Arrays.asList(task3, task1, task4, task2);
            
            // When
            List<Task> sortedTasks = taskService.getTasksSortedByDueDateDescending(unsortedTasks);
            
            // Then
            assertEquals(4, sortedTasks.size());
            
            // Print the actual order for debugging
            for (int i = 0; i < sortedTasks.size(); i++) {
                Task task = sortedTasks.get(i);
                System.out.println(String.format("%d: Task ID=%d, DueDate=%s", 
                    i, task.getId(), task.getDueDate()));
            }
            
            // With explicit handling in the comparator, the expected order is:
            // 1. task3 (latest date)
            // 2. task2 (middle date)
            // 3. task1 (earliest date)
            // 4. task4 (null date - explicitly placed last)
            
            // Test each ID individually to identify the failing one
            int firstId = sortedTasks.get(0).getId();
            assertEquals(3, firstId, "First task should be task3 (latest date)");
            
            int secondId = sortedTasks.get(1).getId();
            assertEquals(2, secondId, "Second task should be task2 (middle date)");
            
            int thirdId = sortedTasks.get(2).getId();
            assertEquals(1, thirdId, "Third task should be task1 (earliest date)");
            
            int fourthId = sortedTasks.get(3).getId();
            assertEquals(4, fourthId, "Fourth task should be task4 (null date)");
        }
        
        @Test
        @DisplayName("should handle empty task list for sorting")
        void shouldHandleEmptyTaskListForSorting() {
            // Given
            List<Task> emptyList = new ArrayList<>();
            
            // When
            List<Task> sortedAscending = taskService.getTasksSortedByDueDateAscending(emptyList);
            List<Task> sortedDescending = taskService.getTasksSortedByDueDateDescending(emptyList);
            
            // Then
            assertTrue(sortedAscending.isEmpty());
            assertTrue(sortedDescending.isEmpty());
        }
    }
} 