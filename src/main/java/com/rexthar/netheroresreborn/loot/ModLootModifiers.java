package com.rexthar.netheroresreborn.loot;

import com.mojang.serialization.Codec;
import com.rexthar.netheroresreborn.util.ModConstants;
import net.minecraftforge.common.loot.IGlobalLootModifier;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

/**
 * Global loot modifier registrations.
 */
public final class ModLootModifiers {
    public static final DeferredRegister<Codec<? extends IGlobalLootModifier>> LOOT_MODIFIER_SERIALIZERS =
            DeferredRegister.create(ForgeRegistries.Keys.GLOBAL_LOOT_MODIFIER_SERIALIZERS, ModConstants.MOD_ID);

    public static final RegistryObject<Codec<? extends IGlobalLootModifier>> ORE_DROP_MULTIPLIER =
            LOOT_MODIFIER_SERIALIZERS.register("ore_drop_multiplier", OreDropMultiplierModifier.CODEC);

    private ModLootModifiers() {
    }
}
