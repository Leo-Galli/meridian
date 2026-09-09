package dev.meridian.gui;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;

import dev.meridian.config.Config;
import dev.meridian.hud.HudModule;
import dev.meridian.hud.HudRenderer;

public class LayoutScreen extends MeridianScreen {

    private HudModule dragging;
    private int grabX;
    private int grabY;

    public LayoutScreen(Screen parent) {
        super("HUD Layout Editor", parent);
    }

    @Override
    protected void init() {
        super.init();
        addDrawable((gfx, mouseX, mouseY, delta) -> drawContent(gfx, mouseX, mouseY));
        addButton(8, 10, 80, 20, "Back", () -> goBack());
        addCenteredText(width / 2, 16, 0xFFFFFFFF, "HUD Layout Editor");
        addButton(width - 96, height - 30, 88, 20, "Done", () -> goBack());
        addCenteredText(width / 2, height - 12, 0xFF6C7686, "Drag modules to reposition them");
    }

    private void drawContent(DrawContext gfx, int mouseX, int mouseY) {
        gfx.fill(0, 0, width, height, 0x5004090E);
        HudRenderer.INSTANCE.drawAllForLayout(gfx, textRenderer);
        HudModule hovered = HudRenderer.INSTANCE.moduleAt(mouseX, mouseY, textRenderer, width, height);
        HudModule highlight = dragging != null ? dragging : hovered;
        if (highlight != null) {
            HudRenderer.Box box = HudRenderer.INSTANCE.bounds(highlight, textRenderer, width, height);
            gfx.fill(box.x - 1, box.y - 1, box.x + box.w + 1, box.y + 2, 0xFFFFD75E);
            gfx.fill(box.x - 1, box.y + box.h - 2, box.x + box.w + 1, box.y + box.h + 1, 0xFFFFD75E);
            gfx.fill(box.x - 1, box.y, box.x, box.y + box.h, 0xFFFFD75E);
            gfx.fill(box.x + box.w, box.y, box.x + box.w + 1, box.y + box.h, 0xFFFFD75E);
        }
        if (dragging != null) {
            HudRenderer.Box box = HudRenderer.INSTANCE.bounds(dragging, textRenderer, width, height);
            String position = dragging.settings.id + "  x: " + box.x + "  y: " + box.y;
            gfx.drawText(textRenderer, position, 8, height - 34, 0xFFB7C2D0, true);
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        int mx = (int) Math.round(mouseX);
        int my = (int) Math.round(mouseY);
        HudModule module = HudRenderer.INSTANCE.moduleAt(mx, my, textRenderer, width, height);
        if (module != null) {
            HudRenderer.Box box = HudRenderer.INSTANCE.bounds(module, textRenderer, width, height);
            dragging = module;
            grabX = mx - box.x;
            grabY = my - box.y;
            return true;
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        if (dragging != null) {
            int mx = (int) Math.round(mouseX);
            int my = (int) Math.round(mouseY);
            dragging.settings.x = Math.max(0, mx - grabX);
            dragging.settings.y = Math.max(0, my - grabY);
            return true;
        }
        return super.mouseDragged(mouseX, mouseY, button, deltaX, deltaY);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        if (dragging != null) {
            dragging = null;
            Config.INSTANCE.save();
            return true;
        }
        return super.mouseReleased(mouseX, mouseY, button);
    }
}