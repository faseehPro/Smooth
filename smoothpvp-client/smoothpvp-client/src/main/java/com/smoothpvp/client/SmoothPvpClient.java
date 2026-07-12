package com.smoothpvp.client;

import com.smoothpvp.client.config.ClientConfig;
import com.smoothpvp.client.hud.HudRenderer;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.entity.LivingEntity;
import org.lwjgl.glfw.GLFW;

/**
 * SmoothPVP Client — fair-play FPS/UI/PVP quality-of-life mod.
 *
 * Features:
 *  - Clean custom HUD (FPS, ping, coords, potion timers, keystrokes)
 *  - Hit indicator flash on landed hits
 *  - Optional disable of view-bob / hurt camera shake for a steadier feel
 *  - Particle cap toggle for cleaner visuals + better FPS in fights
 *
 * Explicitly NOT included: killaura, reach extension, autoclickers,
 * x-ray/ESP, or any auto-aim. This is a cosmetic/QoL client only.
 */
public class SmoothPvpClient implements ClientModInitializer {

    private static KeyBinding toggleHudKey;
    private int prevPlayerHurtTime = 0;

    @Override
    public void onInitializeClient() {
        ClientConfig.get(); // ensure config is loaded/created on startup

        HudRenderer.register();

        toggleHudKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.smoothpvp.togglehud",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_RIGHT_BRACKET,
                "category.smoothpvp.client"
        ));

        ClientTickEvents.END_CLIENT_TICK.register(this::onTick);
    }

    private void onTick(MinecraftClient client) {
        if (toggleHudKey.wasPressed()) {
            client.options.hudHidden = !client.options.hudHidden;
        }

        // Detect a hit landing on the entity the player is currently looking at
        // by watching for a fresh hurt animation tick — simple, network-safe,
        // no packet manipulation involved.
        if (client.targetedEntity instanceof LivingEntity target) {
            int hurtTime = target.hurtTime;
            if (hurtTime > 0 && hurtTime != prevPlayerHurtTime) {
                HudRenderer.triggerHitIndicator();
            }
            prevPlayerHurtTime = hurtTime;
        }
    }
}
