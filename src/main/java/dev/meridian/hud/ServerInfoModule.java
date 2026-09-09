package dev.meridian.hud;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.network.ServerInfo;

import dev.meridian.config.ModuleSettings;

public class ServerInfoModule extends HudModule {

    public ServerInfoModule(ModuleSettings settings) {
        super(settings);
    }

    @Override
    protected int contentWidth(TextRenderer font) {
        return Math.max(font.getWidth(name()), font.getWidth(sub()));
    }

    @Override
    protected int contentHeight(TextRenderer font) {
        return font.fontHeight * 2 + 2;
    }

    @Override
    protected void renderContent(DrawContext gfx, TextRenderer font, float deltaTicks) {
        drawText(gfx, font, name(), 0, 0, settings.accentColor);
        drawText(gfx, font, sub(), 0, font.fontHeight + 2, settings.textColor);
    }

    private String name() {
        MinecraftClient mc = MinecraftClient.getInstance();
        ServerInfo server = mc.getCurrentServerEntry();
        if (server != null && server.name != null) {
            return server.name;
        }
        if (mc.isInSingleplayer()) {
            return "Singleplayer";
        }
        if (mc.getNetworkHandler() != null) {
            return "Connected";
        }
        return "Not connected";
    }

    private String sub() {
        MinecraftClient mc = MinecraftClient.getInstance();
        ServerInfo server = mc.getCurrentServerEntry();
        if (server != null && server.address != null) {
            return server.address;
        }
        if (mc.isInSingleplayer()) {
            return "Local world";
        }
        return "---";
    }
}