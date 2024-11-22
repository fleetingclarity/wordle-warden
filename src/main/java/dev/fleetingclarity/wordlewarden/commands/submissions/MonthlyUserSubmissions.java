package dev.fleetingclarity.wordlewarden.commands.submissions;

public record MonthlyUserSubmissions(
    String username,
    String month,
    int totalSubmissions,
    int differenceFromPreviousMonth
){}
