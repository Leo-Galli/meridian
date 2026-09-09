package dev.meridian.hud;

import java.util.HashMap;
import java.util.Map;

import org.lwjgl.glfw.GLFW;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;

import dev.meridian.config.Config;
import dev.meridian.config.Macro;

public final class MacroEngine {

    public static final MacroEngine INSTANCE = new MacroEngine();

    private final Map<Integer, Boolean> pressed = new HashMap<>();

    private MacroEngine() {
    }

    public void tick(MinecraftClient mc) {
        if (mc.getWindow() == null) {
            return;
        }
        if (mc.world == null || mc.player == null) {
            pressed.clear();
            return;
        }
        if (mc.currentScreen != null) {
            pressed.clear();
            return;
        }
        long window = mc.getWindow().getHandle();
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

    private static void send(MinecraftClient mc, String message) {
        ClientPlayNetworkHandler connection = mc.getNetworkHandler();
        if (connection == null) {
            return;
        }
        if (message.startsWith("/")) {
            connection.sendChatCommand(message.substring(1));
        } else {
            connection.sendChatMessage(message);
        }
    }
}