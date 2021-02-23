package wtf.choco.commons.integration;

import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

/**
 * Represents a native integration with another plugin on the server.
 * <p>
 * For the sake of simplicity, implementations of this interface should declare a
 * constructor that accepts a Plugin instance, at which point it may be safely casted
 * to the integrated plugin's main class instance. Example:
 * <pre>
 * public class PluginIntegrationExample implements PluginIntegration {
 *
 *     private final ExamplePlugin integratedPlugin;
 *
 *     public PluginIntegrationExample(Plugin integratedPlugin) {
 *         this.integratedPlugin = (ExamplePlugin) integratedPlugin; // This is a safe cast
 *     }
 *
 *     {@code @NotNull}
 *     {@code @Override}
 *     public Plugin getIntegratedPlugin() {
 *         return integratedPlugin;
 *     }
 *
 *     public void init() { }
 *
 *     public void disable() { }
 *
 * }
 * </pre>
 * <strong>NOTE:</strong> If you are a developer writing integration with Alchema, you
 * <strong>DO NOT NEED TO USE THIS INTERFACE!</strong> This is for native integrations
 * ONLY (i.e. integrations added by Alchema).
 *
 * @author Parker Hawke - Choco
 */
public interface PluginIntegration {

    /**
     * The plugin instance with which this integration integrates.
     *
     * @return the integrated plugin
     */
    @NotNull
    public Plugin getIntegratedPlugin();

    /**
     * Load this integration. Called {@link JavaPlugin#onLoad()}.
     */
    public void load();

    /**
     * Initialize this integration. Called {@link JavaPlugin#onEnable()}.
     */
    public void enable();

    /**
     * Disable this integration. Called {@link JavaPlugin#onDisable()}.
     */
    public void disable();

    /**
     * Determine whether or not this integration is supported. If this method returns
     * false, this integration will not be loaded by the integration handler.
     *
     * @return true if supported, false otherwise
     */
    public default boolean isSupported() {
        return true;
    }

}
