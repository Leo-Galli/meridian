package dev.meridian.hud;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;

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

    public void renderOverlayContent(GuiGraphicsExtractor gfx, Font font, int screenWidth, int screenHeight) {
    }

    public final int getWidth(Font font) {
        return padding * 2 + contentWidth(font);
    }

    public final int getHeight(Font font) {
        return padding * 2 + contentHeight(font);
    }

    protected abstract int contentWidth(Font font);

    protected abstract int contentHeight(Font font);

    protected abstract void renderContent(GuiGraphicsExtractor gfx, Font font, float deltaTicks);

    protected final void drawText(GuiGraphicsExtractor gfx, Font font, String value, int x, int y, int color) {
        gfx.text(font, value, x, y, color, settings.textShadow);
    }

    protected final int drawPair(GuiGraphicsExtractor gfx, Font font, String label, String value, int x, int y) {
        int labelWidth = font.width(label);
        drawText(gfx, font, label, x, y, settings.textColor);
        drawText(gfx, font, value, x + labelWidth, y, settings.accentColor);
        return labelWidth + font.width(value);
    }

    protected final int pairWidth(Font font, String label, String value) {
        return font.width(label) + font.width(value);
    }

    protected final void drawBar(GuiGraphicsExtractor gfx, int x, int y, int width, int height, float fraction, int color) {
        int track = 0x33000000;
        gfx.fill(x, y, x + width, y + height, track);
        int fill = Math.max(0, Math.min(width, Math.round(width * fraction)));
        if (fill > 0) {
            gfx.fill(x, y, x + fill, y + height, color);
        }
    }
}
