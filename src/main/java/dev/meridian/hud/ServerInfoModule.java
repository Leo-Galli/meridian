package dev.meridian.hud;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.multiplayer.ServerData;

import dev.meridian.config.ModuleSettings;

public class ServerInfoModule extends HudModule {

    public ServerInfoModule(ModuleSettings settings) {
        super(settings);
    }

    @Override
    protected int contentWidth(Font font) {
        return Math.max(font.width(name()), font.width(sub()));
    }

    @Override
    protected int contentHeight(Font font) {
        return font.lineHeight * 2 + 2;
    }

    @Override
    protected void renderContent(GuiGraphicsExtractor gfx, Font font, float deltaTicks) {
        drawText(gfx, font, name(), 0, 0, settings.accentColor);
        drawText(gfx, font, sub(), 0, font.lineHeight + 2, settings.textColor);
    }

    private String name() {
        Minecraft mc = Minecraft.getInstance();
        ServerData server = mc.getCurrentServer();
        if (server != null && server.name != null) {
            return server.name;
        }
        if (mc.hasSingleplayerServer()) {
            return "Singleplayer";
        }
        if (mc.getConnection() != null) {
            return "Connected";
        }
        return "Not connected";
    }

    private String sub() {
        Minecraft mc = Minecraft.getInstance();
        ServerData server = mc.getCurrentServer();
        if (server != null && server.ip != null) {
            return server.ip;
        }
        if (mc.hasSingleplayerServer()) {
            return "Local world";
        }
        return "---";
    }
}
