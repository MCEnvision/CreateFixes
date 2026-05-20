package com.enviouse.createfixes.mixin;

import com.enviouse.createfixes.Config;
import com.simibubi.create.content.logistics.chute.ChuteBlockEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = ChuteBlockEntity.class, remap = false)
public abstract class ChuteBlockEntityMixin {

    @Unique
    private int createfixes$idleCounter;

    @Inject(method = "tick", at = @At("HEAD"), cancellable = true, remap = false)
    private void createfixes$throttleIdleTick(CallbackInfo ci) {
        if (!Config.enableChuteIdleThrottle) return;

        ChuteBlockEntity self = (ChuteBlockEntity) (Object) this;
        Level level = ((BlockEntity) (Object) this).getLevel();
        if (level == null || level.isClientSide) return;

        if (!self.getItem().isEmpty()) {
            createfixes$idleCounter = 0;
            return;
        }

        int skip = Config.chuteIdleTickSkip;
        if (skip <= 1) return;

        if (++createfixes$idleCounter < skip) {
            ci.cancel();
        } else {
            createfixes$idleCounter = 0;
        }
    }
}
