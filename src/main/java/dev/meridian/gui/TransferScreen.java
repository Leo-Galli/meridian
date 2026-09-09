package dev.meridian.gui;

import java.nio.file.Path;

import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.gui.screen.Screen;

import dev.meridian.config.Config;

public class TransferScreen extends MeridianScreen {

    private static final Path TRANSFER_FILE = FabricLoader.getInstance().getGameDir().resolve("meridian-transfer.json");

    private String status = "";
    private int statusColor = 0xFFB7C2D0;

    public TransferScreen(Screen parent) {
        super("Backup & Transfer", parent);
    }

    @Override
    protected void init() {
        super.init();
        addScrim();
        int centerX = width / 2;
        int panelWidth = Math.min(400, width - 40);
        int panelX = centerX - panelWidth / 2;
        addPanel(panelX, 24, panelWidth, height - 48);
        addCenteredText(centerX, 42, 0xFF58E8FF, "BACKUP & TRANSFER");
        addCenteredText(centerX, 56, 0xFF8A93A3, "Move your settings, waypoints and macros");
        addCenteredText(centerX, 78, 0xFF7C8494, "Transfer file:");
        String path = TRANSFER_FILE.toString();
        if (textRenderer.getWidth(path) > panelWidth - 32) {
            path = textRenderer.trimToWidth(path, panelWidth - 32);
        }
        addCenteredText(centerX, 92, 0xFF5A6272, path);

        int innerWidth = panelWidth - 56;
        int y = 124;
        int spacing = 34;
        addButton(centerX - innerWidth / 2, y, innerWidth, 24, "Export Settings", this::export);
        addButton(centerX - innerWidth / 2, y + spacing, innerWidth, 24, "Import Settings", this::importSettings);
        addButton(centerX - innerWidth / 2, y + spacing * 2, innerWidth, 24, "Reset to Defaults", this::reset);
        addCenteredText(centerX, y + spacing * 2 + 42, 0xFF5A6272,
                "Export writes a file you can copy to another computer.");
        addCenteredText(centerX, y + spacing * 2 + 56, 0xFF5A6272,
                "Import loads that file and replaces the current config.");

        addCenteredText(centerX, height - 62, statusColor, status);
        addButton(centerX - 60, height - 38, 120, 20, "Back", () -> goBack());
    }

    private void export() {
        Config.INSTANCE.saveTo(TRANSFER_FILE);
        status = "Settings exported to meridian-transfer.json";
        statusColor = 0xFF7CFF8A;
        clearAndRebuild();
    }

    private void importSettings() {
        if (Config.INSTANCE.loadFrom(TRANSFER_FILE)) {
            status = "Settings imported and applied";
            statusColor = 0xFF7CFF8A;
        } else {
            status = "No transfer file found";
            statusColor = 0xFFFF5C7A;
        }
        clearAndRebuild();
    }

    private void reset() {
        Config.INSTANCE.reset();
        status = "All settings restored to defaults";
        statusColor = 0xFFFFD75E;
        clearAndRebuild();
    }
}