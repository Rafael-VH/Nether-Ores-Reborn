package com.rexthar.netheroresreborn.registry;

import com.rexthar.netheroresreborn.util.ModConstants;
import net.minecraft.world.item.Item;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

/**
 * Item registrations for Nether Ores Reborn.
 */
public final class ModItems {
    public static final DeferredRegister<Item> ITEMS =
            DeferredRegister.create(ForgeRegistries.ITEMS, ModConstants.MOD_ID);

    private ModItems() {
    }
}
