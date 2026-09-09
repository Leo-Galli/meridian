package dev.meridian.hud;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.network.PlayerListEntry;

import dev.meridian.config.ModuleSettings;

public class PingModule extends HudModule {

    public PingModule(ModuleSettings settings) {
        super(settings);
    }

    @Override
    protected int contentWidth(TextRenderer font) {
        return pairWidth(font, "Ping ", "0000ms");
    }

    @Override
    protected int contentHeight(TextRenderer font) {
        return font.fontHeight;
    }

    @Override
    protected void renderContent(DrawContext gfx, TextRenderer font, float deltaTicks) {
        MinecraftClient mc = MinecraftClient.getInstance();
        String value = "---";
        if (mc.player != null && mc.getNetworkHandler() != null) {
            ClientPlayNetworkHandler connection = mc.getNetworkHandler();
            PlayerListEntry info = connection.getPlayerListEntry(mc.player.getUuid());
            if (info != null) {
                value = info.getLatency() + "ms";
            } else {
                value = "0ms";
            }
        }
        drawPair(gfx, font, "Ping ", value, 0, 0);
    }
}