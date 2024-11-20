package dev.fleetingclarity.wordlewarden;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import javax.sql.DataSource;

public class DatabaseConfig {
    private static HikariDataSource dataSource;

    static {
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(getEnvOrProperty("WW_POSTGRES_URL"));
        config.setUsername(getEnvOrProperty("WW_POSTGRES_USER"));
        config.setPassword(getEnvOrProperty("WW_POSTGRES_PASSWORD"));
        dataSource = new HikariDataSource(config);
    }

    public static DataSource getDataSource() {
        return dataSource;
    }

    public static String getEnvOrProperty(final String key) {
        String value = System.getenv(key);
        if (value != null) {
            return value;
        }
        return System.getProperty(key);
    }
}
