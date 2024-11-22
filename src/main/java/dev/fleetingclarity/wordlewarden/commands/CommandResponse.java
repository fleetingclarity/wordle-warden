package dev.fleetingclarity.wordlewarden.commands;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class CommandResponse {
    private final String text;
    private final boolean ephemeral;
}
