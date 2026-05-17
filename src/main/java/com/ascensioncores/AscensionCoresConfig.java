package com.ascensioncores;

import org.slf4j.Logger;

import java.io.IOException;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;

public final class AscensionCoresConfig {

    // Add config fields here.

    private static final Path CONFIG_PATH = Path.of("config", "ascensioncores.properties");

    private AscensionCoresConfig() {}

    public static void load(Logger logger) {
        Properties props = new Properties();

        if (Files.exists(CONFIG_PATH)) {
            try (Reader r = Files.newBufferedReader(CONFIG_PATH, StandardCharsets.UTF_8)) {
                props.load(r);
            } catch (IOException e) {
                logger.error("[AscensionCores] Failed to read config, using defaults", e);
            }
        }

        // Parse config fields here.

        save(logger);
        logger.info("[AscensionCores] Config loaded");
    }

    public static void reload(Logger logger) {
        load(logger);
    }

    public static void save(Logger logger) {
        try {
            Files.createDirectories(CONFIG_PATH.getParent());
            Files.writeString(CONFIG_PATH, toPropertiesString(), StandardCharsets.UTF_8);
        } catch (IOException e) {
            logger.error("[AscensionCores] Failed to save config", e);
        }
    }

    private static String toPropertiesString() {
        return """
                # AscensionCores configuration
                # Changes take effect after /ascensioncores reload or server restart.
                """;
    }

    static boolean parseBoolean(Properties props, String key, boolean def, Logger logger) {
        String raw = props.getProperty(key);
        if (raw == null) return def;
        String s = raw.trim().toLowerCase();
        if (s.equals("true")) return true;
        if (s.equals("false")) return false;
        logger.warn("[AscensionCores] '{}' is not a valid boolean ('{}'), using default {}", key, raw, def);
        return def;
    }

    static int parseInt(Properties props, String key, int def, int min, int max, Logger logger) {
        String raw = props.getProperty(key);
        if (raw == null) return def;
        try {
            int val = Integer.parseInt(raw.trim());
            if (val < min || val > max) {
                logger.warn("[AscensionCores] '{}' value {} out of range [{}, {}], clamping", key, val, min, max);
                return Math.max(min, Math.min(max, val));
            }
            return val;
        } catch (NumberFormatException e) {
            logger.warn("[AscensionCores] '{}' is not a valid integer ('{}'), using default {}", key, raw, def);
            return def;
        }
    }
}
