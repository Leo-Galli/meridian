package dev.meridian.hud;

import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;

import dev.meridian.config.ModuleSettings;

public class CpsModule extends HudModule {

    public CpsModule(ModuleSettings settings) {
        super(settings);
    }

    @Override
    protected int contentWidth(TextRenderer font) {
        return pairWidth(font, "CPS ", "L 000 R 000") + 8;
    }

    @Override
    protected int contentHeight(TextRenderer font) {
        return font.fontHeight;
    }

    @Override
    protected void renderContent(DrawContext gfx, TextRenderer font, float deltaTicks) {
        ClickSampler sampler = HudRenderer.INSTANCE.clickSamplerAccess();
        String value = "L " + sampler.leftCps() + " R " + sampler.rightCps();
        int drawn = drawPair(gfx, font, "CPS ", value, 0, 0);
        int dotY = font.fontHeight / 2 - 1;
        int leftColor = sampler.leftDown() || sampler.leftJustClicked() ? settings.accentColor : 0x55AAAAAA;
        int rightColor = sampler.rightDown() || sampler.rightJustClicked() ? settings.accentColor : 0x55AAAAAA;
        gfx.fill(drawn + 2, dotY, drawn + 5, dotY + 3, leftColor);
        gfx.fill(drawn + 6, dotY, drawn + 9, dotY + 3, rightColor);
    }
}