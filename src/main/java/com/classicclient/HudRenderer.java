package com.classicclient;

import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.hud.VanillaHudElements;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public final class HudRenderer {
    private static final DateTimeFormatter CLOCK = DateTimeFormatter.ofPattern("HH:mm");
    private static final EquipmentSlot[] ARMOR = {
            EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET
    };

    private HudRenderer() {}

    public static void register() {
        HudElementRegistry.attachElementAfter(
                VanillaHudElements.MISC_OVERLAYS,
                Identifier.of(ClassicClient.MOD_ID, "hud"),
                HudRenderer::render);
    }

    private static void render(DrawContext ctx, RenderTickCounter tick) {
        MinecraftClient mc = MinecraftClient.getInstance();
        if (mc.player == null || mc.options.hudHidden) {
            return;
        }
        ClassicConfig cfg = ClassicConfig.get();
        TextRenderer font = mc.textRenderer;

        if (cfg.hudEnabled) {
            drawInfoPanel(ctx, mc, font, cfg);
        }
        if (cfg.heartIndicator) {
            drawHeartIndicator(ctx, mc, font);
        }
    }

    private static void drawInfoPanel(DrawContext ctx, MinecraftClient mc, TextRenderer font, ClassicConfig cfg) {
        PlayerEntity p = mc.player;
        List<String> lines = new ArrayList<>();

        if (cfg.showFps) {
            lines.add("FPS: " + mc.getCurrentFps());
        }
        if (cfg.showPing && mc.getNetworkHandler() != null) {
            PlayerListEntry entry = mc.getNetworkHandler().getPlayerListEntry(p.getUuid());
            if (entry != null) {
                lines.add("Ping: " + entry.getLatency() + " ms");
            }
        }
        if (cfg.showCoords) {
            lines.add(String.format(Locale.ROOT, "XYZ: %.1f / %.1f / %.1f", p.getX(), p.getY(), p.getZ()));
        }
        if (cfg.showDirection) {
            String dir = p.getHorizontalFacing().asString();
            lines.add("Facing: " + dir.substring(0, 1).toUpperCase(Locale.ROOT) + dir.substring(1));
        }
        if (cfg.showHealth) {
            lines.add(String.format(Locale.ROOT, "HP: %.1f", p.getHealth() + p.getAbsorptionAmount()));
        }
        if (cfg.showArmor) {
            int total = 0;
            int count = 0;
            for (EquipmentSlot slot : ARMOR) {
                ItemStack stack = p.getEquippedStack(slot);
                if (!stack.isEmpty() && stack.isDamageable()) {
                    int max = stack.getMaxDamage();
                    total += (int) (100.0f * (max - stack.getDamage()) / max);
                    count++;
                }
            }
            if (count > 0) {
                lines.add("Armor: " + (total / count) + "%");
            }
        }
        if (cfg.showClock) {
            lines.add("Time: " + LocalTime.now().format(CLOCK));
        }
        if (lines.isEmpty()) {
            return;
        }

        int pad = 3;
        int lineH = font.fontHeight + 2;
        int width = 0;
        for (String s : lines) {
            width = Math.max(width, font.getWidth(s));
        }
        int boxW = width + pad * 2;
        int boxH = lines.size() * lineH + pad * 2 - 2;
        int margin = 4;

        int sw = ctx.getScaledWindowWidth();
        int sh = ctx.getScaledWindowHeight();
        boolean right = cfg.hudCorner == 1 || cfg.hudCorner == 3;
        boolean bottom = cfg.hudCorner == 2 || cfg.hudCorner == 3;
        int x = right ? sw - boxW - margin : margin;
        int y = bottom ? sh - boxH - margin - 24 : margin;

        ctx.fill(x, y, x + boxW, y + boxH, 0x80000000);
        int ty = y + pad;
        for (String s : lines) {
            ctx.drawTextWithShadow(font, s, x + pad, ty, 0xFFFFFFFF);
            ty += lineH;
        }
    }

    private static void drawHeartIndicator(DrawContext ctx, MinecraftClient mc, TextRenderer font) {
        if (!(mc.targetedEntity instanceof LivingEntity target) || !target.isAlive()) {
            return;
        }
        float hp = target.getHealth();
        float ratio = MathHelper.clamp(hp / target.getMaxHealth(), 0.0f, 1.0f);
        int r = (int) (255 * (1.0f - ratio));
        int g = (int) (255 * ratio);
        int color = 0xFF000000 | (r << 16) | (g << 8) | 0x20;

        String text = target.getName().getString()
                + "  \u2665 "
                + String.format(Locale.ROOT, "%.1f", hp + target.getAbsorptionAmount());

        int x = ctx.getScaledWindowWidth() / 2 - font.getWidth(text) / 2;
        int y = ctx.getScaledWindowHeight() / 2 + 14;
        ctx.fill(x - 3, y - 2, x + font.getWidth(text) + 3, y + font.fontHeight + 1, 0x80000000);
        ctx.drawTextWithShadow(font, text, x, y, color);
    }
    }
