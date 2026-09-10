package dev.meridian.hud;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.Vec3d;

import dev.meridian.config.ModuleSettings;

public class SpeedometerModule extends HudModule {

    public SpeedometerModule(ModuleSettings settings) {
        super(settings);
    }

    @Override
    protected int contentWidth(TextRenderer font) {
        return pairWidth(font, "Speed ", "0.0 m/s  0.0 km/h");
    }

    @Override
    protected int contentHeight(TextRenderer font) {
        return font.fontHeight;
    }

    @Override
    protected void renderContent(DrawContext gfx, TextRenderer font, float deltaTicks) {
        MinecraftClient mc = MinecraftClient.getInstance();
        PlayerEntity player = mc.player;
        if (player == null) {
            drawPair(gfx, font, "Speed ", "0.0 m/s  0.0 km/h", 0, 0);
            return;
        }
        Vec3d velocity = player.getVelocity();
        double blocksPerSecond = Math.sqrt(velocity.x * velocity.x + velocity.z * velocity.z) * 20.0;
        double kmh = blocksPerSecond * 3.6;
        drawPair(gfx, font, "Speed ", String.format("%.1f m/s  %.1f km/h", blocksPerSecond, kmh), 0, 0);
    }
}