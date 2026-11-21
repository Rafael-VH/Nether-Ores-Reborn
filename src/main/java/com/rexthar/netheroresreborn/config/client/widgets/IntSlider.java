package com.rexthar.netheroresreborn.config.client.widgets;

import net.minecraft.client.gui.components.AbstractSliderButton;
import net.minecraft.network.chat.Component;

import java.util.function.IntConsumer;

/**
 * Slider backed by an integer domain.
 * <p>
 * The mapping between the widget's {@code [0, 1]} value and the integer domain lives in
 * {@link #toSlider(int, int, int)} and {@link #fromSlider(double, int, int)} so an off-by-one bug can
 * only exist in one place. A loaded value outside the domain is shown verbatim with a marker and is
 * replaced by a domain value only when the player actually moves the slider.
 */
public class IntSlider extends AbstractSliderButton {
    private final int min;
    private final int max;
    private final int rawValue;
    private final IntConsumer onChange;
    private boolean showRaw;

    /**
     * Creates an integer slider.
     *
     * @param x            left edge of the widget
     * @param y            top edge of the widget
     * @param width        widget width
     * @param height       widget height
     * @param initialValue value loaded from the configuration
     * @param min          smallest representable integer
     * @param max          largest representable integer
     * @param onChange     called with the new integer whenever the player changes the slider
     */
    public IntSlider(int x, int y, int width, int height, int initialValue, int min, int max,
                     IntConsumer onChange) {
        super(x, y, width, height, label(initialValue, initialValue < min || initialValue > max),
                toSlider(initialValue, min, max));
        this.min = min;
        this.max = max;
        this.rawValue = initialValue;
        this.showRaw = initialValue < min || initialValue > max;
        this.onChange = onChange;
    }

    /**
     * Maps an integer in {@code [min, max]} to the slider's {@code [0, 1]} domain, clamping values
     * outside it so the handle always stays on the track.
     */
    public static double toSlider(int value, int min, int max) {
        if (value <= min) {
            return 0.0D;
        }
        if (value >= max) {
            return 1.0D;
        }
        return (double) (value - min) / (double) (max - min);
    }

    /**
     * Maps a slider position back to an integer in {@code [min, max]}, rounding to the nearest step.
     */
    public static int fromSlider(double sliderValue, int min, int max) {
        double clamped = Math.min(1.0D, Math.max(0.0D, sliderValue));
        return min + (int) Math.round(clamped * (max - min));
    }

    /**
     * Returns the integer currently represented by the handle.
     */
    public int getIntValue() {
        return fromSlider(this.value, this.min, this.max);
    }

    @Override
    protected void updateMessage() {
        this.setMessage(label(this.showRaw ? this.rawValue : getIntValue(), this.showRaw));
    }

    @Override
    protected void applyValue() {
        this.showRaw = false;
        this.updateMessage();
        this.onChange.accept(getIntValue());
    }

    private static Component label(int value, boolean marker) {
        Component text = Component.literal(Integer.toString(value));
        return marker ? Component.translatable("gui.nether_ores_reborn.out_of_domain", text) : text;
    }
}
