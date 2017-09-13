package com.alexo.command;

public class CommandHandler {
    private Command command;

    public void setCommand(Command command) {
        this.command = command;
    }

    public void addBook() {
        command.execute();
    }
}
