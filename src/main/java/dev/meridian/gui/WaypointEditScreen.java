package dev.meridian.gui;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.text.Text;

import dev.meridian.config.Config;
import dev.meridian.config.Waypoint;
import dev.meridian.util.Palette;

public class WaypointEditScreen extends MeridianScreen {

    private final int index;
    private TextFieldWidget nameBox;
    private TextFieldWidget dimensionBox;
    private TextFieldWidget xBox;
    private TextFieldWidget yBox;
    private TextFieldWidget zBox;
    private int color;
    private boolean enabled;
    private String error;

    public WaypointEditScreen(Screen parent, int index) {
        super(index < 0 ? "Add Waypoint" : "Edit Waypoint", parent);
        this.index = index;
    }

    @Override
    protected void init() {
        super.init();
        addScrim();
        addButton(8, 10, 80, 20, "Back", () -> goBack());
        addCenteredText(width / 2, 16, 0xFFFFFFFF, index < 0 ? "Add Waypoint" : "Edit Waypoint");

        Waypoint existing = index >= 0 && index < Config.INSTANCE.waypoints().size() ? Config.INSTANCE.waypoints().get(index) : null;
        MinecraftClient mc = MinecraftClient.getInstance();
        String defaultDimension = existing != null ? existing.dimension : (mc.world != null ? mc.world.getRegistryKey().getValue().toString() : "minecraft:overworld");
        double defaultX = existing != null ? existing.x : (mc.player != null ? mc.player.getX() : 0);
        double defaultY = existing != null ? existing.y : (mc.player != null ? mc.player.getY() : 80);
        double defaultZ = existing != null ? existing.z : (mc.player != null ? mc.player.getZ() : 0);
        String defaultName = existing != null ? existing.name : "";
        color = existing != null ? existing.color : Palette.color(6);
        enabled = existing == null || existing.enabled;

        int panelWidth = Math.min(440, width - 30);
        int panelX = (width - panelWidth) / 2;
        int panelY = 42;
        addPanel(panelX, panelY, panelWidth, 232);

        int labelX = panelX + 16;
        int fieldX = panelX + 150;
        int fieldWidth = panelWidth - fieldX - 16;
        int y = panelY + 16;
        int rowH = 28;

        addText(labelX, y + 4, 0xFFFFFFFF, "Name");
        nameBox = new TextFieldWidget(textRenderer, fieldX, y, fieldWidth, 18, Text.literal("Name"));
        nameBox.setText(defaultName);
        addDrawableChild(nameBox);
        y += rowH;

        addText(labelX, y + 4, 0xFFFFFFFF, "Dimension");
        dimensionBox = new TextFieldWidget(textRenderer, fieldX, y, fieldWidth, 18, Text.literal("Dimension"));
        dimensionBox.setText(defaultDimension);
        addDrawableChild(dimensionBox);
        y += rowH;

        addText(labelX, y + 4, 0xFFFFFFFF, "X / Y / Z");
        int third = (fieldWidth - 16) / 3;
        xBox = new TextFieldWidget(textRenderer, fieldX, y, third, 18, Text.literal("X"));
        xBox.setText(String.valueOf(defaultX));
        addDrawableChild(xBox);
        yBox = new TextFieldWidget(textRenderer, fieldX + third + 8, y, third, 18, Text.literal("Y"));
        yBox.setText(String.valueOf(defaultY));
        addDrawableChild(yBox);
        zBox = new TextFieldWidget(textRenderer, fieldX + (third + 8) * 2, y, third, 18, Text.literal("Z"));
        zBox.setText(String.valueOf(defaultZ));
        addDrawableChild(zBox);
        y += rowH;

        addText(labelX, y + 4, 0xFFFFFFFF, "Color");
        addButton(fieldX, y, fieldWidth, 20, Palette.name(Palette.indexOf(color)), () -> {
            color = Palette.color((Palette.indexOf(color) + 1) % Palette.size());
            clearAndRebuild();
        });
        y += rowH;

        addCheckbox(fieldX, y + 2, "Visible in world", enabled, (checkbox, value) -> enabled = value);
        y += rowH;

        if (error != null) {
            addText(panelX + 16, y + 2, 0xFFFF6B6B, error);
        }

        addButton((width - 240) / 2 - 5, panelY + 248, 116, 20, "Save", () -> save());
        addButton((width - 240) / 2 + 129, panelY + 248, 116, 20, "Cancel", () -> goBack());
        if (mc.player != null) {
            addButton((width - 240) / 2 - 5, height - 26, 240, 20, "Use current player position", () -> {
                xBox.setText(String.valueOf(mc.player.getX()));
                yBox.setText(String.valueOf(mc.player.getY()));
                zBox.setText(String.valueOf(mc.player.getZ()));
            });
        }
    }

    private void save() {
        String name = nameBox.getText().trim();
        String dimension = dimensionBox.getText().trim();
        double x;
        double y;
        double z;
        try {
            x = Double.parseDouble(xBox.getText().trim());
            y = Double.parseDouble(yBox.getText().trim());
            z = Double.parseDouble(zBox.getText().trim());
        } catch (NumberFormatException e) {
            error = "Coordinates must be numbers.";
            clearAndRebuild();
            return;
        }
        if (name.isEmpty()) {
            error = "A name is required.";
            clearAndRebuild();
            return;
        }
        if (dimension.isEmpty()) {
            dimension = "minecraft:overworld";
        }
        if (index >= 0 && index < Config.INSTANCE.waypoints().size()) {
            Waypoint waypoint = Config.INSTANCE.waypoints().get(index);
            waypoint.name = name;
            waypoint.dimension = dimension;
            waypoint.x = x;
            waypoint.y = y;
            waypoint.z = z;
            waypoint.color = color;
            waypoint.enabled = enabled;
        } else {
            Config.INSTANCE.waypoints().add(new Waypoint(name, dimension, x, y, z, color, enabled));
        }
        Config.INSTANCE.save();
        goBack();
    }
}