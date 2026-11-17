package com.rexthar.netheroresreborn;

import com.mojang.logging.LogUtils;
import com.rexthar.netheroresreborn.registry.ModBlocks;
import com.rexthar.netheroresreborn.registry.ModCreativeTabs;
import com.rexthar.netheroresreborn.registry.ModItems;
import com.rexthar.netheroresreborn.util.ModConstants;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.slf4j.Logger;

/**
 * Main mod entrypoint for Nether Ores Reborn.
 */
@Mod(ModConstants.MOD_ID)
public class NetherOresReborn {
    private static final Logger LOGGER = LogUtils.getLogger();

    public NetherOresReborn(FMLJavaModLoadingContext context) {
        IEventBus modEventBus = context.getModEventBus();

        modEventBus.addListener(this::commonSetup);

        ModBlocks.BLOCKS.register(modEventBus);
        ModItems.ITEMS.register(modEventBus);
        ModCreativeTabs.CREATIVE_MODE_TABS.register(modEventBus);

        MinecraftForge.EVENT_BUS.register(this);
        LOGGER.info("{} initialized (Phase 2.1 scaffold)", ModConstants.MOD_NAME);
    }

    private void commonSetup(final FMLCommonSetupEvent event) {
        LOGGER.info("{} common setup complete", ModConstants.MOD_NAME);
    }
}

