package dev.fleetingclarity.wordlewarden.commands.userstats;

public record UserStats (
        String userName,
        int score,
        int scoreCount,
        int totalCount,
        double percentage
){}
