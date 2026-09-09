package dev.meridian.util;

import net.minecraft.client.util.InputUtil;

public final class KeyNames {

    private KeyNames() {
    }

    public static String display(int glfwCode) {
        if (glfwCode <= 0) {
            return "Unbound";
        }
        try {
            InputUtil.Key key = InputUtil.Type.KEYSYM.createFromCode(glfwCode);
            String name = key.getLocalizedText().getString();
            if (name != null && !name.isBlank() && !name.startsWith("key.keyboard")) {
                return name;
            }
        } catch (Exception ignored) {
        }
        return "Key " + glfwCode;
    }
}