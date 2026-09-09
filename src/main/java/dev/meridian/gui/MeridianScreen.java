package dev.meridian.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Checkbox;
import net.minecraft.client.gui.components.Renderable;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public abstract class MeridianScreen extends Screen {

    protected final Screen parent;

    protected MeridianScreen(String title) {
        this(title, null);
    }

    protected MeridianScreen(String title, Screen parent) {
        super(Component.literal(title));
        this.parent = parent;
    }

    protected void goBack() {
        Minecraft.getInstance().gui.setScreen(parent);
    }

    @Override
    public void onClose() {
        goBack();
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    protected void clearAndRebuild() {
        clearWidgets();
        init();
    }

    protected void addScrim() {
        addRenderableOnly((gfx, mouseX, mouseY, delta) -> {
            gfx.fill(0, 0, width, height, 0xC0050A12);
            gfx.horizontalLine(0, width, 0, 0xFF223040);
            gfx.horizontalLine(0, width, height - 1, 0xFF223040);
        });
    }

    protected void addTitle(int y, String text) {
        addCenteredText(width / 2, y, 0xFFFFFFFF, text);
    }

    protected void addCenteredText(int centerX, int y, int color, String text) {
        int w = font.width(text);
        addText(centerX - w / 2, y, color, text);
    }

    protected void addText(int x, int y, int color, String text) {
        addRenderableOnly((gfx, mouseX, mouseY, delta) ->
                gfx.text(font, text, x, y, color, true));
    }

    protected void addText(int x, int y, int color, String text, boolean shadow) {
        addRenderableOnly((gfx, mouseX, mouseY, delta) ->
                gfx.text(font, text, x, y, color, shadow));
    }

    protected void addPanel(int x, int y, int w, int h) {
        addRenderableOnly((gfx, mouseX, mouseY, delta) -> {
            gfx.fill(x, y, x + w, y + h, 0x99070B11);
            gfx.fill(x, y, x + w, y + 1, 0x2E3A4A66);
            gfx.fill(x, y + h - 1, x + w, y + h, 0x66000000);
            gfx.fill(x, y, x + 1, y + h, 0x22304066);
            gfx.fill(x + w - 1, y, x + w, y + h, 0x22304066);
        });
    }

    protected Button addButton(int x, int y, int w, int h, String label, Button.OnPress onPress) {
        Button button = Button.builder(Component.literal(label), onPress)
                .bounds(x, y, w, h)
                .build();
        return addRenderableWidget(button);
    }

    protected Button addButton(int x, int y, int w, int h, String label, Runnable action) {
        return addButton(x, y, w, h, label, button -> action.run());
    }

    protected Checkbox addCheckbox(int x, int y, String label, boolean selected, Checkbox.OnValueChange onChange) {
        Checkbox checkbox = Checkbox.builder(Component.literal(label), font)
                .pos(x, y)
                .selected(selected)
                .onValueChange(onChange)
                .build();
        return addRenderableWidget(checkbox);
    }

    protected static Renderable noOpRenderable() {
        return (gfx, mouseX, mouseY, delta) -> {
        };
    }

    protected void background(GuiGraphicsExtractor gfx) {
    }
}
