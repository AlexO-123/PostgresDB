package com.alexo.command;

import com.alexo.Event.ReturnBookEvent;

public class ReturnBookCommand implements Command {

    ReturnBookEvent returnBookEvent;
    public ReturnBookCommand(ReturnBookEvent returnBookEvent) {
        this.returnBookEvent = returnBookEvent;
    }

    @Override
    public void execute() {
        returnBookEvent.returnBook();
    }
}
