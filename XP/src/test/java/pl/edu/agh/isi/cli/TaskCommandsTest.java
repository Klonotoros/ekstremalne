package pl.edu.agh.isi.cli;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import pl.edu.agh.isi.FamilyMember;
import pl.edu.agh.isi.FamilyMemberService;
import pl.edu.agh.isi.Task;
import pl.edu.agh.isi.TaskRepository;
import pl.edu.agh.isi.TaskService;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.PrintStream;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Task CLI Commands")
class TaskCommandsTest {

    @TempDir
    Path tempDir;
    
    @Mock
    TaskRepository repository;
    
    @Mock
    TaskService service;
    
    @Nested
    @DisplayName("Complete Task Command")
    class CompleteTaskCommandTest {
        
        @Test
        @DisplayName("should complete task and display success message")
        void shouldCompleteTaskAndDisplaySuccessMessage() throws Exception {
            // Given
            TaskService mockService = mock(TaskService.class);
            
            String id = "1";
            Task task = new Task(1, "Clean basement", null, "");
            task.setCompleted(true);
            
            when(mockService.getTask(id)).thenReturn(Optional.of(task));
            when(mockService.markTaskAsCompleted(id, null)).thenReturn(task);
            
            CompleteTaskCommand command = Mockito.spy(new CompleteTaskCommand());
            doReturn(mockService).when(command).createTaskService(any());
            command.id = id;
            command.tasksFile = tempDir.resolve("test_tasks.json").toFile();
            
            // When
            ByteArrayOutputStream outContent = new ByteArrayOutputStream();
            PrintStream originalOut = System.out;
            System.setOut(new PrintStream(outContent));
            
            try {
                command.call();
                
                // Then
                assertTrue(outContent.toString().contains("marked as completed"));
                verify(mockService).getTask(id);
                verify(mockService).markTaskAsCompleted(id, null);
            } finally {
                System.setOut(originalOut);
            }
        }
        
        @Test
        @DisplayName("should complete task with comment")
        void shouldCompleteTaskWithComment() throws Exception {
            // Given
            TaskService mockService = mock(TaskService.class);
            
            String id = "1";
            String comment = "Finished cleaning";
            Task task = new Task(1, "Clean basement", null, "");
            task.setCompleted(true);
            
            when(mockService.getTask(id)).thenReturn(Optional.of(task));
            when(mockService.markTaskAsCompleted(id, comment)).thenReturn(task);
            
            CompleteTaskCommand command = Mockito.spy(new CompleteTaskCommand());
            doReturn(mockService).when(command).createTaskService(any());
            command.id = id;
            command.comment = comment;
            command.tasksFile = tempDir.resolve("test_tasks.json").toFile();
            
            // When
            ByteArrayOutputStream outContent = new ByteArrayOutputStream();
            PrintStream originalOut = System.out;
            System.setOut(new PrintStream(outContent));
            
            try {
                command.call();
                
                // Then
                String output = outContent.toString();
                assertTrue(output.contains("marked as completed"));
                assertTrue(output.contains("Added comment: " + comment));
                verify(mockService).getTask(id);
                verify(mockService).markTaskAsCompleted(id, comment);
            } finally {
                System.setOut(originalOut);
            }
        }
        
        @Test
        @DisplayName("should show error when task not found")
        void shouldShowErrorWhenTaskNotFound() throws Exception {
            // Given
            TaskService mockService = mock(TaskService.class);
            
            String id = "999";
            when(mockService.getTask(id)).thenReturn(Optional.empty());
            
            CompleteTaskCommand command = Mockito.spy(new CompleteTaskCommand());
            doReturn(mockService).when(command).createTaskService(any());
            command.id = id;
            command.tasksFile = tempDir.resolve("test_tasks.json").toFile();
            
            // When
            ByteArrayOutputStream errContent = new ByteArrayOutputStream();
            PrintStream originalErr = System.err;
            System.setErr(new PrintStream(errContent));
            
            try {
                command.call();
                
                // Then
                assertTrue(errContent.toString().contains("Task with ID 999 not found"));
                verify(mockService).getTask(id);
                verify(mockService, never()).markTaskAsCompleted(anyString(), anyString());
            } finally {
                System.setErr(originalErr);
            }
        }
    }
    
    @Nested
    @DisplayName("List Tasks Command")
    class ListTasksCommandTest {
        
