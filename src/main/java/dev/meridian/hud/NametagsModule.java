package dev.meridian.hud;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.Vec3d;

import org.joml.Matrix4f;
import org.joml.Quaternionf;

import dev.meridian.config.Config;
import dev.meridian.config.ModuleRegistry;
import dev.meridian.config.ModuleSettings;

public class NametagsModule extends HudModule {

    private static final double MAX_RANGE = 48.0;
    private static final int BAR_SEGMENTS = 10;

    public NametagsModule(ModuleSettings settings) {
        super(settings);
    }

    @Override
    public boolean isOverlay() {
        return true;
    }

    @Override
    protected int contentWidth(TextRenderer font) {
        return 1;
    }

    @Override
    protected int contentHeight(TextRenderer font) {
        return 1;
    }

    @Override
    protected void renderContent(DrawContext gfx, TextRenderer font, float deltaTicks) {
    }

    public static void renderWorld(VertexConsumerProvider consumers, Camera camera) {
        ModuleSettings settings = Config.INSTANCE.module(ModuleRegistry.NAMETAGS);
        if (!settings.enabled) {
            return;
        }
        MinecraftClient mc = MinecraftClient.getInstance();
        if (mc.world == null || mc.player == null) {
            return;
        }
        TextRenderer font = mc.textRenderer;
        Vec3d camPos = camera.getPos();
        Quaternionf rotation = new Quaternionf(camera.getRotation()).conjugate();
        for (PlayerEntity player : mc.world.getPlayers()) {
            if (player == mc.player || !player.isAlive()) {
                continue;
            }
            Vec3d pos = player.getPos();
            if (pos.distanceTo(camPos) > MAX_RANGE) {
                continue;
            }
            String name = player.getDisplayName().getString();
            float fraction = player.getMaxHealth() <= 0 ? 1f : player.getHealth() / player.getMaxHealth();
            drawLabel(font, consumers, rotation, camPos, pos, player.getHeight(), name, fraction, settings.textColor);
        }
    }

    private static void drawLabel(TextRenderer font, VertexConsumerProvider consumers, Quaternionf rotation,
                                  Vec3d camPos, Vec3d pos, float entityHeight, String name, float fraction,
                                  int nameColor) {
        Matrix4f matrix = new Matrix4f().rotation(rotation);
        matrix.translate(
                (float) (pos.x - camPos.x),
                (float) (pos.y + entityHeight + 0.35 - camPos.y),
                (float) (pos.z - camPos.z)
        );
        int nameWidth = font.getWidth(name);
        font.draw(name, -nameWidth / 2.0f, 0, nameColor, true, matrix, consumers,
                TextRenderer.TextLayerType.SEE_THROUGH, 0x40000000, 0xF000F0);
        int filled = Math.round(BAR_SEGMENTS * Math.max(0f, Math.min(1f, fraction)));
        String full = repeat('\u2588', filled);
        String empty = repeat('\u2591', BAR_SEGMENTS - filled);
        int fullWidth = font.getWidth(full);
        int emptyWidth = font.getWidth(empty);
        int totalWidth = fullWidth + emptyWidth;
        float barY = 11.0f;
        int barColorSafe = fraction > 0.5f ? 0xFF55FF55 : fraction > 0.25f ? 0xFFFFD700 : 0xFFFF5555;
        if (filled > 0) {
            font.draw(full, -totalWidth / 2.0f, barY, barColorSafe, true, matrix, consumers,
                    TextRenderer.TextLayerType.SEE_THROUGH, 0, 0xF000F0);
        }
        if (BAR_SEGMENTS - filled > 0) {
            font.draw(empty, -totalWidth / 2.0f + fullWidth, barY, 0xFF555555, true, matrix, consumers,
                    TextRenderer.TextLayerType.SEE_THROUGH, 0, 0xF000F0);
        }
    }

    private static String repeat(char c, int count) {
        StringBuilder sb = new StringBuilder(count);
        for (int i = 0; i < count; i++) {
            sb.append(c);
        }
        return sb.toString();
    }
}