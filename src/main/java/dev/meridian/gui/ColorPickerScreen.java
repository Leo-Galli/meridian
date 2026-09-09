package dev.meridian.gui;

import java.util.function.IntConsumer;

import net.minecraft.client.gui.screen.Screen;

import dev.meridian.util.Palette;

public class ColorPickerScreen extends MeridianScreen {

    private final IntConsumer onPick;

    public ColorPickerScreen(Screen parent, String title, int initial, IntConsumer onPick) {
        super("Pick Color - " + title, parent);
        this.onPick = onPick;
    }

    @Override
    protected void init() {
        super.init();
        addScrim();
        addButton(8, 10, 80, 20, "Back", () -> goBack());
        addCenteredText(width / 2, 16, 0xFFFFFFFF, getTitle().getString());

        int panelWidth = Math.min(340, width - 30);
        int panelX = (width - panelWidth) / 2;
        int panelY = 44;
        int cols = 4;
        int buttonW = 62;
        int buttonH = 20;
        int gapX = 12;
        int gapY = 8;
        int gridWidth = cols * buttonW + (cols - 1) * gapX;
        int gridX = panelX + (panelWidth - gridWidth) / 2;
        int rows = (int) Math.ceil(Palette.size() / (double) cols);
        int panelHeight = gridY(panelY, rows, buttonH, gapY) + 12;
        addPanel(panelX, panelY, panelWidth, panelHeight);

        for (int i = 0; i < Palette.size(); i++) {
            int index = i;
            int x = gridX + (i % cols) * (buttonW + gapX);
            int y = gridY(panelY, i / cols, buttonH, gapY);
            addButton(x, y, buttonW, buttonH, Palette.name(i), () -> {
                onPick.accept(Palette.color(index));
                goBack();
            });
        }

        addButton((width - 140) / 2, panelY + panelHeight + 10, 140, 20, "Cancel", () -> goBack());
    }

    private static int gridY(int panelY, int row, int buttonH, int gapY) {
        return panelY + 10 + row * (buttonH + gapY);
    }
}