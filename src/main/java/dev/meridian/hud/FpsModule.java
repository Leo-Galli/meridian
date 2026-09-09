package dev.meridian.hud;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;

import dev.meridian.config.ModuleSettings;

public class FpsModule extends HudModule {

    public FpsModule(ModuleSettings settings) {
        super(settings);
    }

    @Override
    protected int contentWidth(Font font) {
        return pairWidth(font, "FPS ", "000");
    }

    @Override
    protected int contentHeight(Font font) {
        return font.lineHeight;
    }

    @Override
    protected void renderContent(GuiGraphicsExtractor gfx, Font font, float deltaTicks) {
        String value = String.valueOf(Minecraft.getInstance().getFps());
        drawPair(gfx, font, "FPS ", value, 0, 0);
    }
}
