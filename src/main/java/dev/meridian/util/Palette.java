package dev.meridian.util;

import java.util.Arrays;
import java.util.List;

public final class Palette {

    public static final int WHITE = 0xFFFFFFFF;
    public static final int LIGHT_GRAY = 0xFFB0B0B0;
    public static final int GRAY = 0xFF808080;
    public static final int DARK_GRAY = 0xFF555555;
    public static final int RED = 0xFFFF5555;
    public static final int ORANGE = 0xFFFFA500;
    public static final int GOLD = 0xFFFFD700;
    public static final int YELLOW = 0xFFFFFF55;
    public static final int LIME = 0xFFB7FF7C;
    public static final int GREEN = 0xFF55FF55;
    public static final int AQUA = 0xFF55FFFF;
    public static final int CYAN = 0xFF4DD8E0;
    public static final int LIGHT_BLUE = 0xFF8AE8FF;
    public static final int BLUE = 0xFF5555FF;
    public static final int PURPLE = 0xFFAA55FF;
    public static final int PINK = 0xFFFF8AB5;

    private static final List<Integer> COLORS = Arrays.asList(
            WHITE, LIGHT_GRAY, GRAY, DARK_GRAY,
            RED, ORANGE, GOLD, YELLOW,
            LIME, GREEN, AQUA, CYAN,
            LIGHT_BLUE, BLUE, PURPLE, PINK
    );

    private static final List<String> NAMES = Arrays.asList(
            "White", "Light Gray", "Gray", "Dark Gray",
            "Red", "Orange", "Gold", "Yellow",
            "Lime", "Green", "Aqua", "Cyan",
            "Light Blue", "Blue", "Purple", "Pink"
    );

    private Palette() {
    }

    public static int color(int index) {
        return COLORS.get(Math.floorMod(index, COLORS.size()));
    }

    public static String name(int index) {
        return NAMES.get(Math.floorMod(index, COLORS.size()));
    }

    public static int indexOf(int color) {
        int best = 0;
        for (int i = 0; i < COLORS.size(); i++) {
            if (COLORS.get(i).intValue() == color) {
                return i;
            }
            if (sameHue(COLORS.get(i), color) && COLORS.get(i).intValue() != WHITE) {
                best = i;
            }
        }
        return best;
    }

    private static boolean sameHue(int a, int b) {
        return Math.abs(((a >> 16) & 0xFF) - ((b >> 16) & 0xFF)) < 40
                && Math.abs(((a >> 8) & 0xFF) - ((b >> 8) & 0xFF)) < 40
                && Math.abs((a & 0xFF) - (b & 0xFF)) < 40;
    }

    public static int size() {
        return COLORS.size();
    }
}
