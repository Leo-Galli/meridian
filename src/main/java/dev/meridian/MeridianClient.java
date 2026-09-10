package dev.meridian;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.InputUtil;

import dev.meridian.config.Config;
import dev.meridian.gui.MeridianConfigScreen;
import dev.meridian.hud.ComboModule;
import dev.meridian.hud.HudRenderer;
import dev.meridian.hud.MacroEngine;
import dev.meridian.hud.SoundIndicatorModule;

public class MeridianClient implements ClientModInitializer {

    public static final String MOD_ID = "meridian";

    private boolean menuKeyDown;

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
        ClientTickEvents.END_CLIENT_TICK.register(this::onEndTick);
        WorldRenderEvents.LAST.register(context ->
                SoundIndicatorModule.renderWorld(context.consumers(), context.matrixStack()));
        ClientLifecycleEvents.CLIENT_STOPPING.register(client -> Config.INSTANCE.save());
    }

    private void onEndTick(MinecraftClient client) {
        MacroEngine.INSTANCE.tick(client);
        ComboModule.update();
        int code = Config.INSTANCE.menuKeyCode;
        if (code > 0 && client.getWindow() != null && client.currentScreen == null) {
            boolean down = InputUtil.isKeyPressed(client.getWindow().getHandle(), code);
            if (down && !menuKeyDown) {
                menuKeyDown = true;
                openMenu(client);
            }
            menuKeyDown = down;
        } else {
            menuKeyDown = false;
        }
    }

    private static void openMenu(MinecraftClient client) {
        if (client.currentScreen == null) {
            client.setScreen(new MeridianConfigScreen());
        }
    }
}