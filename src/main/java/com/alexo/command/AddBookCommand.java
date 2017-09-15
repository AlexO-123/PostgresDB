package com.alexo.command;

import com.alexo.Event.AddBookEvent;

/**
 * Sets event we want to trigger and executes it
 */
public class AddBookCommand implements Command {

    private AddBookEvent addBookEvent;

    public AddBookCommand(AddBookEvent addBookEvent) {
        this.addBookEvent = addBookEvent;
    }

    @Override
    public boolean execute() {
        return addBookEvent.addEvent();
    }
}
