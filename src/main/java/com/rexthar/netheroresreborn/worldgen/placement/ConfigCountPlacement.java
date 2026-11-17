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

import java.util.stream.IntStream;
import java.util.stream.Stream;

/**
 * Placement modifier that derives count from config for a configured ore key.
 * Returns zero positions when generation is disabled for the ore/global config.
 */
public class ConfigCountPlacement extends PlacementModifier {
    public static final Codec<ConfigCountPlacement> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.STRING.fieldOf("ore").forGetter(p -> p.oreKey)
    ).apply(instance, ConfigCountPlacement::new));

    private final String oreKey;

    public ConfigCountPlacement(String oreKey) {
        this.oreKey = oreKey;
    }

    @Override
    public @NotNull Stream<BlockPos> getPositions(PlacementContext context, RandomSource random, BlockPos pos) {
        int count = resolveCount();
        if (count <= 0) {
            return Stream.empty();
        }
        return IntStream.range(0, count).mapToObj(i -> pos);
    }

    private int resolveCount() {
        if (!NetherOresRebornConfig.ENABLE_GENERATION.get()) {
            return 0;
        }
        NetherOresRebornConfig.OreSettings settings = NetherOresRebornConfig.getOreSettings(oreKey);
        if (settings == null || !settings.enabled.get()) {
            return 0;
        }
        return Math.max(0, settings.veinsPerChunk.get());
    }

    @Override
    public @NotNull PlacementModifierType<?> type() {
        return ModPlacementModifiers.CONFIG_COUNT.get();
    }
}
