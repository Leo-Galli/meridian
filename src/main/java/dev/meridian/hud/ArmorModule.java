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
    private static final int ICON_SIZE = 16;
    private static final int SLOT_GAP = 1;

    public ArmorModule(ModuleSettings settings) {
        super(settings);
    }

    @Override
    protected int contentWidth(TextRenderer font) {
        int count = items().size();
        return count == 0 ? font.getWidth("No Armor") : count * ICON_SIZE + (count - 1) * SLOT_GAP;
    }

    @Override
    protected int contentHeight(TextRenderer font) {
        return ICON_SIZE + 2;
    }

    @Override
    protected void renderContent(DrawContext gfx, TextRenderer font, float deltaTicks) {
        List<ItemStack> items = items();
        if (items.isEmpty()) {
            drawText(gfx, font, "No Armor", 0, 2, settings.textColor);
            return;
        }
        int x = 0;
        for (ItemStack stack : items) {
            gfx.drawItem(stack, x, 0);
            if (stack.isDamageable()) {
                int max = stack.getMaxDamage();
                int damage = stack.getDamage();
                float fraction = max <= 0 ? 1f : Math.max(0f, Math.min(1f, (float) (max - damage) / max));
                drawBar(gfx, x, ICON_SIZE, ICON_SIZE, 2, fraction, settings.accentColor);
            }
            x += ICON_SIZE + SLOT_GAP;
        }
    }

    private static List<ItemStack> items() {
        List<ItemStack> list = new ArrayList<>();
        MinecraftClient mc = MinecraftClient.getInstance();
        PlayerEntity player = mc.player;
        if (player == null) {
            return list;
        }
        for (EquipmentSlot slot : SLOTS) {
            ItemStack stack = player.getEquippedStack(slot);
            if (!stack.isEmpty()) {
                list.add(stack);
            }
        }
        return list;
    }
}