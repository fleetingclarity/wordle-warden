package dev.fleetingclarity.wordlewarden;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.LocalDate;

public class WordleScoreDao {
    private static final Logger log = LoggerFactory.getLogger(WordleScoreDao.class);
    private final DataSource dataSource;

    public WordleScoreDao(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public void saveScore(WordleScore wordleScore) {
        final String sql = """
                INSERT INTO wordle_scores (user_id, user_name, message_id, score, puzzle_number, puzzle_date)
                VALUES (?, ?, ?, ?, ?, ?)
                """;
        try(Connection conn = dataSource.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, wordleScore.getUserId());
            ps.setString(2, wordleScore.getUsername());
            ps.setString(3, wordleScore.getMessageId());
            ps.setInt(4, wordleScore.getScore());
            ps.setInt(5, wordleScore.getPuzzleNumber());
            ps.setObject(6, wordleScore.getPuzzleDate());
            ps.executeUpdate();
        } catch (java.sql.SQLException e) {
            log.error("Unable to write wordle score for {}", wordleScore, e);
        }
    }

    private boolean checkForMessageId(final String messageId) {
        final String sql = "select 1 from wordle_scores where message_id = ?";
        try (Connection conn = dataSource.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, messageId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (java.sql.SQLException e) {
            log.error("Unable to search for existing wordle score messageId={}", messageId, e);
            return true;
        }
    }

    public boolean checkForPuzzleDate(final String userId, final LocalDate puzzleDate) {
        final String sql = "select 1 from wordle_scores where user_id = ? and puzzle_date = ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, userId);
            ps.setObject(2, puzzleDate);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (java.sql.SQLException e) {
            log.error("Unable to search for existing wordle score puzzleDate={}", puzzleDate, e);
            return true;
        }
    }

    public boolean shouldSaveScore(final WordleScore wordleScore) {
        var messageCheck = checkForMessageId(wordleScore.getMessageId());
        var puzzleDateCheck = checkForPuzzleDate(wordleScore.getUserId(), wordleScore.getPuzzleDate());
        return !messageCheck && !puzzleDateCheck;
        //return checkForMessageId(wordleScore.getMessageId()) && checkForPuzzleDate(wordleScore.getUserId(), wordleScore.getPuzzleDate());
    }
}
