package dev.meridian.hud;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;

import dev.meridian.config.ModuleSettings;

public class CoordinatesModule extends HudModule {

    public CoordinatesModule(ModuleSettings settings) {
        super(settings);
    }

    @Override
    protected int contentWidth(TextRenderer font) {
        int w1 = pairWidth(font, "XYZ ", "0 0 0");
        int w2 = pairWidth(font, "Facing ", "S  180°");
        int w3 = pairWidth(font, "Biome ", "plains");
        return Math.max(w1, Math.max(w2, w3));
    }

    @Override
    protected int contentHeight(TextRenderer font) {
        return font.fontHeight * 3 + 4;
    }

    @Override
    protected void renderContent(DrawContext gfx, TextRenderer font, float deltaTicks) {
        MinecraftClient mc = MinecraftClient.getInstance();
        PlayerEntity player = mc.player;
        if (player == null) {
            return;
        }
        int x = player.getBlockX();
        int y = player.getBlockY();
        int z = player.getBlockZ();
        int heading = DirectionModule.heading();
        drawPair(gfx, font, "XYZ ", x + " " + y + " " + z, 0, 0);
        drawPair(gfx, font, "Facing ", DirectionModule.windName(heading) + "  " + heading + "°", 0, font.fontHeight + 2);
        drawPair(gfx, font, "Biome ", biomeName(mc), 0, font.fontHeight * 2 + 4);
    }

    private static String biomeName(MinecraftClient mc) {
        if (mc.world == null || mc.player == null) {
            return "?";
        }
        BlockPos pos = mc.player.getBlockPos();
        return mc.world.getBiome(pos).getKey()
                .map(key -> key.getValue().getPath())
                .orElse("?");
    }
}