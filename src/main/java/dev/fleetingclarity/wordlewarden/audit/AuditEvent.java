package dev.fleetingclarity.wordlewarden.audit;

public record AuditEvent (
    String userId,
    String channelId,
    String teamId,
    String event
){}
