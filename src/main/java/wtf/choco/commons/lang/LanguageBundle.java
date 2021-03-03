package wtf.choco.commons.lang;

import java.util.Map;
import java.util.Set;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Represents a key-value mapping of language keys to strings. Used to define externalized
 * strings in a file or in memory.
 * <p>
 * Common use of LanguageBundle may look like the following:
 * <pre>
 * private final LanguageBundle language = FileLanguageBundle.loadFromFile(new File("en_US.lang"));
 *
 * public void foo(Player player) {
 *     player.sendMessage(language.getString("my.language.key"));
 *     player.sendMessage(language.getString("my.language.key.with_data", "First argument", "second argument", 10));
 * }
 * </pre>
 *
 * Language entries may be fetched with optional arguments injected into the returned string. These
 * arguments are represented in the string as non-named/non-identified parameter indexes.
 * <pre>
 * LanguageBundle language = new FileLanguageBundle();
 * language.setString("foo.bar", "Hello {0}. You have {1} coins.");
 * System.out.println(language.getString("foo.bar", "2008Choco", 10)); // Prints "Hello 2008Choco. You have 10 coins."
 * </pre>
 *
 * Language bundles may also define global placeholders with identifiers surrounded with {@code %}
 * characters that will be replaced every time {@link #getString(String)} is invoked. Example:
 * <pre>
 * LanguageBundle language = new FileLanguageBundle();
 * language.setString("foo.bar", "Hello %foo%.");
 * language.addGlobalPlaceholder("foo", "world");
 *
 * System.out.println(language.getString("foo.bar")); // Prints "Hello world."
 * </pre>
 *
 * @author Parker Hawke - Choco
 *
 * @see MemoryLanguageBundle
 * @see FileLanguageBundle
 */
public interface LanguageBundle {

    /**
     * Assign a string to the given node in this language bundle.
     *
     * @param node the node to set
     * @param value the value of the node to set
     */
    public void setString(@NotNull String node, @NotNull String value);

    /**
     * Get the string associated with the given node and inject the given replacements.
     *
     * @param node the node to get
     * @param replacements the replacements to inject
     *
     * @return the string. if no string is found, the node is returned
     */
    @NotNull
    public String getString(@NotNull String node, @Nullable Object @NotNull... replacements);

    /**
     * Get the string associated with the given node.
     *
     * @param node the node to get
     *
     * @return the string. if no string is found, the node is returned
     */
    @NotNull
    public String getString(@NotNull String node);

    /**
     * Get an unmodifiable set of all nodes in this language bundle.
     *
     * @return the key set
     */
    @NotNull
    public Set<@NotNull String> getKeys();

    /**
     * Check whether or not the given node has assigned a string.
     *
     * @param node the node to check
     *
     * @return true if a value is assigned, false otherwise
     */
    public boolean exists(@NotNull String node);

    /**
     * Add a global named placeholder to be injected into every string returned by
     * {@link #getString(String)}.
     * <p>
     * Global placeholders are denoted in strings using percentage notation (i.e.
     * {@code addGlobalPlaceholder("foo", "bar")} will replace any instance of {@code %foo%}
     * in the returned string.
     *
     * @param identifier the placeholder identifier
     * @param replacement the string to inject when the placeholder is found
     *
     * @return the replacement that was previously mapped to the given identifier (if any).
     * null if no previous mapping was found
     */
    @Nullable
    public String addGlobalPlaceholder(@NotNull String identifier, @NotNull String replacement);

    /**
     * Remove a global named placeholder.
     *
     * @param identifier the placeholder identifier
     *
     * @return true if removed, false if no placeholder existed
     */
    public boolean removeGlobalPlaceholder(@NotNull String identifier);

    /**
     * Get this language bundle's keys and values as a Map.
     *
     * @return the map
     */
    @NotNull
    public Map<@NotNull String, @Nullable String> asMap();

}
