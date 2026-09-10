package dev.meridian.hud;

import java.util.Collection;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Identifier;

import dev.meridian.config.ModuleSettings;

public class PotionEffectsModule extends HudModule {

    private static final int ICON_SIZE = 16;
    private static final int ROW_HEIGHT = 18;

    public PotionEffectsModule(ModuleSettings settings) {
        super(settings);
    }

    @Override
    protected int contentWidth(TextRenderer font) {
        int widest = 0;
        for (StatusEffectInstance effect : effects()) {
            int w = ICON_SIZE + 4 + font.getWidth(label(effect)) + font.getWidth(timeText(effect));
            widest = Math.max(widest, w);
        }
        return widest == 0 ? font.getWidth("No Effects") : widest;
    }

    @Override
    protected int contentHeight(TextRenderer font) {
        int count = effects().size();
        return count == 0 ? font.fontHeight : count * ROW_HEIGHT;
    }

    @Override
    protected void renderContent(DrawContext gfx, TextRenderer font, float deltaTicks) {
        Collection<StatusEffectInstance> effects = effects();
        if (effects.isEmpty()) {
            drawText(gfx, font, "No Effects", 0, 0, settings.textColor);
            return;
        }
        int y = 0;
        for (StatusEffectInstance effect : effects) {
            Identifier sprite = spriteId(effect);
            if (sprite != null) {
                gfx.drawGuiTexture(RenderPipelines.GUI_TEXTURED, sprite, 0, y, ICON_SIZE, ICON_SIZE);
            } else {
                gfx.fill(0, y, ICON_SIZE, y + ICON_SIZE, 0xFF222222);
            }
            String label = label(effect);
            int labelWidth = font.getWidth(label);
            drawText(gfx, font, label, ICON_SIZE + 4, y + 4, settings.textColor);
            drawText(gfx, font, timeText(effect), ICON_SIZE + 4 + labelWidth + 6, y + 4, settings.accentColor);
            y += ROW_HEIGHT;
        }
    }

    private static Collection<StatusEffectInstance> effects() {
        MinecraftClient mc = MinecraftClient.getInstance();
        PlayerEntity player = mc.player;
        if (player == null) {
            return java.util.Collections.emptyList();
        }
        return player.getActiveStatusEffects().values();
    }

    private static String label(StatusEffectInstance effect) {
        String name = effect.getEffectType().value().getName().getString();
        int amplifier = effect.getAmplifier();
        if (amplifier > 0) {
            name = name + " " + roman(amplifier + 1);
        }
        return name;
    }

    private static String timeText(StatusEffectInstance effect) {
        int duration = effect.getDuration();
        if (duration < 0 || duration >= 1000000) {
            return "∞";
        }
        int totalSeconds = (duration + 19) / 20;
        int minutes = totalSeconds / 60;
        int seconds = totalSeconds % 60;
        return minutes + ":" + (seconds < 10 ? "0" : "") + seconds;
    }

    private static String roman(int value) {
        String[] numerals = {"", "I", "II", "III", "IV", "V", "VI", "VII", "VIII", "IX", "X"};
        return value < numerals.length ? numerals[value] : String.valueOf(value);
    }

    private static Identifier spriteId(StatusEffectInstance effect) {
        String translationKey = effect.getEffectType().value().getTranslationKey();
        if (translationKey == null || !translationKey.startsWith("effect.")) {
            return null;
        }
        String rest = translationKey.substring("effect.".length());
        int dot = rest.indexOf('.');
        if (dot <= 0) {
            return null;
        }
        String namespace = rest.substring(0, dot);
        String path = rest.substring(dot + 1);
        if (namespace.isEmpty() || path.isEmpty()) {
            return null;
        }
        return Identifier.of(namespace, "hud/effect/" + path);
    }
}