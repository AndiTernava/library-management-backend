package org.andi.librarymanagementbackend.dto;

public class PublisherDto {
    private Long id;
    private String name;

    public PublisherDto() {}

    public PublisherDto(Long id, String name) {
        this.id = id;
        this.name = name;
    }

    // Getters
    public Long getId() { return id; }
    public String getName() { return name; }

    // Setters
    public void setId(Long id) { this.id = id; }
    public void setName(String name) { this.name = name; }
}

