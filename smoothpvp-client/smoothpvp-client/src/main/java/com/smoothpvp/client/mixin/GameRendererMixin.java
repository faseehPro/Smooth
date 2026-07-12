package com.smoothpvp.client.mixin;

import com.smoothpvp.client.config.ClientConfig;
import net.minecraft.client.render.GameRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Cancels view-bob and hurt-camera-shake transforms when the user has
 * disabled them in config, giving a steadier, more competitive camera
 * without touching aim/hitscan/reach in any way.
 */
@Mixin(GameRenderer.class)
public class GameRendererMixin {

    @Inject(method = "bobViewWhenHurt", at = @At("HEAD"), cancellable = true)
    private void smoothpvp$cancelHurtBob(net.minecraft.client.util.math.MatrixStack matrices, float tickDelta, CallbackInfo ci) {
        if (ClientConfig.get().disableHurtCameraShake) {
            ci.cancel();
        }
    }

    @Inject(method = "bobView", at = @At("HEAD"), cancellable = true)
    private void smoothpvp$cancelViewBob(net.minecraft.client.util.math.MatrixStack matrices, float tickDelta, CallbackInfo ci) {
        if (ClientConfig.get().disableViewBob) {
            ci.cancel();
        }
    }
}
