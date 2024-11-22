package dev.fleetingclarity.wordlewarden.commands.submissions;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class UserSubmissionsDao {
    public static final Logger log = LoggerFactory.getLogger(MonthlyUserSubmissions.class);
    private final DataSource dataSource;

    public UserSubmissionsDao(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public List<MonthlyUserSubmissions> getMonthlySubmissionHistory() {
        final String sql = """
                WITH monthly_submissions AS (
                    SELECT user_id,
                           user_name,
                           DATE_TRUNC('month', puzzle_date) AS month,
                           COUNT(*) AS total_submissions
                    FROM wordle_scores
                    GROUP BY user_id, user_name, DATE_TRUNC('month', puzzle_date)
                ),
                submissions_with_difference AS (
                    SELECT user_id,
                           user_name,
                           TO_CHAR(month, 'Month') as month_name,
                           total_submissions,
                           total_submissions - LAG(total_submissions) OVER (PARTITION BY user_id ORDER BY month) AS difference_from_previous_month
                    FROM monthly_submissions
                )
                SELECT user_name,
                       month_name,
                       total_submissions,
                       COALESCE(difference_from_previous_month, 0) AS difference_from_previous_month
                FROM submissions_with_difference
                ORDER BY user_id, month_name;
                """;
        List<MonthlyUserSubmissions> results = new ArrayList<>();
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                String username = rs.getString("user_name");
                String month = rs.getString("month_name");
                int totalSubmissions = rs.getInt("total_submissions");
                int differenceFromPreviousMonth = rs.getInt("difference_from_previous_month");
                results.add(new MonthlyUserSubmissions(username, month, totalSubmissions, differenceFromPreviousMonth));
            }
        } catch (SQLException e) {
            log.error("Unable to get monthly user submissions.", e);
        }
        return results;
    }
}
