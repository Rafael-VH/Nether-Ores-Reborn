package com.rexthar.netheroresreborn.worldgen.placement;

import com.rexthar.netheroresreborn.util.ModConstants;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.levelgen.placement.PlacementModifierType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

/**
 * Custom placement modifier type registrations.
 */
public final class ModPlacementModifiers {
    public static final DeferredRegister<PlacementModifierType<?>> PLACEMENT_MODIFIER_TYPES =
            DeferredRegister.create(Registries.PLACEMENT_MODIFIER_TYPE, ModConstants.MOD_ID);

    public static final RegistryObject<PlacementModifierType<ConfigCountPlacement>> CONFIG_COUNT =
            PLACEMENT_MODIFIER_TYPES.register("config_count", () -> () -> ConfigCountPlacement.CODEC);

    public static final RegistryObject<PlacementModifierType<ConfigUniformHeightRangePlacement>> CONFIG_UNIFORM_HEIGHT_RANGE =
            PLACEMENT_MODIFIER_TYPES.register("config_uniform_height_range", () -> () -> ConfigUniformHeightRangePlacement.CODEC);

    private ModPlacementModifiers() {
    }
}
