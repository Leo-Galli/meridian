package dev.meridian.hud;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.Camera;
import net.minecraft.util.math.Vec3d;

import dev.meridian.config.Config;
import dev.meridian.config.ModuleSettings;
import dev.meridian.config.Waypoint;

public class WaypointsModule extends HudModule {

    public WaypointsModule(ModuleSettings settings) {
        super(settings);
    }

    @Override
    public boolean isOverlay() {
        return true;
    }

    @Override
    protected int contentWidth(TextRenderer font) {
        return 1;
    }

    @Override
    protected int contentHeight(TextRenderer font) {
        return 1;
    }

    @Override
    protected void renderContent(DrawContext gfx, TextRenderer font, float deltaTicks) {
    }

    @Override
    public void renderOverlayContent(DrawContext gfx, TextRenderer font, int screenWidth, int screenHeight) {
        MinecraftClient mc = MinecraftClient.getInstance();
        if (mc.world == null || mc.player == null) {
            return;
        }
        Camera camera = mc.gameRenderer.getCamera();
        if (camera == null) {
            return;
        }
        Vec3d pos = camera.getPos();
        double yaw = Math.toRadians(camera.getYaw());
        double pitch = Math.toRadians(camera.getPitch());
        double cp = Math.cos(pitch);
        double sp = Math.sin(pitch);
        double cy = Math.cos(yaw);
        double sinYaw = Math.sin(yaw);
        Vec3d forward = new Vec3d(-sinYaw * cp, -sp, cy * cp);
        Vec3d right = new Vec3d(-cy, 0, -sinYaw);
        Vec3d up = cross(right, forward);
        double fov = mc.options.getFov().getValue();
        double tanV = Math.tan(Math.toRadians(fov) / 2.0);
        double aspect = screenHeight <= 0 ? 1 : (double) screenWidth / screenHeight;
        double tanH = tanV * aspect;
        String dimension = mc.world.getRegistryKey().getValue().toString();
        double px = mc.player.getX();
        double py = mc.player.getY();
        double pz = mc.player.getZ();
        for (Waypoint waypoint : Config.INSTANCE.waypoints()) {
            if (!waypoint.enabled || waypoint.name == null || waypoint.name.isBlank()) {
                continue;
            }
            if (!waypoint.dimension.equals(dimension)) {
                continue;
            }
            double relX = waypoint.x - pos.x;
            double relY = waypoint.y - pos.y;
            double relZ = waypoint.z - pos.z;
            double depth = relX * forward.x + relY * forward.y + relZ * forward.z;
            if (depth < 0.5) {
                continue;
            }
            double lateral = relX * right.x + relY * right.y + relZ * right.z;
            double vertical = relX * up.x + relY * up.y + relZ * up.z;
            double ndcX = (lateral / depth) / tanH;
            double ndcY = (vertical / depth) / tanV;
            int markerX = (int) Math.round(screenWidth * 0.5 + ndcX * screenWidth * 0.5);
            int markerY = (int) Math.round(screenHeight * 0.5 - ndcY * screenHeight * 0.5);
            int margin = 16;
            int labelWidth = font.getWidth(waypoint.name);
            int distWidth = font.getWidth(distanceText(waypoint, px, py, pz));
            int maxWidth = Math.max(labelWidth, distWidth) + 10;
            markerX = Math.max(margin, Math.min(screenWidth - margin, markerX));
            markerY = Math.max(margin + font.fontHeight + 4, Math.min(screenHeight - margin - 16, markerY));
            int boxX = markerX - maxWidth / 2;
            int boxY = markerY - font.fontHeight - 4;
            boxX = Math.max(2, Math.min(screenWidth - maxWidth - 2, boxX));
            if (settings.background && settings.backgroundOpacity > 0) {
                gfx.fill(boxX, boxY, boxX + maxWidth, boxY + font.fontHeight * 2 + 8, settings.backgroundOpacity << 24);
            }
            gfx.fill(markerX - 4, boxY + 4, markerX - 1, boxY + 7, waypoint.color);
            gfx.fill(markerX + 1, boxY + 4, markerX + 4, boxY + 7, waypoint.color);
            drawText(gfx, font, waypoint.name, markerX - labelWidth / 2, boxY + 1, waypoint.color);
            drawText(gfx, font, distanceText(waypoint, px, py, pz), markerX - distWidth / 2, boxY + font.fontHeight + 2, 0xFFCCCCCC);
        }
    }

    private static String distanceText(Waypoint waypoint, double px, double py, double pz) {
        return waypoint.distanceTo(px, py, pz) + "m";
    }

    private static Vec3d cross(Vec3d a, Vec3d b) {
        return new Vec3d(
                a.y * b.z - a.z * b.y,
                a.z * b.x - a.x * b.z,
                a.x * b.y - a.y * b.x
        );
    }
}