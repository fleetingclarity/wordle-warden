package dev.fleetingclarity.wordlewarden;

import dev.fleetingclarity.wordlewarden.commands.CommandResponse;

public interface CommandHandler {
    CommandResponse handle(String args);
}
