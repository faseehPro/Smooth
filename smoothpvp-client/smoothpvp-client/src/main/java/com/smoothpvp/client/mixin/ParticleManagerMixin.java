package com.smoothpvp.client.mixin;

import com.smoothpvp.client.config.ClientConfig;
import net.minecraft.client.particle.ParticleManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Lets the user cap (or raise) the max particle count, which is one of the
 * biggest FPS killers in close-range PVP (e.g. totem pops, critical hits).
 * This only affects the local client's rendering, same as vanilla's
 * particle distance/amount settings.
 */
@Mixin(ParticleManager.class)
public class ParticleManagerMixin {

    @Inject(method = "getMaxParticleCount", at = @At("HEAD"), cancellable = true, remap = false, require = 0)
    private void smoothpvp$capParticles(CallbackInfoReturnable<Integer> cir) {
        ClientConfig cfg = ClientConfig.get();
        if (cfg.uncapParticles) {
            cir.setReturnValue(cfg.particleCap);
        }
    }
}
