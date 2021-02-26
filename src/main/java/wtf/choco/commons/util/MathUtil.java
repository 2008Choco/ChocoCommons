package wtf.choco.commons.util;

import com.google.common.base.Preconditions;

import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

import org.jetbrains.annotations.NotNull;

/**
 * A series of utilities pertaining to mathematical operations.
 *
 * @author Parker Hawke - Choco
 */
public final class MathUtil {

    private MathUtil() { }

    /**
     * Generate a random number between a minimum and maximum value (inclusive).
     *
     * @param random the random instance to use
     * @param min the minimum value (inclusive)
     * @param max the maximum value (inclusive)
     *
     * @return the randomly generated number
     */
    public static int generateNumberBetween(@NotNull Random random, int min, int max) {
        Preconditions.checkArgument(min <= max, "min (%s) must be <= max (%s)", min, max);

        if (random instanceof ThreadLocalRandom) {
            return ((ThreadLocalRandom) random).nextInt(min, max + 1);
        }

        return (min == max) ? min : min + (random.nextInt(max - min) + 1);
    }

    /**
     * Generate a random number between a minimum and maximum value (inclusive).
     *
     * @param min the minimum value (inclusive)
     * @param max the maximum value (inclusive)
     *
     * @return the randomly generated number
     */
    public static int generateNumberBetween(int min, int max) {
        return generateNumberBetween(ThreadLocalRandom.current(), min, max);
    }

    /**
     * Clamp a value between a minimum and maximum value. If the value exceeds the
     * specified bounds, it will be limited to its exceeding bound.
     *
     * @param value the value to clamp
     * @param min the minimum allowed value
     * @param max the maximum allowed value
     *
     * @return the clamped value. Itself if the boundaries were not exceeded
     */
    public static int clamp(int value, int min, int max) {
        return (value < min ? min : (value > max ? max : value));
    }

    /**
     * Clamp a value between a minimum and maximum value. If the value exceeds the
     * specified bounds, it will be limited to its exceeding bound.
     *
     * @param value the value to clamp
     * @param min the minimum allowed value
     * @param max the maximum allowed value
     *
     * @return the clamped value. Itself if the boundaries were not exceeded
     */
    public static double clamp(double value, double min, double max) {
        return (value < min ? min : (value > max ? max : value));
    }

    /**
     * Clamp a value between a minimum and maximum value. If the value exceeds the
     * specified bounds, it will be limited to its exceeding bound.
     *
     * @param value the value to clamp
     * @param min the minimum allowed value
     * @param max the maximum allowed value
     *
     * @return the clamped value. Itself if the boundaries were not exceeded
     */
    public static float clamp(float value, float min, float max) {
        return (value < min ? min : (value > max ? max : value));
    }

}
