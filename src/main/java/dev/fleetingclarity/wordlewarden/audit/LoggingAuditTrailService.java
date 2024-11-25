package dev.fleetingclarity.wordlewarden.audit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoggingAuditTrailService implements AuditTrailService {
    private static final Logger log = LoggerFactory.getLogger(LoggingAuditTrailService.class);

    @Override
    public void record(String userId, String channelId, String teamId, String command) {
        log.info("audit event: userId={} channelId={} teamId={} event={}", userId, channelId, teamId, command);
    }
}
