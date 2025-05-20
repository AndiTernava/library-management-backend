package org.andi.librarymanagementbackend.dto;

import java.time.LocalDateTime;

public class AnnouncementDto {
    private Long id;
    private String title;
    private String content;
    private LocalDateTime publishDate;

    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public String getTitle() {
        return title;
    }
    public void setTitle(String title) {
        this.title = title;
    }
    public String getContent() {
        return content;
    }
    public void setContent(String content) {
        this.content = content;
    }
    public LocalDateTime getPublishDate() {
        return publishDate;
    }
    public void setPublishDate(LocalDateTime publishDate) {
        this.publishDate = publishDate;
    }
}
