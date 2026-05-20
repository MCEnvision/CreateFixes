package com.enviouse.createfixes.mixin;

import com.enviouse.createfixes.Config;
import com.simibubi.create.content.logistics.factoryBoard.FactoryPanelBehaviour;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = FactoryPanelBehaviour.class, remap = false)
public abstract class FactoryPanelBehaviourMixin {

    @Unique
    private int createfixes$tickCounter;

    @Inject(method = "tick", at = @At("HEAD"), cancellable = true, remap = false)
    private void createfixes$throttleServerTick(CallbackInfo ci) {
        if (!Config.enableFactoryPanelThrottle) return;

        FactoryPanelBehaviour self = (FactoryPanelBehaviour) (Object) this;
        Level world = self.getWorld();
        if (world == null || world.isClientSide) return;

        int skip = Config.factoryPanelTickSkip;
        if (skip <= 1) return;

        if (++createfixes$tickCounter < skip) {
            ci.cancel();
        } else {
            createfixes$tickCounter = 0;
        }
    }
}
