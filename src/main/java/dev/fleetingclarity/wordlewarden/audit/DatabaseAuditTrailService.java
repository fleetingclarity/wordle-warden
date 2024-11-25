package dev.fleetingclarity.wordlewarden.audit;

public class DatabaseAuditTrailService implements AuditTrailService {
    private final AuditTrailDao auditTrailDao;

    public DatabaseAuditTrailService(AuditTrailDao auditTrailDao) {
        this.auditTrailDao = auditTrailDao;
    }

    @Override
    public void record(final String userId, final String channelId, final String teamId, final String event) {
        auditTrailDao.saveAuditEvent(new AuditEvent(userId, channelId, teamId, event));
    }
}
