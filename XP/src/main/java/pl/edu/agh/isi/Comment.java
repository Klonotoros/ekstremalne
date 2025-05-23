package pl.edu.agh.isi;

import java.time.LocalDateTime;

public class Comment {
    private String content;
    private LocalDateTime createdAt;
    private String author;

    // Default constructor for Jackson
    public Comment() {
        this.createdAt = LocalDateTime.now();
    }

    public Comment(String content) {
        this();
        this.content = content;
    }

    public Comment(String content, String author) {
        this(content);
        this.author = author;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }
}
