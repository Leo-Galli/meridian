package dev.meridian.config;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public final class ModuleRegistry {

    public static final String KEYSTROKES = "keystrokes";
    public static final String CPS = "cps";
    public static final String PING = "ping";
    public static final String FPS = "fps";
    public static final String RAM = "ram";
    public static final String SERVER_INFO = "server_info";
    public static final String DIRECTION = "direction";
    public static final String ARMOR = "armor";
    public static final String WAYPOINTS = "waypoints";

    public static final List<String> ORDER = Arrays.asList(
            KEYSTROKES,
            CPS,
            PING,
            FPS,
            RAM,
            SERVER_INFO,
            DIRECTION,
            ARMOR,
            WAYPOINTS
    );

    private static final int WHITE = 0xFFFFFFFF;
    private static final int LIGHT_GRAY = 0xFFAAAAAA;

    private ModuleRegistry() {
    }

    public static List<ModuleSettings> defaults() {
        List<ModuleSettings> list = new ArrayList<>();
        for (String id : ORDER) {
            list.add(defaultsFor(id));
        }
        return list;
    }

    public static ModuleSettings defaultsFor(String id) {
        switch (id) {
            case KEYSTROKES:
                return new ModuleSettings(id, true, 8, -84, 1.0f, WHITE, 0xFF6BC5FF, true, 120, true);
            case CPS:
                return new ModuleSettings(id, true, 8, -196, 1.0f, WHITE, 0xFFFF8AB5, true, 120, true);
            case PING:
                return new ModuleSettings(id, true, -128, 24, 1.0f, WHITE, 0xFF7CFF8A, true, 110, true);
            case FPS:
                return new ModuleSettings(id, true, 2, 2, 1.0f, WHITE, 0xFF58E8FF, true, 110, true);
            case RAM:
                return new ModuleSettings(id, true, 2, 26, 1.0f, WHITE, 0xFF7CFF8A, true, 110, true);
            case SERVER_INFO:
                return new ModuleSettings(id, true, -150, 2, 1.0f, WHITE, 0xFF8AE8FF, true, 110, true);
            case DIRECTION:
                return new ModuleSettings(id, true, 2, 52, 1.0f, WHITE, 0xFFFFE08A, true, 110, true);
            case ARMOR:
                return new ModuleSettings(id, true, -160, -96, 1.0f, WHITE, 0xFFB7FFC9, true, 110, true);
            case WAYPOINTS:
                return new ModuleSettings(id, true, 0, 0, 1.0f, WHITE, 0xFFFFD75E, false, 0, true);
            default:
                return new ModuleSettings(id, true, 2, 2, 1.0f, WHITE, LIGHT_GRAY, true, 110, true);
        }
    }

    public static String title(String id) {
        switch (id) {
            case KEYSTROKES:
                return "Keystrokes";
            case CPS:
                return "CPS Counter";
            case PING:
                return "Ping";
            case FPS:
                return "FPS";
            case RAM:
                return "RAM Usage";
            case SERVER_INFO:
                return "Server Info";
            case DIRECTION:
                return "Direction Compass";
            case ARMOR:
                return "Armor Status";
            case WAYPOINTS:
                return "Waypoint Markers";
            default:
                return id;
        }
    }

    public static String description(String id) {
        switch (id) {
            case KEYSTROKES:
                return "Live WASD, space and mouse key overlay with press highlighting.";
            case CPS:
                return "Tracks left and right click rate per second.";
            case PING:
                return "Shows the current server latency in milliseconds.";
            case FPS:
                return "Shows the current frames per second.";
            case RAM:
                return "Shows used and allocated memory with a usage bar.";
            case SERVER_INFO:
                return "Shows the connected server name and address.";
            case DIRECTION:
                return "Shows your heading with a live compass strip.";
            case ARMOR:
                return "Shows equipped armor with durability bars.";
            case WAYPOINTS:
                return "Renders saved waypoint markers in the world.";
            default:
                return "";
        }
    }
}
