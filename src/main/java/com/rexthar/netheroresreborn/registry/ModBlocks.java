package com.rexthar.netheroresreborn.registry;

import com.rexthar.netheroresreborn.util.ModConstants;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.DropExperienceBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

/**
 * Block registrations for Nether Ores Reborn.
 */
public final class ModBlocks {
    public static final DeferredRegister<Block> BLOCKS =
            DeferredRegister.create(ForgeRegistries.BLOCKS, ModConstants.MOD_ID);

    public static final RegistryObject<Block> BASALT_COAL_ORE = BLOCKS.register("basalt_coal_ore",
            () -> new DropExperienceBlock(
                    BlockBehaviour.Properties.of()
                            .mapColor(MapColor.COLOR_BLACK)
                            .strength(4.5F, 3.0F)
                            .sound(SoundType.BASALT)
                            .requiresCorrectToolForDrops(),
                    UniformInt.of(0, 2)));

    public static final RegistryObject<Block> BASALT_DIAMOND_ORE = BLOCKS.register("basalt_diamond_ore",
            () -> new DropExperienceBlock(
                    BlockBehaviour.Properties.of()
                            .mapColor(MapColor.COLOR_BLACK)
                            .strength(4.5F, 3.0F)
                            .sound(SoundType.BASALT)
                            .requiresCorrectToolForDrops(),
                    UniformInt.of(3, 7)));

    public static final RegistryObject<Block> BLACKSTONE_IRON_ORE = BLOCKS.register("blackstone_iron_ore",
            () -> new Block(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.COLOR_BLACK)
                    .strength(3.0F, 3.0F)
                    .sound(SoundType.STONE)
                    .requiresCorrectToolForDrops()));

    public static final RegistryObject<Block> NETHERRACK_IRON_ORE = BLOCKS.register("netherrack_iron_ore",
            () -> new Block(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.NETHER)
                    .strength(3.0F, 3.0F)
                    .sound(SoundType.NETHER_ORE)
                    .requiresCorrectToolForDrops()));

    private ModBlocks() {
    }
}

