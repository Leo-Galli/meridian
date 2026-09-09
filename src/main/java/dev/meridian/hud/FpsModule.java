package dev.meridian.hud;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;

import dev.meridian.config.ModuleSettings;

public class FpsModule extends HudModule {

    public FpsModule(ModuleSettings settings) {
        super(settings);
    }

    @Override
    protected int contentWidth(TextRenderer font) {
        return pairWidth(font, "FPS ", "000");
    }

    @Override
    protected int contentHeight(TextRenderer font) {
        return font.fontHeight;
    }

    @Override
    protected void renderContent(DrawContext gfx, TextRenderer font, float deltaTicks) {
        String value = String.valueOf(MinecraftClient.getInstance().getCurrentFps());
        drawPair(gfx, font, "FPS ", value, 0, 0);
    }
}