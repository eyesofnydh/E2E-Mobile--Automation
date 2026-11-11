package com.scoot.automation.utils;

import com.scoot.automation.config.ConfigReader;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Thin wrapper over the `adb` command line for test setup/teardown chores
 * that are easier (or only possible) outside the Appium session:
 * clearing app data, granting permissions, checking install state.
 *
 * These shell out to adb, so adb must be on PATH (it is on this machine).
 */
public final class AdbHelper {

    private static final Logger log = LogManager.getLogger(AdbHelper.class);

    private AdbHelper() {
    }

    /** WARNING: wipes all local data for the package (logs the user out). */
    public static void clearAppData(String packageName) {
        log.info("Clearing app data for {}", packageName);
        run("adb", "shell", "pm", "clear", packageName);
    }

    /** Grant a single runtime permission, e.g. android.permission.ACCESS_FINE_LOCATION. */
    public static void grantPermission(String packageName, String permission) {
        log.info("Granting {} to {}", permission, packageName);
        run("adb", "shell", "pm", "grant", packageName, permission);
    }

    public static void grantPermissions(String packageName, String... permissions) {
        for (String p : permissions) {
            grantPermission(packageName, p);
        }
    }

    public static boolean isInstalled(String packageName) {
        List<String> out = run("adb", "shell", "pm", "list", "packages", packageName);
        return out.stream().anyMatch(line -> line.contains("package:" + packageName));
    }

    /**
     * The adb serial of the device under test, resolved the same way BaseTest
     * pins the Appium session: {@code udid} first, else {@code deviceName}.
     * Returns null when neither is set (single-device case).
     */
    private static String targetSerial() {
        // activeUdid() returns the currently-active session's serial in a
        // cross-app E2E (context-aware), else the -Dudid system property.
        String udid = ConfigReader.activeUdid();
        if (udid != null && !udid.isBlank()) {
            return udid.trim();
        }
        String deviceName = ConfigReader.get("deviceName");
        if (deviceName != null && !deviceName.isBlank()) {
            return deviceName.trim();
        }
        return null;
    }

    /**
     * Inserts {@code -s <serial>} right after {@code adb} so the command targets
     * the device under test. Without this, a bare {@code adb shell ...} fails with
     * "more than one device/emulator" whenever a second device is connected (e.g.
     * the user phone + the driver emulator), and the failure is silently swallowed
     * — which e.g. left the destination search box empty and broke place-search.
     * No-op when only one device is used or a serial is already present.
     */
    private static String[] withDeviceTarget(String... command) {
        if (command.length == 0 || !"adb".equals(command[0])) {
            return command;
        }
        for (String arg : command) {
            if ("-s".equals(arg)) {
                return command; // caller already targeted a device
            }
        }
        String serial = targetSerial();
        if (serial == null) {
            return command; // single device: let adb pick it
        }
        List<String> targeted = new ArrayList<>(command.length + 2);
        targeted.add(command[0]);
        targeted.add("-s");
        targeted.add(serial);
        targeted.addAll(Arrays.asList(command).subList(1, command.length));
        return targeted.toArray(new String[0]);
    }

    /** Runs a command and returns stdout lines; logs (does not throw) on failure. */
    public static List<String> run(String... command) {
        List<String> output = new ArrayList<>();
        command = withDeviceTarget(command);
        try {
            Process process = new ProcessBuilder(command)
                    .redirectErrorStream(true)
                    .start();
            try (BufferedReader reader = new BufferedReader(
                    new InputStreamReader(process.getInputStream(), StandardCharsets.UTF_8))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    output.add(line);
                }
            }
            int exit = process.waitFor();
            if (exit != 0) {
                log.warn("Command exited {} : {}", exit, Arrays.toString(command));
            }
        } catch (Exception e) {
            log.error("Failed to run {} : {}", Arrays.toString(command), e.getMessage());
        }
        return output;
    }
}
