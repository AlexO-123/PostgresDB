package com.alexo.command;

import com.alexo.Event.ReturnBookEvent;

/**
 * Defines a binding between the action and the receiver (event)
 */
public class ReturnBookCommand implements Command {

    private ReturnBookEvent returnBookEvent;
    public ReturnBookCommand(ReturnBookEvent returnBookEvent) {
        this.returnBookEvent = returnBookEvent;
    }

    @Override
    public boolean execute() {
        return returnBookEvent.returnBook();
    }
}
