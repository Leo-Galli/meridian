package dev.meridian.hud;

import java.util.ArrayDeque;
import java.util.Deque;

import org.lwjgl.glfw.GLFW;

import net.minecraft.client.Minecraft;

public final class ClickSampler {

    private static final int CLICK_WINDOW_MS = 1000;

    private final Deque<Long> left = new ArrayDeque<>();
    private final Deque<Long> right = new ArrayDeque<>();
    private boolean leftDown;
    private boolean rightDown;
    private long lastLeft;
    private long lastRight;

    public void poll() {
        Minecraft mc = Minecraft.getInstance();
        long now = System.currentTimeMillis();
        boolean l = isButtonDown(mc, GLFW.GLFW_MOUSE_BUTTON_LEFT);
        boolean r = isButtonDown(mc, GLFW.GLFW_MOUSE_BUTTON_RIGHT);
        if (l && !leftDown) {
            left.addLast(now);
            lastLeft = now;
        }
        if (r && !rightDown) {
            right.addLast(now);
            lastRight = now;
        }
        leftDown = l;
        rightDown = r;
        trim(left, now);
        trim(right, now);
    }

    private static boolean isButtonDown(Minecraft mc, int button) {
        if (mc.getWindow() == null) {
            return false;
        }
        return GLFW.glfwGetMouseButton(mc.getWindow().handle(), button) == GLFW.GLFW_PRESS;
    }

    private static void trim(Deque<Long> deque, long now) {
        while (!deque.isEmpty() && now - deque.peekFirst() > CLICK_WINDOW_MS) {
            deque.removeFirst();
        }
    }

    public boolean leftDown() {
        return leftDown;
    }

    public boolean rightDown() {
        return rightDown;
    }

    public int leftCps() {
        return left.size();
    }

    public int rightCps() {
        return right.size();
    }

    public boolean leftJustClicked() {
        return System.currentTimeMillis() - lastLeft < 200;
    }

    public boolean rightJustClicked() {
        return System.currentTimeMillis() - lastRight < 200;
    }
}
