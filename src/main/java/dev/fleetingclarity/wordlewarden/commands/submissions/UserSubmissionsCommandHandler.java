package dev.fleetingclarity.wordlewarden.commands.submissions;

import dev.fleetingclarity.wordlewarden.CommandHandler;
import dev.fleetingclarity.wordlewarden.commands.CommandResponse;

import java.util.List;

public class UserSubmissionsCommandHandler implements CommandHandler {
    private final UserSubmissionsDao dao;

    public UserSubmissionsCommandHandler(final UserSubmissionsDao dao) {
        this.dao = dao;
    }

    @Override
    public CommandResponse handle(final String args) {
        List<MonthlyUserSubmissions> submissions = dao.getMonthlySubmissionHistory();

        StringBuilder result = new StringBuilder("Monthly Submissions Report:\n\n```\n");
        result.append("-------------------------------------------------------------------------\n");

        for (MonthlyUserSubmissions s : submissions) {
            result.append(String.format(
                    "%-15s | %-15s | %-20d | %-15d\n",
                    s.username(),
                    s.month(),
                    s.totalSubmissions(),
                    s.differenceFromPreviousMonth()
            ));
        }

        result.append("```");

        return new CommandResponse(result.toString(), false);
    }
}
