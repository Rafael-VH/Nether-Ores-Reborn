package com.rexthar.netheroresreborn.config;

import net.minecraftforge.common.ForgeConfigSpec;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Common configuration for Nether Ores Reborn.
 * Registered as {@code config/nether-ores-reborn.toml}.
 * <p>
 * Defaults live in {@link #ORE_DEFAULTS} and {@link #DEFAULT_ENABLE_GENERATION} /
 * {@link #DEFAULT_GLOBAL_DROP_MULTIPLIER} so the spec builder and the in-game config screen share a
 * single source of truth.
 */
public final class NetherOresRebornConfig {
    public static final ForgeConfigSpec SPEC;

    public static final ForgeConfigSpec.BooleanValue ENABLE_GENERATION;
    public static final ForgeConfigSpec.DoubleValue GLOBAL_DROP_MULTIPLIER;

    public static final OreSettings BASALT_COAL_ORE;
    public static final OreSettings BASALT_DIAMOND_ORE;
    public static final OreSettings BLACKSTONE_IRON_ORE;
    public static final OreSettings NETHERRACK_IRON_ORE;

    public static final boolean DEFAULT_ENABLE_GENERATION = true;
    public static final double DEFAULT_GLOBAL_DROP_MULTIPLIER = 1.0D;
    public static final boolean DEFAULT_ORE_ENABLED = true;

    /**
     * Built-in defaults per ore key. Insertion order is stable because it also drives the config
     * screen pages.
     */
    public static final Map<String, OreDefaults> ORE_DEFAULTS = createOreDefaults();

    static {
        ForgeConfigSpec.Builder builder = new ForgeConfigSpec.Builder();

        builder.comment("Global settings for Nether Ores Reborn world generation and drops")
                .push("general");
        ENABLE_GENERATION = builder
                .comment("Master switch for Nether Ores Reborn ore generation")
                .define("enableGeneration", DEFAULT_ENABLE_GENERATION);
        GLOBAL_DROP_MULTIPLIER = builder
                .comment("Global multiplier applied to ore drops where supported")
                .defineInRange("dropMultiplier", DEFAULT_GLOBAL_DROP_MULTIPLIER, 0.0D, 100.0D);
        builder.pop();

        builder.comment("Per-ore settings (approved baseline matrix)").push("ores");
        BASALT_COAL_ORE = new OreSettings(builder, "basalt_coal_ore");
        BASALT_DIAMOND_ORE = new OreSettings(builder, "basalt_diamond_ore");
        BLACKSTONE_IRON_ORE = new OreSettings(builder, "blackstone_iron_ore");
        NETHERRACK_IRON_ORE = new OreSettings(builder, "netherrack_iron_ore");
        builder.pop();

        SPEC = builder.build();
    }

    private NetherOresRebornConfig() {
    }

    private static Map<String, OreDefaults> createOreDefaults() {
        Map<String, OreDefaults> defaults = new LinkedHashMap<>();
        defaults.put("basalt_coal_ore", new OreDefaults(24, 12, 5, 118, 1.0D));
        defaults.put("basalt_diamond_ore", new OreDefaults(8, 6, 5, 118, 1.0D));
        defaults.put("blackstone_iron_ore", new OreDefaults(48, 10, 5, 32, 1.0D));
        defaults.put("netherrack_iron_ore", new OreDefaults(24, 10, 5, 118, 1.0D));
        return Collections.unmodifiableMap(defaults);
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
     * Built-in defaults for one ore variant.
     */
    public record OreDefaults(int veinsPerChunk, int veinSize, int minY, int maxY, double dropMultiplier) {
    }

    /**
     * Per-ore configuration values.
     */
    public static final class OreSettings {
        public final String key;
        public final OreDefaults defaults;
        public final ForgeConfigSpec.BooleanValue enabled;
        public final ForgeConfigSpec.IntValue veinsPerChunk;
        public final ForgeConfigSpec.IntValue veinSize;
        public final ForgeConfigSpec.IntValue minY;
        public final ForgeConfigSpec.IntValue maxY;
        public final ForgeConfigSpec.DoubleValue dropMultiplier;

        private OreSettings(ForgeConfigSpec.Builder builder, String key) {
            this.key = key;
            this.defaults = ORE_DEFAULTS.get(key);

            builder.push(key);
            enabled = builder
                    .comment("Enable generation for this ore variant")
                    .define("enabled", DEFAULT_ORE_ENABLED);
            veinsPerChunk = builder
                    .comment("Approximate number of generation attempts/veins per chunk")
                    .defineInRange("veinsPerChunk", defaults.veinsPerChunk(), 0, 64);
            veinSize = builder
                    .comment("Target vein size")
                    .defineInRange("veinSize", defaults.veinSize(), 0, 64);
            minY = builder
                    .comment("Minimum Y level for generation")
                    .defineInRange("minY", defaults.minY(), -64, 320);
            maxY = builder
                    .comment("Maximum Y level for generation")
                    .defineInRange("maxY", defaults.maxY(), -64, 320);
            dropMultiplier = builder
                    .comment("Drop multiplier for this ore variant (applied where supported)")
                    .defineInRange("dropMultiplier", defaults.dropMultiplier(), 0.0D, 100.0D);
            builder.pop();
        }

        /**
         * Translation key used by the config screen for this ore's display name.
         */
        public String displayNameKey() {
            return "gui.nether_ores_reborn.ores." + key;
        }
    }
}
