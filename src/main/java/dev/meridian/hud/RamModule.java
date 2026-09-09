package dev.meridian.hud;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;

import dev.meridian.config.ModuleSettings;

public class RamModule extends HudModule {

    private static final int BAR_WIDTH = 64;
    private static final int BAR_HEIGHT = 3;

    public RamModule(ModuleSettings settings) {
        super(settings);
    }

    @Override
    protected int contentWidth(Font font) {
        return Math.max(pairWidth(font, "Memory ", "0000/0000MB"), BAR_WIDTH);
    }

    @Override
    protected int contentHeight(Font font) {
        return font.lineHeight + 5;
    }

    @Override
    protected void renderContent(GuiGraphicsExtractor gfx, Font font, float deltaTicks) {
        Runtime runtime = Runtime.getRuntime();
        long used = runtime.totalMemory() - runtime.freeMemory();
        long max = runtime.maxMemory();
        int usedMb = (int) (used / 1048576L);
        int maxMb = (int) (max / 1048576L);
        float fraction = maxMb <= 0 ? 0 : (float) usedMb / maxMb;
        int percent = Math.round(fraction * 100);
        String value = usedMb + "/" + maxMb + "MB (" + percent + "%)";
        drawPair(gfx, font, "Memory ", value, 0, 0);
        drawBar(gfx, 0, font.lineHeight + 2, BAR_WIDTH, BAR_HEIGHT, fraction, settings.accentColor);
    }
}
