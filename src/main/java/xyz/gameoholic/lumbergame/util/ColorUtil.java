package xyz.gameoholic.lumbergame.util;

import net.kyori.adventure.text.format.TextColor;

import static net.kyori.adventure.text.Component.text;

public class ColorUtil {
    /**
     * @param value The fraction value (between 0-1).
     * @return A color matching the value. Green for 1.0, and the lower it is, the more red it is, Red for 0.0.
     */
    public static TextColor getGreenRedColor(double value) {
        return TextColor.color((int) (255 * (1 - value)), (int) (255 * value), 0);
    }
}
