package pl.edu.agh.isi;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDateTime;
import java.util.ArrayList;

class TaskTest {
    
    private Task task;
    private LocalDateTime now;
    private LocalDateTime dueDate;
    
    @BeforeEach
    void setUp() {
        now = LocalDateTime.now();
        dueDate = now.plusDays(1);
        task = new Task("Clean the kitchen", dueDate, "Wash dishes and clean countertops");
        task.setId(1);
    }
    
    @Test
    void testConstructorWithRequiredFields() {
        assertEquals(1, task.getId());
        assertEquals("Clean the kitchen", task.getTopic());
        assertEquals(dueDate, task.getDueDate());
        assertEquals("Wash dishes and clean countertops", task.getDescription());
        assertFalse(task.isCompleted());
        assertNotNull(task.getCreatedAt());
        assertNotNull(task.getComments());
        assertTrue(task.getComments().isEmpty());
    }
    
    @Test
    void testEmptyTopicThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> {
            new Task("", dueDate, "Description");
        });
    }
    
    @Test
    void testNullTopicThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> {
            new Task(null, dueDate, "Description");
        });
    }
    
    @Test
    void testSetters() {
        LocalDateTime newDueDate = dueDate.plusDays(1);
        task.setTopic("Updated topic");
        task.setDueDate(newDueDate);
        task.setDescription("Updated description");
        task.setCompleted(true);
        task.setAssignedTo("John");
        
        assertEquals("Updated topic", task.getTopic());
        assertEquals(newDueDate, task.getDueDate());
        assertEquals("Updated description", task.getDescription());
        assertTrue(task.isCompleted());
        assertEquals("John", task.getAssignedTo());
    }
    
    @Test
    void testAddComment() {
        Comment comment = new Comment("Task completed", "John");
        task.addComment(comment);
        
        assertEquals(1, task.getComments().size());
        assertEquals("Task completed", task.getComments().get(0).getContent());
        assertEquals("John", task.getComments().get(0).getAuthor());
    }
    
    @Test
    void testSetEmptyTopicThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> {
            task.setTopic("");
        });
    }
    
    @Test
    void testSetNullTopicThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> {
            task.setTopic(null);
        });
    }
}