        @Test
        @DisplayName("should list active tasks by default")
        void shouldListActiveTasksByDefault() throws Exception {
            // Given
            TaskService mockService = mock(TaskService.class);
            
            List<Task> tasks = new ArrayList<>();
            Task task1 = new Task(1, "Clean basement", null, "");
            Task task2 = new Task(2, "Wash dishes", null, "");
            tasks.add(task1);
            tasks.add(task2);
            
            when(mockService.getActiveTasks()).thenReturn(tasks);
            
            ListTasksCommand command = Mockito.spy(new ListTasksCommand());
            doReturn(mockService).when(command).createTaskService(any());
            doReturn(mock(FamilyMemberService.class)).when(command).createFamilyMemberService(any());
            command.tasksFile = tempDir.resolve("test_tasks.json").toFile();
            
            // When
            ByteArrayOutputStream outContent = new ByteArrayOutputStream();
            PrintStream originalOut = System.out;
            System.setOut(new PrintStream(outContent));
            
            try {
                command.call();
                
                // Then
                String output = outContent.toString();
                assertTrue(output.contains("Active Tasks"));
                assertTrue(output.contains("Clean basement"));
                assertTrue(output.contains("Wash dishes"));
                assertTrue(output.contains("Total: 2 task(s)"));
                verify(mockService).getActiveTasks();
            } finally {
                System.setOut(originalOut);
            }
        }
        
        @Test
        @DisplayName("should list all tasks when requested")
        void shouldListAllTasksWhenRequested() throws Exception {
            // Given
            TaskService mockService = mock(TaskService.class);
            
            List<Task> allTasks = new ArrayList<>();
            Task task1 = new Task(1, "Clean basement", null, "");
            Task task2 = new Task(2, "Wash dishes", null, "");
            task2.setCompleted(true);
            allTasks.add(task1);
            allTasks.add(task2);
            
            when(mockService.getAllTasks()).thenReturn(allTasks);
            
            ListTasksCommand command = Mockito.spy(new ListTasksCommand());
            doReturn(mockService).when(command).createTaskService(any());
            doReturn(mock(FamilyMemberService.class)).when(command).createFamilyMemberService(any());
            command.showAll = true;
            command.tasksFile = tempDir.resolve("test_tasks.json").toFile();
            
            // When
            ByteArrayOutputStream outContent = new ByteArrayOutputStream();
            PrintStream originalOut = System.out;
            System.setOut(new PrintStream(outContent));
            
            try {
                command.call();
                
                // Then
                String output = outContent.toString();
                assertTrue(output.contains("All Tasks"));
                assertTrue(output.contains("Clean basement"));
                assertTrue(output.contains("Wash dishes"));
                assertTrue(output.contains("Total: 2 task(s)"));
                verify(mockService).getAllTasks();
            } finally {
                System.setOut(originalOut);
            }
        }
        
        @Test
        @DisplayName("should list only completed tasks when requested")
        void shouldListOnlyCompletedTasksWhenRequested() throws Exception {
            // Given
            TaskService mockService = mock(TaskService.class);
            
            List<Task> completedTasks = new ArrayList<>();
            Task task = new Task(2, "Wash dishes", null, "");
            task.setCompleted(true);
            completedTasks.add(task);
            
            when(mockService.getCompletedTasks()).thenReturn(completedTasks);
            
            ListTasksCommand command = Mockito.spy(new ListTasksCommand());
            doReturn(mockService).when(command).createTaskService(any());
            doReturn(mock(FamilyMemberService.class)).when(command).createFamilyMemberService(any());
            command.showOnlyCompleted = true;
            command.tasksFile = tempDir.resolve("test_tasks.json").toFile();
            
            // When
            ByteArrayOutputStream outContent = new ByteArrayOutputStream();
            PrintStream originalOut = System.out;
            System.setOut(new PrintStream(outContent));
            
            try {
                command.call();
                
                // Then
                String output = outContent.toString();
                assertTrue(output.contains("Completed Tasks"));
                assertTrue(output.contains("Wash dishes"));
                assertTrue(output.contains("Total: 1 task(s)"));
                verify(mockService).getCompletedTasks();
            } finally {
                System.setOut(originalOut);
            }
        }
        
