package wtf.choco.commons.lang;

import com.google.common.base.Preconditions;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.bukkit.ChatColor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Represents an in-memory implementation of {@link LanguageBundle}. All keys and values
 * are kept in an internal Map.
 *
 * @author Parker Hawke - Choco
 */
public abstract class MemoryLanguageBundle implements LanguageBundle {

    private final Map<@NotNull String, @Nullable String> languageNodes = new HashMap<>();
    private final Map<@NotNull String, @Nullable String> globalPlaceholders = new HashMap<>();

    /**
     * Load data into this language bundle from the given string. The provided string should
     * be newline delimited.
     *
     * @param string the string from which to load data
     */
    public abstract void loadFromString(@NotNull String string);

    /**
     * Save data from this language bundle into a newline delimited string.
     *
     * @return the string data
     */
    @NotNull
    public abstract String saveToString();

    @Override
    public void setString(@NotNull String node, @NotNull String value) {
        Preconditions.checkArgument(node != null, "node must not be null");
        Preconditions.checkArgument(value != null, "value must not be null");

        this.languageNodes.put(node, value);
    }

    @NotNull
    @Override
    public String getString(@NotNull String node, @Nullable Object @NotNull... replacements) {
        Preconditions.checkArgument(node != null, "node must not be null");
        Preconditions.checkArgument(replacements != null, "replacements must not be null");

        String value = languageNodes.get(node);
        return value != null ? String.format(processString(value, replacements), replacements) : node;
    }

    @NotNull
    @Override
    public String getString(@NotNull String node) {
        Preconditions.checkArgument(node != null, "node must not be null");

        String value = languageNodes.get(node);
        return value != null ? processString(value, null) : node;
    }

    @NotNull
    private String processString(@NotNull String string, @Nullable Object @Nullable[] replacements) {
        // Index-based replacements
        if (replacements != null) {
            for (int i = 0; i < replacements.length; i++) {
                Object replacement = replacements[i];
                string = string.replace("{" + i + "}", (replacement != null) ? replacement.toString() : "null");
            }
        }

        // Global replacements
        for (Map.Entry<@NotNull String, @Nullable String> placeholder : globalPlaceholders.entrySet()) {
            string = string.replace("%" + placeholder.getKey() + "%", placeholder.getValue());
        }

        return ChatColor.translateAlternateColorCodes('&', string);
    }

    @NotNull
    @Override
    public Set<@NotNull String> getKeys() {
        return Collections.unmodifiableSet(languageNodes.keySet());
    }

    @Override
    public boolean exists(@NotNull String node) {
        Preconditions.checkArgument(node != null, "node must not be null");
        return languageNodes.containsKey(node);
    }

    @Nullable
    @Override
    public String addGlobalPlaceholder(@NotNull String identifier, @NotNull String replacement) {
        Preconditions.checkArgument(identifier != null, "identifier must not be null");
        Preconditions.checkArgument(replacement != null, "replacement must not be null");

        return globalPlaceholders.put(identifier, replacement);
    }

    @Override
    public boolean removeGlobalPlaceholder(@NotNull String identifier) {
        return globalPlaceholders.remove(identifier) != null;
    }

    @NotNull
    @Override
    public Map<@NotNull String, @Nullable String> asMap() {
        return Collections.unmodifiableMap(languageNodes);
    }

    /**
     * Clear all data in this language bundle.
     */
    public void clear() {
        this.languageNodes.clear();
    }

}
