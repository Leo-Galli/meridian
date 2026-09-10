package dev.meridian.hud;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.sound.SoundInstance;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;

import org.joml.Matrix4f;

import dev.meridian.config.Config;
import dev.meridian.config.ModuleRegistry;
import dev.meridian.config.ModuleSettings;

public class SoundIndicatorModule extends HudModule {

    private static final long DURATION_MS = 1100L;
    private static final double MAX_RANGE = 32.0;
    private static final int MAX_MARKERS = 64;

    private static final List<Marker> markers = new ArrayList<>();

    private static final class Marker {
        double x;
        double y;
        double z;
        long start;
        int color;
    }

    public SoundIndicatorModule(ModuleSettings settings) {
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

    public static void onSound(SoundInstance sound) {
        ModuleSettings settings = Config.INSTANCE.module(ModuleRegistry.SOUND_INDICATOR);
        if (!settings.enabled) {
            return;
        }
        MinecraftClient mc = MinecraftClient.getInstance();
        if (mc.player == null || mc.world == null) {
            return;
        }
        double sx = sound.getX();
        double sy = sound.getY();
        double sz = sound.getZ();
        if (sx == 0 && sy == 0 && sz == 0) {
            return;
        }
        double dx = sx - mc.player.getX();
        double dy = sy - mc.player.getY();
        double dz = sz - mc.player.getZ();
        if (Math.sqrt(dx * dx + dy * dy + dz * dz) > MAX_RANGE) {
            return;
        }
        Marker marker = new Marker();
        marker.x = sx;
        marker.y = sy;
        marker.z = sz;
        marker.start = System.currentTimeMillis();
        marker.color = colorFor(sound.getId());
        markers.add(marker);
        while (markers.size() > MAX_MARKERS) {
            markers.remove(0);
        }
    }

    private static int colorFor(Identifier id) {
        String path = id == null ? "" : id.getPath();
        if (path.contains("explosion") || path.contains("thunder")) {
            return 0xFFFF5555;
        }
        if (path.contains("step") || path.contains("hit") || path.contains("hurt")) {
            return 0xFF7CFF8A;
        }
        return 0xFF58E8FF;
    }

    public static void renderWorld(VertexConsumerProvider consumers, MatrixStack matrices) {
        ModuleSettings settings = Config.INSTANCE.module(ModuleRegistry.SOUND_INDICATOR);
        if (!settings.enabled || markers.isEmpty()) {
            return;
        }
        long now = System.currentTimeMillis();
        markers.removeIf(marker -> now - marker.start > DURATION_MS);
        if (markers.isEmpty()) {
            return;
        }
        VertexConsumer buffer = consumers.getBuffer(RenderLayer.getLines());
        Matrix4f matrix = matrices.peek().getPositionMatrix();
        int segments = 40;
        for (Marker marker : markers) {
            float progress = Math.min(1f, (float) (now - marker.start) / DURATION_MS);
            float radius = 0.35f + progress * 2.6f;
            int alpha = Math.max(0, Math.min(255, Math.round(190 * (1f - progress))));
            int r = (marker.color >> 16) & 0xFF;
            int g = (marker.color >> 8) & 0xFF;
            int b = marker.color & 0xFF;
            float y = (float) marker.y + 0.15f;
            for (int i = 0; i < segments; i++) {
                double a1 = i * Math.PI * 2 / segments;
                double a2 = (i + 1) * Math.PI * 2 / segments;
                buffer.vertex(matrix, (float) (marker.x + Math.cos(a1) * radius), y, (float) (marker.z + Math.sin(a1) * radius))
                        .color(r, g, b, alpha).normal(0, 1, 0);
                buffer.vertex(matrix, (float) (marker.x + Math.cos(a2) * radius), y, (float) (marker.z + Math.sin(a2) * radius))
                        .color(r, g, b, alpha).normal(0, 1, 0);
            }
        }
    }
}