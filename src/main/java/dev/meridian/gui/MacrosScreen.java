package dev.meridian.gui;

import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;

import dev.meridian.config.Config;
import dev.meridian.config.Macro;
import dev.meridian.util.KeyNames;

public class MacrosScreen extends MeridianScreen {

    private static final int ROW_HEIGHT = 24;

    private int scroll;

    public MacrosScreen(Screen parent) {
        super("AutoText Macros", parent);
    }

    @Override
    protected void init() {
        super.init();
        addScrim();
        addButton(8, 10, 80, 20, "Back", () -> goBack());
        addCenteredText(width / 2, 16, 0xFFFFFFFF, "AutoText Macros");
        addCenteredText(width / 2, 28, 0xFF7C8494, "Send preset chat messages by pressing a key while in game.");

        int panelWidth = Math.min(620, width - 30);
        int panelX = (width - panelWidth) / 2;
        int panelY = 46;
        int panelHeight = height - panelY - 58;
        addPanel(panelX, panelY, panelWidth, panelHeight);

        List<Macro> macros = Config.INSTANCE.macros();
        int listTop = panelY + 8;
        int listHeight = panelHeight - 16;
        int maxRows = listHeight / ROW_HEIGHT;
        int maxScroll = Math.max(0, macros.size() - maxRows);
        scroll = Math.min(scroll, maxScroll);

        if (macros.isEmpty()) {
            addCenteredText(width / 2, panelY + panelHeight / 2 - 10, 0xFF6C7686,
                    "No macros defined yet.");
        } else {
            for (int i = 0; i < macros.size(); i++) {
                Macro macro = macros.get(i);
                int rowY = listTop + i * ROW_HEIGHT - scroll * ROW_HEIGHT;
                int rowBottom = rowY + ROW_HEIGHT;
                if (rowBottom < panelY || rowY > panelY + panelHeight) {
                    continue;
                }
                addRow(panelX + 10, rowY, panelWidth - 20, i, macro);
            }
        }

        addButton((width - 240) / 2 - 10, height - 38, 240, 20, "Add Macro", () -> open(new MacroEditScreen(this, -1)));
    }

    private void addRow(int x, int rowY, int rowWidth, int index, Macro macro) {
        addCheckbox(x, rowY + 2, "", macro.enabled, (checkbox, value) -> {
            macro.enabled = value;
            Config.INSTANCE.save();
        });
        int textX = x + 24;
        String name = macro.name == null || macro.name.isEmpty() ? "Macro" : macro.name;
        int nameMax = Math.max(60, Math.min(180, rowWidth - 340));
        String clippedName = font.plainSubstrByWidth(name, nameMax);
        addText(textX, rowY + 3, macro.enabled ? 0xFFFFFFFF : 0xFF6C7686, clippedName);
        String message = macro.message == null ? "" : macro.message;
        int messageMax = Math.max(40, rowWidth - 400);
        String clippedMessage = font.plainSubstrByWidth(message, messageMax);
        addText(textX + Math.min(font.width(clippedName), nameMax) + 10, rowY + 3, 0xFF7C8494, clippedMessage);
        addText(x + rowWidth - 300, rowY + 3, 0xFFB7C2D0, KeyNames.display(macro.key));

        int right = x + rowWidth;
        int buttonWidth = 54;
        addButton(right - buttonWidth - 8 - buttonWidth - 8, rowY, buttonWidth, 20, "Edit",
                () -> open(new MacroEditScreen(this, index)));
        addButton(right - buttonWidth, rowY, buttonWidth, 20, "Remove", () -> {
            Config.INSTANCE.macros().remove(index);
            Config.INSTANCE.save();
            clearAndRebuild();
        });
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
        int total = Config.INSTANCE.macros().size();
        int panelHeight = height - 46 - 58;
        int visible = Math.max(1, (panelHeight - 16) / ROW_HEIGHT);
        int maxScroll = Math.max(0, total - visible);
        if (maxScroll <= 0) {
            return super.mouseScrolled(mouseX, mouseY, horizontalAmount, verticalAmount);
        }
        if (verticalAmount > 0) {
            scroll = Math.max(0, scroll - 1);
        } else if (verticalAmount < 0) {
            scroll = Math.min(maxScroll, scroll + 1);
        }
        clearAndRebuild();
        return true;
    }

    private static void open(Screen screen) {
        Minecraft.getInstance().gui.setScreen(screen);
    }
}
