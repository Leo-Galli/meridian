package dev.meridian.config;

public class Macro {

    public String name;
    public String message;
    public int key;
    public boolean enabled;

    public Macro() {
    }

    public Macro(String name, String message, int key, boolean enabled) {
        this.name = name;
        this.message = message;
        this.key = key;
        this.enabled = enabled;
    }
}
