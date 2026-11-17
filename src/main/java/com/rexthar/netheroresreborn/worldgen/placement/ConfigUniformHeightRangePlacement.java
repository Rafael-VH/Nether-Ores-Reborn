package com.rexthar.netheroresreborn.worldgen.placement;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.rexthar.netheroresreborn.config.NetherOresRebornConfig;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.levelgen.placement.PlacementContext;
import net.minecraft.world.level.levelgen.placement.PlacementModifier;
import net.minecraft.world.level.levelgen.placement.PlacementModifierType;
import org.jetbrains.annotations.NotNull;

import java.util.stream.Stream;

/**
 * Placement modifier that derives a uniform Y range from config for a configured ore key.
 */
public class ConfigUniformHeightRangePlacement extends PlacementModifier {
    public static final Codec<ConfigUniformHeightRangePlacement> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.STRING.fieldOf("ore").forGetter(p -> p.oreKey)
    ).apply(instance, ConfigUniformHeightRangePlacement::new));

    private final String oreKey;

    public ConfigUniformHeightRangePlacement(String oreKey) {
        this.oreKey = oreKey;
    }

    @Override
    public @NotNull Stream<BlockPos> getPositions(PlacementContext context, RandomSource random, BlockPos pos) {
        NetherOresRebornConfig.OreSettings settings = NetherOresRebornConfig.getOreSettings(oreKey);
        if (settings == null || !NetherOresRebornConfig.ENABLE_GENERATION.get() || !settings.enabled.get()) {
            return Stream.empty();
        }

        int minY = settings.minY.get();
        int maxY = settings.maxY.get();
        if (maxY < minY) {
            int tmp = minY;
            minY = maxY;
            maxY = tmp;
        }

        int y = (minY == maxY) ? minY : random.nextIntBetweenInclusive(minY, maxY);
        return Stream.of(new BlockPos(pos.getX(), y, pos.getZ()));
    }

    @Override
    public @NotNull PlacementModifierType<?> type() {
        return ModPlacementModifiers.CONFIG_UNIFORM_HEIGHT_RANGE.get();
    }
}
