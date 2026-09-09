package dev.meridian.gui;

import org.lwjgl.glfw.GLFW;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.text.Text;

import dev.meridian.config.Config;
import dev.meridian.config.Macro;
import dev.meridian.util.KeyNames;

public class MacroEditScreen extends MeridianScreen {

    private final int index;
    private TextFieldWidget nameBox;
    private TextFieldWidget messageBox;
    private ButtonWidget keyButton;
    private int key;
    private boolean capturing;
    private String error;

    public MacroEditScreen(Screen parent, int index) {
        super(index < 0 ? "Add Macro" : "Edit Macro", parent);
        this.index = index;
    }

    @Override
    protected void init() {
        super.init();
        addScrim();
        addButton(8, 10, 80, 20, "Back", () -> goBack());
        addCenteredText(width / 2, 16, 0xFFFFFFFF, index < 0 ? "Add Macro" : "Edit Macro");

        Macro existing = index >= 0 && index < Config.INSTANCE.macros().size() ? Config.INSTANCE.macros().get(index) : null;
        String defaultName = existing != null ? existing.name : "";
        String defaultMessage = existing != null ? existing.message : "";
        key = existing != null ? existing.key : 0;

        int panelWidth = Math.min(480, width - 30);
        int panelX = (width - panelWidth) / 2;
        int panelY = 42;
        addPanel(panelX, panelY, panelWidth, 150);

        int labelX = panelX + 16;
        int fieldX = panelX + 130;
        int fieldWidth = panelWidth - fieldX - 16;
        int y = panelY + 16;

        addText(labelX, y + 4, 0xFFFFFFFF, "Name");
        nameBox = new TextFieldWidget(textRenderer, fieldX, y, fieldWidth, 18, Text.literal("Macro name"));
        nameBox.setText(defaultName);
        addDrawableChild(nameBox);
        y += 28;

        addText(labelX, y + 4, 0xFFFFFFFF, "Message");
        messageBox = new TextFieldWidget(textRenderer, fieldX, y, fieldWidth, 18, Text.literal("Message to send"));
        messageBox.setText(defaultMessage);
        messageBox.setMaxLength(256);
        addDrawableChild(messageBox);
        y += 28;

        addText(labelX, y + 4, 0xFFFFFFFF, "Key Bind");
        keyButton = addButton(fieldX, y, fieldWidth, 20, keyLabel(), button -> {
            capturing = !capturing;
            updateKeyLabel();
        });
        y += 28;

        if (error != null) {
            addText(panelX + 16, y + 4, 0xFFFF6B6B, error);
        }

        String hint = "Press a key while this screen is open to capture it. Escape cancels.";
        addText(panelX + 16, panelY + 158, 0xFF5E6878, textRenderer.trimToWidth(hint, panelWidth - 32));

        addButton((width - 240) / 2 - 5, panelY + 176, 116, 20, "Save", () -> save());
        addButton((width - 240) / 2 + 129, panelY + 176, 116, 20, "Cancel", () -> goBack());
    }

    private void updateKeyLabel() {
        keyButton.setMessage(Text.literal(keyLabel()));
    }

    private String keyLabel() {
        if (capturing) {
            return "Press a key...";
        }
        return "Key: " + KeyNames.display(key);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (capturing) {
            if (keyCode == GLFW.GLFW_KEY_ESCAPE) {
                capturing = false;
            } else {
                key = keyCode;
                capturing = false;
            }
            updateKeyLabel();
            return true;
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    private void save() {
        String name = nameBox.getText().trim();
        String message = messageBox.getText();
        if (name.isEmpty()) {
            error = "A name is required.";
            clearAndRebuild();
            return;
        }
        if (message.isEmpty()) {
            error = "A message is required.";
            clearAndRebuild();
            return;
        }
        if (key <= 0) {
            error = "Press a key to bind this macro.";
            clearAndRebuild();
            return;
        }
        if (index >= 0 && index < Config.INSTANCE.macros().size()) {
            Macro macro = Config.INSTANCE.macros().get(index);
            macro.name = name;
            macro.message = message;
            macro.key = key;
        } else {
            Config.INSTANCE.macros().add(new Macro(name, message, key, true));
        }
        Config.INSTANCE.save();
        goBack();
    }
}