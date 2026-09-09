package dev.meridian.hud;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;

import dev.meridian.config.ModuleSettings;

public class ArmorModule extends HudModule {

    private static final EquipmentSlot[] SLOTS = {EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET};

    public ArmorModule(ModuleSettings settings) {
        super(settings);
    }

    @Override
    protected int contentWidth(TextRenderer font) {
        int widest = 0;
        for (Row row : rows()) {
            widest = Math.max(widest, row.nameWidth + row.valueWidth);
        }
        return widest;
    }

    @Override
    protected int contentHeight(TextRenderer font) {
        int count = rows().size();
        return count == 0 ? font.fontHeight : count * (font.fontHeight + 6);
    }

    @Override
    protected void renderContent(DrawContext gfx, TextRenderer font, float deltaTicks) {
        List<Row> rows = rows();
        if (rows.isEmpty()) {
            drawText(gfx, font, "No Armor", 0, 0, settings.textColor);
            return;
        }
        int y = 0;
        for (Row row : rows) {
            drawText(gfx, font, row.name, 0, y, settings.textColor);
            drawText(gfx, font, row.value, row.nameWidth, y, settings.accentColor);
            if (row.bar) {
                drawBar(gfx, 0, y + font.fontHeight + 2, row.nameWidth + row.valueWidth, 2, row.fraction, settings.accentColor);
            }
            y += font.fontHeight + 6;
        }
    }

    private List<Row> rows() {
        List<Row> list = new ArrayList<>();
        MinecraftClient mc = MinecraftClient.getInstance();
        TextRenderer font = mc.textRenderer;
        PlayerEntity player = mc.player;
        if (player == null) {
            return list;
        }
        for (EquipmentSlot slot : SLOTS) {
            ItemStack stack = player.getEquippedStack(slot);
            if (stack.isEmpty()) {
                continue;
            }
            Row row = new Row();
            row.name = stack.getName().getString();
            row.nameWidth = font.getWidth(row.name);
            if (stack.isDamageable()) {
                int max = stack.getMaxDamage();
                int damage = stack.getDamage();
                int current = Math.max(0, max - damage);
                row.value = "  " + current + "/" + max;
                row.valueWidth = font.getWidth(row.value);
                row.fraction = max <= 0 ? 1f : (float) current / max;
                row.bar = true;
            } else {
                row.value = "";
                row.valueWidth = 0;
                row.fraction = 1f;
                row.bar = false;
            }
            list.add(row);
        }
        return list;
    }

    private static final class Row {
        String name;
        String value;
        int nameWidth;
        int valueWidth;
        float fraction;
        boolean bar;
    }
}