package dev.fleetingclarity;

import org.flywaydb.core.Flyway;

import javax.sql.DataSource;

public class DatabaseMigration {
    public static void runMigrations(final DataSource dataSource) {
        Flyway flyway = Flyway.configure()
                .dataSource(dataSource)
                .load();
        flyway.migrate();
    }
}
