package com.rexthar.netheroresreborn.registry;

import com.rexthar.netheroresreborn.util.ModConstants;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraftforge.registries.DeferredRegister;

/**
 * Creative mode tab registrations for Nether Ores Reborn.
 */
public final class ModCreativeTabs {
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS =
            DeferredRegister.create(Registries.CREATIVE_MODE_TAB, ModConstants.MOD_ID);

    private ModCreativeTabs() {
    }
}
