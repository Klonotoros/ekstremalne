package pl.edu.agh.isi.cli;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import pl.edu.agh.isi.Task;
import pl.edu.agh.isi.TaskRepository;
import pl.edu.agh.isi.TaskService;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.PrintStream;
import java.nio.file.Path;
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
        void shouldCompleteTaskAndDisplaySuccessMessage() {
            // Given
            String id = "1";
            Task task = new Task(1, "Clean basement", null, "");
            task.setCompleted(true);
            
            // Create a custom CompleteTaskCommand that uses our mocked service
            CompleteTaskCommand command = new CompleteTaskCommand() {
                @Override
                public Integer call() throws Exception {
                    TaskRepository repository = mock(TaskRepository.class);
                    TaskService service = mock(TaskService.class);
                    
                    when(service.getTask(id)).thenReturn(Optional.of(task));
                    when(service.markTaskAsCompleted(id, null)).thenReturn(task);
                    
                    // Capture standard output
                    ByteArrayOutputStream outContent = new ByteArrayOutputStream();
                    PrintStream originalOut = System.out;
                    System.setOut(new PrintStream(outContent));
                    
                    try {
                        this.id = id;
                        this.tasksFile = tempDir.resolve("test_tasks.json").toFile();
                        
                        // Replace actual repository creation with our mock
                        return super.call();
                    } finally {
                        System.setOut(originalOut);
                    }
                }
            };
            
            // When
            try {
                ByteArrayOutputStream outContent = new ByteArrayOutputStream();
                PrintStream originalOut = System.out;
                System.setOut(new PrintStream(outContent));
                
                command.call();
                
                System.setOut(originalOut);
                
                // Then
                assertTrue(outContent.toString().contains("marked as completed"));
            } catch (Exception e) {
                fail("Exception should not be thrown: " + e.getMessage());
            }
        }
        
        @Test
        @DisplayName("should complete task with comment")
        void shouldCompleteTaskWithComment() {
            // Given
            String id = "1";
            String comment = "Finished cleaning";
            Task task = new Task(1, "Clean basement", null, "");
            task.setCompleted(true);
            
            // Create a custom CompleteTaskCommand that uses our mocked service
            CompleteTaskCommand command = new CompleteTaskCommand() {
                @Override
                public Integer call() throws Exception {
                    TaskRepository repository = mock(TaskRepository.class);
                    TaskService service = mock(TaskService.class);
                    
                    when(service.getTask(id)).thenReturn(Optional.of(task));
                    when(service.markTaskAsCompleted(id, comment)).thenReturn(task);
                    
                    // Capture standard output
                    ByteArrayOutputStream outContent = new ByteArrayOutputStream();
                    PrintStream originalOut = System.out;
                    System.setOut(new PrintStream(outContent));
                    
                    try {
                        this.id = id;
                        this.comment = comment;
                        this.tasksFile = tempDir.resolve("test_tasks.json").toFile();
                        
                        // Replace actual repository creation with our mock
                        return super.call();
                    } finally {
                        System.setOut(originalOut);
                    }
                }
            };
            
            // When
            try {
                ByteArrayOutputStream outContent = new ByteArrayOutputStream();
                PrintStream originalOut = System.out;
                System.setOut(new PrintStream(outContent));
                
                command.call();
                
                System.setOut(originalOut);
                
                // Then
                String output = outContent.toString();
                assertTrue(output.contains("marked as completed"));
                assertTrue(output.contains("Added comment: " + comment));
            } catch (Exception e) {
                fail("Exception should not be thrown: " + e.getMessage());
            }
        }
        
        @Test
        @DisplayName("should show error when task not found")
        void shouldShowErrorWhenTaskNotFound() {
            // Given
            String id = "999";
            
            // Create a custom CompleteTaskCommand that uses our mocked service
            CompleteTaskCommand command = new CompleteTaskCommand() {
                @Override
                public Integer call() throws Exception {
                    TaskRepository repository = mock(TaskRepository.class);
                    TaskService service = mock(TaskService.class);
                    
                    when(service.getTask(id)).thenReturn(Optional.empty());
                    
                    // Capture standard error
                    ByteArrayOutputStream errContent = new ByteArrayOutputStream();
                    PrintStream originalErr = System.err;
                    System.setErr(new PrintStream(errContent));
                    
                    try {
                        this.id = id;
                        this.tasksFile = tempDir.resolve("test_tasks.json").toFile();
                        
                        // Replace actual repository creation with our mock
                        return super.call();
                    } finally {
                        System.setErr(originalErr);
                    }
                }
            };
            
            // When
            try {
                ByteArrayOutputStream errContent = new ByteArrayOutputStream();
                PrintStream originalErr = System.err;
                System.setErr(new PrintStream(errContent));
                
                command.call();
                
                System.setErr(originalErr);
                
                // Then
                assertTrue(errContent.toString().contains("Task with ID 999 not found"));
            } catch (Exception e) {
                fail("Exception should not be thrown: " + e.getMessage());
            }
        }
    }
    
    @Nested
    @DisplayName("List Tasks Command")
    class ListTasksCommandTest {
        
        @Test
        @DisplayName("should list active tasks by default")
        void shouldListActiveTasksByDefault() {
            // Given
            List<Task> activeTasks = new ArrayList<>();
            Task task1 = new Task(1, "Clean basement", null, "");
            Task task2 = new Task(2, "Wash dishes", null, "");
            activeTasks.add(task1);
            activeTasks.add(task2);
            
            // Create a custom ListTasksCommand that uses our mocked service
            ListTasksCommand command = new ListTasksCommand() {
                @Override
                public Integer call() throws Exception {
                    TaskRepository repository = mock(TaskRepository.class);
                    TaskService service = mock(TaskService.class);
                    
                    when(service.getActiveTasks()).thenReturn(activeTasks);
                    
                    // Capture standard output
                    ByteArrayOutputStream outContent = new ByteArrayOutputStream();
                    PrintStream originalOut = System.out;
                    System.setOut(new PrintStream(outContent));
                    
                    try {
                        this.tasksFile = tempDir.resolve("test_tasks.json").toFile();
                        
                        // Replace actual repository creation with our mock
                        return super.call();
                    } finally {
                        System.setOut(originalOut);
                    }
                }
            };
            
            // When
            try {
                ByteArrayOutputStream outContent = new ByteArrayOutputStream();
                PrintStream originalOut = System.out;
                System.setOut(new PrintStream(outContent));
                
                command.call();
                
                System.setOut(originalOut);
                
                // Then
                String output = outContent.toString();
                assertTrue(output.contains("Active Tasks"));
                assertTrue(output.contains("Clean basement"));
                assertTrue(output.contains("Wash dishes"));
                assertTrue(output.contains("Total: 2 task(s)"));
            } catch (Exception e) {
                fail("Exception should not be thrown: " + e.getMessage());
            }
        }
        
        @Test
        @DisplayName("should list all tasks when requested")
        void shouldListAllTasksWhenRequested() {
            // Given
            List<Task> allTasks = new ArrayList<>();
            Task task1 = new Task(1, "Clean basement", null, "");
            Task task2 = new Task(2, "Wash dishes", null, "");
            task2.setCompleted(true);
            allTasks.add(task1);
            allTasks.add(task2);
            
            // Create a custom ListTasksCommand that uses our mocked service
            ListTasksCommand command = new ListTasksCommand() {
                @Override
                public Integer call() throws Exception {
                    TaskRepository repository = mock(TaskRepository.class);
                    TaskService service = mock(TaskService.class);
                    
                    when(service.getAllTasks()).thenReturn(allTasks);
                    
                    // Capture standard output
                    ByteArrayOutputStream outContent = new ByteArrayOutputStream();
                    PrintStream originalOut = System.out;
                    System.setOut(new PrintStream(outContent));
                    
                    try {
                        this.showAll = true;
                        this.tasksFile = tempDir.resolve("test_tasks.json").toFile();
                        
                        // Replace actual repository creation with our mock
                        return super.call();
                    } finally {
                        System.setOut(originalOut);
                    }
                }
            };
            
            // When
            try {
                ByteArrayOutputStream outContent = new ByteArrayOutputStream();
                PrintStream originalOut = System.out;
                System.setOut(new PrintStream(outContent));
                
                command.call();
                
                System.setOut(originalOut);
                
                // Then
                String output = outContent.toString();
                assertTrue(output.contains("All Tasks"));
                assertTrue(output.contains("Clean basement"));
                assertTrue(output.contains("Wash dishes"));
                assertTrue(output.contains("Total: 2 task(s)"));
            } catch (Exception e) {
                fail("Exception should not be thrown: " + e.getMessage());
            }
        }
        
        @Test
        @DisplayName("should list only completed tasks when requested")
        void shouldListOnlyCompletedTasksWhenRequested() {
            // Given
            List<Task> completedTasks = new ArrayList<>();
            Task task = new Task(2, "Wash dishes", null, "");
            task.setCompleted(true);
            completedTasks.add(task);
            
            // Create a custom ListTasksCommand that uses our mocked service
            ListTasksCommand command = new ListTasksCommand() {
                @Override
                public Integer call() throws Exception {
                    TaskRepository repository = mock(TaskRepository.class);
                    TaskService service = mock(TaskService.class);
                    
                    when(service.getCompletedTasks()).thenReturn(completedTasks);
                    
                    // Capture standard output
                    ByteArrayOutputStream outContent = new ByteArrayOutputStream();
                    PrintStream originalOut = System.out;
                    System.setOut(new PrintStream(outContent));
                    
                    try {
                        this.showOnlyCompleted = true;
                        this.tasksFile = tempDir.resolve("test_tasks.json").toFile();
                        
                        // Replace actual repository creation with our mock
                        return super.call();
                    } finally {
                        System.setOut(originalOut);
                    }
                }
            };
            
            // When
            try {
                ByteArrayOutputStream outContent = new ByteArrayOutputStream();
                PrintStream originalOut = System.out;
                System.setOut(new PrintStream(outContent));
                
                command.call();
                
                System.setOut(originalOut);
                
                // Then
                String output = outContent.toString();
                assertTrue(output.contains("Completed Tasks"));
                assertTrue(output.contains("Wash dishes"));
                assertTrue(output.contains("Total: 1 task(s)"));
            } catch (Exception e) {
                fail("Exception should not be thrown: " + e.getMessage());
            }
        }
        
        @Test
        @DisplayName("should show message when no tasks found")
        void shouldShowMessageWhenNoTasksFound() {
            // Given
            List<Task> emptyList = new ArrayList<>();
            
            // Create a custom ListTasksCommand that uses our mocked service
            ListTasksCommand command = new ListTasksCommand() {
                @Override
                public Integer call() throws Exception {
                    TaskRepository repository = mock(TaskRepository.class);
                    TaskService service = mock(TaskService.class);
                    
                    when(service.getActiveTasks()).thenReturn(emptyList);
                    
                    // Capture standard output
                    ByteArrayOutputStream outContent = new ByteArrayOutputStream();
                    PrintStream originalOut = System.out;
                    System.setOut(new PrintStream(outContent));
                    
                    try {
                        this.tasksFile = tempDir.resolve("test_tasks.json").toFile();
                        
                        // Replace actual repository creation with our mock
                        return super.call();
                    } finally {
                        System.setOut(originalOut);
                    }
                }
            };
            
            // When
            try {
                ByteArrayOutputStream outContent = new ByteArrayOutputStream();
                PrintStream originalOut = System.out;
                System.setOut(new PrintStream(outContent));
                
                command.call();
                
                System.setOut(originalOut);
                
                // Then
                assertTrue(outContent.toString().contains("No tasks found"));
            } catch (Exception e) {
                fail("Exception should not be thrown: " + e.getMessage());
            }
        }
    }
} 