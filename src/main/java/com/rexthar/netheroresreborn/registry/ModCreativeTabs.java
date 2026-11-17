package com.rexthar.netheroresreborn.registry;

import com.rexthar.netheroresreborn.util.ModConstants;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

/**
 * Creative mode tab registrations for Nether Ores Reborn.
 */
public final class ModCreativeTabs {
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS =
            DeferredRegister.create(Registries.CREATIVE_MODE_TAB, ModConstants.MOD_ID);

    public static final RegistryObject<CreativeModeTab> NETHER_ORES_REBORN_TAB = CREATIVE_MODE_TABS.register(
            "nether_ores_reborn_tab",
            () -> CreativeModeTab.builder()
                    .title(Component.translatable("creativetab." + ModConstants.MOD_ID + ".main"))
                    .icon(() -> new ItemStack(ModItems.BASALT_DIAMOND_ORE_ITEM.get()))
                    .displayItems((parameters, output) -> {
                        output.accept(ModItems.BASALT_COAL_ORE_ITEM.get());
                        output.accept(ModItems.BASALT_DIAMOND_ORE_ITEM.get());
                        output.accept(ModItems.BLACKSTONE_IRON_ORE_ITEM.get());
                        output.accept(ModItems.NETHERRACK_IRON_ORE_ITEM.get());
                    })
                    .build());

    private ModCreativeTabs() {
    }
}

