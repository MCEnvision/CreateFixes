package com.enviouse.createfixes.mixin;

import com.enviouse.createfixes.Config;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Skips {@code TileEntityMekanism#tickServer} every N ticks. Targeted by string so this class
 * compiles even when Mekanism is not on the classpath; the mixin is no-op if the target class
 * never loads. Shipped with tickSkip=1 (disabled) by default because Mekanism machines rely on
 * per-tick energy/chemical transfers — users must opt in and accept proportionally slower
 * recipe throughput.
 */
@Pseudo
@Mixin(targets = "mekanism.common.tile.base.TileEntityMekanism", remap = false)
public abstract class TileEntityMekanismMixin {

    @Unique
    private int createfixes$tickCounter;

    @Inject(method = "tickServer", at = @At("HEAD"), cancellable = true, remap = false, require = 0, expect = 0)
    private void createfixes$throttleServerTick(CallbackInfo ci) {
        if (!Config.enableMekanismThrottle) return;

        Level level = ((BlockEntity) (Object) this).getLevel();
        if (level == null || level.isClientSide) return;

        int skip = Config.mekanismTickSkip;
        if (skip <= 1) return;

        if (++createfixes$tickCounter < skip) {
            ci.cancel();
        } else {
            createfixes$tickCounter = 0;
        }
    }
}
