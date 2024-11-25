package dev.fleetingclarity.wordlewarden.audit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class AuditTrailDao {
    private static final Logger log = LoggerFactory.getLogger(AuditTrailDao.class);

    private final DataSource dataSource;

    public AuditTrailDao(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public void saveAuditEvent(final AuditEvent event) {
        final String sql = "INSERT INTO audit_trail (user_id, channel_id, team_id, event) VALUES (?, ?, ?, ?)";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, event.userId());
            ps.setString(2, event.channelId());
            ps.setString(3, event.teamId());
            ps.setString(4, event.event());
            ps.executeUpdate();
        } catch (SQLException e) {
            log.error("unable to save audit event {}", event, e);
        }
    }
}
