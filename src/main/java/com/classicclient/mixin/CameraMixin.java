package com.classicclient.mixin;

import com.classicclient.Freelook;
import net.minecraft.client.render.Camera;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

@Mixin(Camera.class)
public class CameraMixin {
    /** Replaces the camera angles with the freelook angles. */
    @ModifyArgs(
            method = "update",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/Camera;setRotation")
    )
    private void classicclient$rotation(Args args) {
        if (Freelook.isActive()) {
            args.set(0, Freelook.getYaw());
            args.set(1, Freelook.getPitch());
        }
    }
}
