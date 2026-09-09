package dev.meridian.gui;

import java.util.Arrays;
import java.util.List;
import java.util.function.IntConsumer;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;

import dev.meridian.config.Config;
import dev.meridian.config.ModuleRegistry;
import dev.meridian.config.ModuleSettings;
import dev.meridian.util.Palette;

public class ModuleSettingsScreen extends MeridianScreen {

    private static final List<Float> SCALES = Arrays.asList(0.5f, 0.75f, 1.0f, 1.25f, 1.5f, 1.75f, 2.0f);
    private static final List<Integer> OPACITIES = Arrays.asList(0, 30, 70, 110, 150, 200, 255);

    private final String moduleId;

    public ModuleSettingsScreen(Screen parent, String moduleId) {
        super(ModuleRegistry.title(moduleId), parent);
        this.moduleId = moduleId;
    }

    @Override
    protected void init() {
        super.init();
        addScrim();
        addButton(8, 10, 80, 20, "Back", () -> goBack());
        addCenteredText(width / 2, 16, 0xFFFFFFFF, ModuleRegistry.title(moduleId));

        ModuleSettings settings = Config.INSTANCE.module(moduleId);
        int panelWidth = Math.min(460, width - 30);
        int panelX = (width - panelWidth) / 2;
        int panelY = 42;
        int panelHeight = Math.min(300, height - panelY - 34);
        addPanel(panelX, panelY, panelWidth, panelHeight);

        int leftX = panelX + 16;
        int controlX = panelX + panelWidth - 224;
        int controlWidth = 208;
        int y = panelY + 12;
        int rowH = 27;

        addCheckbox(controlX, y, "Enabled", settings.enabled, (checkbox, value) -> {
            settings.enabled = value;
            Config.INSTANCE.save();
        });
        addText(leftX, y + 2, 0xFFFFFFFF, "Module enabled");
        y += rowH;

        ButtonWidget scale = buildButton(controlX, y, controlWidth, 20, "Scale: " + percent(settings.scale),
                button -> {
                    settings.scale = next(SCALES, settings.scale, 1.0f);
                    Config.INSTANCE.save();
                    button.setMessage(Text.literal("Scale: " + percent(settings.scale)));
                });
        addDrawableChild(scale);
        addText(leftX, y + 2, 0xFFFFFFFF, "Text scale");
        y += rowH;

        addCheckbox(controlX, y, "Background", settings.background, (checkbox, value) -> {
            settings.background = value;
            Config.INSTANCE.save();
        });
        addText(leftX, y + 2, 0xFFFFFFFF, "Draw background box");
        y += rowH;

        ButtonWidget opacity = buildButton(controlX, y, controlWidth, 20, "Opacity: " + opacityLabel(settings.backgroundOpacity),
                button -> {
                    settings.backgroundOpacity = next(OPACITIES, settings.backgroundOpacity, 110);
                    Config.INSTANCE.save();
                    button.setMessage(Text.literal("Opacity: " + opacityLabel(settings.backgroundOpacity)));
                });
        addDrawableChild(opacity);
        addText(leftX, y + 2, 0xFFFFFFFF, "Background opacity");
        y += rowH;

        ButtonWidget shadow = buildButton(controlX, y, controlWidth, 20, "Text shadow: " + onOff(settings.textShadow),
                button -> {
                    settings.textShadow = !settings.textShadow;
                    Config.INSTANCE.save();
                    button.setMessage(Text.literal("Text shadow: " + onOff(settings.textShadow)));
                });
        addDrawableChild(shadow);
        addText(leftX, y + 2, 0xFFFFFFFF, "Text shadow");
        y += rowH;

        int finalTextColor = settings.textColor;
        ButtonWidget textColor = buildButton(controlX, y, controlWidth, 20, "Text Color: " + Palette.name(Palette.indexOf(finalTextColor)),
                button -> openColorPicker("Text Color", finalTextColor, color -> {
                    settings.textColor = color;
                    Config.INSTANCE.save();
                    button.setMessage(Text.literal("Text Color: " + Palette.name(Palette.indexOf(color))));
                }));
        textColor.setTooltip(Tooltip.of(Text.literal("Custom color for the module text")));
        addDrawableChild(textColor);
        addText(leftX, y + 2, 0xFFFFFFFF, "Text color");
        y += rowH;

        int finalAccent = settings.accentColor;
        ButtonWidget accentColor = buildButton(controlX, y, controlWidth, 20, "Accent Color: " + Palette.name(Palette.indexOf(finalAccent)),
                button -> openColorPicker("Accent Color", finalAccent, color -> {
                    settings.accentColor = color;
                    Config.INSTANCE.save();
                    button.setMessage(Text.literal("Accent Color: " + Palette.name(Palette.indexOf(color))));
                }));
        accentColor.setTooltip(Tooltip.of(Text.literal("Custom color for values and highlights")));
        addDrawableChild(accentColor);
        addText(leftX, y + 2, 0xFFFFFFFF, "Accent color");
        y += rowH;

        addButton(controlX, y, controlWidth, 20, "Reset Position", () -> {
            ModuleSettings fallback = ModuleRegistry.defaultsFor(moduleId);
            settings.x = fallback.x;
            settings.y = fallback.y;
            Config.INSTANCE.save();
        });
        addText(leftX, y + 2, 0xFFFFFFFF, "Reset HUD position");
        addText(panelX + 16, panelY + panelHeight - 20, 0xFF5E6878, "Move and scale modules live from the HUD Layout Editor.");

        addButton((width - 120) / 2, height - 26, 120, 20, "Done", () -> goBack());
    }

    private void openColorPicker(String title, int color, IntConsumer onChange) {
        MinecraftClient.getInstance().setScreen(new ColorPickerScreen(this, title, color, onChange));
    }

    private ButtonWidget buildButton(int x, int y, int w, int h, String label, ButtonWidget.PressAction onPress) {
        return ButtonWidget.builder(Text.literal(label), onPress).dimensions(x, y, w, h).build();
    }

    private static String percent(float scale) {
        return Math.round(scale * 100) + "%";
    }

    private static String opacityLabel(int opacity) {
        if (opacity <= 0) {
            return "Hidden";
        }
        if (opacity >= 255) {
            return "Opaque";
        }
        return Math.round(opacity / 255.0f * 100) + "%";
    }

    private static String onOff(boolean value) {
        return value ? "On" : "Off";
    }

    private static <T> T next(List<T> values, T current, T fallback) {
        int index = values.indexOf(current);
        if (index < 0) {
            return fallback;
        }
        return values.get((index + 1) % values.size());
    }
}