        @Test
        @DisplayName("should show message when no tasks found")
        void shouldShowMessageWhenNoTasksFound() throws Exception {
            // Given
            TaskService mockService = mock(TaskService.class);
            
            List<Task> emptyList = new ArrayList<>();
            when(mockService.getActiveTasks()).thenReturn(emptyList);
            
            ListTasksCommand command = Mockito.spy(new ListTasksCommand());
            doReturn(mockService).when(command).createTaskService(any());
            doReturn(mock(FamilyMemberService.class)).when(command).createFamilyMemberService(any());
            command.tasksFile = tempDir.resolve("test_tasks.json").toFile();
            
            // When
            ByteArrayOutputStream outContent = new ByteArrayOutputStream();
            PrintStream originalOut = System.out;
            System.setOut(new PrintStream(outContent));
            
            try {
                command.call();
                
                // Then
                assertTrue(outContent.toString().contains("No tasks found"));
                verify(mockService).getActiveTasks();
            } finally {
                System.setOut(originalOut);
            }
        }
        
        @Test
        @DisplayName("should sort tasks by due date ascending when requested")
        void shouldSortTasksByDueDateAscendingWhenRequested() throws Exception {
            // Given
            TaskService mockService = mock(TaskService.class);
            
            List<Task> tasks = new ArrayList<>();
            Task task1 = new Task(1, "Clean basement", LocalDateTime.now().plusDays(2), "");
            Task task2 = new Task(2, "Wash dishes", LocalDateTime.now().plusDays(1), "");
            tasks.add(task1);
            tasks.add(task2);
            
            List<Task> sortedTasks = new ArrayList<>();
            sortedTasks.add(task2); // Due date earlier
            sortedTasks.add(task1); // Due date later
            
            when(mockService.getActiveTasks()).thenReturn(tasks);
            when(mockService.getTasksSortedByDueDateAscending(tasks)).thenReturn(sortedTasks);
            
            ListTasksCommand command = Mockito.spy(new ListTasksCommand());
            doReturn(mockService).when(command).createTaskService(any());
            doReturn(mock(FamilyMemberService.class)).when(command).createFamilyMemberService(any());
            command.sortAscending = true;
            command.tasksFile = tempDir.resolve("test_tasks.json").toFile();
            
            // When
            ByteArrayOutputStream outContent = new ByteArrayOutputStream();
            PrintStream originalOut = System.out;
            System.setOut(new PrintStream(outContent));
            
            try {
                command.call();
                
                // Then
                String output = outContent.toString();
                assertTrue(output.contains("Active Tasks (Sorted by due date, earliest first)"));
                verify(mockService).getActiveTasks();
                verify(mockService).getTasksSortedByDueDateAscending(tasks);
            } finally {
                System.setOut(originalOut);
            }
        }
        
        @Test
        @DisplayName("should sort tasks by due date descending when requested")
        void shouldSortTasksByDueDateDescendingWhenRequested() throws Exception {
            // Given
            TaskService mockService = mock(TaskService.class);
            
            List<Task> tasks = new ArrayList<>();
            Task task1 = new Task(1, "Clean basement", LocalDateTime.now().plusDays(1), "");
            Task task2 = new Task(2, "Wash dishes", LocalDateTime.now().plusDays(2), "");
            tasks.add(task1);
            tasks.add(task2);
            
            List<Task> sortedTasks = new ArrayList<>();
            sortedTasks.add(task2); // Due date later
            sortedTasks.add(task1); // Due date earlier
            
            when(mockService.getActiveTasks()).thenReturn(tasks);
            when(mockService.getTasksSortedByDueDateDescending(tasks)).thenReturn(sortedTasks);
            
            ListTasksCommand command = Mockito.spy(new ListTasksCommand());
            doReturn(mockService).when(command).createTaskService(any());
            doReturn(mock(FamilyMemberService.class)).when(command).createFamilyMemberService(any());
            command.sortDescending = true;
            command.tasksFile = tempDir.resolve("test_tasks.json").toFile();
            
            // When
            ByteArrayOutputStream outContent = new ByteArrayOutputStream();
            PrintStream originalOut = System.out;
            System.setOut(new PrintStream(outContent));
            
            try {
                command.call();
                
                // Then
                String output = outContent.toString();
                assertTrue(output.contains("Active Tasks (Sorted by due date, latest first)"));
                verify(mockService).getActiveTasks();
                verify(mockService).getTasksSortedByDueDateDescending(tasks);
            } finally {
                System.setOut(originalOut);
            }
        }
        
