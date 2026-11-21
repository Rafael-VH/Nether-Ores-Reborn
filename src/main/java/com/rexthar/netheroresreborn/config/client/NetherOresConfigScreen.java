package com.rexthar.netheroresreborn.config.client;

import com.mojang.logging.LogUtils;
import com.rexthar.netheroresreborn.config.NetherOresRebornConfig;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.CycleButton;
import net.minecraft.client.gui.components.EditBox;
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

    private final Screen parent;
    private final Map<ForgeConfigSpec.ConfigValue<?>, Object> pending = new LinkedHashMap<>();

    private ConfigPage page = ConfigPage.GENERAL;
    private boolean specReady;
    private Component error;
    private EditBox dropMultiplierBox;

    public NetherOresConfigScreen(Screen parent) {
        super(Component.translatable("gui.nether_ores_reborn.title"));
        this.parent = parent;
    }

    @Override
    protected void init() {
        this.clearWidgets();
        this.dropMultiplierBox = null;
        this.specReady = NetherOresRebornConfig.SPEC.isLoaded();

        int left = (this.width - CONTENT_WIDTH) / 2;
        int controlX = left + LABEL_WIDTH + CONTROL_GAP;

        initPageNavigation(left);
        initPageBody(controlX);
        initFooter(left);
    }

    private void initPageNavigation(int left) {
        int navY = 30;
        Button previous = Button.builder(Component.literal("<"), button -> changePage(-1))
                .bounds(left, navY, 24, WIDGET_HEIGHT)
                .build();
        previous.active = ConfigPage.values().length > 1;
        addRenderableWidget(previous);

        Button next = Button.builder(Component.literal(">"), button -> changePage(1))
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
        }
    }

    private void initGeneral(int controlX, int firstRowY) {
        addRenderableWidget(CycleButton.onOffBuilder(current(NetherOresRebornConfig.ENABLE_GENERATION))
                .create(controlX, firstRowY, CONTROL_WIDTH, WIDGET_HEIGHT,
                        Component.translatable("gui.nether_ores_reborn.general.enable_generation"),
                        (button, value) -> pending.put(NetherOresRebornConfig.ENABLE_GENERATION, value)));

        this.dropMultiplierBox = new EditBox(this.font, controlX, firstRowY + ROW_HEIGHT, CONTROL_WIDTH,
                WIDGET_HEIGHT, Component.translatable("gui.nether_ores_reborn.general.drop_multiplier"));
        this.dropMultiplierBox.setMaxLength(8);
        this.dropMultiplierBox.setFilter(NetherOresConfigScreen::isDecimalInput);
        this.dropMultiplierBox.setValue(formatDecimal(current(NetherOresRebornConfig.GLOBAL_DROP_MULTIPLIER)));
        addRenderableWidget(this.dropMultiplierBox);
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

        if (this.error != null) {
            graphics.drawCenteredString(this.font, this.error, centerX, this.height - 48, ERROR_COLOR);
        }
    }

    /**
     * Renders the label column for the active page. Labels are drawn separately from the widgets so
     * each row stays a single line of code at its call site.
     */
    private void renderRowLabels(GuiGraphics graphics) {
        if (this.page != ConfigPage.GENERAL) {
            return;
        }
        int labelX = (this.width - CONTENT_WIDTH) / 2;
        drawRowLabel(graphics, "gui.nether_ores_reborn.general.enable_generation", labelX, 0);
        drawRowLabel(graphics, "gui.nether_ores_reborn.general.drop_multiplier", labelX, 1);
    }

    private void drawRowLabel(GuiGraphics graphics, String translationKey, int labelX, int row) {
        int y = FIRST_ROW_Y + row * ROW_HEIGHT + 6;
        graphics.drawString(this.font, Component.translatable(translationKey), labelX, y, TEXT_COLOR, false);
    }

    private void changePage(int delta) {
        ConfigPage[] pages = ConfigPage.values();
        int index = Math.floorMod(this.page.ordinal() + delta, pages.length);
        this.page = pages[index];
        this.error = null;
        rebuildWidgets();
    }

    private void resetPage() {
        switch (this.page) {
            case GENERAL -> {
                this.pending.put(NetherOresRebornConfig.ENABLE_GENERATION,
                        NetherOresRebornConfig.DEFAULT_ENABLE_GENERATION);
                this.pending.put(NetherOresRebornConfig.GLOBAL_DROP_MULTIPLIER,
                        NetherOresRebornConfig.DEFAULT_GLOBAL_DROP_MULTIPLIER);
            }
        }
        this.error = null;
        rebuildWidgets();
    }

    private void saveAndClose() {
        if (!commitTextFields()) {
            return;
        }
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
     * Moves text field contents into {@link #pending}. Returns {@code false} when any field holds an
     * invalid value, in which case nothing is applied or saved.
     */
    private boolean commitTextFields() {
        if (this.dropMultiplierBox == null) {
            return true;
        }
        String raw = this.dropMultiplierBox.getValue().trim();
        if (!isDecimalInput(raw) || !hasDigits(raw)) {
            this.error = Component.translatable("gui.nether_ores_reborn.error.invalid_number");
            rebuildWidgets();
            return false;
        }
        double parsed = Double.parseDouble(raw);
        if (parsed < 0.0D || parsed > 100.0D) {
            this.error = Component.translatable("gui.nether_ores_reborn.error.out_of_range");
            rebuildWidgets();
            return false;
        }
        this.pending.put(NetherOresRebornConfig.GLOBAL_DROP_MULTIPLIER, parsed);
        return true;
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

    private static boolean isDecimalInput(String raw) {
        return raw.matches("\\d{0,3}(\\.\\d{0,2})?");
    }

    /**
     * The character filter also accepts an empty string and a bare decimal point, neither of which
     * {@link Double#parseDouble(String)} can parse.
     */
    private static boolean hasDigits(String raw) {
        for (int i = 0; i < raw.length(); i++) {
            if (Character.isDigit(raw.charAt(i))) {
                return true;
            }
        }
        return false;
    }

    private static String formatDecimal(double value) {
        if (value == Math.rint(value)) {
            return Long.toString((long) value);
        }
        return Double.toString(value);
    }

    /**
     * Pages of the screen. Only the General page exists at this stage; the per-ore pages are added by
     * the next work unit.
     */
    private enum ConfigPage {
        GENERAL("gui.nether_ores_reborn.page.general");

        private final String titleKey;

        ConfigPage(String titleKey) {
            this.titleKey = titleKey;
        }

        Component title() {
            return Component.translatable(this.titleKey);
        }
    }
}
