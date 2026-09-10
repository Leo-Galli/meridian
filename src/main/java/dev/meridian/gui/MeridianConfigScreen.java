package dev.meridian.gui;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;
import net.minecraft.util.Util;

import org.lwjgl.glfw.GLFW;

import java.net.URI;

import dev.meridian.config.Config;
import dev.meridian.util.KeyNames;

public class MeridianConfigScreen extends MeridianScreen {

    private static final String VERSION = "1.6.0";
    private static final String WEBSITE = "https://meridian-mod.vercel.app/";

    private MeridianButton menuKeyButton;
    private boolean capturingKey;

    public MeridianConfigScreen() {
        super("Meridian");
    }

    public MeridianConfigScreen(Screen parent) {
        super("Meridian", parent);
    }

    @Override
    protected void init() {
        super.init();
        addScrim();
        int centerX = width / 2;
        int panelWidth = Math.min(380, width - 40);
        int panelX = centerX - panelWidth / 2;
        addPanel(panelX, 24, panelWidth, height - 48);
        addHeader(centerX, 40, "MERIDIAN");
        addCenteredText(centerX, 62, 0xFF8A93A3, "Client HUD suite  -  version " + VERSION);
        addCenteredText(centerX, 76, 0xFFB7C2D0, "FPS, ping, keystrokes, waypoints, macros and more.");

        int innerWidth = panelWidth - 48;
        int y = 118;
        int row = 28;
        addMenu(y, innerWidth, centerX, "Modules & Appearance", () -> open(new ModulesScreen(this)));
        addMenu(y + row, innerWidth, centerX, "HUD Layout Editor", () -> open(new LayoutScreen(this)));
        addMenu(y + row * 2, innerWidth, centerX, "Waypoints", () -> open(new WaypointsScreen(this)));
        addMenu(y + row * 3, innerWidth, centerX, "AutoText Macros", () -> open(new MacrosScreen(this)));
        addMenu(y + row * 4, innerWidth, centerX, "Backup & Transfer", () -> open(new TransferScreen(this)));

        int keyY = y + row * 5 + 4;
        menuKeyButton = (MeridianButton) addButton(centerX - innerWidth / 2, keyY, innerWidth, 22, keyLabel(), b -> startCapture());
        addCenteredText(centerX, keyY + 27, 0xFF6C7686, "Click to change the key that opens this menu");

        int siteY = keyY + 40;
        addButton(centerX - innerWidth / 2, siteY, innerWidth, 22, "Visit Website: meridian-mod.vercel.app", () -> openUrl(WEBSITE), 0xFF7C5CFF);
        addCenteredText(centerX, siteY + 27, 0xFF6C7686, "Docs, downloads and release notes");

        addButton(centerX - 60, height - 38, 120, 20, "Close Menu", () -> goBack());
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (capturingKey) {
            if (keyCode == GLFW.GLFW_KEY_ESCAPE) {
                stopCapture();
                return true;
            }
            if (keyCode > 0) {
                Config.INSTANCE.menuKeyCode = keyCode;
                Config.INSTANCE.save();
                stopCapture();
                return true;
            }
            return true;
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (capturingKey) {
            return true;
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public void render(DrawContext gfx, int mouseX, int mouseY, float delta) {
        super.render(gfx, mouseX, mouseY, delta);
        if (capturingKey) {
            String msg = "Press any key to bind  -  ESC to cancel";
            int cw = textRenderer.getWidth(msg);
            gfx.fill(0, 0, width, height, 0x6604090E);
            gfx.fill(0, height / 2 - 30, width, height / 2 + 6, 0xAA0A141F);
            gfx.fillGradient(0, height / 2 - 30, width, height / 2 - 29, 0xFF58E8FF, 0xFF7C5CFF);
            gfx.drawText(textRenderer, msg, (width - cw) / 2, height / 2 - 22, 0xFFFFFFFF, true);
        }
    }

    private void startCapture() {
        capturingKey = true;
        menuKeyButton.setMessage(Text.literal("Press a key...  (ESC to cancel)"));
    }

    private void stopCapture() {
        capturingKey = false;
        menuKeyButton.setMessage(Text.literal(keyLabel()));
    }

    private static String keyLabel() {
        return "Menu Key: " + KeyNames.display(Config.INSTANCE.menuKeyCode);
    }

    private static void openUrl(String url) {
        Util.getOperatingSystem().open(URI.create(url));
    }

    private void addMenu(int y, int innerWidth, int centerX, String label, Runnable action) {
        addButton(centerX - innerWidth / 2, y, innerWidth, 22, label, action);
    }

    private static void open(Screen screen) {
        net.minecraft.client.MinecraftClient.getInstance().setScreen(screen);
    }
}