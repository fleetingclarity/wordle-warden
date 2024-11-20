package dev.fleetingclarity.wordlewarden;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class EnvLoader {
    private static final Logger log = LoggerFactory.getLogger(EnvLoader.class);
    private static final String ENV_FILE = ".env";

    public static void loadEnv() {
        Map<String, String> envVariables = new HashMap<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(ENV_FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.trim().isEmpty() || line.startsWith("#")) {
                    continue; // Skip empty lines and comments
                }
                String[] parts = line.split("=", 2);
                if (parts.length == 2) {
                    envVariables.put(parts[0], parts[1]);
                    System.setProperty(parts[0], parts[1]); // Set as system property
                }
            }
        } catch (IOException e) {
            log.warn("Env file did not exist or was not readable", e);
        }

        // Optionally print loaded variables for debugging
        envVariables.forEach((key, value) -> log.trace("{}={}", key, value));
    }

    public static String getEnvOrProperty(final String key) {
        String value = System.getenv(key);
        if (value != null) {
            return value;
        }
        return System.getProperty(key);
    }
}
