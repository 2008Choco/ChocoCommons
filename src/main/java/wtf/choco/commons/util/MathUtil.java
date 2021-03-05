package wtf.choco.commons.util;

import com.google.common.base.Preconditions;

import java.util.EnumSet;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.math.NumberUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * A series of utilities pertaining to mathematical operations.
 *
 * @author Parker Hawke - Choco
 */
public final class MathUtil {

    private static final Pattern TIME_PATTERN = Pattern.compile("(\\d+)([wdhms])");

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

    /**
     * Parse a timestamp value (i.e. 1w2d3h4m5s) and return its value in seconds.
     *
     * @param value the value to parse
     * @param defaultSeconds the value to return if "value" is null (i.e. from a config)
     *
     * @return the amount of time in seconds represented by the supplied value
     */
    public static int parseSeconds(@Nullable String value, int defaultSeconds) {
        return (value != null) ? parseSeconds(value) : defaultSeconds;
    }

    /**
     * Parse a timestamp value (i.e. 1w2d3h4m5s) and return its value in seconds.
     *
     * @param value the value to parse
     *
     * @return the amount of time in seconds represented by the supplied value
     */
    public static int parseSeconds(@Nullable String value) {
        if (value == null) {
            return 0;
        }

        // Handle legacy (i.e. no timestamps... for example, just "600")
        int legacyTime = NumberUtils.toInt(value, -1);
        if (legacyTime != -1) {
            return legacyTime;
        }

        int seconds = 0;

        Matcher matcher = TIME_PATTERN.matcher(value);
        while (matcher.find()) {
            int amount = NumberUtils.toInt(matcher.group(1));

            switch (matcher.group(2)) {
                case "w":
                    seconds += TimeUnit.DAYS.toSeconds(amount * 7);
                    break;
                case "d":
                    seconds += TimeUnit.DAYS.toSeconds(amount);
                    break;
                case "h":
                    seconds += TimeUnit.HOURS.toSeconds(amount);
                    break;
                case "m":
                    seconds += TimeUnit.MINUTES.toSeconds(amount);
                    break;
                case "s":
                    seconds += amount;
                    break;
            }
        }

        return seconds;
    }

    /**
     * Get a formatted time String from a time in seconds. Formatted time should be in the
     * format, "x hours, y minutes, z seconds". Alternatively, if time is 0, "now", or
     * "invalid seconds" otherwise.
     *
     * @param timeInSeconds the time in seconds
     * @param condensed whether or not to condense time units (i.e. hours {@literal ->} h)
     * @param omitions the time units to omit
     *
     * @return the formatted time
     */
    @NotNull
    public static String getFormattedTime(int timeInSeconds, boolean condensed, @NotNull TimeUnit @NotNull... omitions) {
        Preconditions.checkArgument(omitions != null, "omitions must not be null");

        if (timeInSeconds <= 0) {
            return (timeInSeconds == 0) ? "now" : "invalid seconds";
        }

        Set<@NotNull TimeUnit> omitionsSet = EnumSet.noneOf(TimeUnit.class);
        if (omitions != null) {
            for (TimeUnit unit : omitions) {
                omitionsSet.add(unit);
            }
        }

        StringBuilder resultBuilder = new StringBuilder();

        if (timeInSeconds >= 604800) { // Weeks
            MathUtil.appendAndSeparate(resultBuilder, (int) Math.floor(timeInSeconds / 604800), "week", condensed);
            timeInSeconds %= 604800;
        }

        if (timeInSeconds >= 86400) { // Days
            if (!omitionsSet.contains(TimeUnit.DAYS)) {
                MathUtil.appendAndSeparate(resultBuilder, (int) Math.floor(timeInSeconds / 86400), "day", condensed);
            }

            timeInSeconds %= 86400;
        }

        if (timeInSeconds >= 3600) { // Hours
            if (!omitionsSet.contains(TimeUnit.HOURS)) {
                MathUtil.appendAndSeparate(resultBuilder, (int) Math.floor(timeInSeconds / 3600), "hour", condensed);
            }

            timeInSeconds %= 3600;
        }

        if (timeInSeconds >= 60) { // Minutes
            if (!omitionsSet.contains(TimeUnit.MINUTES)) {
                MathUtil.appendAndSeparate(resultBuilder, (int) Math.floor(timeInSeconds / 60), "minute", condensed);
            }

            timeInSeconds %= 60;
        }

        if (!omitionsSet.contains(TimeUnit.SECONDS) && timeInSeconds > 0) { // Seconds
            MathUtil.appendAndSeparate(resultBuilder, timeInSeconds, "second", condensed);
        }

        String result = resultBuilder.toString().trim();
        if (result.endsWith(",")) {
            result = result.substring(0, result.length() - 1);
        }

        if (result.isEmpty()) {
            result = "soon";
        }

        return result;
    }

    private static void appendAndSeparate(StringBuilder builder, int value, String toAppend, boolean condensed) {
        builder.append(value).append(' ').append(condensed ? toAppend.charAt(0) : toAppend);

        if (!condensed && value > 1) {
            builder.append('s');
        }

        builder.append(", ");
    }

}
