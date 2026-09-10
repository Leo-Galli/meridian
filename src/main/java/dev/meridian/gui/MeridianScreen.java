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
            gfx.fillGradient(0, 0, width, height, 0xE00A1320, 0xF004080E);
            gfx.fill(0, 0, width, 1, 0xFF58E8FF);
            gfx.drawHorizontalLine(0, width, height - 1, 0xFF223040);
        });
    }

    protected void addHeader(int centerX, int y, String text) {
        int w = textRenderer.getWidth(text);
        addText(centerX - w / 2, y, 0xFFFFFFFF, text);
        int half = Math.max(48, w / 2);
        addDrawable((gfx, mouseX, mouseY, delta) ->
                gfx.fillGradient(centerX - half, y + 15, centerX + half, y + 16, 0xFF58E8FF, 0xFF7C5CFF));
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
            gfx.fillGradient(x, y, x + w, y + h, 0xCC0A141F, 0xE5070B11);
            gfx.fillGradient(x, y, x + w, y + 2, 0xFF58E8FF, 0x22000000);
            gfx.fill(x, y + h - 1, x + w, y + h, 0x99304050);
            gfx.fill(x, y, x + 1, y + h, 0x441E2A38);
            gfx.fill(x + w - 1, y, x + w, y + h, 0x441E2A38);
        });
    }

    protected ButtonWidget addButton(int x, int y, int w, int h, String label, ButtonWidget.PressAction onPress) {
        return addButton(x, y, w, h, label, onPress, 0xFF2E3A4A);
    }

    protected ButtonWidget addButton(int x, int y, int w, int h, String label, Runnable action) {
        return addButton(x, y, w, h, label, button -> action.run(), 0xFF2E3A4A);
    }

    protected ButtonWidget addButton(int x, int y, int w, int h, String label, Runnable action, int accent) {
        return addButton(x, y, w, h, label, button -> action.run(), accent);
    }

    protected ButtonWidget addButton(int x, int y, int w, int h, String label, ButtonWidget.PressAction onPress, int accent) {
        MeridianButton button = new MeridianButton(x, y, w, h, Text.literal(label), onPress, accent);
        return addDrawableChild(button);
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