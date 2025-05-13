package org.andi.librarymanagementbackend.dto;

public class BookDto {
    private Long id;
    private String title;
    private String isbn;
    private int quantity;
    private Long authorId;
    private Long categoryId;
    private Long publisherId;

    public BookDto() {
    }


    public BookDto(Long id, String title, String isbn, int quantity,
                   Long authorId, Long categoryId, Long publisherId) {
        this.id = id;
        this.title = title;
        this.isbn = isbn;
        this.quantity = quantity;
        this.authorId = authorId;
        this.categoryId = categoryId;
        this.publisherId = publisherId;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public void setAuthorId(Long aLong) {
        this.authorId = aLong;
    }

    public void setCategoryId(Long aLong) {
        this.categoryId = aLong;
    }

    public void setPublisherId(Long aLong) {
        this.publisherId = aLong;
    }

    public Long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getIsbn() {
        return isbn;
    }

    public int getQuantity() {
        return quantity;
    }


    public Long getAuthorId() {
        return authorId;
    }

    public Long getCategoryId() {
        return categoryId;
    }

    public Long getPublisherId() {
        return publisherId;
    }
}