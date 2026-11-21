package com.rexthar.netheroresreborn.config.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;

/**
 * Client-only factory for the mod's config screen.
 * <p>
 * Keeping this in a separate class from {@link ConfigScreenRegistration} matters: the registration
 * class must remain loadable on a dedicated server, because Forge resolves its method reference
 * while validating a {@code DistExecutor.safeRunWhenOn} supplier on both sides. A class that mentions
 * {@link Screen} in any method signature is rejected by Forge's runtime dist cleaner on a server.
 */
public final class ConfigScreenFactory {

    private ConfigScreenFactory() {
    }

    /**
     * Creates the screen from Forge's config-screen extension point.
     *
     * @param minecraft the current client instance, supplied by Forge
     * @param parent    the Mods list screen that opened this one
     * @return the new config screen
     */
    public static Screen create(Minecraft minecraft, Screen parent) {
        return new NetherOresConfigScreen(parent);
    }
}
