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
        if (multiplier == 1.0D) {
            return generatedLoot;
        }
        if (multiplier <= 0.0D) {
            generatedLoot.clear();
            return generatedLoot;
        }

        ObjectArrayList<ItemStack> scaledLoot = new ObjectArrayList<>(generatedLoot.size());
        for (ItemStack stack : generatedLoot) {
            if (stack.isEmpty()) {
                continue;
            }

            // Do not multiply Silk Touch/self-drop of the ore block item.
            if (stack.is(state.getBlock().asItem())) {
                scaledLoot.add(stack);
                continue;
            }

            int targetCount = scaleCount(stack.getCount(), multiplier, context);
            if (targetCount <= 0) {
                continue;
            }

            addSplitStacks(scaledLoot, stack, targetCount);
        }
        return scaledLoot;
    }

    private static int scaleCount(int baseCount, double multiplier, LootContext context) {
        double scaled = baseCount * multiplier;
        if (scaled <= 0.0D) {
            return 0;
        }

        int whole = (int) Math.floor(scaled);
        double fractional = scaled - whole;
        if (fractional > 0.0D && context.getRandom().nextDouble() < fractional) {
            whole++;
        }
        return Math.max(0, whole);
    }

    private static void addSplitStacks(ObjectArrayList<ItemStack> output, ItemStack source, int totalCount) {
        int maxStackSize = Math.max(1, source.getMaxStackSize());
        int remaining = totalCount;
        while (remaining > 0) {
            int take = Math.min(remaining, maxStackSize);
            ItemStack part = source.copy();
            part.setCount(take);
            output.add(part);
            remaining -= take;
        }
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