        @Test
        @DisplayName("should prioritize ascending when both sort options specified")
        void shouldPrioritizeAscendingWhenBothSortOptionsSpecified() throws Exception {
            // Given
            TaskService mockService = mock(TaskService.class);
            
            List<Task> tasks = new ArrayList<>();
            Task task1 = new Task(1, "Clean basement", LocalDateTime.now().plusDays(2), "");
            Task task2 = new Task(2, "Wash dishes", LocalDateTime.now().plusDays(1), "");
            tasks.add(task1);
            tasks.add(task2);
            
            List<Task> sortedTasks = new ArrayList<>();
            sortedTasks.add(task2); // Due date earlier
            sortedTasks.add(task1); // Due date later
            
            when(mockService.getActiveTasks()).thenReturn(tasks);
            when(mockService.getTasksSortedByDueDateAscending(tasks)).thenReturn(sortedTasks);
            
            ListTasksCommand command = Mockito.spy(new ListTasksCommand());
            doReturn(mockService).when(command).createTaskService(any());
            doReturn(mock(FamilyMemberService.class)).when(command).createFamilyMemberService(any());
            command.sortAscending = true;
            command.sortDescending = true;
            command.tasksFile = tempDir.resolve("test_tasks.json").toFile();
            
            // When
            ByteArrayOutputStream outContent = new ByteArrayOutputStream();
            PrintStream originalOut = System.out;
            System.setOut(new PrintStream(outContent));
            
            ByteArrayOutputStream errContent = new ByteArrayOutputStream();
            PrintStream originalErr = System.err;
            System.setErr(new PrintStream(errContent));
            
            try {
                command.call();
                
                // Then
                assertTrue(errContent.toString().contains("Warning: Both ascending and descending sort options specified"));
                verify(mockService).getActiveTasks();
                verify(mockService).getTasksSortedByDueDateAscending(tasks);
                verify(mockService, never()).getTasksSortedByDueDateDescending(any());
            } finally {
                System.setOut(originalOut);
                System.setErr(originalErr);
            }
        }
    }
    
    @Nested
    @DisplayName("Assign Task Command")
    class AssignTaskCommandTest {
        
        @Test
        @DisplayName("should assign task to family member")
        void shouldAssignTaskToFamilyMember() throws Exception {
            // Given
            TaskService mockTaskService = mock(TaskService.class);
            FamilyMemberService mockFamilyMemberService = mock(FamilyMemberService.class);
            
            String taskId = "1";
            String familyMemberId = "2";
            Task task = new Task(1, "Clean basement", null, "");
            FamilyMember member = new FamilyMember(2, "John Smith");
            Task updatedTask = new Task(1, "Clean basement", null, "");
            updatedTask.setAssignedTo(familyMemberId);
            
            when(mockTaskService.getTask(taskId)).thenReturn(Optional.of(task));
            when(mockFamilyMemberService.getFamilyMember(familyMemberId)).thenReturn(Optional.of(member));
            when(mockTaskService.assignTask(taskId, familyMemberId)).thenReturn(updatedTask);
            
            AssignTaskCommand command = Mockito.spy(new AssignTaskCommand());
            doReturn(mockTaskService).when(command).createTaskService(any());
            doReturn(mockFamilyMemberService).when(command).createFamilyMemberService(any());
            command.taskId = taskId;
            command.familyMemberId = familyMemberId;
            command.tasksFile = tempDir.resolve("test_tasks.json").toFile();
            command.familyMembersFile = tempDir.resolve("test_family_members.json").toFile();
            
            // When
            ByteArrayOutputStream outContent = new ByteArrayOutputStream();
            PrintStream originalOut = System.out;
            System.setOut(new PrintStream(outContent));
            
            try {
                command.call();
                
                // Then
                String output = outContent.toString();
                assertTrue(output.contains("assigned to John Smith"));
                verify(mockTaskService).getTask(taskId);
                verify(mockFamilyMemberService).getFamilyMember(familyMemberId);
                verify(mockTaskService).assignTask(taskId, familyMemberId);
            } finally {
                System.setOut(originalOut);
            }
        }
        
        @Test
        @DisplayName("should show error when task not found")
        void shouldShowErrorWhenTaskNotFound() throws Exception {
            // Given
            TaskService mockTaskService = mock(TaskService.class);
            FamilyMemberService mockFamilyMemberService = mock(FamilyMemberService.class);
            
            String taskId = "999";
            String familyMemberId = "2";
            
            when(mockTaskService.getTask(taskId)).thenReturn(Optional.empty());
            
            AssignTaskCommand command = Mockito.spy(new AssignTaskCommand());
            doReturn(mockTaskService).when(command).createTaskService(any());
            doReturn(mockFamilyMemberService).when(command).createFamilyMemberService(any());
            command.taskId = taskId;
            command.familyMemberId = familyMemberId;
            command.tasksFile = tempDir.resolve("test_tasks.json").toFile();
            command.familyMembersFile = tempDir.resolve("test_family_members.json").toFile();
            
            // When
            ByteArrayOutputStream errContent = new ByteArrayOutputStream();
            PrintStream originalErr = System.err;
            System.setErr(new PrintStream(errContent));
            
            try {
                command.call();
                
                // Then
                String error = errContent.toString();
                assertTrue(error.contains("Task with ID 999 not found"));
                verify(mockTaskService).getTask(taskId);
                verify(mockFamilyMemberService, never()).getFamilyMember(anyString());
                verify(mockTaskService, never()).assignTask(anyString(), anyString());
            } finally {
                System.setErr(originalErr);
            }
        }
        
