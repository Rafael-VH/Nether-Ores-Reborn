package com.rexthar.netheroresreborn;

import com.mojang.logging.LogUtils;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.slf4j.Logger;

@Mod(NetherOresReborn.MOD_ID)
public class NetherOresReborn {
    public static final String MOD_ID = "nether_ores_reborn";
    private static final Logger LOGGER = LogUtils.getLogger();

    public NetherOresReborn(FMLJavaModLoadingContext context) {
        IEventBus modEventBus = context.getModEventBus();
        modEventBus.addListener(this::commonSetup);

        MinecraftForge.EVENT_BUS.register(this);
        LOGGER.info("Nether Ores Reborn initialized (baseline bootstrap)");
    }

    private void commonSetup(final FMLCommonSetupEvent event) {
        LOGGER.info("Nether Ores Reborn common setup complete");
    }
}
