package dev.meridian.hud;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;

import dev.meridian.config.ModuleSettings;

public class KeystrokesModule extends HudModule {

    private static final String[] LABELS = {"W", "A", "S", "D", "SPACE", "LMB", "RMB"};
    private static final int[] XS = {24, 0, 24, 48, 0, 0, 36};
    private static final int[] YS = {0, 24, 24, 24, 48, 66, 66};
    private static final int[] WS = {22, 22, 22, 22, 70, 34, 34};
    private static final int[] HS = {22, 22, 22, 22, 12, 20, 20};
    private static final int SLOT_COUNT = LABELS.length;

    private final boolean[] pressed = new boolean[SLOT_COUNT];
    private final float[] flash = new float[SLOT_COUNT];

    public KeystrokesModule(ModuleSettings settings) {
        super(settings, 0);
    }

    @Override
    protected int contentWidth(Font font) {
        return 70;
    }

    @Override
    protected int contentHeight(Font font) {
        return 86;
    }

    @Override
    protected void renderContent(GuiGraphicsExtractor gfx, Font font, float deltaTicks) {
        Minecraft mc = Minecraft.getInstance();
        ClickSampler sampler = HudRenderer.INSTANCE.clickSamplerAccess();
        boolean[] states = {
                mc.options.keyUp.isDown(),
                mc.options.keyLeft.isDown(),
                mc.options.keyDown.isDown(),
                mc.options.keyRight.isDown(),
                mc.options.keyJump.isDown(),
                sampler.leftDown(),
                sampler.rightDown()
        };
        for (int i = 0; i < SLOT_COUNT; i++) {
            if (states[i] && !pressed[i]) {
                flash[i] = 0.18f;
            }
            if (!states[i]) {
                flash[i] = Math.max(0, flash[i] - deltaTicks / 3.6f);
            }
            pressed[i] = states[i];
            drawKey(gfx, font, XS[i], YS[i], WS[i], HS[i], LABELS[i], states[i], flash[i]);
        }
    }

    private void drawKey(GuiGraphicsExtractor gfx, Font font, int x, int y, int w, int h, String label, boolean down, float flash) {
        gfx.fill(x, y, x + w, y + h, 0x55000000);
        if (down) {
            gfx.fill(x, y, x + w, y + h, settings.accentColor);
        } else if (flash > 0.001f) {
            int alpha = (int) (200 * Math.min(1, flash / 0.18f));
            gfx.fill(x, y, x + w, y + h, (alpha << 24) | (settings.accentColor & 0xFFFFFF));
        }
        gfx.fill(x, y, x + w, y + 1, 0x66FFFFFF);
        gfx.fill(x, y, x + 1, y + h, 0x33FFFFFF);
        gfx.fill(x + w - 1, y, x + w, y + h, 0x44000000);
        gfx.fill(x, y + h - 1, x + w, y + h, 0x44000000);
        int color = down ? 0xFF0B0E12 : settings.textColor;
        int labelWidth = font.width(label);
        gfx.text(font, label, x + (w - labelWidth) / 2, y + (h - font.lineHeight) / 2 + 1, color, down ? false : settings.textShadow);
    }
}
