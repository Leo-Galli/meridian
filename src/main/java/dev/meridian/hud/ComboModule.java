package dev.meridian.hud;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;

import dev.meridian.config.ModuleSettings;

public class ComboModule extends HudModule {

    private static final long RESET_MS = 3000L;

    private static int combo;
    private static long lastHit;
    private static float lastHealth = -1f;

    public ComboModule(ModuleSettings settings) {
        super(settings);
    }

    public static void onPlayerAttack(PlayerEntity player, Entity target) {
        MinecraftClient mc = MinecraftClient.getInstance();
        if (mc.player == null || player != mc.player || target == null || target == mc.player) {
            return;
        }
        combo++;
        lastHit = System.currentTimeMillis();
        lastHealth = mc.player.getHealth();
    }

    public static void update() {
        MinecraftClient mc = MinecraftClient.getInstance();
        if (mc.player == null || mc.world == null) {
            combo = 0;
            lastHealth = -1f;
            return;
        }
        float health = mc.player.getHealth();
        if (lastHealth < 0) {
            lastHealth = health;
        }
        if (health < lastHealth - 0.001f) {
            combo = 0;
        }
        lastHealth = health;
        if (System.currentTimeMillis() - lastHit > RESET_MS) {
            combo = 0;
        }
    }

    @Override
    protected int contentWidth(TextRenderer font) {
        return pairWidth(font, "Combo: ", String.valueOf(combo));
    }

    @Override
    protected int contentHeight(TextRenderer font) {
        return font.fontHeight;
    }

    @Override
    protected void renderContent(DrawContext gfx, TextRenderer font, float deltaTicks) {
        drawPair(gfx, font, "Combo: ", String.valueOf(combo), 0, 0);
    }
}