        @Test
        @DisplayName("should show error when family member not found")
        void shouldShowErrorWhenFamilyMemberNotFound() throws Exception {
            // Given
            TaskService mockTaskService = mock(TaskService.class);
            FamilyMemberService mockFamilyMemberService = mock(FamilyMemberService.class);
            
            String taskId = "1";
            String familyMemberId = "999";
            Task task = new Task(1, "Clean basement", null, "");
            
            when(mockTaskService.getTask(taskId)).thenReturn(Optional.of(task));
            when(mockFamilyMemberService.getFamilyMember(familyMemberId)).thenReturn(Optional.empty());
            
            AssignTaskCommand command = Mockito.spy(new AssignTaskCommand());
            doReturn(mockTaskService).when(command).createTaskService(any());
            doReturn(mockFamilyMemberService).when(command).createFamilyMemberService(any());
            command.taskId = taskId;
            command.familyMemberId = familyMemberId;
            command.tasksFile = tempDir.resolve("test_tasks.json").toFile();
            command.familyMembersFile = tempDir.resolve("test_family_members.json").toFile();
            
            // When
            ByteArrayOutputStream errContent = new ByteArrayOutputStream();
            PrintStream originalErr = System.err;
            System.setErr(new PrintStream(errContent));
            
            try {
                command.call();
                
                // Then
                String error = errContent.toString();
                assertTrue(error.contains("Family member with ID 999 not found"));
                verify(mockTaskService).getTask(taskId);
                verify(mockFamilyMemberService).getFamilyMember(familyMemberId);
                verify(mockTaskService, never()).assignTask(anyString(), anyString());
            } finally {
                System.setErr(originalErr);
            }
        }
        
        @Test
        @DisplayName("should show error when task is already assigned")
        void shouldShowErrorWhenTaskIsAlreadyAssigned() throws Exception {
            // Given
            TaskService mockTaskService = mock(TaskService.class);
            FamilyMemberService mockFamilyMemberService = mock(FamilyMemberService.class);
            
            String taskId = "1";
            String familyMemberId = "2";
            Task task = new Task(1, "Clean basement", null, "");
            task.setAssignedTo("3"); // Already assigned to another member
            FamilyMember member = new FamilyMember(2, "John Smith");
            
            when(mockTaskService.getTask(taskId)).thenReturn(Optional.of(task));
            when(mockFamilyMemberService.getFamilyMember(familyMemberId)).thenReturn(Optional.of(member));
            
            AssignTaskCommand command = Mockito.spy(new AssignTaskCommand());
            doReturn(mockTaskService).when(command).createTaskService(any());
            doReturn(mockFamilyMemberService).when(command).createFamilyMemberService(any());
            command.taskId = taskId;
            command.familyMemberId = familyMemberId;
            command.tasksFile = tempDir.resolve("test_tasks.json").toFile();
            command.familyMembersFile = tempDir.resolve("test_family_members.json").toFile();
            
            // When
            ByteArrayOutputStream errContent = new ByteArrayOutputStream();
            PrintStream originalErr = System.err;
            System.setErr(new PrintStream(errContent));
            
            try {
                command.call();
                
                // Then
                String error = errContent.toString();
                assertTrue(error.contains("Task is already assigned"));
                verify(mockTaskService).getTask(taskId);
                verify(mockFamilyMemberService).getFamilyMember(familyMemberId);
                verify(mockTaskService, never()).assignTask(anyString(), anyString());
            } finally {
                System.setErr(originalErr);
            }
        }
    }
    
    @Nested
    @DisplayName("Unassign Task Command")
    class UnassignTaskCommandTest {
        
