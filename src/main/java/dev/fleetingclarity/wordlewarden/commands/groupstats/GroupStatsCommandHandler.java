package dev.fleetingclarity.wordlewarden.commands.groupstats;

import dev.fleetingclarity.wordlewarden.CommandHandler;
import dev.fleetingclarity.wordlewarden.commands.CommandResponse;

import java.util.List;

public class GroupStatsCommandHandler implements CommandHandler {
    private final GroupStatsDao dao;

    public GroupStatsCommandHandler(GroupStatsDao dao) {
        this.dao = dao;
    }

    @Override
    public CommandResponse handle(String args) {
        List<UserStats> report = dao.getGroupAverageReport();
        StringBuilder result = new StringBuilder("Group Average Report:\n\n```\n");
        result.append(String.format("%-15s | %-15s | %-15s | %-15s\n", "User", "Avg Score", "Group Avg", "Diff"));
        result.append("---------------------------------------------------------------\n");
        for (UserStats stat : report) {
            result.append(String.format(
                    "%-15s | %-15.2f | %-15.2f | %-15.2f\n",
                    stat.getUsername(),
                    stat.getAverageScore(),
                    stat.getGroupAverage(),
                    stat.getDifferenceFromGroup()
            ));
        }
        result.append("```");
        return new CommandResponse(result.toString(), false);
    }
}
