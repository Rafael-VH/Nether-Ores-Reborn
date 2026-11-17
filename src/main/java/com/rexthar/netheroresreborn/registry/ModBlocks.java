package com.rexthar.netheroresreborn.registry;

import com.rexthar.netheroresreborn.util.ModConstants;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

/**
 * Block registrations for Nether Ores Reborn.
 */
public final class ModBlocks {
    public static final DeferredRegister<Block> BLOCKS =
            DeferredRegister.create(ForgeRegistries.BLOCKS, ModConstants.MOD_ID);

    private ModBlocks() {
    }
}