        @Test
        @DisplayName("should unassign task from family member")
        void shouldUnassignTaskFromFamilyMember() throws Exception {
            // Given
            TaskService mockTaskService = mock(TaskService.class);
            FamilyMemberService mockFamilyMemberService = mock(FamilyMemberService.class);
            
            String taskId = "1";
            String familyMemberId = "2";
            Task task = new Task(1, "Clean basement", null, "");
            task.setAssignedTo(familyMemberId);
            FamilyMember member = new FamilyMember(2, "John Smith");
            Task updatedTask = new Task(1, "Clean basement", null, "");
            updatedTask.setAssignedTo(null); // Unassigned
            
            when(mockTaskService.getTask(taskId)).thenReturn(Optional.of(task));
            when(mockFamilyMemberService.getFamilyMember(familyMemberId)).thenReturn(Optional.of(member));
            when(mockTaskService.unassignTask(taskId)).thenReturn(updatedTask);
            
            UnassignTaskCommand command = Mockito.spy(new UnassignTaskCommand());
            doReturn(mockTaskService).when(command).createTaskService(any());
            doReturn(mockFamilyMemberService).when(command).createFamilyMemberService(any());
            command.taskId = taskId;
            command.tasksFile = tempDir.resolve("test_tasks.json").toFile();
            command.familyMembersFile = tempDir.resolve("test_family_members.json").toFile();
            
            // When
            ByteArrayOutputStream outContent = new ByteArrayOutputStream();
            PrintStream originalOut = System.out;
            System.setOut(new PrintStream(outContent));
            
            try {
                command.call();
                
                // Then
                String output = outContent.toString();
                assertTrue(output.contains("unassigned from"));
                verify(mockTaskService).getTask(taskId);
                verify(mockTaskService).unassignTask(taskId);
            } finally {
                System.setOut(originalOut);
            }
        }
        
        @Test
        @DisplayName("should show error when task not found")
        void shouldShowErrorWhenTaskNotFound() throws Exception {
            // Given
            TaskService mockTaskService = mock(TaskService.class);
            FamilyMemberService mockFamilyMemberService = mock(FamilyMemberService.class);
            
            String taskId = "999";
            
            when(mockTaskService.getTask(taskId)).thenReturn(Optional.empty());
            
            UnassignTaskCommand command = Mockito.spy(new UnassignTaskCommand());
            doReturn(mockTaskService).when(command).createTaskService(any());
            doReturn(mockFamilyMemberService).when(command).createFamilyMemberService(any());
            command.taskId = taskId;
            command.tasksFile = tempDir.resolve("test_tasks.json").toFile();
            command.familyMembersFile = tempDir.resolve("test_family_members.json").toFile();
            
            // When
            ByteArrayOutputStream errContent = new ByteArrayOutputStream();
            PrintStream originalErr = System.err;
            System.setErr(new PrintStream(errContent));
            
            try {
                command.call();
                
                // Then
                String error = errContent.toString();
                assertTrue(error.contains("Task with ID 999 not found"));
                verify(mockTaskService).getTask(taskId);
                verify(mockTaskService, never()).unassignTask(anyString());
            } finally {
                System.setErr(originalErr);
            }
        }
        
        @Test
        @DisplayName("should show error when task is not assigned")
        void shouldShowErrorWhenTaskIsNotAssigned() throws Exception {
            // Given
            TaskService mockTaskService = mock(TaskService.class);
            FamilyMemberService mockFamilyMemberService = mock(FamilyMemberService.class);
            
            String taskId = "1";
            Task task = new Task(1, "Clean basement", null, "");
            // Task is not assigned
            
            when(mockTaskService.getTask(taskId)).thenReturn(Optional.of(task));
            
            UnassignTaskCommand command = Mockito.spy(new UnassignTaskCommand());
            doReturn(mockTaskService).when(command).createTaskService(any());
            doReturn(mockFamilyMemberService).when(command).createFamilyMemberService(any());
            command.taskId = taskId;
            command.tasksFile = tempDir.resolve("test_tasks.json").toFile();
            command.familyMembersFile = tempDir.resolve("test_family_members.json").toFile();
            
            // When
            ByteArrayOutputStream errContent = new ByteArrayOutputStream();
            PrintStream originalErr = System.err;
            System.setErr(new PrintStream(errContent));
            
            try {
                command.call();
                
                // Then
                String error = errContent.toString();
                assertTrue(error.contains("Task is not assigned"));
                verify(mockTaskService).getTask(taskId);
                verify(mockTaskService, never()).unassignTask(anyString());
            } finally {
                System.setErr(originalErr);
            }
        }
    }
} 