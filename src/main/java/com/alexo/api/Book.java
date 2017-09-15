package com.alexo.api;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Properties of a book
 * Getters/setters necessary for JDBI
 */
public class Book {

    @JsonProperty
    private String isbn;

    @JsonProperty
    private String title;

    @JsonProperty
    private int pages;

    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }

    public String getIsbn() {
        return isbn;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }

    public void setPages(int pages) {
        this.pages = pages;
    }

    public int getPages() {
        return pages;
    }

}
