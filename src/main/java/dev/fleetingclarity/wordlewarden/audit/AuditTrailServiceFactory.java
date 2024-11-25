package dev.fleetingclarity.wordlewarden.audit;

import dev.fleetingclarity.wordlewarden.EnvLoader;

public class AuditTrailServiceFactory {

    public static final String AUDIT_TRAIL_DATABASE = "database";
    public static final String AUDIT_TRAIL_LOG = "logger";
    public static final String AUDIT_TRAIL_DEFAULT = "none";

    public static AuditTrailService create(AuditTrailDao dao) {
        final String auditTrailConfig = EnvLoader.getEnvOrProperty("WW_AUDIT_TRAIL_TYPE", AUDIT_TRAIL_DEFAULT);
        switch (auditTrailConfig.toLowerCase()) {
            case AUDIT_TRAIL_DATABASE:
                return new DatabaseAuditTrailService(dao);
            case AUDIT_TRAIL_LOG:
                return new LoggingAuditTrailService();
            case AUDIT_TRAIL_DEFAULT:
            default:
                return new NoOpAuditTrailService();
        }
    }
}
