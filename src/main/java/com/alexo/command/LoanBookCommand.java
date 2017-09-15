package com.alexo.command;

import com.alexo.Event.LoanBookEvent;

public class LoanBookCommand implements Command {

    LoanBookEvent loanBookEvent;

    public LoanBookCommand(LoanBookEvent loanBookEvent) {
        this.loanBookEvent = loanBookEvent;
    }

    @Override
    public void execute() {
        loanBookEvent.loanEvent();
    }
}
