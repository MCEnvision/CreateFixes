package com.enviouse.createfixes.mixin;

import com.enviouse.createfixes.Config;
import com.simibubi.create.content.kinetics.mechanicalArm.ArmBlockEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = ArmBlockEntity.class, remap = false)
public abstract class ArmBlockEntityMixin {

    @Shadow
    float chasedPointProgress;

    @Shadow
    ArmBlockEntity.Phase phase;

    @Unique
    private int createfixes$tickCounter;

    @Inject(method = "tick", at = @At("HEAD"), cancellable = true, remap = false)
    private void createfixes$throttleIdleTick(CallbackInfo ci) {
        if (!Config.enableArmThrottle) return;

        Level level = ((BlockEntity) (Object) this).getLevel();
        if (level == null || level.isClientSide) return;

        // Throttle only when the arm is parked (movement finished) and in a searching phase with
        // nothing to do. While moving or dancing, tick every tick so animation and transfer are smooth.
        boolean parked = chasedPointProgress >= 1.0F
                && (phase == ArmBlockEntity.Phase.SEARCH_INPUTS || phase == ArmBlockEntity.Phase.SEARCH_OUTPUTS);
        if (!parked) {
            createfixes$tickCounter = 0;
            return;
        }

        int skip = Config.armTickSkip;
        if (skip <= 1) return;

        if (++createfixes$tickCounter < skip) {
            ci.cancel();
        } else {
            createfixes$tickCounter = 0;
        }
    }
}
