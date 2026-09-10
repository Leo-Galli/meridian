package dev.meridian.gui;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;

public class MeridianButton extends ButtonWidget {

    private final int accent;

    public MeridianButton(int x, int y, int width, int height, Text message, PressAction onPress, int accent) {
        super(x, y, width, height, message, onPress, DEFAULT_NARRATION_SUPPLIER);
        this.accent = accent;
    }

    public MeridianButton(int x, int y, int width, int height, Text message, PressAction onPress) {
        this(x, y, width, height, message, onPress, 0xFF2E3A4A);
    }

    @Override
    protected void renderWidget(DrawContext gfx, int mouseX, int mouseY, float delta) {
        TextRenderer tr = MinecraftClient.getInstance().textRenderer;
        int x = getX();
        int y = getY();
        int w = getWidth();
        int h = getHeight();
        boolean hovered = isHovered() && active;
        gfx.fill(x, y, x + w, y + h, hovered ? 0xCC0E1D2E : 0x99070B11);
        int border = hovered ? 0xFFFFD75E : accent;
        gfx.fill(x, y, x + w, y + 1, border);
        gfx.fill(x, y + h - 1, x + w, y + h, border);
        gfx.fill(x, y, x + 1, y + h, border);
        gfx.fill(x + w - 1, y, x + w, y + h, border);
        Text message = getMessage();
        int tw = tr.getWidth(message);
        gfx.drawText(tr, message, x + (w - tw) / 2, y + (h - 9) / 2, active ? 0xFFFFFFFF : 0xFF6C7686, true);
    }
}