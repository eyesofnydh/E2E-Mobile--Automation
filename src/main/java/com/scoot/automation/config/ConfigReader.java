package com.scoot.automation.config;

import java.io.IOException;
import java.io.InputStream;
import java.time.Duration;
import java.util.Properties;

/**
 * Loads config.properties from the classpath once and exposes typed getters.
 *
 * Any property can be overridden on the command line, e.g.:
 *   mvn test -DtargetApp=driver -DdeviceName=emulator-5554
 * System properties win over the file, so CI can inject values without edits.
 */
public final class ConfigReader {

    private static final Properties PROPS = new Properties();

    static {
        // 1) Base config (committed, no secrets).
        try (InputStream in = ConfigReader.class.getClassLoader()
                .getResourceAsStream("config.properties")) {
            if (in == null) {
                throw new IllegalStateException("config.properties not found on classpath");
            }
            PROPS.load(in);
        } catch (IOException e) {
            throw new ExceptionInInitializerError("Failed to load config.properties: " + e.getMessage());
        }
        // 2) Optional local overrides (gitignored) — put real credentials here.
        //    Values present in config.local.properties override config.properties.
        try (InputStream local = ConfigReader.class.getClassLoader()
                .getResourceAsStream("config.local.properties")) {
            if (local != null) {
                PROPS.load(local);
            }
        } catch (IOException ignored) {
            // Local overrides are optional; ignore if absent/unreadable.
        }
    }

    private ConfigReader() {
    }

    // ---- Active-session context (for multi-app / cross-app E2E) -----------
    // A single test method can drive two Appium sessions at once (e.g. the user
    // app on one phone and the driver app on another). Page objects resolve the
    // app package via targetApp() and the adb serial via activeUdid(); switching
    // the active context re-points BOTH at whichever session is currently in use.
    // When no context is set, behaviour is unchanged (single-app runs use the
    // -DtargetApp / -Dudid system properties as before).
    private static final ThreadLocal<String> ACTIVE_APP  = new ThreadLocal<>();
    private static final ThreadLocal<String> ACTIVE_UDID = new ThreadLocal<>();

    /** Point targetApp()/activeUdid() at a specific session until cleared. */
    public static void setActiveContext(String app, String udid) {
        if (app != null && !app.isBlank())  ACTIVE_APP.set(app.toLowerCase().trim());
        if (udid != null && !udid.isBlank()) ACTIVE_UDID.set(udid.trim());
    }

    public static void clearActiveContext() {
        ACTIVE_APP.remove();
        ACTIVE_UDID.remove();
    }

    /** The adb serial of the session currently in use (context, else -Dudid). */
    public static String activeUdid() {
        String ctx = ACTIVE_UDID.get();
        if (ctx != null && !ctx.isBlank()) {
            return ctx;
        }
        return get("udid");
    }

    /** System property overrides the file value; falls back to the file. */
    public static String get(String key) {
        String sys = System.getProperty(key);
        if (sys != null && !sys.isBlank()) {
            return sys.trim();
        }
        String val = PROPS.getProperty(key);
        return val == null ? null : val.trim();
    }

    public static String get(String key, String defaultValue) {
        String val = get(key);
        return (val == null || val.isBlank()) ? defaultValue : val;
    }

    public static int getInt(String key, int defaultValue) {
        String val = get(key);
        try {
            return (val == null || val.isBlank()) ? defaultValue : Integer.parseInt(val);
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    public static boolean getBoolean(String key, boolean defaultValue) {
        String val = get(key);
        return (val == null || val.isBlank()) ? defaultValue : Boolean.parseBoolean(val);
    }

    public static Duration getSeconds(String key, int defaultSeconds) {
        return Duration.ofSeconds(getInt(key, defaultSeconds));
    }

    // ---- Convenience accessors resolved against the selected target app ----

    /** "user" or "driver". Honours an active-context override when one is set. */
    public static String targetApp() {
        String ctx = ACTIVE_APP.get();
        if (ctx != null && !ctx.isBlank()) {
            return ctx;
        }
        return get("targetApp", "user").toLowerCase();
    }

    /** appPackage for the currently selected target app. */
    public static String appPackage() {
        return get(targetApp() + ".appPackage");
    }

    /** appActivity for the currently selected target app. */
    public static String appActivity() {
        return get(targetApp() + ".appActivity");
    }

    /** Optional APK path for the selected app (blank = use installed build). */
    public static String appPath() {
        return get(targetApp() + ".app", "");
    }
}
