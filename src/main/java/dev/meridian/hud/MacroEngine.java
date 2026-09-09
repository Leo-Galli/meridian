package dev.meridian.hud;

import java.util.HashMap;
import java.util.Map;

import org.lwjgl.glfw.GLFW;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;

import dev.meridian.config.Config;
import dev.meridian.config.Macro;

public final class MacroEngine {

    public static final MacroEngine INSTANCE = new MacroEngine();

    private final Map<Integer, Boolean> pressed = new HashMap<>();

    private MacroEngine() {
    }

    public void tick(Minecraft mc) {
        if (mc.getWindow() == null) {
            return;
        }
        if (mc.level == null || mc.player == null) {
            pressed.clear();
            return;
        }
        if (mc.gui.screen() != null) {
            pressed.clear();
            return;
        }
        long window = mc.getWindow().handle();
        for (Macro macro : Config.INSTANCE.macros()) {
            if (!macro.enabled || macro.key <= 0 || macro.message == null || macro.message.isEmpty()) {
                continue;
            }
            boolean down = GLFW.glfwGetKey(window, macro.key) == GLFW.GLFW_PRESS;
            Boolean previous = pressed.get(macro.key);
            if (down && (previous == null || !previous)) {
                send(mc, macro.message);
            }
            pressed.put(macro.key, down);
        }
    }

    private static void send(Minecraft mc, String message) {
        ClientPacketListener connection = mc.getConnection();
        if (connection == null) {
            return;
        }
        if (message.startsWith("/")) {
            connection.sendCommand(message.substring(1));
        } else {
            connection.sendChat(message);
        }
    }
}
