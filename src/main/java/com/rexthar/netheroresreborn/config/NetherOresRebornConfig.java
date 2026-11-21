package com.rexthar.netheroresreborn.config;

import net.minecraftforge.common.ForgeConfigSpec;

/**
 * Common configuration for Nether Ores Reborn.
 * Registered as {@code config/nether-ores-reborn.toml}.
 */
public final class NetherOresRebornConfig {
    public static final ForgeConfigSpec SPEC;

    public static final ForgeConfigSpec.BooleanValue ENABLE_GENERATION;
    public static final ForgeConfigSpec.DoubleValue GLOBAL_DROP_MULTIPLIER;

    public static final OreSettings BASALT_COAL_ORE;
    public static final OreSettings BASALT_DIAMOND_ORE;
    public static final OreSettings BLACKSTONE_IRON_ORE;
    public static final OreSettings NETHERRACK_IRON_ORE;

    static {
        ForgeConfigSpec.Builder builder = new ForgeConfigSpec.Builder();

        builder.comment("Global settings for Nether Ores Reborn world generation and drops")
                .push("general");
        ENABLE_GENERATION = builder
                .comment("Master switch for Nether Ores Reborn ore generation")
                .define("enableGeneration", true);
        GLOBAL_DROP_MULTIPLIER = builder
                .comment("Global multiplier applied to ore drops where supported")
                .defineInRange("dropMultiplier", 1.0D, 0.0D, 100.0D);
        builder.pop();

        builder.comment("Per-ore settings (approved baseline matrix)").push("ores");
        BASALT_COAL_ORE = new OreSettings(builder, "basalt_coal_ore", true, 24, 12, 5, 118, 1.0D);
        BASALT_DIAMOND_ORE = new OreSettings(builder, "basalt_diamond_ore", true, 8, 6, 5, 118, 1.0D);
        BLACKSTONE_IRON_ORE = new OreSettings(builder, "blackstone_iron_ore", true, 48, 10, 5, 32, 1.0D);
        NETHERRACK_IRON_ORE = new OreSettings(builder, "netherrack_iron_ore", true, 24, 10, 5, 118, 1.0D);
        builder.pop();

        SPEC = builder.build();
    }

    private NetherOresRebornConfig() {
    }

    public static OreSettings getOreSettings(String oreKey) {
        return switch (oreKey) {
            case "basalt_coal_ore" -> BASALT_COAL_ORE;
            case "basalt_diamond_ore" -> BASALT_DIAMOND_ORE;
            case "blackstone_iron_ore" -> BLACKSTONE_IRON_ORE;
            case "netherrack_iron_ore" -> NETHERRACK_IRON_ORE;
            default -> null;
        };
    }

    /**
     * Per-ore configuration values.
     */
    public static final class OreSettings {
        public final ForgeConfigSpec.BooleanValue enabled;
        public final ForgeConfigSpec.IntValue veinsPerChunk;
        public final ForgeConfigSpec.IntValue veinSize;
        public final ForgeConfigSpec.IntValue minY;
        public final ForgeConfigSpec.IntValue maxY;
        public final ForgeConfigSpec.DoubleValue dropMultiplier;

        private OreSettings(ForgeConfigSpec.Builder builder,
                            String name,
                            boolean defaultEnabled,
                            int defaultVeinsPerChunk,
                            int defaultVeinSize,
                            int defaultMinY,
                            int defaultMaxY,
                            double defaultDropMultiplier) {
            builder.push(name);
            enabled = builder
                    .comment("Enable generation for this ore variant")
                    .define("enabled", defaultEnabled);
            veinsPerChunk = builder
                    .comment("Approximate number of generation attempts/veins per chunk")
                    .defineInRange("veinsPerChunk", defaultVeinsPerChunk, 0, 64);
            veinSize = builder
                    .comment("Target vein size")
                    .defineInRange("veinSize", defaultVeinSize, 0, 64);
            minY = builder
                    .comment("Minimum Y level for generation")
                    .defineInRange("minY", defaultMinY, -64, 320);
            maxY = builder
                    .comment("Maximum Y level for generation")
                    .defineInRange("maxY", defaultMaxY, -64, 320);
            dropMultiplier = builder
                    .comment("Drop multiplier for this ore variant (applied where supported)")
                    .defineInRange("dropMultiplier", defaultDropMultiplier, 0.0D, 100.0D);
            builder.pop();
        }
    }
}
