package dev.meridian.hud;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.client.multiplayer.PlayerInfo;

import dev.meridian.config.ModuleSettings;

public class PingModule extends HudModule {

    public PingModule(ModuleSettings settings) {
        super(settings);
    }

    @Override
    protected int contentWidth(Font font) {
        return pairWidth(font, "Ping ", "0000ms");
    }

    @Override
    protected int contentHeight(Font font) {
        return font.lineHeight;
    }

    @Override
    protected void renderContent(GuiGraphicsExtractor gfx, Font font, float deltaTicks) {
        Minecraft mc = Minecraft.getInstance();
        String value = "---";
        if (mc.player != null && mc.getConnection() != null) {
            ClientPacketListener connection = mc.getConnection();
            PlayerInfo info = connection.getPlayerInfo(mc.player.getUUID());
            if (info != null) {
                value = info.getLatency() + "ms";
            } else {
                value = "0ms";
            }
        }
        drawPair(gfx, font, "Ping ", value, 0, 0);
    }
}
