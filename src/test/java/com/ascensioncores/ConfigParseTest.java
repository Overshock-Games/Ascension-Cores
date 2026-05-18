package com.ascensioncores;

import org.junit.jupiter.api.Test;
import org.slf4j.helpers.NOPLogger;

import java.util.Properties;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class ConfigParseTest {

    private static final org.slf4j.Logger LOG = NOPLogger.NOP_LOGGER;

    // ── parseInt ──────────────────────────────────────────────────────────────

    @Test
    void parseInt_missingKey_returnsDefault() {
        assertEquals(5, AscensionCoresConfig.parseInt(new Properties(), "x", 5, 0, 10, LOG));
    }

    @Test
    void parseInt_validValue_returnsParsed() {
        Properties p = props("x", "7");
        assertEquals(7, AscensionCoresConfig.parseInt(p, "x", 5, 0, 10, LOG));
    }

    @Test
    void parseInt_belowMin_clampsToMin() {
        Properties p = props("x", "-5");
        assertEquals(0, AscensionCoresConfig.parseInt(p, "x", 5, 0, 10, LOG));
    }

    @Test
    void parseInt_aboveMax_clampsToMax() {
        Properties p = props("x", "99");
        assertEquals(10, AscensionCoresConfig.parseInt(p, "x", 5, 0, 10, LOG));
    }

    @Test
    void parseInt_nonNumeric_returnsDefault() {
        Properties p = props("x", "abc");
        assertEquals(5, AscensionCoresConfig.parseInt(p, "x", 5, 0, 10, LOG));
    }

    // ── parseDouble ───────────────────────────────────────────────────────────

    @Test
    void parseDouble_missingKey_returnsDefault() {
        assertEquals(0.5, AscensionCoresConfig.parseDouble(new Properties(), "x", 0.5, 0.0, 1.0, LOG));
    }

    @Test
    void parseDouble_validValue_returnsParsed() {
        Properties p = props("x", "0.75");
        assertEquals(0.75, AscensionCoresConfig.parseDouble(p, "x", 0.5, 0.0, 1.0, LOG), 1e-9);
    }

    @Test
    void parseDouble_belowMin_clampsToMin() {
        Properties p = props("x", "-1.0");
        assertEquals(0.0, AscensionCoresConfig.parseDouble(p, "x", 0.5, 0.0, 1.0, LOG), 1e-9);
    }

    @Test
    void parseDouble_aboveMax_clampsToMax() {
        Properties p = props("x", "2.0");
        assertEquals(1.0, AscensionCoresConfig.parseDouble(p, "x", 0.5, 0.0, 1.0, LOG), 1e-9);
    }

    // ── parseBoolean ──────────────────────────────────────────────────────────

    @Test
    void parseBoolean_true_returnsTrue() {
        assertTrue(AscensionCoresConfig.parseBoolean(props("x", "true"), "x", false, LOG));
    }

    @Test
    void parseBoolean_false_returnsFalse() {
        assertFalse(AscensionCoresConfig.parseBoolean(props("x", "false"), "x", true, LOG));
    }

    @Test
    void parseBoolean_invalid_returnsDefault() {
        assertFalse(AscensionCoresConfig.parseBoolean(props("x", "yes"), "x", false, LOG));
    }

    @Test
    void parseBoolean_missing_returnsDefault() {
        assertTrue(AscensionCoresConfig.parseBoolean(new Properties(), "x", true, LOG));
    }

    // ── parseStringSet ────────────────────────────────────────────────────────

    @Test
    void parseStringSet_empty_returnsEmptySet() {
        assertEquals(Set.of(), AscensionCoresConfig.parseStringSet(new Properties(), "x", LOG));
    }

    @Test
    void parseStringSet_blank_returnsEmptySet() {
        assertEquals(Set.of(), AscensionCoresConfig.parseStringSet(props("x", "  "), "x", LOG));
    }

    @Test
    void parseStringSet_single_returnsSingleton() {
        Set<String> result = AscensionCoresConfig.parseStringSet(props("x", "venom"), "x", LOG);
        assertEquals(Set.of("venom"), result);
    }

    @Test
    void parseStringSet_commaSeparated_returnsAll() {
        Set<String> result = AscensionCoresConfig.parseStringSet(props("x", "venom,shock, frostbite "), "x", LOG);
        assertEquals(Set.of("venom", "shock", "frostbite"), result);
    }

    @Test
    void parseStringSet_duplicates_deduplicated() {
        Set<String> result = AscensionCoresConfig.parseStringSet(props("x", "venom,venom"), "x", LOG);
        assertEquals(1, result.size());
        assertTrue(result.contains("venom"));
    }

    // ── helpers ───────────────────────────────────────────────────────────────

    private static Properties props(String key, String value) {
        Properties p = new Properties();
        p.setProperty(key, value);
        return p;
    }
}
