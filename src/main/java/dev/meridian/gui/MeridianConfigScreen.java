package dev.meridian.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;

public class MeridianConfigScreen extends MeridianScreen {

    private static final String VERSION = "1.0.0";

    public MeridianConfigScreen() {
        super("Meridian");
    }

    public MeridianConfigScreen(Screen parent) {
        super("Meridian", parent);
    }

    @Override
    protected void init() {
        super.init();
        addScrim();
        int centerX = width / 2;
        int panelWidth = Math.min(360, width - 40);
        int panelX = centerX - panelWidth / 2;
        addPanel(panelX, 24, panelWidth, height - 48);
        addCenteredText(centerX, 42, 0xFF58E8FF, "MERIDIAN");
        addCenteredText(centerX, 56, 0xFF8A93A3, "Client HUD suite  -  version " + VERSION);
        addCenteredText(centerX, 70, 0xFFB7C2D0, "FPS, ping, keystrokes, waypoints, macros and more.");

        int innerWidth = panelWidth - 48;
        int y = 106;
        int spacing = 30;
        addMenu(y, innerWidth, centerX, "Modules & Appearance", () -> open(new ModulesScreen(this)));
        addMenu(y + spacing, innerWidth, centerX, "HUD Layout Editor", () -> open(new LayoutScreen(this)));
        addMenu(y + spacing * 2, innerWidth, centerX, "Waypoints", () -> open(new WaypointsScreen(this)));
        addMenu(y + spacing * 3, innerWidth, centerX, "AutoText Macros", () -> open(new MacrosScreen(this)));

        int hintY = y + spacing * 4;
        addCenteredText(centerX, hintY, 0xFF7C8494, "Open this menu with Right Shift");
        addCenteredText(centerX, hintY + 12, 0xFF5A6272, "Rebindable in Options > Controls > Key Binds");

        addButton(centerX - 60, height - 38, 120, 20, "Close Menu", () -> goBack());
    }

    private void addMenu(int y, int innerWidth, int centerX, String label, Runnable action) {
        addButton(centerX - innerWidth / 2, y, innerWidth, 22, label, action);
    }

    private static void open(Screen screen) {
        Minecraft.getInstance().gui.setScreen(screen);
    }
}
