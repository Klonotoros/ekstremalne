package pl.edu.agh.isi;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDateTime;

class CommentTest {
    
    private Comment comment;
    
    @BeforeEach
    void setUp() {
        comment = new Comment();
    }
    
    @Test
    void testConstructorWithContent() {
        comment = new Comment("Great work!");
        assertEquals("Great work!", comment.getContent());
        assertNotNull(comment.getCreatedAt());
        assertNull(comment.getAuthor());
    }
    
    @Test
    void testConstructorWithContentAndAuthor() {
        comment = new Comment("Great work!", "John");
        assertEquals("Great work!", comment.getContent());
        assertEquals("John", comment.getAuthor());
        assertNotNull(comment.getCreatedAt());
    }
    
    @Test
    void testSetContent() {
        comment.setContent("Task completed");
        assertEquals("Task completed", comment.getContent());
    }
    
    @Test
    void testSetAuthor() {
        comment.setAuthor("Jane");
        assertEquals("Jane", comment.getAuthor());
    }
    
    @Test
    void testSetCreatedAt() {
        LocalDateTime now = LocalDateTime.now();
        comment.setCreatedAt(now);
        assertEquals(now, comment.getCreatedAt());
    }
}
