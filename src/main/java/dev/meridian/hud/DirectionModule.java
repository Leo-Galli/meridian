package dev.meridian.hud;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.util.math.MathHelper;

import dev.meridian.config.ModuleSettings;

public class DirectionModule extends HudModule {

    private static final String[] WINDS = {
            "N", "NNE", "NE", "ENE", "E", "ESE", "SE", "SSE",
            "S", "SSW", "SW", "WSW", "W", "WNW", "NW", "NNW"
    };

    private static final String[] STRIP = {"N", "NE", "E", "SE", "S", "SW", "W", "NW"};

    private static final int STRIP_WIDTH = 130;
    private static final int STRIP_HEIGHT = 10;

    public DirectionModule(ModuleSettings settings) {
        super(settings);
    }

    @Override
    protected int contentWidth(TextRenderer font) {
        return Math.max(font.getWidth("Direction  000°"), STRIP_WIDTH);
    }

    @Override
    protected int contentHeight(TextRenderer font) {
        return font.fontHeight + 4 + STRIP_HEIGHT;
    }

    @Override
    protected void renderContent(DrawContext gfx, TextRenderer font, float deltaTicks) {
        int heading = heading();
        String name = WINDS[Math.floorMod(Math.round(heading / 22.5f), 16)];
        drawText(gfx, font, "Direction  ", 0, 0, settings.textColor);
        drawText(gfx, font, name + "  " + heading + "°", font.getWidth("Direction  "), 0, settings.accentColor);
        drawStrip(gfx, font, heading);
    }

    private void drawStrip(DrawContext gfx, TextRenderer font, int heading) {
        int y = font.fontHeight + 6;
        int center = STRIP_WIDTH / 2;
        gfx.fill(0, y, STRIP_WIDTH, y + 1, 0xAA000000);
        gfx.fill(0, y + STRIP_HEIGHT - 1, STRIP_WIDTH, y + STRIP_HEIGHT, 0xAA000000);
        int pxPerDegree = Math.max(1, Math.round(STRIP_WIDTH / 150.0f));
        for (int i = 0; i < STRIP.length; i++) {
            int angle = i * 45;
            int rel = MathHelper.wrapDegrees(angle - heading);
            if (rel < -75 || rel > 75) {
                continue;
            }
            int x = center + rel * pxPerDegree;
            boolean primary = rel == 0;
            int color = primary ? settings.accentColor : settings.textColor;
            drawText(gfx, font, STRIP[i], x - font.getWidth(STRIP[i]) / 2, y + 1, color);
        }
    }

    public static int heading() {
        MinecraftClient mc = MinecraftClient.getInstance();
        float yaw = 0;
        if (mc.player != null) {
            yaw = mc.player.getYaw();
        } else if (mc.gameRenderer != null && mc.gameRenderer.getCamera() != null) {
            yaw = mc.gameRenderer.getCamera().getYaw();
        }
        return Math.floorMod(Math.round(yaw) + 180, 360);
    }
}