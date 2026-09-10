package dev.meridian.gui;

import java.util.List;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;

import dev.meridian.config.Config;
import dev.meridian.config.ModuleRegistry;
import dev.meridian.config.ModuleSettings;

public class ModulesScreen extends MeridianScreen {

    private final List<String> ids = ModuleRegistry.ORDER;

    public ModulesScreen(Screen parent) {
        super("Modules & Appearance", parent);
    }

    @Override
    protected void init() {
        super.init();
        addScrim();
        addButton(8, 10, 80, 20, "Back", () -> goBack());
        addCenteredText(width / 2, 16, 0xFFFFFFFF, "Modules & Appearance");

        int panelWidth = Math.min(520, width - 30);
        int panelX = (width - panelWidth) / 2;
        int panelY = 42;
        int panelHeight = height - panelY - 30;
        addPanel(panelX, panelY, panelWidth, panelHeight);
        int listTop = panelY + 8;
        int rowHeight = 26;
        int maxRows = (panelHeight - 16) / rowHeight;
        int rows = Math.min(ids.size(), Math.max(1, maxRows));
        for (int i = 0; i < rows; i++) {
            String id = ids.get(i);
            ModuleSettings settings = Config.INSTANCE.module(id);
            int y = listTop + i * rowHeight;
            addRow(panelX + 10, y, panelWidth - 20, id, settings);
        }
        int hintY = panelY + panelHeight - 14;
        addText(panelX + 10, hintY - 8, 0xFF6C7686, "Drag modules in the HUD Layout Editor to reposition them.");
        addButton(panelX + 10, height - 26, 130, 20, "Reset All Modules", () -> {
            for (ModuleSettings s : Config.INSTANCE.modules().values()) {
                ModuleSettings d = ModuleRegistry.defaultsFor(s.id);
                s.enabled = d.enabled;
                s.x = d.x;
                s.y = d.y;
                s.scale = d.scale;
                s.textColor = d.textColor;
                s.accentColor = d.accentColor;
                s.background = d.background;
                s.backgroundOpacity = d.backgroundOpacity;
                s.textShadow = d.textShadow;
            }
            Config.INSTANCE.save();
            clearAndRebuild();
        });
        addButton((width - 160) / 2, height - 26, 160, 20, "HUD Layout Editor", () -> open(new LayoutScreen(this)));
    }

    private void addRow(int x, int y, int rowWidth, String id, ModuleSettings settings) {
        addCheckbox(x, y, "", settings.enabled, (checkbox, value) -> {
            settings.enabled = value;
            Config.INSTANCE.save();
        });
        addText(x + 24, y + 1, settings.enabled ? 0xFFFFFFFF : 0xFF7A8393, ModuleRegistry.title(id));
        int buttonWidth = 108;
        int buttonX = x + rowWidth - buttonWidth;
        addButton(buttonX, y - 1, buttonWidth, 20, "Customize", () -> open(new ModuleSettingsScreen(this, id)));
        int infoX = x + 24 + Math.min(textRenderer.getWidth(ModuleRegistry.title(id)), 220) + 14;
        String description = ModuleRegistry.description(id);
        int available = buttonX - infoX - 12;
        if (available > 60) {
            String clipped = textRenderer.trimToWidth(description, available);
            addText(infoX, y + 2, 0xFF7C8494, clipped);
        }
    }

    private static void open(Screen screen) {
        MinecraftClient.getInstance().setScreen(screen);
    }
}