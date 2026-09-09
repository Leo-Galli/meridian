package dev.meridian.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.MouseButtonEvent;

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
        addRenderableOnly((gfx, mouseX, mouseY, delta) -> drawContent(gfx, mouseX, mouseY));
        addButton(8, 10, 80, 20, "Back", () -> goBack());
        addCenteredText(width / 2, 16, 0xFFFFFFFF, "HUD Layout Editor");
        addButton(width - 96, height - 30, 88, 20, "Done", () -> goBack());
        addCenteredText(width / 2, height - 12, 0xFF6C7686, "Drag modules to reposition them");
    }

    private void drawContent(GuiGraphicsExtractor gfx, int mouseX, int mouseY) {
        gfx.fill(0, 0, width, height, 0x5004090E);
        HudRenderer.INSTANCE.drawAllForLayout(gfx, font);
        HudModule hovered = HudRenderer.INSTANCE.moduleAt(mouseX, mouseY, font, width, height);
        HudModule highlight = dragging != null ? dragging : hovered;
        if (highlight != null) {
            HudRenderer.Box box = HudRenderer.INSTANCE.bounds(highlight, font, width, height);
            gfx.fill(box.x - 1, box.y - 1, box.x + box.w + 1, box.y + 2, 0xFFFFD75E);
            gfx.fill(box.x - 1, box.y + box.h - 2, box.x + box.w + 1, box.y + box.h + 1, 0xFFFFD75E);
            gfx.fill(box.x - 1, box.y, box.x, box.y + box.h, 0xFFFFD75E);
            gfx.fill(box.x + box.w, box.y, box.x + box.w + 1, box.y + box.h, 0xFFFFD75E);
        }
        if (dragging != null) {
            HudRenderer.Box box = HudRenderer.INSTANCE.bounds(dragging, font, width, height);
            String position = dragging.settings.id + "  x: " + box.x + "  y: " + box.y;
            gfx.text(font, position, 8, height - 34, 0xFFB7C2D0, true);
        }
    }

    @Override
    public boolean mouseClicked(MouseButtonEvent event, boolean doubleClicked) {
        int mx = (int) Math.round(event.x());
        int my = (int) Math.round(event.y());
        HudModule module = HudRenderer.INSTANCE.moduleAt(mx, my, font, width, height);
        if (module != null) {
            HudRenderer.Box box = HudRenderer.INSTANCE.bounds(module, font, width, height);
            dragging = module;
            grabX = mx - box.x;
            grabY = my - box.y;
            return true;
        }
        return super.mouseClicked(event, doubleClicked);
    }

    @Override
    public boolean mouseDragged(MouseButtonEvent event, double dragX, double dragY) {
        if (dragging != null) {
            int mx = (int) Math.round(event.x());
            int my = (int) Math.round(event.y());
            dragging.settings.x = Math.max(0, mx - grabX);
            dragging.settings.y = Math.max(0, my - grabY);
            return true;
        }
        return super.mouseDragged(event, dragX, dragY);
    }

    @Override
    public boolean mouseReleased(MouseButtonEvent event) {
        if (dragging != null) {
            dragging = null;
            Config.INSTANCE.save();
            return true;
        }
        return super.mouseReleased(event);
    }

    private static Minecraft mc() {
        return Minecraft.getInstance();
    }
}
