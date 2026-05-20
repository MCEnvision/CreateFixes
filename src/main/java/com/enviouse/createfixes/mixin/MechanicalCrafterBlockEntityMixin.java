package com.enviouse.createfixes.mixin;

import com.enviouse.createfixes.Config;
import com.simibubi.create.content.kinetics.crafter.MechanicalCrafterBlockEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.lang.reflect.Field;

@Mixin(value = MechanicalCrafterBlockEntity.class, remap = false)
public abstract class MechanicalCrafterBlockEntityMixin {

    // Phase is a package-private nested enum on MechanicalCrafterBlockEntity — we can't @Shadow a
    // typed reference to it from outside the package, and @Shadow with Enum<?> trips APT descriptor
    // checks. A cached reflective Field lets us read the enum constant and compare by name.
    @Unique
    private static final Field createfixes$PHASE_FIELD;

    static {
        Field f;
        try {
            f = MechanicalCrafterBlockEntity.class.getDeclaredField("phase");
            f.setAccessible(true);
        } catch (NoSuchFieldException e) {
            f = null;
        }
        createfixes$PHASE_FIELD = f;
    }

    @Unique
    private int createfixes$tickCounter;

    @Inject(method = "tick", at = @At("HEAD"), cancellable = true, remap = false)
    private void createfixes$throttleIdleTick(CallbackInfo ci) {
        if (!Config.enableMechanicalCrafterThrottle) return;

        Level level = ((BlockEntity) (Object) this).getLevel();
        if (level == null || level.isClientSide) return;

        if (createfixes$PHASE_FIELD == null) return;

        Enum<?> phase;
        try {
            phase = (Enum<?>) createfixes$PHASE_FIELD.get(this);
        } catch (IllegalAccessException e) {
            return;
        }

        // Only throttle when the crafter has no active recipe in flight.
        if (phase == null || !"IDLE".equals(phase.name())) {
            createfixes$tickCounter = 0;
            return;
        }

        int skip = Config.mechanicalCrafterTickSkip;
        if (skip <= 1) return;

        if (++createfixes$tickCounter < skip) {
            ci.cancel();
        } else {
            createfixes$tickCounter = 0;
        }
    }
}
