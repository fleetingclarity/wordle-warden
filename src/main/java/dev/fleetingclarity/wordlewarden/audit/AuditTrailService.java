package dev.fleetingclarity.wordlewarden.audit;

public interface AuditTrailService {
    void record(String userId, String channelId, String teamId, String event);
}
