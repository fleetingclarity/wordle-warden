package dev.fleetingclarity.wordlewarden.commands.groupstats;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class GroupStatsDao {
    private static final Logger log = LoggerFactory.getLogger(GroupStatsDao.class);
    private final DataSource dataSource;

    public GroupStatsDao(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    List<UserStats> getGroupAverageReport() {
        final String sql = """
                WITH user_averages AS (
                    SELECT user_name,
                           AVG(score) AS average_score_per_person
                    FROM wordle_scores
                    GROUP BY user_name
                ),
                group_average AS (
                    SELECT AVG(average_score_per_person) AS group_average_score
                    FROM user_averages
                )
                SELECT ua.user_name,
                       ua.average_score_per_person,
                       ga.group_average_score,
                       ua.average_score_per_person - ga.group_average_score AS difference_from_group
                FROM user_averages ua
                CROSS JOIN group_average ga
                ORDER BY ua.user_name;
                """;
        List<UserStats> report = new ArrayList<>();
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                String username = rs.getString("user_name");
                double averageScore = rs.getDouble("average_score_per_person");
                double groupAverage = rs.getDouble("group_average_score");
                double difference = rs.getDouble("difference_from_group");
                report.add(new UserStats(username, averageScore, groupAverage, difference));
            }
        } catch (SQLException e) {
            log.error("Unable to get the group average report", e);
            return new ArrayList<>();
        }
        return report;
    }
}
