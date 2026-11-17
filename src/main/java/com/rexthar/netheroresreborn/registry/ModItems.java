package com.rexthar.netheroresreborn.registry;

import com.rexthar.netheroresreborn.util.ModConstants;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

/**
 * Item registrations for Nether Ores Reborn.
 */
public final class ModItems {
    public static final DeferredRegister<Item> ITEMS =
            DeferredRegister.create(ForgeRegistries.ITEMS, ModConstants.MOD_ID);

    public static final RegistryObject<Item> BASALT_COAL_ORE_ITEM = ITEMS.register("basalt_coal_ore",
            () -> new BlockItem(ModBlocks.BASALT_COAL_ORE.get(), new Item.Properties()));

    public static final RegistryObject<Item> BASALT_DIAMOND_ORE_ITEM = ITEMS.register("basalt_diamond_ore",
            () -> new BlockItem(ModBlocks.BASALT_DIAMOND_ORE.get(), new Item.Properties()));

    public static final RegistryObject<Item> BLACKSTONE_IRON_ORE_ITEM = ITEMS.register("blackstone_iron_ore",
            () -> new BlockItem(ModBlocks.BLACKSTONE_IRON_ORE.get(), new Item.Properties()));

    public static final RegistryObject<Item> NETHERRACK_IRON_ORE_ITEM = ITEMS.register("netherrack_iron_ore",
            () -> new BlockItem(ModBlocks.NETHERRACK_IRON_ORE.get(), new Item.Properties()));

    private ModItems() {
    }
}

