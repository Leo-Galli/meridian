package dev.meridian.util;

import com.mojang.blaze3d.platform.InputConstants;

public final class KeyNames {

    private KeyNames() {
    }

    public static String display(int glfwCode) {
        if (glfwCode <= 0) {
            return "Unbound";
        }
        try {
            InputConstants.Key key = InputConstants.Type.KEYSYM.getOrCreate(glfwCode);
            String name = key.getDisplayName().getString();
            if (name != null && !name.isBlank() && !name.startsWith("key.keyboard")) {
                return name;
            }
        } catch (Exception ignored) {
        }
        return "Key " + glfwCode;
    }
}
