package com.rexthar.netheroresreborn.config.client;

import com.mojang.logging.LogUtils;
import com.rexthar.netheroresreborn.config.NetherOresRebornConfig;
import com.rexthar.netheroresreborn.config.NetherOresRebornConfig.OreSettings;
import com.rexthar.netheroresreborn.config.client.widgets.DecimalField;
import com.rexthar.netheroresreborn.config.client.widgets.IntSlider;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.CycleButton;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraftforge.common.ForgeConfigSpec;
import org.slf4j.Logger;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * In-game configuration screen for Nether Ores Reborn.
 * <p>
 * Edits are collected in {@link #pending} and only applied to the {@link ForgeConfigSpec} when the
 * player presses Done. Cancel therefore needs no restore logic, and a value the player never touched
 * can never be overwritten.
 * <p>
 * Client-only: never load this class on a dedicated server.
 */
public class NetherOresConfigScreen extends Screen {
    private static final Logger LOGGER = LogUtils.getLogger();

    private static final int CONTENT_WIDTH = 310;
    private static final int LABEL_WIDTH = 150;
    private static final int CONTROL_WIDTH = 150;
    private static final int CONTROL_GAP = 10;
    private static final int ROW_HEIGHT = 26;
    private static final int FIRST_ROW_Y = 64;
    private static final int WIDGET_HEIGHT = 20;
    private static final int TEXT_COLOR = 0xA0A0A0;
    private static final int ERROR_COLOR = 0xFF5555;
    private static final int WARNING_COLOR = 0xFFAA00;

    private static final int MAX_VEINS_PER_CHUNK = 64;
    private static final int UI_MIN_Y = 0;
    private static final int UI_MAX_Y = 127;

    private final Screen parent;
    private final Map<ForgeConfigSpec.ConfigValue<?>, Object> pending = new LinkedHashMap<>();

    private ConfigPage page = ConfigPage.GENERAL;
    private boolean specReady;
    private Component error;
    private DecimalField dropMultiplierField;

    public NetherOresConfigScreen(Screen parent) {
        super(Component.translatable("gui.nether_ores_reborn.title"));
        this.parent = parent;
    }

    @Override
    protected void init() {
        this.clearWidgets();
        this.dropMultiplierField = null;
        this.specReady = NetherOresRebornConfig.SPEC.isLoaded();

        int left = (this.width - CONTENT_WIDTH) / 2;
        int controlX = left + LABEL_WIDTH + CONTROL_GAP;

        initPageNavigation(left);
        initPageBody(controlX);
        initFooter(left);
    }

    private void initPageNavigation(int left) {
        int navY = 30;
        Button previous = Button.builder(
                Component.translatable("gui.nether_ores_reborn.previous_page"), button -> changePage(-1))
                .bounds(left, navY, 24, WIDGET_HEIGHT)
                .build();
        previous.active = ConfigPage.values().length > 1;
        addRenderableWidget(previous);

        Button next = Button.builder(
                Component.translatable("gui.nether_ores_reborn.next_page"), button -> changePage(1))
                .bounds(left + CONTENT_WIDTH - 24, navY, 24, WIDGET_HEIGHT)
                .build();
        next.active = ConfigPage.values().length > 1;
        addRenderableWidget(next);
    }

    private void initPageBody(int controlX) {
        if (!this.specReady) {
            return;
        }
        switch (this.page) {
            case GENERAL -> initGeneral(controlX, FIRST_ROW_Y);
            case NETHERRACK_IRON_ORE, BLACKSTONE_IRON_ORE, BASALT_COAL_ORE, BASALT_DIAMOND_ORE ->
                    initOre(this.page.settings(), controlX, FIRST_ROW_Y);
        }
    }

    private void initGeneral(int controlX, int firstRowY) {
        addRenderableWidget(CycleButton.onOffBuilder(current(NetherOresRebornConfig.ENABLE_GENERATION))
                .create(controlX, firstRowY, CONTROL_WIDTH, WIDGET_HEIGHT,
                        Component.translatable("gui.nether_ores_reborn.general.enable_generation"),
                        (button, value) -> pending.put(NetherOresRebornConfig.ENABLE_GENERATION, value)));

        this.dropMultiplierField = new DecimalField(this.font, controlX, firstRowY + ROW_HEIGHT,
                CONTROL_WIDTH, WIDGET_HEIGHT,
                Component.translatable("gui.nether_ores_reborn.general.drop_multiplier"),
                current(NetherOresRebornConfig.GLOBAL_DROP_MULTIPLIER), 0.0D, 100.0D,
                value -> pending.put(NetherOresRebornConfig.GLOBAL_DROP_MULTIPLIER, value));
        addRenderableWidget(this.dropMultiplierField);
    }

    private void initOre(OreSettings ore, int controlX, int firstRowY) {
        addRenderableWidget(CycleButton.onOffBuilder(current(ore.enabled))
                .create(controlX, firstRowY, CONTROL_WIDTH, WIDGET_HEIGHT,
                        Component.translatable("gui.nether_ores_reborn.ore.enabled"),
                        (button, value) -> pending.put(ore.enabled, value)));

        addRenderableWidget(new IntSlider(controlX, firstRowY + ROW_HEIGHT, CONTROL_WIDTH, WIDGET_HEIGHT,
                current(ore.veinsPerChunk), 0, MAX_VEINS_PER_CHUNK,
                value -> pending.put(ore.veinsPerChunk, value)));
        addRenderableWidget(new IntSlider(controlX, firstRowY + 2 * ROW_HEIGHT, CONTROL_WIDTH, WIDGET_HEIGHT,
                current(ore.minY), UI_MIN_Y, UI_MAX_Y,
                value -> pending.put(ore.minY, value)));
        addRenderableWidget(new IntSlider(controlX, firstRowY + 3 * ROW_HEIGHT, CONTROL_WIDTH, WIDGET_HEIGHT,
                current(ore.maxY), UI_MIN_Y, UI_MAX_Y,
                value -> pending.put(ore.maxY, value)));

        this.dropMultiplierField = new DecimalField(this.font, controlX, firstRowY + 4 * ROW_HEIGHT,
                CONTROL_WIDTH, WIDGET_HEIGHT,
                Component.translatable("gui.nether_ores_reborn.ore.drop_multiplier"),
                current(ore.dropMultiplier), 0.0D, 100.0D,
                value -> pending.put(ore.dropMultiplier, value));
        addRenderableWidget(this.dropMultiplierField);
    }

    private void initFooter(int left) {
        int footerY = this.height - 32;
        Button reset = Button.builder(Component.translatable("gui.nether_ores_reborn.reset_page"), button -> resetPage())
                .bounds(left, footerY, 100, WIDGET_HEIGHT)
                .build();
        reset.active = this.specReady;
        addRenderableWidget(reset);

        addRenderableWidget(Button.builder(Component.translatable("gui.cancel"), button -> onClose())
                .bounds(left + 120, footerY, 90, WIDGET_HEIGHT)
                .build());
        addRenderableWidget(Button.builder(Component.translatable("gui.done"), button -> saveAndClose())
                .bounds(left + 215, footerY, 95, WIDGET_HEIGHT)
                .build());
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        this.renderBackground(graphics);

        int centerX = this.width / 2;
        graphics.drawCenteredString(this.font, this.title, centerX, 8, 0xFFFFFF);
        graphics.drawCenteredString(this.font, this.page.title(), centerX, 36, TEXT_COLOR);

        super.render(graphics, mouseX, mouseY, partialTick);

        if (!this.specReady) {
            graphics.drawCenteredString(this.font,
                    Component.translatable("gui.nether_ores_reborn.error.spec_not_loaded"),
                    centerX, FIRST_ROW_Y, ERROR_COLOR);
        } else {
            renderRowLabels(graphics);
        }

        renderWarnings(graphics, centerX);

        if (this.error != null) {
            graphics.drawCenteredString(this.font, this.error, centerX, this.height - 48, ERROR_COLOR);
        }
    }

    /**
     * Renders the label column for the active page. Labels are drawn separately from the widgets so
     * each row stays a single line of code at its call site.
     */
    private void renderRowLabels(GuiGraphics graphics) {
        int labelX = (this.width - CONTENT_WIDTH) / 2;
        if (this.page == ConfigPage.GENERAL) {
            drawRowLabel(graphics, "gui.nether_ores_reborn.general.enable_generation", labelX, 0);
            drawRowLabel(graphics, "gui.nether_ores_reborn.general.drop_multiplier", labelX, 1);
            return;
        }

        drawRowLabel(graphics, "gui.nether_ores_reborn.ore.enabled", labelX, 0);
        drawRowLabel(graphics, "gui.nether_ores_reborn.ore.veins_per_chunk", labelX, 1);
        drawRowLabel(graphics, "gui.nether_ores_reborn.ore.min_y", labelX, 2);
        drawRowLabel(graphics, "gui.nether_ores_reborn.ore.max_y", labelX, 3);
        drawRowLabel(graphics, "gui.nether_ores_reborn.ore.drop_multiplier", labelX, 4);

        if (current(this.page.settings().veinsPerChunk) == 0) {
            graphics.drawString(this.font,
                    Component.translatable("gui.nether_ores_reborn.ore.zero_veins_hint"),
                    labelX, FIRST_ROW_Y + 5 * ROW_HEIGHT + 2, TEXT_COLOR, false);
        }
    }

    /**
     * Shows the chunk warning on every page and adds a second warning when the client is connected to
     * a remote server, where this screen only edits the local configuration copy.
     */
    private void renderWarnings(GuiGraphics graphics, int centerX) {
        int y = this.height - 62;
        graphics.drawCenteredString(this.font,
                Component.translatable("gui.nether_ores_reborn.warning.new_chunks"),
                centerX, y, WARNING_COLOR);
        if (this.minecraft != null && this.minecraft.getCurrentServer() != null) {
            graphics.drawCenteredString(this.font,
                    Component.translatable("gui.nether_ores_reborn.warning.remote_server"),
                    centerX, y - 14, WARNING_COLOR);
        }
    }

    private void drawRowLabel(GuiGraphics graphics, String translationKey, int labelX, int row) {
        int y = FIRST_ROW_Y + row * ROW_HEIGHT + 6;
        graphics.drawString(this.font, Component.translatable(translationKey), labelX, y, TEXT_COLOR, false);
    }

    private void changePage(int delta) {
        if (!commitDropMultiplier()) {
            return;
        }
        ConfigPage[] pages = ConfigPage.values();
        int index = Math.floorMod(this.page.ordinal() + delta, pages.length);
        this.page = pages[index];
        this.error = null;
        rebuildWidgets();
    }

    private void resetPage() {
        if (this.page == ConfigPage.GENERAL) {
            this.pending.put(NetherOresRebornConfig.ENABLE_GENERATION,
                    NetherOresRebornConfig.DEFAULT_ENABLE_GENERATION);
            this.pending.put(NetherOresRebornConfig.GLOBAL_DROP_MULTIPLIER,
                    NetherOresRebornConfig.DEFAULT_GLOBAL_DROP_MULTIPLIER);
        } else {
            OreSettings ore = this.page.settings();
            this.pending.put(ore.enabled, NetherOresRebornConfig.DEFAULT_ORE_ENABLED);
            this.pending.put(ore.veinsPerChunk, ore.defaults.veinsPerChunk());
            this.pending.put(ore.minY, ore.defaults.minY());
            this.pending.put(ore.maxY, ore.defaults.maxY());
            this.pending.put(ore.dropMultiplier, ore.defaults.dropMultiplier());
        }
        this.error = null;
        rebuildWidgets();
    }

    /**
     * Commits the decimal field on the active page. An invalid value keeps the player on that page so
     * it cannot be silently discarded by navigating away or by pressing Done.
     */
    private boolean commitDropMultiplier() {
        if (this.dropMultiplierField == null) {
            return true;
        }
        this.dropMultiplierField.commit();
        if (!this.dropMultiplierField.isValid()) {
            this.error = Component.translatable(this.dropMultiplierField.errorKey());
            return false;
        }
        return true;
    }

    private void saveAndClose() {
        if (!commitDropMultiplier()) {
            return;
        }
        reconcileHeightRanges();
        try {
            applyPending();
            NetherOresRebornConfig.SPEC.save();
        } catch (RuntimeException exception) {
            LOGGER.error("Failed to save Nether Ores Reborn configuration", exception);
            this.error = Component.translatable("gui.nether_ores_reborn.error.save_failed");
            rebuildWidgets();
            return;
        }
        onClose();
    }

    /**
     * Keeps each ore's height range ordered. Only ranges touched on this screen are considered, so a
     * hand-edited out-of-domain value is never rewritten by an unrelated edit.
     * <p>
     * The bound the player moved wins. When the other bound is outside the UI domain and cannot be
     * changed safely, the edited bound is clamped to it instead.
     */
    private void reconcileHeightRanges() {
        for (ConfigPage candidate : ConfigPage.values()) {
            OreSettings ore = candidate.settings();
            if (ore == null) {
                continue;
            }
            boolean minEdited = this.pending.containsKey(ore.minY);
            boolean maxEdited = this.pending.containsKey(ore.maxY);
            if (!minEdited && !maxEdited) {
                continue;
            }
            int min = current(ore.minY);
            int max = current(ore.maxY);
            int fix = heightRangeFix(min, max, minEdited, maxEdited);
            if (fix == 1) {
                this.pending.put(ore.maxY, min);
            } else if (fix == -1) {
                this.pending.put(ore.minY, max);
            }
        }
    }

    /**
     * Pure decision helper for an inverted height range: returns {@code 1} when {@code maxY} should be
     * raised to {@code minY}, {@code -1} when {@code minY} should be lowered to {@code maxY}, and
     * {@code 0} when no adjustment is needed.
     * <p>
     * A bound may be adjusted when the player edited it or when its loaded value is inside the Nether
     * UI domain. That keeps a hand-edited out-of-domain value on the other bound from being rewritten.
     */
    public static int heightRangeFix(int min, int max, boolean minEdited, boolean maxEdited) {
        if ((!minEdited && !maxEdited) || min <= max) {
            return 0;
        }
        boolean minWritable = minEdited || isNetherY(min);
        boolean maxWritable = maxEdited || isNetherY(max);
        if (minEdited) {
            return maxWritable ? 1 : -1;
        }
        return minWritable ? -1 : 1;
    }

    private static boolean isNetherY(int value) {
        return value >= UI_MIN_Y && value <= UI_MAX_Y;
    }

    private void applyPending() {
        for (Map.Entry<ForgeConfigSpec.ConfigValue<?>, Object> entry : this.pending.entrySet()) {
            setRaw(entry.getKey(), entry.getValue());
        }
        this.pending.clear();
    }

    /**
     * Applies one pending value. The cast is unchecked because {@link #pending} is keyed by value
     * type rather than parametrized on it; every entry is written by a control whose value type
     * matches its key.
     */
    @SuppressWarnings({"unchecked", "rawtypes"})
    private static void setRaw(ForgeConfigSpec.ConfigValue<?> value, Object newValue) {
        ((ForgeConfigSpec.ConfigValue) value).set(newValue);
    }

    /**
     * Returns the effective value for a control: the pending edit when one exists, otherwise the
     * value currently loaded from the config file.
     */
    @SuppressWarnings("unchecked")
    private <T> T current(ForgeConfigSpec.ConfigValue<T> value) {
        if (this.pending.containsKey(value)) {
            return (T) this.pending.get(value);
        }
        return value.get();
    }

    @Override
    public void onClose() {
        if (this.minecraft != null) {
            this.minecraft.setScreen(this.parent);
        }
    }

    /**
     * Screen pages. The General page is followed by one page per approved ore variant.
     */
    private enum ConfigPage {
        GENERAL(null),
        NETHERRACK_IRON_ORE(NetherOresRebornConfig.NETHERRACK_IRON_ORE),
        BLACKSTONE_IRON_ORE(NetherOresRebornConfig.BLACKSTONE_IRON_ORE),
        BASALT_COAL_ORE(NetherOresRebornConfig.BASALT_COAL_ORE),
        BASALT_DIAMOND_ORE(NetherOresRebornConfig.BASALT_DIAMOND_ORE);

        private final OreSettings settings;

        ConfigPage(OreSettings settings) {
            this.settings = settings;
        }

        OreSettings settings() {
            return this.settings;
        }

        Component title() {
            return this.settings == null
                    ? Component.translatable("gui.nether_ores_reborn.page.general")
                    : Component.translatable(this.settings.displayNameKey());
        }
    }
}
