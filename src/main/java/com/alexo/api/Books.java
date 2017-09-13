package com.alexo.api;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.List;

public class Books {

    @JsonProperty
    private List<Book> books = new ArrayList<>();

    /**
     * Gets a list of EmployeePojo
     * @return list of employees
     */
    public List<Book> getBooks() {
        return books;
    }
}
