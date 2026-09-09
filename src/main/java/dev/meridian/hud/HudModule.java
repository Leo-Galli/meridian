package dev.meridian.hud;

import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;

import dev.meridian.config.ModuleSettings;

public abstract class HudModule {

    public final ModuleSettings settings;
    private final int padding;

    protected HudModule(ModuleSettings settings) {
        this(settings, 3);
    }

    protected HudModule(ModuleSettings settings, int padding) {
        this.settings = settings;
        this.padding = padding;
    }

    public final int padding() {
        return padding;
    }

    public boolean isOverlay() {
        return false;
    }

    public void renderOverlayContent(DrawContext gfx, TextRenderer font, int screenWidth, int screenHeight) {
    }

    public final int getWidth(TextRenderer font) {
        return padding * 2 + contentWidth(font);
    }

    public final int getHeight(TextRenderer font) {
        return padding * 2 + contentHeight(font);
    }

    protected abstract int contentWidth(TextRenderer font);

    protected abstract int contentHeight(TextRenderer font);

    protected abstract void renderContent(DrawContext gfx, TextRenderer font, float deltaTicks);

    protected final void drawText(DrawContext gfx, TextRenderer font, String value, int x, int y, int color) {
        gfx.drawText(font, value, x, y, color, settings.textShadow);
    }

    protected final int drawPair(DrawContext gfx, TextRenderer font, String label, String value, int x, int y) {
        int labelWidth = font.getWidth(label);
        drawText(gfx, font, label, x, y, settings.textColor);
        drawText(gfx, font, value, x + labelWidth, y, settings.accentColor);
        return labelWidth + font.getWidth(value);
    }

    protected final int pairWidth(TextRenderer font, String label, String value) {
        return font.getWidth(label) + font.getWidth(value);
    }

    protected final void drawBar(DrawContext gfx, int x, int y, int width, int height, float fraction, int color) {
        int track = 0x33000000;
        gfx.fill(x, y, x + width, y + height, track);
        int fill = Math.max(0, Math.min(width, Math.round(width * fraction)));
        if (fill > 0) {
            gfx.fill(x, y, x + fill, y + height, color);
        }
    }
}