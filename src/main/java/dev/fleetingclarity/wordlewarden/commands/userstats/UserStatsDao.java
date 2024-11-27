package dev.fleetingclarity.wordlewarden.commands.userstats;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class UserStatsDao {
    private static final Logger log = LoggerFactory.getLogger(UserStatsDao.class);
    private final DataSource dataSource;

    public UserStatsDao(final DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public List<UserStats> getUserStats() {
        final String sql = """
                WITH user_score_counts AS (
                    -- Count each score for each user
                    SELECT user_id,
                           user_name,
                           score,
                           COUNT(*) AS score_count
                    FROM wordle_scores
                    GROUP BY user_id, user_name, score
                ),
                user_total_counts AS (
                    -- Calculate the total number of scores per user
                    SELECT user_id,
                           user_name,
                           SUM(score_count) AS total_count
                    FROM user_score_counts
                    GROUP BY user_id, user_name
                ),
                user_score_percentages AS (
                    -- Calculate the percentage of each score
                    SELECT usc.user_id,
                           usc.user_name,
                           usc.score,
                           usc.score_count,
                           utc.total_count,
                           (usc.score_count::DECIMAL / utc.total_count * 100) AS percentage
                    FROM user_score_counts usc
                    JOIN user_total_counts utc
                    ON usc.user_id = utc.user_id
                )
                -- Output the report
                SELECT user_name,
                       score,
                       score_count,
                       total_count,
                       ROUND(percentage, 4) AS percentage
                FROM user_score_percentages
                ORDER BY user_name, score;
                """;
        List<UserStats> userStats = new ArrayList<>();
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                userStats.add(new UserStats(
                        rs.getString("user_name"),
                        rs.getInt("score"),
                        rs.getInt("score_count"),
                        rs.getInt("total_count"),
                        rs.getDouble("percentage")
                ));
            }
        } catch (SQLException e) {
            log.error("unable to get user stats", e);
        }
        return userStats;
    }
}
