package com.alexo.api;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Book {

    @JsonProperty
    public String isbn;

    @JsonProperty
    public String title;

    @JsonProperty
    public int pages;

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
