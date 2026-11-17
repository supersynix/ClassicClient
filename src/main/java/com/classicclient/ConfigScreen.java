package com.classicclient;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;

import java.util.function.Consumer;
import java.util.function.Supplier;

/** Simple two-column settings screen with big buttons that work well on touch. */
public class ConfigScreen extends Screen {
    private static final String[] CORNERS = {"Top left", "Top right", "Bottom left", "Bottom right"};

    private final Screen parent;
    private int index;

    public ConfigScreen(Screen parent) {
        super(Text.literal("Classic Client"));
        this.parent = parent;
    }

    @Override
    protected void init() {
        ClassicConfig c = ClassicConfig.get();
        index = 0;

        toggle("HUD panel", () -> c.hudEnabled, v -> c.hudEnabled = v);
        toggle("FPS", () -> c.showFps, v -> c.showFps = v);
        toggle("Ping", () -> c.showPing, v -> c.showPing = v);
        toggle("Coordinates", () -> c.showCoords, v -> c.showCoords = v);
        toggle("Direction", () -> c.showDirection, v -> c.showDirection = v);
        toggle("Own health", () -> c.showHealth, v -> c.showHealth = v);
        toggle("Armor durability", () -> c.showArmor, v -> c.showArmor = v);
        toggle("Clock", () -> c.showClock, v -> c.showClock = v);
        toggle("Heart indicator", () -> c.heartIndicator, v -> c.heartIndicator = v);
        toggle("Freelook third person", () -> c.freelookThirdPerson, v -> c.freelookThirdPerson = v);

        // Freelook mode (tap vs hold)
        ButtonWidget mode = ButtonWidget.builder(Text.literal(modeLabel(c)), b -> {
            c.freelookHold = !c.freelookHold;
            b.setMessage(Text.literal(modeLabel(c)));
        }).dimensions(colX(index), rowY(index), 150, 20).build();
        addDrawableChild(mode);
        index++;

        // HUD corner
        ButtonWidget corner = ButtonWidget.builder(Text.literal(cornerLabel(c)), b -> {
            c.hudCorner = (c.hudCorner + 1) % CORNERS.length;
            b.setMessage(Text.literal(cornerLabel(c)));
        }).dimensions(colX(index), rowY(index), 150, 20).build();
        addDrawableChild(corner);

        addDrawableChild(ButtonWidget.builder(Text.literal("Done"), b -> close())
                .dimensions(width / 2 - 100, height - 28, 200, 20).build());
    }

    private void toggle(String label, Supplier<Boolean> getter, Consumer<Boolean> setter) {
        ButtonWidget button = ButtonWidget.builder(Text.literal(text(label, getter.get())), b -> {
            boolean next = !getter.get();
            setter.accept(next);
            b.setMessage(Text.literal(text(label, next)));
        }).dimensions(colX(index), rowY(index), 150, 20).build();
        addDrawableChild(button);
        index++;
    }

    private int colX(int i) {
        return (i % 2 == 0) ? width / 2 - 155 : width / 2 + 5;
    }

    private int rowY(int i) {
        return 40 + (i / 2) * 24;
    }

    private static String text(String label, boolean on) {
        return label + ": " + (on ? "ON" : "OFF");
    }

    private static String modeLabel(ClassicConfig c) {
        return "Freelook key: " + (c.freelookHold ? "Hold" : "Tap");
    }

    private static String cornerLabel(ClassicConfig c) {
        return "HUD corner: " + CORNERS[c.hudCorner];
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        super.render(context, mouseX, mouseY, delta);
        context.drawCenteredTextWithShadow(textRenderer, title, width / 2, 16, 0xFFFFFFFF);
    }

    @Override
    public void close() {
        ClassicConfig.save();
        MinecraftClient.getInstance().setScreen(parent);
    }
}
