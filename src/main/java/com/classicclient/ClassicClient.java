package com.classicclient;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.util.Identifier;
import org.lwjgl.glfw.GLFW;

public class ClassicClient implements ClientModInitializer {
    public static final String MOD_ID = "classicclient";

    private static KeyBinding freelookKey;
    private static KeyBinding settingsKey;

    @Override
    public void onInitializeClient() {
        ClassicConfig.load();

        KeyBinding.Category category = KeyBinding.Category.create(Identifier.of(MOD_ID, "main"));
        freelookKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.classicclient.freelook", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_LEFT_ALT, category));
        settingsKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.classicclient.settings", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_RIGHT_SHIFT, category));

        HudRenderer.register();
        ClientTickEvents.END_CLIENT_TICK.register(ClassicClient::tick);
    }

    private static void tick(MinecraftClient mc) {
        if (mc.player == null) {
            Freelook.disable(mc);
            return;
        }

        ClassicConfig cfg = ClassicConfig.get();

        if (cfg.freelookHold) {
            boolean down = freelookKey.isPressed();
            while (freelookKey.wasPressed()) {
                // drain presses
            }
            if (down) {
                Freelook.enable(mc);
            } else {
                Freelook.disable(mc);
            }
        } else {
            while (freelookKey.wasPressed()) {
                Freelook.toggle(mc);
            }
        }

        while (settingsKey.wasPressed()) {
            if (mc.currentScreen == null) {
                Freelook.disable(mc);
                mc.setScreen(new ConfigScreen(null));
            }
        }
    }
}
