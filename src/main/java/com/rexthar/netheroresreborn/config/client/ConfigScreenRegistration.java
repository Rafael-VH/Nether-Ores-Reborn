package com.rexthar.netheroresreborn.config.client;

import net.minecraftforge.client.ConfigScreenHandler;
import net.minecraftforge.fml.ModLoadingContext;

/**
 * Registers the mod's configuration screen with Forge's Mods list.
 * <p>
 * This class is referenced through
 * {@code DistExecutor.safeRunWhenOn(Dist.CLIENT, () -> ConfigScreenRegistration::register)}. It must
 * stay loadable on a dedicated server: Forge resolves the method reference on both sides while
 * validating the safe referent. For that reason it contains no client types; the screen itself is
 * only reachable through {@link ConfigScreenFactory}.
 */
public final class ConfigScreenRegistration {

    private ConfigScreenRegistration() {
    }

    /**
     * Registers the {@link ConfigScreenHandler.ConfigScreenFactory} extension point for this mod.
     * Forge's Mods list enables its Config button for any mod that provides one.
     * <p>
     * This must stay a no-argument static method. Forge rejects a {@code SafeRunnable} whose
     * implementation class is the same class that captured it.
     */
    @SuppressWarnings("removal")
    public static void register() {
        // The static accessor is deprecated in 47.4.x, but the safe-referent pattern requires a
        // no-argument static method. Registry state is already active at this point.
        ModLoadingContext.get().registerExtensionPoint(
                ConfigScreenHandler.ConfigScreenFactory.class,
                () -> new ConfigScreenHandler.ConfigScreenFactory(ConfigScreenFactory::create));
    }
}
