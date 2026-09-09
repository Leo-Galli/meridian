package dev.meridian.hud;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;

import dev.meridian.config.ModuleSettings;

public class CpsModule extends HudModule {

    public CpsModule(ModuleSettings settings) {
        super(settings);
    }

    @Override
    protected int contentWidth(Font font) {
        return pairWidth(font, "CPS ", "L 000 R 000") + 8;
    }

    @Override
    protected int contentHeight(Font font) {
        return font.lineHeight;
    }

    @Override
    protected void renderContent(GuiGraphicsExtractor gfx, Font font, float deltaTicks) {
        ClickSampler sampler = HudRenderer.INSTANCE.clickSamplerAccess();
        String value = "L " + sampler.leftCps() + " R " + sampler.rightCps();
        int drawn = drawPair(gfx, font, "CPS ", value, 0, 0);
        int dotY = font.lineHeight / 2 - 1;
        int leftColor = sampler.leftDown() || sampler.leftJustClicked() ? settings.accentColor : 0x55AAAAAA;
        int rightColor = sampler.rightDown() || sampler.rightJustClicked() ? settings.accentColor : 0x55AAAAAA;
        gfx.fill(drawn + 2, dotY, drawn + 5, dotY + 3, leftColor);
        gfx.fill(drawn + 6, dotY, drawn + 9, dotY + 3, rightColor);
    }
}
