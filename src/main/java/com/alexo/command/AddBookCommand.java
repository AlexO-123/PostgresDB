package com.alexo.command;

import com.alexo.Event.AddBookEvent;
import com.alexo.api.Book;

public class AddBookCommand implements Command {

    AddBookEvent addBookEvent;
    public AddBookCommand(AddBookEvent addBookEvent) {
        this.addBookEvent = addBookEvent;
    }

    @Override
    public void execute() {
        addBookEvent.addEvent();
    }
}
