package dev.meridian.hud;

import java.util.ArrayList;
import java.util.List;

import org.joml.Matrix3x2fStack;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderTickCounter;

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
                case ModuleRegistry.COMBO:
                    modules.add(new ComboModule(settings));
                    break;
                case ModuleRegistry.COORDINATES:
                    modules.add(new CoordinatesModule(settings));
                    break;
                case ModuleRegistry.SPEEDOMETER:
                    modules.add(new SpeedometerModule(settings));
                    break;
                case ModuleRegistry.POTION_EFFECTS:
                    modules.add(new PotionEffectsModule(settings));
                    break;
                case ModuleRegistry.WAYPOINTS:
                    modules.add(new WaypointsModule(settings));
                    break;
                case ModuleRegistry.SOUND_INDICATOR:
                    modules.add(new SoundIndicatorModule(settings));
                    break;
                default:
                    break;
            }
        }
    }

    public void render(DrawContext gfx, TextRenderer font, RenderTickCounter tickCounter) {
        MinecraftClient mc = MinecraftClient.getInstance();
        if (mc.getWindow() == null) {
            return;
        }
        clickSampler.poll();
        int screenWidth = gfx.getScaledWindowWidth();
        int screenHeight = gfx.getScaledWindowHeight();
        for (HudModule module : modules) {
            if (module.settings.enabled && module.isOverlay()) {
                module.renderOverlayContent(gfx, font, screenWidth, screenHeight);
            } else if (module.settings.enabled) {
                drawModule(gfx, font, module, screenWidth, screenHeight, tickCounter.getDynamicDeltaTicks());
            }
        }
    }

    public void drawAllForLayout(DrawContext gfx, TextRenderer font) {
        int screenWidth = gfx.getScaledWindowWidth();
        int screenHeight = gfx.getScaledWindowHeight();
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

    private void drawModule(DrawContext gfx, TextRenderer font, HudModule module, int screenWidth, int screenHeight, float deltaTicks) {
        ModuleSettings settings = module.settings;
        int contentWidth = module.getWidth(font);
        int contentHeight = module.getHeight(font);
        float scale = settings.scale;
        int boxWidth = Math.round(contentWidth * scale);
        int boxHeight = Math.round(contentHeight * scale);
        float x = settings.effectiveX(screenWidth, boxWidth);
        float y = settings.effectiveY(screenHeight, boxHeight);
        Matrix3x2fStack pose = gfx.getMatrices();
        pose.pushMatrix();
        pose.translate(x, y);
        pose.scale(scale, scale);
        if (settings.background && settings.backgroundOpacity > 0) {
            gfx.fill(0, 0, contentWidth, contentHeight, settings.backgroundOpacity << 24);
        }
        pose.pushMatrix();
        pose.translate(module.padding(), module.padding());
        module.renderContent(gfx, font, deltaTicks);
        pose.popMatrix();
        pose.popMatrix();
    }

    public Box bounds(HudModule module, TextRenderer font, int screenWidth, int screenHeight) {
        ModuleSettings settings = module.settings;
        int boxWidth = Math.round(module.getWidth(font) * settings.scale);
        int boxHeight = Math.round(module.getHeight(font) * settings.scale);
        int x = Math.round(settings.effectiveX(screenWidth, boxWidth));
        int y = Math.round(settings.effectiveY(screenHeight, boxHeight));
        return new Box(x, y, boxWidth, boxHeight);
    }

    public HudModule moduleAt(int x, int y, TextRenderer font, int screenWidth, int screenHeight) {
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