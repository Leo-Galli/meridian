package dev.meridian;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;

import org.lwjgl.glfw.GLFW;

import dev.meridian.config.Config;
import dev.meridian.gui.MeridianConfigScreen;
import dev.meridian.hud.HudRenderer;
import dev.meridian.hud.MacroEngine;

public class MeridianClient implements ClientModInitializer {

    public static final String MOD_ID = "meridian";

    private static final KeyBinding OPEN_MENU = new KeyBinding(
            "key.meridian.openMenu",
            InputUtil.Type.KEYSYM,
            GLFW.GLFW_KEY_RIGHT_SHIFT,
            KeyBinding.MISC_CATEGORY
    );

    @Override
    public void onInitializeClient() {
        Config.INSTANCE.load();
        HudRenderCallback.EVENT.register((drawContext, tickCounter) -> {
            MinecraftClient mc = MinecraftClient.getInstance();
            if (mc.player == null || mc.world == null) {
                return;
            }
            HudRenderer.INSTANCE.render(drawContext, mc.textRenderer, tickCounter);
        });
        KeyBindingHelper.registerKeyBinding(OPEN_MENU);
        ClientTickEvents.END_CLIENT_TICK.register(this::onEndTick);
        ClientLifecycleEvents.CLIENT_STOPPING.register(client -> Config.INSTANCE.save());
    }

    private void onEndTick(MinecraftClient client) {
        MacroEngine.INSTANCE.tick(client);
        while (OPEN_MENU.wasPressed()) {
            openMenu(client);
        }
    }

    private static void openMenu(MinecraftClient client) {
        if (client.currentScreen == null) {
            client.setScreen(new MeridianConfigScreen());
        }
    }
}