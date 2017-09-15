package com.alexo.command;

import com.alexo.Event.LoanBookEvent;

/**
 * Defines a binding between the action and the reciever (event)
 */
public class LoanBookCommand implements Command {

    private LoanBookEvent loanBookEvent;

    public LoanBookCommand(LoanBookEvent loanBookEvent) {
        this.loanBookEvent = loanBookEvent;
    }

    @Override
    public boolean execute() {
        return loanBookEvent.loanEvent();
    }
}
