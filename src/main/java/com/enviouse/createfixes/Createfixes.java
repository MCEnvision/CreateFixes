package com.enviouse.createfixes;

import com.mojang.logging.LogUtils;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import org.slf4j.Logger;

@Mod(Createfixes.MODID)
public class Createfixes {
    public static final String MODID = "createfixes";
    private static final Logger LOGGER = LogUtils.getLogger();

    public Createfixes() {
        ModLoadingContext.get().registerConfig(ModConfig.Type.SERVER, Config.SPEC);
        LOGGER.info("CreateFixes loaded — Factory Panel throttle ready");
    }
}
