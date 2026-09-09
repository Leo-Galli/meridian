package dev.meridian.hud;

import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElement;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;

public final class MeridianHudElement implements HudElement {

    @Override
    public void extractRenderState(GuiGraphicsExtractor gfx, DeltaTracker deltaTracker) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.gui.hud == null || mc.player == null || mc.level == null) {
            return;
        }
        HudRenderer.INSTANCE.render(gfx, mc.gui.hud.getFont(), deltaTracker);
    }
}
