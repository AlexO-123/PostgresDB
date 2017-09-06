package com.alexo.api;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Book {

    @JsonProperty
    public String isbn;

    @JsonProperty
    public String name;

    @JsonProperty
    public int pages;

}
