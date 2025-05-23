package pl.edu.agh.isi.cli;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import picocli.CommandLine;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static org.junit.jupiter.api.Assertions.*;

class CreateTaskCommandTest {

    @TempDir
    Path tempDir;
    
    private File tasksFile;
    private CommandLine cmd;
    private ByteArrayOutputStream outputStream;
    private PrintStream originalOut;
    
    @BeforeEach
    void setUp() {
        tasksFile = tempDir.resolve("tasks-test.json").toFile();
        cmd = new CommandLine(new CreateTaskCommand());
        
        // Capture System.out
        originalOut = System.out;
        outputStream = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outputStream));
    }
    
    @Test
    void shouldCreateTaskWithRequiredParameters() {
        // given
        String topic = "Test task";
        
        // when
        int exitCode = cmd.execute("-f", tasksFile.getAbsolutePath(), topic);
        
        // then
        assertEquals(0, exitCode);
        assertTrue(outputStream.toString().contains("Task created successfully with ID:"));
    }
    
    @Test
    void shouldCreateTaskWithAllParameters() {
        // given
        String topic = "Complete task with due date";
        String dueDate = LocalDateTime.now().plusDays(1).format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
        String description = "This is a test task with all parameters";
        
        // when
        int exitCode = cmd.execute(
            "-f", tasksFile.getAbsolutePath(),
            "-d", dueDate,
            "-i", description,
            topic
        );
        
        // then
        assertEquals(0, exitCode);
        assertTrue(outputStream.toString().contains("Task created successfully with ID:"));
    }
    
    @Test
    void shouldFailWithEmptyTopic() {
        // when
        int exitCode = cmd.execute("-f", tasksFile.getAbsolutePath(), "");
        
        // then
        assertEquals(1, exitCode);
    }
    
    @Test
    void shouldFailWithInvalidDateFormat() {
        // given
        String topic = "Task with invalid date";
        String invalidDate = "2023/05/15";
        
        // when
        int exitCode = cmd.execute(
            "-f", tasksFile.getAbsolutePath(),
            "-d", invalidDate,
            topic
        );
        
        // then
        assertEquals(1, exitCode);
    }
} 