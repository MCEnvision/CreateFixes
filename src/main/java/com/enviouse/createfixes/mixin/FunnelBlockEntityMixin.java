package com.enviouse.createfixes.mixin;

import com.enviouse.createfixes.Config;
import com.simibubi.create.content.logistics.funnel.FunnelBlockEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = FunnelBlockEntity.class, remap = false)
public abstract class FunnelBlockEntityMixin {

    @Unique
    private int createfixes$tickCounter;

    @Inject(method = "tick", at = @At("HEAD"), cancellable = true, remap = false)
    private void createfixes$throttleServerTick(CallbackInfo ci) {
        if (!Config.enableFunnelThrottle) return;

        Level level = ((BlockEntity) (Object) this).getLevel();
        if (level == null || level.isClientSide) return;

        int skip = Config.funnelTickSkip;
        if (skip <= 1) return;

        if (++createfixes$tickCounter < skip) {
            ci.cancel();
        } else {
            createfixes$tickCounter = 0;
        }
    }
}
