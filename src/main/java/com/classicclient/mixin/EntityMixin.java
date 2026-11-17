package com.classicclient.mixin;

import com.classicclient.Freelook;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Entity.class)
public class EntityMixin {
    /** While freelook is on, mouse/touch movement turns the camera instead of the player. */
    @Inject(method = "changeLookDirection", at = @At("HEAD"), cancellable = true)
    private void classicclient$freelook(double dx, double dy, CallbackInfo ci) {
        if ((Object) this instanceof ClientPlayerEntity && Freelook.isActive()) {
            Freelook.onMouse(dx, dy);
            ci.cancel();
        }
    }
}
