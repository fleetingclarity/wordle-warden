CREATE TABLE audit_trail (
    id SERIAL PRIMARY KEY,
    timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    user_id VARCHAR(50) NOT NULL,
    channel_id VARCHAR(50) NOT NULL,
    team_id VARCHAR(50) NOT NULL,
    event TEXT NOT NULL
);

CREATE INDEX idx_audit_user_id ON audit_trail(user_id);

CREATE INDEX idx_audit_timestamp ON audit_trail(timestamp);

CREATE INDEX idx_audit_user_id_timestamp ON audit_trail(user_id, timestamp);

