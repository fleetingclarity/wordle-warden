package dev.fleetingclarity.wordlewarden.commands;

import dev.fleetingclarity.wordlewarden.CommandHandler;

public class NoOpCommandHandler implements CommandHandler {
    @Override
    public CommandResponse handle(final String args) {
        return new CommandResponse("Unknown command", true);
    }
}
