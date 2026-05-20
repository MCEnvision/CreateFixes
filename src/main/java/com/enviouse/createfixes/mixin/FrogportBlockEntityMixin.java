package com.enviouse.createfixes.mixin;

import com.enviouse.createfixes.Config;
import com.simibubi.create.content.logistics.packagePort.frogport.FrogportBlockEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = FrogportBlockEntity.class, remap = false)
public abstract class FrogportBlockEntityMixin {

    @Unique
    private int createfixes$tickCounter;

    @Inject(method = "tick", at = @At("HEAD"), cancellable = true, remap = false)
    private void createfixes$throttleIdleTick(CallbackInfo ci) {
        if (!Config.enableFrogportThrottle) return;

        FrogportBlockEntity self = (FrogportBlockEntity) (Object) this;
        Level level = ((BlockEntity) (Object) this).getLevel();
        if (level == null || level.isClientSide) return;

        // Never skip while an animation is playing; lazyTick (every 10t by default) still runs to
        // pick up new inventories, and startAnimation / external events kick us out of idle.
        if (self.isAnimationInProgress()) {
            createfixes$tickCounter = 0;
            return;
        }

        int skip = Config.frogportTickSkip;
        if (skip <= 1) return;

        if (++createfixes$tickCounter < skip) {
            ci.cancel();
        } else {
            createfixes$tickCounter = 0;
        }
    }
}
