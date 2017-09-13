package com.alexo.command;

import com.alexo.api.Book;

public class AddBookCommand implements Command {
    Book book;
    public AddBookCommand(Book book) {
        this.book = book;
    }

    @Override
    public void execute() {
        
    }
}
