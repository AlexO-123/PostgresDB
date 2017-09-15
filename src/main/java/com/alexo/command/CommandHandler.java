package com.alexo.command;

/**
 * Holds a command and gets command to execute request
 */
public class CommandHandler {
    private Command command;

    public void setCommand(Command command) {
        this.command = command;
    }

    public boolean triggerEvent() {
        return command.execute();
    }
}
