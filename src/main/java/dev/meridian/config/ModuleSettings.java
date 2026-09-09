package dev.meridian.config;

public class ModuleSettings {

    public String id;
    public boolean enabled;
    public float x;
    public float y;
    public float scale;
    public int textColor;
    public int accentColor;
    public boolean background;
    public int backgroundOpacity;
    public boolean textShadow;

    public ModuleSettings() {
    }

    public ModuleSettings(String id, boolean enabled, float x, float y, float scale, int textColor, int accentColor, boolean background, int backgroundOpacity, boolean textShadow) {
        this.id = id;
        this.enabled = enabled;
        this.x = x;
        this.y = y;
        this.scale = scale;
        this.textColor = textColor;
        this.accentColor = accentColor;
        this.background = background;
        this.backgroundOpacity = backgroundOpacity;
        this.textShadow = textShadow;
    }

    public float effectiveX(int screenWidth, int boxWidth) {
        if (x >= 0) {
            return x;
        }
        return screenWidth + x - boxWidth;
    }

    public float effectiveY(int screenHeight, int boxHeight) {
        if (y >= 0) {
            return y;
        }
        return screenHeight + y - boxHeight;
    }
}
