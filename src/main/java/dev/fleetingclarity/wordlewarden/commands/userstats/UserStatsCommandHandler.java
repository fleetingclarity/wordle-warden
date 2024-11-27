package dev.fleetingclarity.wordlewarden.commands.userstats;

import dev.fleetingclarity.wordlewarden.CommandHandler;
import dev.fleetingclarity.wordlewarden.commands.CommandResponse;

import java.util.List;

public class UserStatsCommandHandler implements CommandHandler {
    private final UserStatsDao dao;

    public UserStatsCommandHandler(UserStatsDao dao) {
        this.dao = dao;
    }

    @Override
    public CommandResponse handle(String args) {
        List<UserStats> userStats = dao.getUserStats();
        StringBuilder output = new StringBuilder("*User Stats:*\n");
        output.append("```")
              .append(String.format("%-15s %-6s %-12s %-10s %-10s%n", "User Name", "Score", "Count", "Total", "Percentage"))
              .append("------------------------------------------------\n");
        for (UserStats userStat : userStats) {
            output.append(String.format("%-15s %-6d %-12d %-10d %-10.2f%%%n",
                    userStat.userName(),
                    userStat.score(),
                    userStat.scoreCount(),
                    userStat.totalCount(),
                    userStat.percentage()));
        }
        output.append("```");
        return new CommandResponse(output.toString(), false);
    }
}
