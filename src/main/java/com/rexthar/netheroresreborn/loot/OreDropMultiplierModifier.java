package com.rexthar.netheroresreborn.loot;

import com.google.common.base.Suppliers;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.rexthar.netheroresreborn.config.NetherOresRebornConfig;
import com.rexthar.netheroresreborn.registry.ModBlocks;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraftforge.common.loot.IGlobalLootModifier;
import net.minecraftforge.common.loot.LootModifier;
import org.jetbrains.annotations.NotNull;

import java.util.function.Supplier;

/**
 * Applies configured drop multipliers to Nether Ores Reborn ore blocks.
 */
public class OreDropMultiplierModifier extends LootModifier {
    public static final Supplier<Codec<OreDropMultiplierModifier>> CODEC = Suppliers.memoize(() ->
            RecordCodecBuilder.create(inst -> codecStart(inst).apply(inst, OreDropMultiplierModifier::new)));

    protected OreDropMultiplierModifier(LootItemCondition[] conditionsIn) {
        super(conditionsIn);
    }

    @Override
    protected @NotNull ObjectArrayList<ItemStack> doApply(ObjectArrayList<ItemStack> generatedLoot, LootContext context) {
        BlockState state = context.getParamOrNull(LootContextParams.BLOCK_STATE);
        if (state == null || generatedLoot.isEmpty()) {
            return generatedLoot;
        }

        double multiplier = resolveMultiplier(state.getBlock());
        if (multiplier <= 1.0D) {
            if (multiplier <= 0.0D) {
                generatedLoot.clear();
            }
            return generatedLoot;
        }

        // Multiply stack counts (simple baseline behavior); cap to max stack sizes per stack entry.
        ObjectArrayList<ItemStack> multiplied = new ObjectArrayList<>(generatedLoot.size());
        for (ItemStack stack : generatedLoot) {
            if (stack.isEmpty()) {
                continue;
            }
            ItemStack copy = stack.copy();
            long scaled = Math.round(copy.getCount() * multiplier);
            if (scaled <= 0L) {
                continue;
            }
            int max = copy.getMaxStackSize();
            while (scaled > 0L) {
                int take = (int) Math.min(scaled, max);
                ItemStack part = copy.copy();
                part.setCount(take);
                multiplied.add(part);
                scaled -= take;
            }
        }
        return multiplied;
    }

    private static double resolveMultiplier(Block block) {
        double global = NetherOresRebornConfig.GLOBAL_DROP_MULTIPLIER.get();
        double perOre = 1.0D;
        if (block == ModBlocks.BASALT_COAL_ORE.get()) {
            perOre = NetherOresRebornConfig.BASALT_COAL_ORE.dropMultiplier.get();
        } else if (block == ModBlocks.BASALT_DIAMOND_ORE.get()) {
            perOre = NetherOresRebornConfig.BASALT_DIAMOND_ORE.dropMultiplier.get();
        } else if (block == ModBlocks.BLACKSTONE_IRON_ORE.get()) {
            perOre = NetherOresRebornConfig.BLACKSTONE_IRON_ORE.dropMultiplier.get();
        } else if (block == ModBlocks.NETHERRACK_IRON_ORE.get()) {
            perOre = NetherOresRebornConfig.NETHERRACK_IRON_ORE.dropMultiplier.get();
        } else {
            return 1.0D;
        }
        return global * perOre;
    }

    @Override
    public Codec<? extends IGlobalLootModifier> codec() {
        return CODEC.get();
    }
}
