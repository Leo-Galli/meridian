package dev.meridian.gui;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.Drawable;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.CheckboxWidget;
import net.minecraft.text.Text;

public abstract class MeridianScreen extends Screen {

    protected final Screen parent;

    protected MeridianScreen(String title) {
        this(title, null);
    }

    protected MeridianScreen(String title, Screen parent) {
        super(Text.literal(title));
        this.parent = parent;
    }

    protected void goBack() {
        MinecraftClient.getInstance().setScreen(parent);
    }

    @Override
    public void close() {
        goBack();
    }

    @Override
    public boolean shouldPause() {
        return false;
    }

    protected void clearAndRebuild() {
        clearChildren();
        init();
    }

    protected void addScrim() {
        addDrawable((gfx, mouseX, mouseY, delta) -> {
            gfx.fill(0, 0, width, height, 0xC0050A12);
            gfx.drawHorizontalLine(0, width, 0, 0xFF223040);
            gfx.drawHorizontalLine(0, width, height - 1, 0xFF223040);
        });
    }

    protected void addTitle(int y, String text) {
        addCenteredText(width / 2, y, 0xFFFFFFFF, text);
    }

    protected void addCenteredText(int centerX, int y, int color, String text) {
        int w = textRenderer.getWidth(text);
        addText(centerX - w / 2, y, color, text);
    }

    protected void addText(int x, int y, int color, String text) {
        addDrawable((gfx, mouseX, mouseY, delta) ->
                gfx.drawText(textRenderer, text, x, y, color, true));
    }

    protected void addText(int x, int y, int color, String text, boolean shadow) {
        addDrawable((gfx, mouseX, mouseY, delta) ->
                gfx.drawText(textRenderer, text, x, y, color, shadow));
    }

    protected void addPanel(int x, int y, int w, int h) {
        addDrawable((gfx, mouseX, mouseY, delta) -> {
            gfx.fill(x, y, x + w, y + h, 0x99070B11);
            gfx.fill(x, y, x + w, y + 1, 0x2E3A4A66);
            gfx.fill(x, y + h - 1, x + w, y + h, 0x66000000);
            gfx.fill(x, y, x + 1, y + h, 0x22304066);
            gfx.fill(x + w - 1, y, x + w, y + h, 0x22304066);
        });
    }

    protected ButtonWidget addButton(int x, int y, int w, int h, String label, ButtonWidget.PressAction onPress) {
        ButtonWidget button = ButtonWidget.builder(Text.literal(label), onPress)
                .dimensions(x, y, w, h)
                .build();
        return addDrawableChild(button);
    }

    protected ButtonWidget addButton(int x, int y, int w, int h, String label, Runnable action) {
        return addButton(x, y, w, h, label, button -> action.run());
    }

    protected CheckboxWidget addCheckbox(int x, int y, String label, boolean selected, CheckboxWidget.Callback onChange) {
        CheckboxWidget checkbox = CheckboxWidget.builder(Text.literal(label), textRenderer)
                .pos(x, y)
                .checked(selected)
                .callback(onChange)
                .build();
        return addDrawableChild(checkbox);
    }

    protected static Drawable noOpRenderable() {
        return (gfx, mouseX, mouseY, delta) -> {
        };
    }

    protected void background(DrawContext gfx) {
    }
}