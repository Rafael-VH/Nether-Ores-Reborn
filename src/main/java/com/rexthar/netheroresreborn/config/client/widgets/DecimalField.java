package com.rexthar.netheroresreborn.config.client.widgets;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.network.chat.Component;

import java.util.function.Consumer;

/**
 * Decimal text field that validates on Enter, on focus loss and on explicit request.
 * <p>
 * The character filter only restricts what can be typed; it deliberately does not reject an empty
 * string or a bare decimal point because those are intermediate states while typing. {@link #commit()}
 * is the single place that parses and validates, and it reports a translation key when the contents
 * cannot be accepted.
 */
public class DecimalField extends EditBox {
    private static final int DEFAULT_TEXT_COLOR = 0xE0E0E0;
    private static final int ERROR_TEXT_COLOR = 0xFF5555;
    private static final int ENTER_KEY = 256;

    private final double min;
    private final double max;
    private final Consumer<Double> onChange;
    private boolean valid = true;
    private String errorKey;

    /**
     * Creates a validated decimal field.
     *
     * @param font         font used to render the text
     * @param x            left edge of the widget
     * @param y            top edge of the widget
     * @param width        widget width
     * @param height       widget height
     * @param message      accessible name of the field
     * @param initialValue value loaded from the configuration
     * @param min          smallest accepted value
     * @param max          largest accepted value
     * @param onChange     called with the parsed value after a successful commit
     */
    public DecimalField(Font font, int x, int y, int width, int height, Component message,
                        double initialValue, double min, double max, Consumer<Double> onChange) {
        super(font, x, y, width, height, message);
        this.min = min;
        this.max = max;
        this.onChange = onChange;
        setMaxLength(8);
        setFilter(DecimalField::isDecimalInput);
        setValue(format(initialValue));
    }

    /**
     * Parses the current text. On success the value is passed to the change callback; on failure the
     * field enters an invalid state and {@link #errorKey()} describes why.
     */
    public void commit() {
        String error = validationErrorKey(getValue(), this.min, this.max);
        if (error != null) {
            markInvalid(error);
            return;
        }
        this.valid = true;
        this.errorKey = null;
        setTextColor(DEFAULT_TEXT_COLOR);
        this.onChange.accept(Double.parseDouble(getValue().trim()));
    }

    /**
     * Pure validation used by {@link #commit()}: returns the translation key describing why the text
     * is unusable, or {@code null} when it can be parsed and is inside the domain.
     */
    public static String validationErrorKey(String raw, double min, double max) {
        String trimmed = raw.trim();
        if (!isDecimalInput(trimmed) || !hasDigits(trimmed)) {
            return "gui.nether_ores_reborn.error.invalid_number";
        }
        double parsed = Double.parseDouble(trimmed);
        if (parsed < min || parsed > max) {
            return "gui.nether_ores_reborn.error.out_of_range";
        }
        return null;
    }

    /**
     * Returns whether the last commit produced an acceptable value.
     */
    public boolean isValid() {
        return this.valid;
    }

    /**
     * Returns the translation key for the last validation error, or {@code null} when valid.
     */
    public String errorKey() {
        return this.errorKey;
    }

    /**
     * Renders a configuration value without spurious trailing zeroes.
     */
    public static String format(double value) {
        if (value == Math.rint(value)) {
            return Long.toString((long) value);
        }
        return Double.toString(value);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (keyCode == ENTER_KEY) {
            commit();
            return true;
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public void setFocused(boolean focused) {
        if (isFocused() && !focused) {
            commit();
        }
        super.setFocused(focused);
    }

    private void markInvalid(String key) {
        this.valid = false;
        this.errorKey = key;
        setTextColor(ERROR_TEXT_COLOR);
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
}
