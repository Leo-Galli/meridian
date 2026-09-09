package dev.meridian.hud;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;

import dev.meridian.config.Config;
import dev.meridian.config.ModuleRegistry;
import dev.meridian.config.ModuleSettings;

public final class HudRenderer {

    public static final HudRenderer INSTANCE = new HudRenderer();

    private final ClickSampler clickSampler = new ClickSampler();
    private final List<HudModule> modules = new ArrayList<>();

    private HudRenderer() {
        for (String id : ModuleRegistry.ORDER) {
            ModuleSettings settings = Config.INSTANCE.module(id);
            switch (id) {
                case ModuleRegistry.KEYSTROKES:
                    modules.add(new KeystrokesModule(settings));
                    break;
                case ModuleRegistry.CPS:
                    modules.add(new CpsModule(settings));
                    break;
                case ModuleRegistry.PING:
                    modules.add(new PingModule(settings));
                    break;
                case ModuleRegistry.FPS:
                    modules.add(new FpsModule(settings));
                    break;
                case ModuleRegistry.RAM:
                    modules.add(new RamModule(settings));
                    break;
                case ModuleRegistry.SERVER_INFO:
                    modules.add(new ServerInfoModule(settings));
                    break;
                case ModuleRegistry.DIRECTION:
                    modules.add(new DirectionModule(settings));
                    break;
                case ModuleRegistry.ARMOR:
                    modules.add(new ArmorModule(settings));
                    break;
                case ModuleRegistry.WAYPOINTS:
                    modules.add(new WaypointsModule(settings));
                    break;
                default:
                    break;
            }
        }
    }

    public void render(GuiGraphicsExtractor gfx, Font font, DeltaTracker deltaTracker) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.getWindow() == null) {
            return;
        }
        clickSampler.poll();
        int screenWidth = gfx.guiWidth();
        int screenHeight = gfx.guiHeight();
        for (HudModule module : modules) {
            if (module.settings.enabled && module.isOverlay()) {
                module.renderOverlayContent(gfx, font, screenWidth, screenHeight);
            } else if (module.settings.enabled) {
                drawModule(gfx, font, module, screenWidth, screenHeight, deltaTracker.getRealtimeDeltaTicks());
            }
        }
    }

    public void drawAllForLayout(GuiGraphicsExtractor gfx, Font font) {
        int screenWidth = gfx.guiWidth();
        int screenHeight = gfx.guiHeight();
        for (HudModule module : modules) {
            if (module.isOverlay()) {
                continue;
            }
            boolean dimmed = !module.settings.enabled;
            drawModule(gfx, font, module, screenWidth, screenHeight, 0);
            if (dimmed) {
                Box box = bounds(module, font, screenWidth, screenHeight);
                gfx.fill(box.x, box.y, box.x + box.w, box.y + box.h, 0x66000000);
            }
        }
    }

    private void drawModule(GuiGraphicsExtractor gfx, Font font, HudModule module, int screenWidth, int screenHeight, float deltaTicks) {
        ModuleSettings settings = module.settings;
        int contentWidth = module.getWidth(font);
        int contentHeight = module.getHeight(font);
        float scale = settings.scale;
        int boxWidth = Math.round(contentWidth * scale);
        int boxHeight = Math.round(contentHeight * scale);
        float x = settings.effectiveX(screenWidth, boxWidth);
        float y = settings.effectiveY(screenHeight, boxHeight);
        gfx.pose().pushMatrix();
        gfx.pose().translate(x, y);
        gfx.pose().scale(scale, scale);
        if (settings.background && settings.backgroundOpacity > 0) {
            gfx.fill(0, 0, contentWidth, contentHeight, settings.backgroundOpacity << 24);
        }
        gfx.pose().pushMatrix();
        gfx.pose().translate(module.padding(), module.padding());
        module.renderContent(gfx, font, deltaTicks);
        gfx.pose().popMatrix();
        gfx.pose().popMatrix();
    }

    public Box bounds(HudModule module, Font font, int screenWidth, int screenHeight) {
        ModuleSettings settings = module.settings;
        int boxWidth = Math.round(module.getWidth(font) * settings.scale);
        int boxHeight = Math.round(module.getHeight(font) * settings.scale);
        int x = Math.round(settings.effectiveX(screenWidth, boxWidth));
        int y = Math.round(settings.effectiveY(screenHeight, boxHeight));
        return new Box(x, y, boxWidth, boxHeight);
    }

    public HudModule moduleAt(int x, int y, Font font, int screenWidth, int screenHeight) {
        for (HudModule module : modules) {
            if (module.isOverlay()) {
                continue;
            }
            if (bounds(module, font, screenWidth, screenHeight).contains(x, y)) {
                return module;
            }
        }
        return null;
    }

    public List<HudModule> modules() {
        return modules;
    }

    public ClickSampler clickSamplerAccess() {
        return clickSampler;
    }

    public static final class Box {
        public final int x;
        public final int y;
        public final int w;
        public final int h;

        public Box(int x, int y, int w, int h) {
            this.x = x;
            this.y = y;
            this.w = w;
            this.h = h;
        }

        public boolean contains(int px, int py) {
            return px >= x && px < x + w && py >= y && py < y + h;
        }
    }
}
