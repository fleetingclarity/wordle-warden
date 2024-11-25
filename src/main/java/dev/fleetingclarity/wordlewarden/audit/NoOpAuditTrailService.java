package dev.fleetingclarity.wordlewarden.audit;

public class NoOpAuditTrailService implements AuditTrailService {
    @Override
    public void record(String userId, String channelId, String teamId, String command) {
        // do nothing
    }
}
