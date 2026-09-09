package dev.meridian.config;

public class Waypoint {

    public String name;
    public String dimension;
    public double x;
    public double y;
    public double z;
    public int color;
    public boolean enabled;

    public Waypoint() {
    }

    public Waypoint(String name, String dimension, double x, double y, double z, int color, boolean enabled) {
        this.name = name;
        this.dimension = dimension;
        this.x = x;
        this.y = y;
        this.z = z;
        this.color = color;
        this.enabled = enabled;
    }

    public int distanceTo(double px, double py, double pz) {
        return (int) Math.round(Math.sqrt(Math.pow(x - px, 2) + Math.pow(y - py, 2) + Math.pow(z - pz, 2)));
    }
}
