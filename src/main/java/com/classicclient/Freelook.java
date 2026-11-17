package com.classicclient;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.Perspective;
import net.minecraft.util.math.MathHelper;

/** Holds the detached camera angles while freelook is on. */
public final class Freelook {
    private static boolean active;
    private static float yaw;
    private static float pitch;
    private static Perspective previous;

    private Freelook() {}

    public static boolean isActive() {
        return active;
    }

    public static float getYaw() {
        return yaw;
    }

    public static float getPitch() {
        return pitch;
    }

    public static void enable(MinecraftClient mc) {
        if (active || mc.player == null) {
            return;
        }
        active = true;
        yaw = mc.player.getYaw();
        pitch = mc.player.getPitch();
        previous = mc.options.getPerspective();
        if (ClassicConfig.get().freelookThirdPerson) {
            mc.options.setPerspective(Perspective.THIRD_PERSON_BACK);
        }
    }

    public static void disable(MinecraftClient mc) {
        if (!active) {
            return;
        }
        active = false;
        if (previous != null && mc.options != null) {
            mc.options.setPerspective(previous);
        }
        previous = null;
    }

    public static void toggle(MinecraftClient mc) {
        if (active) {
            disable(mc);
        } else {
            enable(mc);
        }
    }

    /** Called with raw mouse/touch deltas instead of turning the player. */
    public static void onMouse(double dx, double dy) {
        yaw += (float) dx * 0.15f;
        pitch = MathHelper.clamp(pitch + (float) dy * 0.15f, -90.0f, 90.0f);
    }
}
