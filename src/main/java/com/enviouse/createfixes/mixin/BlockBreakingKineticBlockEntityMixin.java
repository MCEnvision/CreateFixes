package com.enviouse.createfixes.mixin;

import com.enviouse.createfixes.Config;
import com.simibubi.create.content.kinetics.base.BlockBreakingKineticBlockEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = BlockBreakingKineticBlockEntity.class, remap = false)
public abstract class BlockBreakingKineticBlockEntityMixin {

    @Shadow
    protected int ticksUntilNextProgress;

    @Unique
    private int createfixes$tickCounter;

    @Inject(method = "tick", at = @At("HEAD"), cancellable = true, remap = false)
    private void createfixes$throttleIdleTick(CallbackInfo ci) {
        if (!Config.enableBlockBreakerThrottle) return;

        Level level = ((BlockEntity) (Object) this).getLevel();
        if (level == null || level.isClientSide) return;

        // ticksUntilNextProgress == -1 means "no block to break right now". lazyTick re-triggers
        // when a new block appears, so we can safely slow the idle poll.
        if (ticksUntilNextProgress != -1) {
            createfixes$tickCounter = 0;
            return;
        }

        int skip = Config.blockBreakerTickSkip;
        if (skip <= 1) return;

        if (++createfixes$tickCounter < skip) {
            ci.cancel();
        } else {
            createfixes$tickCounter = 0;
        }
    }
}
