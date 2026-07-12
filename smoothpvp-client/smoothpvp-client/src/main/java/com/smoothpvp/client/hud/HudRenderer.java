package com.smoothpvp.client.hud;

import com.smoothpvp.client.config.ClientConfig;
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElement;
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;

import java.util.Collection;

/**
 * A single, clean HUD overlay drawn on top of vanilla UI.
 * Kept deliberately minimal/flat instead of vanilla's cluttered look.
 */
public class HudRenderer {

    // Tick counter used to flash the hit indicator briefly after a hit lands.
    public static int hitIndicatorTicks = 0;
    private static final int HIT_INDICATOR_DURATION = 8; // ticks (~0.4s)

    public static void register() {
        // Fabric's HUD rendering was fully reworked in 1.21.6 (HudRenderCallback was
        // removed); elements are now registered by id through HudElementRegistry
        // instead of a single global callback.
        HudElementRegistry.attachElementAfter(
                HudElementRegistry.MISC_OVERLAYS,
                Identifier.of("smoothpvp", "main_hud"),
                (HudElement) HudRenderer::render
        );
    }

    /** Call this from the damage-taken/hit-landed hook to flash the indicator. */
    public static void triggerHitIndicator() {
        hitIndicatorTicks = HIT_INDICATOR_DURATION;
    }

    private static void render(DrawContext context, RenderTickCounter tickCounter) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.options.hudHidden) return;
        ClientConfig cfg = ClientConfig.get();

        int x = 6;
        int y = 6;
        int lineHeight = 10;
        int color = 0xFFFFFFFF;

        if (cfg.showFps) {
            context.drawTextWithShadow(client.textRenderer, "FPS: " + client.getCurrentFps(), x, y, color);
            y += lineHeight;
        }

        if (cfg.showPing && client.player != null && client.getNetworkHandler() != null) {
            PlayerListEntry entry = client.getNetworkHandler().getPlayerListEntry(client.player.getUuid());
            int ping = entry != null ? entry.getLatency() : -1;
            context.drawTextWithShadow(client.textRenderer, "Ping: " + (ping >= 0 ? ping + "ms" : "--"), x, y, color);
            y += lineHeight;
        }

        if (cfg.showCoords && client.player != null) {
            BlockPos pos = client.player.getBlockPos();
            context.drawTextWithShadow(client.textRenderer,
                    String.format("XYZ: %d / %d / %d", pos.getX(), pos.getY(), pos.getZ()), x, y, color);
            y += lineHeight;
        }

        if (cfg.showPotionTimers && client.player != null) {
            Collection<StatusEffectInstance> effects = client.player.getStatusEffects();
            for (StatusEffectInstance effect : effects) {
                String name = effect.getEffectType().value().getName().getString();
                int seconds = effect.getDuration() / 20;
                context.drawTextWithShadow(client.textRenderer,
                        name + ": " + seconds + "s", x, y, 0xFFAAFFAA);
                y += lineHeight;
            }
        }

        if (cfg.showKeystrokes) {
            drawKeystrokes(context, client, x, y);
        }

        if (cfg.showHitIndicator && hitIndicatorTicks > 0) {
            int screenW = client.getWindow().getScaledWidth();
            int screenH = client.getWindow().getScaledHeight();
            int alpha = (int) (255 * ((float) hitIndicatorTicks / HIT_INDICATOR_DURATION));
            int flashColor = (alpha << 24) | 0xFF3333;
            context.fill(screenW / 2 - 40, screenH / 2 - 40, screenW / 2 + 40, screenH / 2 + 40, flashColor & 0x22FFFFFF);
            hitIndicatorTicks--;
        }

        if (cfg.cleanCrosshair && !client.options.getPerspective().isFirstPerson()) {
            // vanilla already hides crosshair in 3rd person; nothing extra needed
        }
    }

    private static void drawKeystrokes(DrawContext context, MinecraftClient client, int x, int y) {
        int box = 18;
        int gap = 2;
        int baseX = client.getWindow().getScaledWidth() - (box * 3 + gap * 2) - 8;
        int baseY = client.getWindow().getScaledHeight() - (box * 3 + gap * 2) - 8;

        KeyBinding forward = client.options.forwardKey;
        KeyBinding left = client.options.leftKey;
        KeyBinding back = client.options.backKey;
        KeyBinding right = client.options.rightKey;
        KeyBinding jump = client.options.jumpKey;

        drawKey(context, client, "W", baseX + box + gap, baseY, box, forward.isPressed());
        drawKey(context, client, "A", baseX, baseY + box + gap, box, left.isPressed());
        drawKey(context, client, "S", baseX + box + gap, baseY + box + gap, box, back.isPressed());
        drawKey(context, client, "D", baseX + (box + gap) * 2, baseY + box + gap, box, right.isPressed());
        drawKey(context, client, "SPACE", baseX, baseY + (box + gap) * 2, box * 3 + gap * 2, jump.isPressed());
    }

    private static void drawKey(DrawContext context, MinecraftClient client, String label, int x, int y, int w, boolean active) {
        int bg = active ? 0xAA55AAFF : 0x66222222;
        context.fill(x, y, x + w, y + 18, bg);
        int textX = x + (w / 2) - (client.textRenderer.getWidth(label) / 2);
        context.drawTextWithShadow(client.textRenderer, label, textX, y + 5, 0xFFFFFFFF);
    }
}
