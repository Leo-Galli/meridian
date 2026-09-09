package dev.meridian.gui;

import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;

import dev.meridian.config.Config;
import dev.meridian.config.Waypoint;

public class WaypointsScreen extends MeridianScreen {

    private static final int ROW_HEIGHT = 24;

    private int scroll;

    public WaypointsScreen(Screen parent) {
        super("Waypoints", parent);
    }

    @Override
    protected void init() {
        super.init();
        addScrim();
        addButton(8, 10, 80, 20, "Back", () -> goBack());
        addCenteredText(width / 2, 16, 0xFFFFFFFF, "Waypoints");

        int panelWidth = Math.min(640, width - 30);
        int panelX = (width - panelWidth) / 2;
        int panelY = 42;
        int panelHeight = height - panelY - 58;
        addPanel(panelX, panelY, panelWidth, panelHeight);

        List<Waypoint> waypoints = Config.INSTANCE.waypoints();
        int listTop = panelY + 8;
        int listHeight = panelHeight - 16;
        int maxRows = listHeight / ROW_HEIGHT;
        int maxScroll = Math.max(0, waypoints.size() - maxRows);
        scroll = Math.min(scroll, maxScroll);

        if (waypoints.isEmpty()) {
            addCenteredText(width / 2, panelY + panelHeight / 2 - 10, 0xFF6C7686,
                    "No waypoints saved yet. Add one to start marking locations.");
        } else {
            for (int i = 0; i < waypoints.size(); i++) {
                Waypoint waypoint = waypoints.get(i);
                int rowY = listTop + i * ROW_HEIGHT - scroll * ROW_HEIGHT;
                int rowBottom = rowY + ROW_HEIGHT;
                if (rowBottom < panelY || rowY > panelY + panelHeight) {
                    continue;
                }
                addRow(panelX + 10, rowY, panelWidth - 20, i, waypoint);
            }
        }
        if (maxScroll > 0) {
            addText(panelX + panelWidth - 110, panelY + panelHeight + 4, 0xFF5E6878,
                    scroll > 0 ? "Scroll for more" : "Scroll for more");
        }

        int footerY = height - 38;
        addButton((width - 240) / 2 - 10, footerY, 240, 20, "Add Waypoint", () -> open(new WaypointEditScreen(this, -1)));
    }

    private void addRow(int x, int rowY, int rowWidth, int index, Waypoint waypoint) {
        addCheckbox(x, rowY + 2, "", waypoint.enabled, (checkbox, value) -> {
            waypoint.enabled = value;
            Config.INSTANCE.save();
        });
        int colorX = x + 24;
        int finalRowY = rowY;
        addRenderableOnly((gfx, mouseX, mouseY, delta) ->
                gfx.fill(colorX, finalRowY + 6, colorX + 8, finalRowY + 14, waypoint.color));
        int textX = colorX + 16;
        String name = waypoint.name == null ? "Waypoint" : waypoint.name;
        int nameMax = Math.max(60, Math.min(200, rowWidth - 330));
        String clippedName = font.plainSubstrByWidth(name, nameMax);
        addText(textX, rowY + 3, waypoint.enabled ? 0xFFFFFFFF : 0xFF6C7686, clippedName);
        String coords = "(" + (int) waypoint.x + ", " + (int) waypoint.y + ", " + (int) waypoint.z + ")";
        addText(textX + Math.min(font.width(clippedName), nameMax) + 8, rowY + 3, 0xFF7C8494, coords);

        int right = x + rowWidth;
        int buttonWidth = 54;
        addButton(right - buttonWidth - 8 - buttonWidth - 8, rowY, buttonWidth, 20, "Edit",
                () -> open(new WaypointEditScreen(this, index)));
        addButton(right - buttonWidth, rowY, buttonWidth, 20, "Remove", () -> {
            Config.INSTANCE.waypoints().remove(index);
            Config.INSTANCE.save();
            clearAndRebuild();
        });
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
        int total = Config.INSTANCE.waypoints().size();
        int panelHeight = Math.min(640, width - 30) > 0 ? height - 42 - 58 : 0;
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
