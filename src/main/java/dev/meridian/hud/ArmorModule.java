package dev.meridian.hud;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import dev.meridian.config.ModuleSettings;

public class ArmorModule extends HudModule {

    private static final EquipmentSlot[] SLOTS = {EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET};

    public ArmorModule(ModuleSettings settings) {
        super(settings);
    }

    @Override
    protected int contentWidth(Font font) {
        int widest = 0;
        for (Row row : rows()) {
            widest = Math.max(widest, row.nameWidth + row.valueWidth);
        }
        return widest;
    }

    @Override
    protected int contentHeight(Font font) {
        int count = rows().size();
        return count == 0 ? font.lineHeight : count * (font.lineHeight + 6);
    }

    @Override
    protected void renderContent(GuiGraphicsExtractor gfx, Font font, float deltaTicks) {
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
                drawBar(gfx, 0, y + font.lineHeight + 2, row.nameWidth + row.valueWidth, 2, row.fraction, settings.accentColor);
            }
            y += font.lineHeight + 6;
        }
    }

    private List<Row> rows() {
        List<Row> list = new ArrayList<>();
        Minecraft mc = Minecraft.getInstance();
        Font font = mc.gui.hud.getFont();
        Player player = mc.player;
        if (player == null) {
            return list;
        }
        for (EquipmentSlot slot : SLOTS) {
            ItemStack stack = player.getItemBySlot(slot);
            if (stack.isEmpty()) {
                continue;
            }
            Row row = new Row();
            row.name = stack.getHoverName().getString();
            row.nameWidth = font.width(row.name);
            if (stack.isDamageableItem()) {
                int max = stack.getMaxDamage();
                int damage = stack.getDamageValue();
                int current = Math.max(0, max - damage);
                row.value = "  " + current + "/" + max;
                row.valueWidth = font.width(row.value);
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
