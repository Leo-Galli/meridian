package dev.meridian;

import com.mojang.blaze3d.platform.InputConstants;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keymapping.v1.KeyMappingHelper;
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.Identifier;

import org.lwjgl.glfw.GLFW;

import dev.meridian.config.Config;
import dev.meridian.gui.MeridianConfigScreen;
import dev.meridian.hud.MacroEngine;
import dev.meridian.hud.MeridianHudElement;

public class MeridianClient implements ClientModInitializer {

    public static final String MOD_ID = "meridian";

    private static final KeyMapping OPEN_MENU = new KeyMapping(
            "key.meridian.openMenu",
            InputConstants.Type.KEYSYM,
            GLFW.GLFW_KEY_RIGHT_SHIFT,
            KeyMapping.Category.MISC
    );

    @Override
    public void onInitializeClient() {
        Config.INSTANCE.load();
        HudElementRegistry.addLast(Identifier.fromNamespaceAndPath(MOD_ID, "main_hud"), new MeridianHudElement());
        KeyMappingHelper.registerKeyMapping(OPEN_MENU);
        ClientTickEvents.END_CLIENT_TICK.register(this::onEndTick);
        ClientLifecycleEvents.CLIENT_STOPPING.register(client -> Config.INSTANCE.save());
    }

    private void onEndTick(Minecraft client) {
        MacroEngine.INSTANCE.tick(client);
        while (OPEN_MENU.consumeClick()) {
            openMenu(client);
        }
    }

    private static void openMenu(Minecraft client) {
        if (client.gui.screen() == null) {
            client.gui.setScreen(new MeridianConfigScreen());
        }
    }
}
